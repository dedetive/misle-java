package com.ded.misle.world.boxes;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.renderer.FloatingText;
import com.ded.misle.items.DropTable;
import com.ded.misle.world.enemies.Enemy;
import com.ded.misle.world.npcs.NPC;
import com.ded.misle.world.player.Player;
import com.ded.misle.world.player.PlayerAttributes;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static com.ded.misle.core.GamePanel.getWindow;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.PhysicsEngine.ObjectType.HP_BOX;
import static com.ded.misle.renderer.FontManager.itemInfoFont;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.boxes.BoxManipulation.moveBox;
import static com.ded.misle.renderer.ColorManager.*;

public class HPBox extends Box {
    private double HP;
    private double maxHP;
    private double lockedHP;
    private double defense;
    private boolean isRegenerationDoubled;
    private double inversion;
    private long lastRegenerationMillis;
    private double regenerationQuality;
    private double regenerationRate = 1;
    private boolean isInvulnerable;
    private DropTable dropTable;
    private static List<HPBox> HPBoxes = new ArrayList<>();

    public static List<HPBox> getHPBoxes() {
        return HPBoxes;
    }
    public static void clearHPBoxes () {
        HPBoxes.clear();
        HPBoxes.add(player);
    }

    public HPBox(int x, int y) {
        super(x, y);
        this.setObjectType(HP_BOX);
        this.HP = 1;
        this.maxHP = 1;
        HPBoxes.add(this);
    }

    public HPBox() {
        this.HP = 1;
        this.maxHP = 1;
        HPBoxes.add(this);
    }

    public void setHP(double HP) {
        this.HP = HP;
        checkIfDead();
    }

    public void setMaxHP(double maxHP) {
        this.maxHP = maxHP;
    }

    public double getHP() {
        return HP;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public boolean checkIfDead() {
        if (this.HP == 0) {
            if (!(this instanceof Player)) {
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

                        int playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()) / scale);
                        int playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()) / scale);
                        int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
                        int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formattedXPGain = "+" + df.format(xpGain) + LanguageManager.getText("xp");
                        new FloatingText(formattedXPGain, xpGainColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);

                        int coinGain = ((Enemy) this).getCoinDrop();
                        player.attr.addBalance(coinGain);

