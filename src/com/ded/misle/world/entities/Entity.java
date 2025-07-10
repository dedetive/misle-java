package com.ded.misle.world.entities;

import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.renderer.smoother.modifiers.BounceModifier;
import com.ded.misle.renderer.smoother.modifiers.ShakeModifier;
import com.ded.misle.world.data.*;
import com.ded.misle.world.data.entity.configurations.EnemyType;
import com.ded.misle.world.data.entity.configurations.EntityType;
import com.ded.misle.world.data.entity.GenericType;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorController;
import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.entities.npcs.NPC;
import com.ded.misle.world.entities.player.Player;

import java.time.Duration;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.items.Item.createDroppedItem;
import static com.ded.misle.world.logic.PhysicsEngine.ObjectType.ENTITY;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.entities.Entity.HealFlag.ABSOLUTE;

/**
 * Represents an entity with health points (HP).
 * Handles health, damage, healing, and death logic. Also manages shared Entity instances.
 */
public class Entity extends Box {
    /** The type of this entity, which defines its default configuration. */
    protected GenericType type;
    /** A scaling factor that magnifies the entity's attributes (e.g. HP, damage). */
    protected final double magnification;
    /** Controls the AI behaviors of this entity. */
    protected final BehaviorController controller = new BehaviorController(this);
    /** Current HP of the entity. */
    protected double HP = 1;

    /** Maximum HP the entity can have. */
    protected double maxHP = 1;

    /**
     * Represents whether the entity can have its health bar displayed.
     */
    protected boolean displayHP = false;

    /**
     * HP smoothness manager. Should be updated every frame to stay relevant.
     * <p>
     * Shows current HP of the entity that is going to be rendered. Has a slight delay compared to real internal {@link #HP}.
     */
    protected final SmoothValue HPSmoother = new SmoothValue((float) HP);;

    /** Locked HP that cannot be recovered or reduced normally. */
    protected double lockedHP;

    /** Defense value used to reduce incoming damage. */
    protected double defense;

    protected boolean isRegenerationDoubled;

    /** Chance (0-100%) to invert damage into healing. */
    protected double inversion;

    /** Quality factor used for regeneration calculation. */
    protected double regenerationQuality;

    /** Represents how many turns it takes to regenerate again. Default value is 5. */
    protected int regenerationRate = 5;

    /**
     * Represents whether the regeneration cooldown has passed or not.
     */
    protected boolean canRegenerate = true;

    /**
     * Timer responsible for turning {@link #canRegenerate} true.
     */
    protected TurnTimer regenerationTimer;

    /** If true, entity is immune to all damage. */
    protected boolean isInvulnerable;

    /** Drop table for items when this entity dies. */
    protected DropTable dropTable;

    /** Static list of all active Entities. */
    private static final List<Entity> entities = new ArrayList<>();
    /** The amount of XP awarded to the player when this entity is defeated. */
    protected double xpDrop = 0;
    /** The range of coins that may drop from this entity. */
    protected CoinDropRange coinDrop = new CoinDropRange(0);
    protected int turnsToRespawn = Integer.MIN_VALUE;
    protected TurnTimer respawnTimer;

    /**
     * Returns the list of all Entities.
     * @return list of Entity instances.
     */
    public static List<Entity> getEntities() {
        return entities;
    }

    /**
     * Clears all Entities except the player.
     */
    public static void clearEntities() {
        entities.clear();
        entities.add(player);
    }

    /**
     * Constructs an Entity at a specific position. Default values of HP and max HP are 1.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public Entity(int x, int y, GenericType type, double magnification) {
        super(x, y);

        this.magnification = magnification;
        this.setObjectType(ENTITY);
        entities.add(this);
        updateRegenerationTimer();
        this.type = type;
        this.load();
    }

    /**
     * Constructs an Entity at a specific position. Default values of HP and max HP are 1.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public Entity(int x, int y, double magnification) {
        this(x, y, null, magnification);
    }

    /**
     * Constructs an Entity with HP and max HP set as 1 and no position.
     */
    public Entity() {
        this.magnification = 1;
        this.setObjectType(ENTITY);
        this.type = null;
        entities.add(this);
        updateRegenerationTimer();
    }

