package com.ded.misle.boxes;

import com.ded.misle.PhysicsEngine;
import com.ded.misle.player.PlayerAttributes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.PhysicsEngine.ObjectType.BOX;
import static com.ded.misle.PhysicsEngine.ObjectType.HP_BOX;
import static com.ded.misle.boxes.BoxHandling.deleteBox;
import static com.ded.misle.boxes.BoxManipulation.moveCollisionBox;
import static com.ded.misle.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.PlayingRenderer.createFloatingText;

public class HPBox extends Box {
    private double HP;
    private double maxHP;
    private double lockedHP;
    private double defense;
    private boolean isRegenerationDoubled;
    private double inversion;
    private long lastRegenerationMillis;
    private long lastHitMillis;
    private double regenerationQuality;
    private double regenerationRate = 1;
    private boolean isInvulnerable;
    private static List<HPBox> HPBoxes = new ArrayList<>();

    public static List<HPBox> getHPBoxes() {
        return HPBoxes;
    }

    public HPBox(double x, double y) {
        this.setX(x);
        this.setY(y);
        this.setColor(defaultBoxColor);
        this.setTexture("solid");
        this.setHasCollision(true);
        this.setBoxScaleHorizontal(1);
        this.setBoxScaleVertical(1);
        this.setEffect(new String[]{""});
        this.setRotation(0);
        this.setObjectType(HP_BOX);
        this.setKnockbackDirection(NONE);
        this.setInteractsWithPlayer(true);
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
            if (getObjectType() != PhysicsEngine.ObjectType.PLAYER) {
                deleteBox(this);
            } else {
                player.attr.playerDies();
            }
            return true;
        }
        return false;
    }

    // DAMAGE AND HEAL

    /**
     * REASONS: <br>
     * - "normal": defense and item effects take place normally. <br><br>
     * - "post-mortem": the damage will be dealt even if the player dies. Defense and item effects take place normally. May result in negative values. <br><br>
     * - "absolute post-mortem": the damage will be dealt even if the player dies. The damage will be dealt no matter what. May result in negative values. <br><br>
     * - "absolute": the damage will be dealt no matter what, unless player dies. <br><br>
     * - "locker": no damage is actually done. instead, a portion of the HP is locked and is temporarily not considered. Takes args[0] as how many milliseconds it takes for the HP to be unlocked. <br><br>
     *
     * @param damage the damage to be dealt
     * @param reason the kind of damage that's taking place; see above for a list
     * @return Final damage dealt
     */
    public double takeDamage(double damage, String reason, String[] args, PlayerAttributes.KnockbackDirection knockbackDirection) {

        isRegenerationDoubled = false;

        // Early exit for invalid damage
        if (damage <= 0) {
            return 0;
        }

        boolean inversionTriggers = Math.random() * 100 < inversion;

        double damageToReceive;

        // Define boolean flags for different conditions
        boolean isNormalOrAbsolute = reason.contains("normal") || reason.contains("absolute");
        boolean isPostMortem = reason.contains("post-mortem");
        boolean isLocker = reason.contains("locker");

        if (inversionTriggers) {
            damageToReceive = calculateDamage(damage, reason);
            this.receiveHeal(damageToReceive, "absolute");

            int playerScreenX;
            int playerScreenY;
            if (this == player) {
                playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()) / scale);
                playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()) / scale);
            } else {
                playerScreenX = (int) (this.getX() * scale);
                playerScreenY = (int) (this.getY() * scale);
            }
            int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
            int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedHealAmount = df.format(damageToReceive);
            createFloatingText("+" + formattedHealAmount, healColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);
        } else {
            if (isLocker) {
                damageToReceive = calculateDamage(damage, reason);
                lockedHP += damageToReceive;
                // Schedule unlockHP() to run after a few seconds, based on args[0] in milliseconds
                Timer timerToUnlock = new Timer();
                timerToUnlock.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        unlockHP(damage);
                    }
                }, Integer.parseInt(args[0]));
            } else if (isNormalOrAbsolute) {
                damageToReceive = calculateDamage(damage, reason);
                this.setHP(Math.max(this.getHP() - damageToReceive, 0)); // Ensure HP doesn't go below 0 for non post mortem
            } else if (isPostMortem) {
                damageToReceive = calculateDamage(damage, reason);
                this.setHP(this.getHP() - damageToReceive); // Apply damage without floor so it can go below 0
            } else {
                throw new IllegalArgumentException("Invalid reason: " + reason);
            }

            // Check if the player dies
            if (this.getHP() <= 0 || (lockedHP > player.getHP()) && this == player) {
                checkIfDead();
            }

            // Displayed numerical value

            int playerScreenX;
            int playerScreenY;
            if (this == player) {
                playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()) / scale);
                playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()) / scale);
            } else {
                playerScreenX = (int) (this.getX());
                playerScreenY = (int) (this.getY());
            }
            int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
            int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedHealAmount = df.format(damageToReceive);
            createFloatingText("-" + formattedHealAmount, damageColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);
        }

        switch (knockbackDirection) {
            case RIGHT -> moveCollisionBox(this, -30, 0, 50);
            case LEFT -> moveCollisionBox(this, 30, 0, 50);
            case DOWN -> moveCollisionBox(this, 0, -30, 50);
            case UP -> moveCollisionBox(this, 0, 30, 50);
        }

        if (damageToReceive > 0) {
            lastHitMillis = System.currentTimeMillis();
        }

        return damageToReceive;
    }

    /**
     *
     * The documentation for the method {@link #takeDamage(double, String, String[], PlayerAttributes.KnockbackDirection)} is valid for this method too.
     *
     */
    public double calculateDamage(double damage, String reason) {
        // Early exit for invalid damage
        if (damage <= 0) {
            return 0;
        }

        double defenseCalculation = damage - ((damage * this.defense) / (this.defense + 100)) - this.defense / 2;
        double theoreticalDamage;

        // Define boolean flags for different conditions
        boolean isAbsolute = reason.contains("absolute");
        boolean isPostMortem = reason.contains("post-mortem");

        // Calculate damage based on the reason
        if (isAbsolute && isPostMortem) {
            // Absolute post-mortem: damage will be dealt past 0 and defense effects are ignored
            theoreticalDamage = damage; // Return full damage
        } else if (isAbsolute) {
            // Absolute: defense effects are ignored
            theoreticalDamage = Math.max(Math.min(damage, this.getHP()), 0);
        } else if (isPostMortem && !getIsInvulnerable()) {
            // Post-mortem: damage will be dealt past 0 into the negatives
            theoreticalDamage = defenseCalculation;
        } else if (!getIsInvulnerable()) {
            // Normal: defense and item effects take place normally
            theoreticalDamage = Math.max(Math.min(defenseCalculation, this.getHP()), 0);
        } else {
            theoreticalDamage = 0;
        }

        return theoreticalDamage;
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

    // REGENERATION

    public void updateRegenerationHP(long currentTime) {
        // Calculate the interval for healing 1 HP, based on the existing regeneration rate and quality
        double regenerationInterval = (250L / regenerationRate);

        if (lastHitMillis + 2500 < currentTime && lastRegenerationMillis + regenerationInterval < currentTime && (!player.attr.isDead() || this != player) && this.getHP() < this.getMaxHP()) {
            receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
            if (isRegenerationDoubled) receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
            lastRegenerationMillis = currentTime;
        } else {
            if (this.getHP() >= this.getMaxHP()) {
                isRegenerationDoubled = false;
            }
        }
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
        // Early exit for invalid heal or if the character is dead without revival
        if (heal <= 0 || ((player.attr.isDead() || this != player) && (!reason.contains("revival"))) || ((player.attr.isDead() || this != player) && lockedHP > this.getHP() && this.getHP() >= 0)) {
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
            if (reason.contains("revival") && player.getHP() > 0 && this == player) {
                player.attr.playerRevived(); // Set isDead to false if revived
            }
        } else {
            throw new IllegalArgumentException("Invalid reason: " + reason); // Handle invalid reasons
        }

        if (this == player && player.getHP() > lockedHP) {
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
        // Invalid heal or dead character without revival
        if (heal <= 0 || ((player.attr.isDead() || this != player) && !reason.contains("revival"))) {
            return 0;
        }

        double healingExpression = 0;
        boolean isAbsolute = reason.contains("absolute");
        boolean isOverheal = reason.contains("overheal");
        boolean isRevivalExclusive = reason.contains("revival exclusive");
        boolean isRevival = reason.contains("revival");
        boolean isNormal = reason.contains("normal");

        if (isRevivalExclusive) {
            if (!player.attr.isDead() || this != player) {
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

    // OTHER ATTRIBUTES

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) { this.defense = defense; }

    public void setInversion(double inversion) { this.inversion = inversion; }

    public boolean getIsInvulnerable() { return isInvulnerable; }

    public void setIsInvulnerable(boolean isInvulnerable) { this.isInvulnerable = isInvulnerable; }
}
