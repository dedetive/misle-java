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
import static com.ded.misle.world.boxes.HPBox.HealFlag.ABSOLUTE;

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
        if (player.pos.world == null) return false;

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
        receiveHeal(amount, HealFlag.of(ABSOLUTE));
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

    public enum HealFlag {
        NORMAL,
        ABSOLUTE,
        OVERHEAL,
        REVIVAL,
        REVIVAL_EXCLUSIVE,

        ;

        public static EnumSet<HealFlag> of(HealFlag... flags) {
            return EnumSet.copyOf(List.of(flags));
        }
    }

    public double receiveHeal(double heal, EnumSet<HealFlag> flags) {
        boolean isPlayer = this instanceof Player;

        if (heal <= 0 ||
            (isPlayer &&
                ((player.attr.isDead() || this != player) && !flags.contains(HealFlag.REVIVAL) &&
                    !flags.contains(HealFlag.REVIVAL_EXCLUSIVE) ||
            ((player.attr.isDead() || this != player) && lockedHP > this.getHP() && this.getHP() >= 0)))) {
            return 0;
        }

        double healToReceive = calculateHeal(heal, flags);
        this.setHP(this.getHP() + healToReceive);

        // Revival triggers
        if (isPlayer && player.getHP() > 0 &&
            (flags.contains(HealFlag.REVIVAL) || flags.contains(HealFlag.REVIVAL_EXCLUSIVE))) {
            player.attr.playerRevived();
        }

        if (isPlayer && player.getHP() > lockedHP) {
            player.attr.playerRevived();
        }

        return healToReceive;
    }

    public double calculateHeal(double heal, EnumSet<HealFlag> flags) {
        boolean isPlayer = this instanceof Player;
        boolean isDead = (isPlayer && player.attr.isDead()) || this.HP < 0;

        if (heal <= 0) return 0;

        boolean isAbsolute = flags.contains(ABSOLUTE);
        boolean isOverheal = flags.contains(HealFlag.OVERHEAL);
        boolean isRevival = flags.contains(HealFlag.REVIVAL);
        boolean isRevivalExclusive = flags.contains(HealFlag.REVIVAL_EXCLUSIVE);

        if (isRevivalExclusive && !isDead) return 0;

        if (!(isRevival || isRevivalExclusive) && isDead) return 0;

        if (isOverheal) {
            return heal; // Absolute or not doesn't change behavior *yet*
        }

        return isAbsolute
            ? Math.min(heal, this.getMaxHP() - this.getHP()) // Will be changed later when reduced healing factors exist
            : Math.min(heal, this.getMaxHP() - this.getHP());
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
            receiveHeal(Math.max(getRegenerationQuality(), 1), HealFlag.of(HealFlag.NORMAL));
            if (isRegenerationDoubled) receiveHeal(Math.max(getRegenerationQuality(), 1), HealFlag.of(HealFlag.NORMAL));
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