    /**
     * Sets the current HP. Also checks if this died.
     * @param HP New HP value.
     */
    public void setHP(double HP) {
        this.HP = HP;
        this.HPSmoother.setTarget((float) HP);
        handleDeath();
    }

    /**
     * Applies the configuration from this entity's {@link EntityType}.
     */
    protected void load() {
        if (type == null) return;
        type.applyTo(this);
    }

    /**
     * Returns the magnification factor used to scale the entity's stats.
     *
     * @return the magnification multiplier
     */
    public double getMagnification() {
        return this.magnification;
    }

    /**
     * Returns the {@link EntityType} of this entity.
     *
     * @return the type of this entity
     */
    public GenericType getEntityType() {
        return type;
    }

    /**
     * Returns the amount of XP this entity drops when defeated.
     *
     * @return the XP drop value
     */
    public double getXPDrop() {
        return xpDrop;
    }

    /**
     * Sets the amount of XP this entity will drop.
     *
     * @param xp the XP amount
     */
    public void setXpDrop(int xp) {
        this.xpDrop = xp;
    }

    /**
     * Sets the coin drop range using minimum and maximum values.
     *
     * @param min the minimum number of coins
     * @param max the maximum number of coins
     */
    public void setCoinDropRange(int min, int max) {
        this.coinDrop = new CoinDropRange(min, max);
    }

    /**
     * Sets the coin drop range using a {@link CoinDropRange} object.
     *
     * @param coinDropRange the coin drop range
     */
    public void setCoinDropRange(CoinDropRange coinDropRange) {
        this.coinDrop = coinDropRange;
    }

    /**
     * Sets a fixed coin drop amount (min = max = amount).
     *
     * @param coinDrop the fixed number of coins
     */
    public void setCoinDrop(int coinDrop) {
        this.coinDrop = new CoinDropRange(coinDrop);
    }

    /**
     * Returns a randomly rolled coin drop based on the drop range.
     *
     * @return the coin drop amount
     */
    public int getCoinDrop() {
        return coinDrop.roll();
    }

    /**
     * Sets the AI behaviors for this entity.
     *
     * @param behaviors the list of behaviors
     */
    public void setBehaviors(AIBehavior... behaviors) {
        controller.setBehaviors(behaviors);
    }

    /**
     * Returns the list of AI behaviors assigned to this entity.
     *
     * @return the array of behaviors
     */
    public AIBehavior[] getBehaviors() {
        return controller.getBehaviors();
    }

    /**
     * Returns the {@link BehaviorController} managing this entity's behavior.
     *
     * @return the behavior controller
     */
    public BehaviorController getController() {
        return controller;
    }

