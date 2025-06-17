package com.ded.misle.world.entities;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.renderer.smoother.SmoothPosition;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.renderer.FloatingText;
import com.ded.misle.items.DropTable;
import com.ded.misle.renderer.FontManager;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.enemies.Enemy;
import com.ded.misle.world.entities.npcs.NPC;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.entities.player.PlayerAttributes;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.FontManager.coinTextFont;
import static com.ded.misle.world.logic.PhysicsEngine.ObjectType.ENTITY;
import static com.ded.misle.renderer.FontManager.itemInfoFont;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.boxes.BoxManipulation.moveBox;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.world.entities.Entity.HealFlag.ABSOLUTE;

/**
 * Represents an entity with health points (HP).
 * Handles health, damage, healing, and death logic. Also manages shared Entity instances.
 */
public class Entity extends Box {
    /** Current HP of the entity. */
    private double HP;

    /** Maximum HP the entity can have. */
    private double maxHP;

    /**
     * HP smoothness manager. Should be updated every frame to stay relevant.
     * <p>
     * Shows current HP of the entity that is going to be rendered. Has a slight delay compared to real internal {@link #HP}.
     */
    private final SmoothValue HPSmoother;

    /** Locked HP that cannot be recovered or reduced normally. */
    private double lockedHP;

    /** Defense value used to reduce incoming damage. */
    private double defense;

    private boolean isRegenerationDoubled;

    /** Chance (0-100%) to invert damage into healing. */
    private double inversion;

    /** Quality factor used for regeneration calculation. */
    private double regenerationQuality;

    /** Represents how many turns it takes to regenerate again. Default value is 5. */
    private int regenerationRate = 5;

    /**
     * Represents whether the regeneration cooldown has passed or not.
     */
    private boolean canRegenerate = true;

    /**
     * Timer responsible for turning {@link #canRegenerate} true.
     */
    private TurnTimer regenerationTimer;

    /** If true, entity is immune to all damage. */
    private boolean isInvulnerable;

    /** Drop table for items when this entity dies. */
    private DropTable dropTable;

    /** Static list of all active Entities. */
    private static List<Entity> entities = new ArrayList<>();

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
    public Entity(int x, int y) {
        super(x, y);
        this.setOrigin(new Point(x, y));
        this.setObjectType(ENTITY);
        this.HP = 1;
        this.maxHP = 1;
        this.HPSmoother = new SmoothValue((float) HP);
        entities.add(this);
        updateRegenerationTimer();
    }

    /**
     * Constructs an Entity with HP and max HP set as 1 and no position.
     */
    public Entity() {
        this.setObjectType(ENTITY);
        this.HP = 1;
        this.maxHP = 1;
        this.HPSmoother = new SmoothValue((float) HP);
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
        checkIfDead();
    }

    /**
     * Sets the maximum HP.
     * @param maxHP New maximum HP.
     */
    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP;
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

    /**
     * Checks if the entity is dead. If so, handles drop and removal logic.
     * @return true if dead, false otherwise.
     */
    public boolean checkIfDead() {
        if (player.pos.world == null) return false;

        if (this.HP == 0) {
            if (!(this instanceof Player)) {
                if (this.HPSmoother.getCurrentFloat() >= 0.5) {
                    javax.swing.Timer checkAgainTimer = new javax.swing.Timer(30,
                        e -> checkIfDead());
                    checkAgainTimer.setRepeats(false);
                    checkAgainTimer.start();
                    return true;
                }

                if (this.dropTable != null) {
                    boolean canGoMinus = false;
                    boolean canGoPlus = false;
                    if (getCollisionBoxesInRange(this.getX() - 20, this.getY(), 10, 6).isEmpty()) {
                        canGoMinus = true;
                    }
                    if (getCollisionBoxesInRange(this.getX() + 20, this.getY(), 10, 6).isEmpty()) {
                        canGoPlus = true;
                    }

                    int[] results = dropTable.getRandomItemID();
                    int id = results[0];
                    int count = results[1];

                    this.spawnItem(canGoMinus, canGoPlus, id, count);

                    if (this instanceof Enemy) {
                        double xpGain = ((Enemy) this).getXPDrop();
                        player.attr.addXP(xpGain);

                        int playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()));
                        int playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()));
                        int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
                        int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formattedXPGain = "+" + df.format(xpGain) + LanguageManager.getText("xp");
                        new FloatingText(formattedXPGain, xpGainColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);

                        int coinGain = ((Enemy) this).getCoinDrop();
                        player.attr.addBalance(coinGain);

                        FontMetrics fm = FontManager.getCachedMetrics(getWindow().getGraphics(), itemInfoFont);
                        int newY = (int) (playerScreenY + randomPosY + fm.getHeight());
                        String formattedCoinGain = "+" + coinGain + "i{COIN}";
                        new FloatingText(formattedCoinGain, coinGainColor, playerScreenX + randomPosX, newY, true);
                    }
                }

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
        return takeDamage(rawDamage, flags, Optional.empty(), PlayerAttributes.KnockbackDirection.NONE);
    }

    /**
     * Deals damage to the entity. <p></p>
     * LockDuration is defaulted to empty.
     * @param rawDamage Raw incoming damage.
     * @param flags A set of {@link DamageFlag} values.
     * @param knockback Direction of the applied knockback.
     * @return Final damage applied.
     */
    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags, PlayerAttributes.KnockbackDirection knockback) {
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
    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags, Optional<Duration> lockDuration, PlayerAttributes.KnockbackDirection knockback) {
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

            renderDamageText(finalDamage);
        }

        applyKnockback(knockback);

        return finalDamage;
    }

    /**
     * Applies inverted healing instead of damage.
     * @param amount Amount to heal.
     */
    private void handleInversionHeal(double amount) {
        receiveHeal(amount, HealFlag.of(ABSOLUTE));
        renderFloatingText("+" + format(amount), healColor, true);
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
     * Renders damage floating text.
     * @param amount Damage amount.
     */
    private void renderDamageText(double amount) {
        renderFloatingText("-" + format(amount), damageColor, true);
    }

    /**
     * Shows floating text over this entity.
     * @param text The string to show.
     * @param color Color of the text.
     * @param bold If true, shows bold text.
     */
    private void renderFloatingText(String text, Color color, boolean bold) {
        int x = getX() * originalTileSize;
        int y = getY() * originalTileSize;

        int offsetX = (int) ((Math.random() * 80) - 40);
        int offsetY = (int) ((Math.random() * 50) - 25);
        new FloatingText(text, color, x + offsetX, y + offsetY, bold);
    }

    /**
     * Applies knockback effect.
     * @param dir Direction of knockback.
     */
    private void applyKnockback(PlayerAttributes.KnockbackDirection dir) {
        switch (dir) {
            case RIGHT -> moveBox(this, -30, 1);
            case LEFT -> moveBox(this, 30, 1);
            case DOWN -> moveBox(this, 0, 1);
            case UP -> moveBox(this, 0, 1);
        }
    }

    /**
     * Formats a double to string with two decimals.
     * @param number Number to format.
     * @return Formatted string.
     */
    private String format(double number) {
        return new DecimalFormat("#.##").format(number);
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

    private int maxSight;

    public void setMaxSight(int maxSight) {
        this.maxSight = maxSight;
    }

    public int getMaxSight() {
        return maxSight;
    }

    private Point origin;

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getOrigin() {
        return origin;
    }
}