                        FontMetrics fm = getWindow().getFontMetrics(itemInfoFont);
                        int newY = (int) (playerScreenY + randomPosY + fm.getHeight() / scale);
                        String formattedCoinGain = "+" + coinGain + "i{COIN}";
                        new FloatingText(formattedCoinGain, coinGainColor, playerScreenX + randomPosX, newY, true);
                    }
                }

                HPBoxes.remove(this);
                deleteBox(this);
                if (this instanceof Enemy) {
                    ((Enemy) this).removeEnemyBox();
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

    public enum DamageFlag {
        NORMAL,
        ABSOLUTE,
        POST_MORTEM,
        LOCKER,

        ;

        public static EnumSet<DamageFlag> of(DamageFlag... flags) {
            return EnumSet.copyOf(List.of(flags));
        }
    }

    // DAMAGE AND HEAL

    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags) {
        return takeDamage(rawDamage, flags, Optional.empty(), PlayerAttributes.KnockbackDirection.NONE);
    }

    public double takeDamage(double rawDamage, EnumSet<DamageFlag> flags, PlayerAttributes.KnockbackDirection knockback) {
        return takeDamage(rawDamage, flags, Optional.empty(), knockback);
    }

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

    private void handleInversionHeal(double amount) {
        receiveHeal(amount, "absolute");
        renderFloatingText("+" + format(amount), healColor, true);
    }

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

    private void applyDamageToHP(double amount, EnumSet<DamageFlag> flags) {
        if (flags.contains(DamageFlag.POST_MORTEM)) {
            setHP(getHP() - amount); // Can go negative
        } else {
            setHP(Math.max(getHP() - amount, 0));
        }
    }

    private void renderDamageText(double amount) {
        renderFloatingText("-" + format(amount), damageColor, true);
    }

    private void renderFloatingText(String text, Color color, boolean bold) {
        int x = (this == player)
            ? (int) ((player.getX() - player.pos.getCameraOffsetX()) / scale)
            : (int) (getX() * scale);
        int y = (this == player)
            ? (int) ((player.getY() - player.pos.getCameraOffsetY()) / scale)
            : (int) (getY() * scale);

        int offsetX = (int) ((Math.random() * 80) - 40);
        int offsetY = (int) ((Math.random() * 50) - 25);
        new FloatingText(text, color, x + offsetX, y + offsetY, bold);
    }

    private void applyKnockback(PlayerAttributes.KnockbackDirection dir) {
        switch (dir) {
            case RIGHT -> moveBox(this, -30, 1);
            case LEFT -> moveBox(this, 30, 1);
            case DOWN -> moveBox(this, 0, 1);
            case UP -> moveBox(this, 0, 1);
        }
    }

    private String format(double number) {
        return new DecimalFormat("#.##").format(number);
    }

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

    public double getLockedHP() {
        return lockedHP;
    }

    public double setLockedHP(double lockedHP) {
        this.lockedHP = lockedHP;
        return lockedHP;
    }

    public void unlockHP(double damage) {
        this.setLockedHP(Math.max(lockedHP - damage, 0));
    }



    /**
     * REASONS: <br>
     * - "normal": the heal can be affected by external forces, but is limited by max HP <br> <br>
     * - "absolute": the heal will be received no matter what, unless max HP is hit <br> <br>
     * - "overheal": the heal can be affected by external forces, but is NOT limited by max HP <br> <br>
     * - "absolute overheal": the heal will be received no matter what, and is NOT limited by max HP <br> <br>
     * - "revival": the heal will be received regardless of whether the player is dead. This can revive the player. The value entered may be affected by external forces <br><br>
     * - "absolute revival": the heal will be received regardless of whether the player is dead. This can revive the player. The value entered will not be affected by external forces <br><br>
     * - "revival exclusive": the heal will only be received if the player is dead. This will revive the player. The value entered may be affected by external forces <br><br>
     * - "absolute revival exclusive": the heal will only be received if the player is dead. This will revive the player. The value entered will not be affected by external forces
     *
     * @param heal the heal to be received
     * @param reason the kind of heal that's taking place; see above for a list
     * @return Final heal received
     */
    public double receiveHeal(double heal, String reason) {
        boolean isPlayer = this instanceof Player;
        // Early exit for invalid heal or if the character is dead without revival
        if (heal <= 0 ||
            (isPlayer &&
            ((player.attr.isDead() || this != player) && (!reason.contains("revival"))) ||
            ((player.attr.isDead() || this != player) && lockedHP > this.getHP() && this.getHP() >= 0))) {
            return 0;
        }

        double healToReceive;
        // Define boolean flags for different conditions
        boolean isValidReason = reason.contains("normal") || reason.contains("overheal") ||
            reason.contains("revival") || reason.contains("revival exclusive") ||
            reason.contains("absolute");

        // Check for valid healing reasons
        if (isValidReason) {
            healToReceive = calculateHeal(heal, reason);
            this.setHP(this.getHP() + healToReceive);

            // Check for revival condition
            if (reason.contains("revival") && player.getHP() > 0 && isPlayer) {
                player.attr.playerRevived(); // Set isDead to false if revived
            }
        } else {
            throw new IllegalArgumentException("Invalid reason: " + reason); // Handle invalid reasons
        }

        if (isPlayer && player.getHP() > lockedHP) {
            player.attr.playerRevived();        // Set isDead to false if new HP is higher than locked HP
        }

        return healToReceive;
    }


    /**
     *
     * The documentation for the method {@link #receiveHeal(double, String)} is valid for this method too.
     *
     */
    public double calculateHeal(double heal, String reason) {
        boolean isPlayer = this instanceof Player;

        // Invalid heal or dead character without revival
        if (isPlayer && (heal <= 0 || ((player.attr.isDead() || this != player) && !reason.contains("revival")))) {
            return 0;
        }

        double healingExpression = 0;
        boolean isAbsolute = reason.contains("absolute");
        boolean isOverheal = reason.contains("overheal");
        boolean isRevivalExclusive = reason.contains("revival exclusive");
        boolean isRevival = reason.contains("revival");
        boolean isNormal = reason.contains("normal");

        if (isRevivalExclusive) {
            if (!player.attr.isDead() || !isPlayer) {
                return 0; // Revival exclusive healing only works if the character is dead
            }
            // Overheal logic for revival exclusive
            if (isOverheal) {
                healingExpression = isAbsolute ?
                    heal :
                    heal;
            } else {
                healingExpression = isAbsolute ?
                    Math.min(heal, this.getMaxHP() - this.getHP()) : // These will eventually be different
                    Math.min(heal, this.getMaxHP() - this.getHP());
            }
        } else if (isNormal || isRevival) {
            // Normal or revival healing logic
            healingExpression = isAbsolute ?
                Math.min(heal, this.getMaxHP() - this.getHP()) : // These will eventually be different
                Math.min(heal, this.getMaxHP() - this.getHP());
        } else if (isAbsolute) {
            healingExpression = Math.min(heal, this.getMaxHP() - this.getHP());
        }

        // Overheal logic
        if (isOverheal && !isRevivalExclusive) {
            healingExpression = isAbsolute ? // These will eventually be different
                heal:
                heal;
        }

        return healingExpression;
    }

    // REGENERATION

    public long getLastRegenerationMillis() {
        return lastRegenerationMillis;
    }

    public double getRegenerationQuality() {
        return regenerationQuality;
    }

    public void setRegenerationQuality(double regenerationQuality) {
        this.regenerationQuality = regenerationQuality;
    }

    public double getRegenerationRate() {
        return regenerationRate;
    }

    public void setRegenerationRate(double regenerationRate) {
        this.regenerationRate = regenerationRate;
    }

    public void turnRegenerationDoubledOn() {
        isRegenerationDoubled = true;
    }

    public void updateRegenerationHP(long currentTime) {
        // Calculate the interval for healing 1 HP, based on the existing regeneration rate and quality
        double regenerationInterval = (250L / regenerationRate);

        // TODO: Update regeneration to turns system
        if (lastRegenerationMillis + regenerationInterval < currentTime &&
            (!player.attr.isDead() || this != player) && this.getHP() < this.getMaxHP()) {
            receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
            if (isRegenerationDoubled) receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
            lastRegenerationMillis = currentTime;
        } else {
            if (this.getHP() >= this.getMaxHP()) {
                isRegenerationDoubled = false;
            }
        }
    }

    // DROP TABLE

    public DropTable getDropTable() {
        return dropTable;
    }

    public void setDropTable(DropTable dropTable) {
        this.dropTable = dropTable;
    }

    // OTHER ATTRIBUTES

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) { this.defense = defense; }

    public void setInversion(double inversion) { this.inversion = inversion; }

    public boolean getIsInvulnerable() { return isInvulnerable; }

    public void setIsInvulnerable(boolean isInvulnerable) { this.isInvulnerable = isInvulnerable; }
}