    /**
     * Sets the maximum HP.
     * @param maxHP New maximum HP.
     */
    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP * getMagnification();
    }

    /**
     * Returns the current HP.
     * @return current HP.
     */
    public double getHP() {
        return HP;
    }

    /**
     * Returns the maximum HP.
     * @return maximum HP.
     */
    public double getMaxHP() {
        return maxHP;
    }

    /**
     * Instantly sets HP to maximum.
     */
    public void fillHP() {
        this.HP = maxHP;
        this.HPSmoother.setTarget((float) HP);
    }

    public void setTurnsToRespawn(int turnsToRespawn) {
        this.turnsToRespawn = turnsToRespawn;
        respawnTimer = new TurnTimer(turnsToRespawn + 1, e -> respawnIfPossible());
        respawnTimer.setRoomScoped(true);
    }

    public boolean canRespawn() {
        return player.loadTimerFromUUID(this.getId()) <= 0;
    }

    public void respawnIfPossible() {
        if (canRespawn()) {
            respawn();
            respawnTimer.reset();
        }
    }

    /**
     * Checks if the entity is dead. If so, handles drop and removal logic.
     * @return true if dead, false otherwise.
     */
    private boolean checkIfDead() {
        if (player.pos.world == null) return false;

        if (this.HP == 0) {
            if (!(this instanceof Player)) {
                if (this.HPSmoother.getCurrentFloat() >= 0.5 && this.displayHP) {
                    javax.swing.Timer checkAgainTimer = new javax.swing.Timer(30,
                        e -> handleDeath());
                    checkAgainTimer.setRepeats(false);
                    checkAgainTimer.start();
                    return true;
                }

                if (this.dropTable != null) {
                    int[] results = dropTable.getRandomItemID();
                    int id = results[0];
                    int count = results[1];

                    createDroppedItem(this.getX(), this.getY(), id, count);
                }

                player.attr.addXP(this.getXPDrop());

                player.attr.addBalance(this.getCoinDrop());

                entities.remove(this);
                deleteBox(this);
                if (this instanceof Enemy) {
                    ((Enemy) this).kill();
                } else if (this instanceof NPC) {
                    ((NPC) this).deleteNPC();
                }
                deleteBox(this);
            } else {
                player.attr.playerDies();
            }
            return true;
        }
        return false;
    }

    public SmoothValue getHPSmoother() {
        return HPSmoother;
    }

    public boolean displayHP() {
        return displayHP;
    }

    public void setDisplayHP(boolean displayHP) {
        this.displayHP = displayHP;
    }

    public boolean handleDeath() {
        boolean result = checkIfDead();

        if (result && turnsToRespawn > 0) {
            player.storeTimerInUUID(this.getId(), turnsToRespawn);
            respawnTimer.start();
        } else if (result && turnsToRespawn != Integer.MIN_VALUE) {
            respawn();
        }

        return result;
    }

    protected void respawn() {
        switch (this.getClass().getSimpleName()) {
            case "Entity" -> new Entity(this.getOrigin().x, this.getOrigin().y, type, this.magnification);
            case "Enemy" -> new Enemy(this.getOrigin(), (EnemyType) type, this.magnification);
        }
    }

    public void scheduleRespawn() {
        int turns = player.loadTimerFromUUID(this.getId());
        respawnTimer = new TurnTimer(turns, e -> respawnIfPossible());
        respawnTimer.setRoomScoped(true);
        respawnTimer.start();
    }

    /**
     * Damage flags used to control how damage is applied.
     */
    public enum DamageFlag {
        /**
         * Defense and item effects take place normally.
         */
        NORMAL,

        /**
         * The damage value given will be dealt regardless of defense or other factors, unless the player dies.
         */
        ABSOLUTE,

        /**
         * The damage will be dealt even if the player dies. May result in negative HP.
         */
        POST_MORTEM,

        /**
         * No damage is actually done. Instead, a portion of the HP is locked and temporarily not considered. Uses <code>lockDuration</code> to define how many milliseconds it takes for the HP to be unlocked.
         */
        LOCKER,

        ;

        /**
         * Creates an EnumSet from given DamageFlags.
         * @param flags Array of flags.
         * @return EnumSet of flags.
         */
        public static EnumSet<DamageFlag> of(DamageFlag... flags) {
            return EnumSet.copyOf(List.of(flags));
        }
    }

    // DAMAGE AND HEAL

    /**
     * Deals damage to the entity. <p></p>
     * LockDuration is defaulted to empty and knockback direction is set to NONE.
     * @param rawDamage Raw incoming damage.
     * @param flags A set of {@link DamageFlag} values.
     * @return Final damage applied.
     */
    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags) {
        return takeDamage(rawDamage, flags, Optional.empty(), Direction.NONE);
    }

    /**
     * Deals damage to the entity. <p></p>
     * LockDuration is defaulted to empty.
     * @param rawDamage Raw incoming damage.
     * @param flags A set of {@link DamageFlag} values.
     * @param knockback Direction of the applied knockback.
     * @return Final damage applied.
     */
    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags, Direction knockback) {
        return takeDamage(rawDamage, flags, Optional.empty(), knockback);
    }

    /**
     * Deals damage to the entity.
     * @param rawDamage Raw incoming damage.
     * @param flags A set of {@link DamageFlag} values.
     * @param knockback Direction of the applied knockback.
     * @param lockDuration Duration before locked HP resets. Does nothing if LOCKER flag is not given.
     * @return Final damage applied.
     */
    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags, Optional<Duration> lockDuration, Direction knockback) {
        isRegenerationDoubled = false;

        if (rawDamage <= 0) return 0;

        boolean inversionTriggers = Math.random() * 100 < inversion;
        double finalDamage = calculateDamage(rawDamage, flags);

        if (inversionTriggers) {
            handleInversionHeal(finalDamage);
        } else {
            if (flags.contains(DamageFlag.LOCKER)) {
                applyLocker(finalDamage, lockDuration);
            } else {
                applyDamageToHP(finalDamage, flags);
            }
        }

        applyKnockback(knockback);

        for (Runnable r : onDamage) {
            r.run();
        }
        onDamage.clear();

        return finalDamage;
    }

    /**
     * Applies inverted healing instead of damage.
     * @param amount Amount to heal.
     */
    private void handleInversionHeal(double amount) {
        receiveHeal(amount, HealFlag.of(ABSOLUTE));
    }

    /**
     * Locks a portion of HP.
     * @param amount Amount to lock.
     * @param durationOpt Optional duration to unlock automatically.
     */
    private void applyLocker(double amount, Optional<Duration> durationOpt) {
        lockedHP += amount;
        durationOpt.ifPresent(duration -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    unlockHP(amount);
                }
            }, duration.toMillis());
        });
    }

    /**
     * Applies final damage value to HP.
     * @param amount Damage to apply.
     * @param flags Damage flags.
     */
    private void applyDamageToHP(double amount, EnumSet<DamageFlag> flags) {
        if (flags.contains(DamageFlag.POST_MORTEM)) {
            setHP(getHP() - amount); // Can go negative
        } else {
            setHP(Math.max(getHP() - amount, 0));
        }
    }

    /**
     * Applies knockback effect.
     * @param dir Direction of knockback.
     */
    private void applyKnockback(Direction dir) {
        float amplitude = 0.5f;
        float duration = 0.3f;
        float freq = 8f;
        ShakeModifier shaking = new ShakeModifier(0.04f, 0.08f);
        if (this instanceof Player) dir = dir.getOpposite();

        try {
            player.pos.shakeScreen(1f);

            switch (dir) {
                case RIGHT -> {
                    visualOffsetX.addModifiers(
                        new BounceModifier(amplitude, duration, freq),
                        shaking.clone());

                    visualOffsetY.addModifiers(shaking.clone());
                }
                case LEFT -> {
                    visualOffsetX.addModifiers(
                        new BounceModifier(-amplitude, duration, freq),
                        shaking.clone());

                    visualOffsetY.addModifiers(shaking.clone());
                }
                case DOWN -> {
                    visualOffsetY.addModifiers(
                        new BounceModifier(amplitude, duration, freq),
                        shaking.clone());

                    visualOffsetX.addModifiers(shaking.clone());
                }
                case UP -> {
                    visualOffsetY.addModifiers(
                        new BounceModifier(-amplitude, duration, freq),
                        shaking.clone());

                    visualOffsetX.addModifiers(shaking.clone());
                }
            }
        } catch (CloneNotSupportedException ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * Calculates final damage after defense and flags.
     * @param rawDamage Raw incoming damage.
     * @param flags Damage flags.
     * @return Final damage value.
     */
    public double calculateDamage(double rawDamage, EnumSet<DamageFlag> flags) {
        if (rawDamage <= 0) return 0;

        boolean isInvulnerable = getIsInvulnerable();
        boolean isAbsolute = flags.contains(DamageFlag.ABSOLUTE);
        boolean isPostMortem = flags.contains(DamageFlag.POST_MORTEM);

        if (isAbsolute && isPostMortem) return rawDamage;
        if (isAbsolute) return Math.max(Math.min(rawDamage, this.getHP()), 0);

        double defenseReduction = rawDamage - ((rawDamage * defense) / (defense + 100)) - (defense / 2);

        if (isPostMortem && !isInvulnerable) return defenseReduction;
        if (!isInvulnerable) return Math.max(Math.min(defenseReduction, this.getHP()), 0);

        return 0;
    }


    // LOCKED HP

    /**
     * Returns locked HP amount.
     * @return Locked HP.
     */
    public double getLockedHP() {
        return lockedHP;
    }

    /**
     * Sets locked HP.
     * @param lockedHP New locked HP.
     * @return New value of locked HP.
     */
    public double setLockedHP(double lockedHP) {
        this.lockedHP = lockedHP;
        return lockedHP;
    }

    /**
     * Unlocks a portion of locked HP.
     * @param damage Amount to unlock.
     */
    public void unlockHP(double damage) {
        this.setLockedHP(Math.max(lockedHP - damage, 0));
    }

    /**
     * Heal flags to control how healing is applied.
     */
    public enum HealFlag {
        /**
         * The heal can be affected by external forces, but is limited by max HP.
         */
        NORMAL,

        /**
         * The heal will be received no matter what, unless max HP is hit.
         */
        ABSOLUTE,

        /**
         * The heal can be affected by external forces, but is NOT limited by max HP.
         */
        OVERHEAL,

        /**
         * The heal will be received regardless of whether the player is dead.
         * This can revive the player. The value may be affected by external forces.
         */
        REVIVAL,

        /**
         * The heal will only be received if the player is dead.
         * This will revive the player. The value may be affected by external forces.
         */
        REVIVAL_EXCLUSIVE;

        /**
         * Creates an EnumSet from given HealFlags.
         *
         * @param flags Flags to include.
         * @return EnumSet of heal flags.
         */
        public static EnumSet<HealFlag> of(HealFlag... flags) {
            return EnumSet.copyOf(List.of(flags));
        }
    }


    /**
     * Applies healing to this entity.
     * @param heal Heal amount.
     * @param flags A set of {@link HealFlag} values.
     * @return Final amount healed.
     */
    public double receiveHeal(double heal, EnumSet<HealFlag> flags) {
        if (!canReceiveHeal(heal, flags)) return 0;

        double healToReceive = calculateHeal(heal, flags);
        this.setHP(this.getHP() + healToReceive);

        if (shouldRevive(flags)) {
            player.attr.playerRevived();
        }

        return healToReceive;
    }

    /**
     * Validates whether the healing can be applied.
     * @param heal Amount to heal.
     * @param flags Healing flags.
     * @return true if healing is valid.
     */
    private boolean canReceiveHeal(double heal, EnumSet<HealFlag> flags) {
        if (heal <= 0) return false;

        boolean isPlayer = this instanceof Player;
        boolean isDead = (isPlayer && player.attr.isDead()) || this.HP < 0;

        boolean isRevival = flags.contains(HealFlag.REVIVAL);
        boolean isRevivalExclusive = flags.contains(HealFlag.REVIVAL_EXCLUSIVE);

        if (isRevivalExclusive && !isDead) return false;
        if (!(isRevival || isRevivalExclusive) && isDead) return false;

        if (isPlayer && this != player && (isDead || lockedHP > this.getHP()) && this.getHP() >= 0)
            return false;

        return true;
    }

    /**
     * Determines if this heal attempt should revive the entity.
     * @param flags Healing flags.
     * @return true if revival should occur.
     */
    private boolean shouldRevive(EnumSet<HealFlag> flags) {
        if (!(this instanceof Player)) return false;

        boolean revived = this.getHP() > 0 &&
            (flags.contains(HealFlag.REVIVAL) || flags.contains(HealFlag.REVIVAL_EXCLUSIVE));

        boolean surpassedLocked = this.getHP() > lockedHP;

        return revived || surpassedLocked;
    }

    /**
     * Calculates how much healing is actually applied.
     * @param heal Raw heal amount.
     * @param flags Healing flags.
     * @return Final heal value.
     */
    public double calculateHeal(double heal, EnumSet<HealFlag> flags) {
        if (heal <= 0) return 0;

        boolean isPlayer = this instanceof Player;
        boolean isDead = (isPlayer && player.attr.isDead()) || this.HP < 0;

        boolean isAbsolute = flags.contains(HealFlag.ABSOLUTE);
        boolean isOverheal = flags.contains(HealFlag.OVERHEAL);
        boolean isRevival = flags.contains(HealFlag.REVIVAL);
        boolean isRevivalExclusive = flags.contains(HealFlag.REVIVAL_EXCLUSIVE);

        if (isRevivalExclusive && !isDead) return 0;
        if (!(isRevival || isRevivalExclusive) && isDead) return 0;

        if (isOverheal) return heal;

        return isAbsolute
            ? Math.min(heal, this.getMaxHP() - this.getHP())
            : Math.min(heal, this.getMaxHP() - this.getHP());
    }

    // REGENERATION

    public double getRegenerationQuality() {
        return regenerationQuality;
    }

    public void setRegenerationQuality(double regenerationQuality) {
        this.regenerationQuality = regenerationQuality;
    }

    public double getRegenerationRate() {
        return regenerationRate;
    }

    public void setRegenerationRate(int regenerationRate) {
        this.regenerationRate = regenerationRate;
        updateRegenerationTimer();
    }

    public void turnRegenerationDoubledOn() {
        isRegenerationDoubled = true;
    }

    public void updateRegenerationHP() {
        if (canRegenerate && ( /* Checks if timer is due */
            (!player.attr.isDead() || this != player) /* Checks if this is a player, and if it is, whether it is dead or not */
                && this.getHP() < this.getMaxHP())) { /* Checks if hp is lower than max */
            receiveHeal(regenerationQuality, HealFlag.of(HealFlag.NORMAL));
            if (isRegenerationDoubled) receiveHeal(regenerationQuality, HealFlag.of(HealFlag.NORMAL));

            canRegenerate = false;
            regenerationTimer.restart();
        } else {
            if (this.getHP() >= this.getMaxHP()) {
                isRegenerationDoubled = false;
            }
        }
    }

    /**
     * Uses current regenerationRate to update {@link #regenerationTimer}.
     */
    private void updateRegenerationTimer() {
        regenerationTimer = new TurnTimer(regenerationRate, e -> canRegenerate = true)
            .setRoomScoped(!(this instanceof Player));
    }

    // DROP TABLE

    /**
     * Returns the drop table associated with this Entity.
     *
     * @return The current DropTable of this Entity.
     */
    public DropTable getDropTable() {
        return dropTable;
    }

    /**
     * Sets the drop table for this Entity.
     *
     * @param dropTable The DropTable to be set.
     */
    public void setDropTable(DropTable dropTable) {
        this.dropTable = dropTable;
    }

// OTHER ATTRIBUTES

    /**
     * Returns the defense value of this Entity.
     * Defense reduces the amount of incoming damage.
     *
     * @return The current defense value.
     */
    public double getDefense() {
        return defense;
    }

    /**
     * Sets the defense value of this Entity.
     *
     * @param defense The new defense value to be assigned.
     */
    public void setDefense(double defense) {
        this.defense = defense;
    }

    /**
     * Sets the inversion chance of this Entity.
     * Inversion defines the chance for damage to be converted into healing.
     *
     * @param inversion A percentage chance (0 to 100) for inversion to occur.
     */
    public void setInversion(double inversion) {
        this.inversion = inversion;
    }

    /**
     * Checks whether this Entity is currently invulnerable to damage.
     *
     * @return True if the Entity is invulnerable; otherwise, false.
     */
    public boolean getIsInvulnerable() {
        return isInvulnerable;
    }

    /**
     * Sets whether this Entity is invulnerable to damage.
     *
     * @param isInvulnerable True to make the Entity invulnerable; false otherwise.
     */
    public void setIsInvulnerable(boolean isInvulnerable) {
        this.isInvulnerable = isInvulnerable;
    }

    protected int maxSight;

    public void setMaxSight(int maxSight) {
        this.maxSight = maxSight;
    }

    public int getMaxSight() {
        return maxSight;
    }

    private final List<Runnable> onDamage = new ArrayList<>();

    public void scheduleOnDamage(Runnable runnable) {
        onDamage.add(runnable);
    }

    public void setTextureInEntitiesDirectory(boolean textureInEntitiesDirectory) {
        this.textureInEntitiesDirectory = textureInEntitiesDirectory;
    }

    private boolean textureInEntitiesDirectory = true;

    @Override
    public void setTexture(String texture) {
        String prefix = "";
	    if (!texture.equals("invisible") && !texture.isEmpty() && textureInEntitiesDirectory) prefix = "../characters/entities/";
	    super.setTexture(prefix + texture);
    }
}
