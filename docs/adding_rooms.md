# How-to of adding new rooms

## TL;DR

> Assuming you're in `com.ded.misle`.
> 1. Go to `world/`.
> 2. In `RoomManager`, find the enum `Room` at the top of the class and add a `Room` with an unused `ID`.
> 3. In `WorldLoader`, find the `loadBoxes()` method and add a new case, using your created `Room`.
> 4. Inside the case, add the boxes as you'd like, optimally following the [ordering conventions](#ordering-conventions) later discussed here.
>
> **Note:** You should ideally add a way to enter the new rooms, usually done by [travel boxes](#creating-travel-boxes) to the new rooms that can be edited through `RoomManager`'s TravelTransition enum or editing player [spawnpoint](#creating-spawnpoints) to the new room, although it should be noted that the spawnpoint has to be edited in the `player/PlayerPosition`'s `reloadSpawnpoint()` method.

## Creating New Rooms

Inside the `src/com/ded/misle` directory, locate the folder `world/` and locate the class `RoomManager`.
Inside it, there's the `Room` enum, which contains all the existing rooms with their respective IDs inside the parenthesis. There should be only one room per ID. `Room`s use CONSTANT_CASE naming convention. For example:

```java
    VOID(0),
    TUANI_CITY(1),
    TUANI_HOUSE_1(2),
    TUANI_1(3),
    TUANI_2(4),
```

Moving to the `WorldLoader` class, also inside the `world/` directory, locate the `loadBoxes()` method.
Each case represents a `Room`. All `Room`s should have a case, otherwise they'd be devoid of anything and just a black space.
Follow the [ordering conventions](#ordering-conventions) rules in order to create a `Room`.

## Creating Travel Boxes

Travel boxes are what's used to have a transition between two different areas. Whenever a new area is entered, the previous boxes are deleted and the new boxes of the area are created.
To create a working travel box, you'll need to first define the TravelTransition and then spawn the travel box in a `Room`.

Firstly go to `world/RoomManager` class and locate the enum `TravelTransition`. Note these take in three parameters:
1. The `Room` the player will enter.
2. The X position the player will spawn in.
3. The Y position the player will spawn in.

Now, in `WorldLoader`, choose the `Room` you want to add the travel box to, and add the following lines:
```java
    int travelBoxesAdded = lineAddBox(startX, startY, numBoxesInXDirection, numBoxesInYDirection, "travel", FILL);
    editLastBox(EFFECT, "{travel, TRAVEL_TRANSITION}", travelBoxesAdded);
```
And modify only:
1. startX: X world unit where travel boxes will start.
2. startY: Y world unit where travel boxes will start.
3. numBoxesInXDirection: How many boxes in the X direction.
4. numBoxesInYDirection: How many boxes in the Y direction. (usually it's just a line, so choose one of them to be equal to 1)
5. TRAVEL_TRANSITION: The exact name of the TravelTransition you chose earlier.

## Creating Spawnpoints

Spawnpoints are used whenever the player dies or when entering the game.

To create a new spawnpoint, you'll need to first define the Spawnpoint X and Y world unit coordinates and then spawn the spawnpoint in a `Room`.

Firstly go to `world/player/PlayerPosition` and locate the method `reloadSpawnpoint()`. 
Then, add a new case using the ID of the `Room` you want to create a spawnpoint in, and add a new int[] consisting of the X and Y positions the player will spawn in.

Finally, in `WorldLoader`, choose the `Room` you want to add the spawnpoint box to, and add the following lines:
```java
    addBox(X, Y, "spawnpoint");
```
And modify only:
1. X: X world unit the spawnpoint will be in.
2. Y: Y world unit the spawnpoint will be in.

Take note that it is ideal that the X and Y positions of the box are the same as the spawnpoint will spawn the player in. This is not mandatory, however.

[//]: # (## Creating Enemies)

[//]: # ()
[//]: # (Enemies are creatures that can damage the player, drop items, move by themselves, and more.)

[//]: # ()
[//]: # (To create a new Enemy, you must go to `world/enemies/Enemy`'s EnemyType and add a new entry. )

[//]: # (Then, go to the `loadEnemy&#40;&#41;` method in the same class and add your new EnemyType to the switch.)

[//]: # (There, you can modify base Attributes &#40;HP, Damage, DamageRate&#41;, )

[//]: # (Structural changes &#40;texture, collision, size, etc.&#41;,)

[//]: # (Drop changes &#40;drop table, XP, coin interval&#41;,)

[//]: # (and Breadcrumb information, which is basically the memory and how the AI will work if it uses breadcrumbs.)

[//]: # ()
[//]: # (After creating the Enemy entry, locate `world/enemies/EnemyAI` and add a new entry to `updateEnemyAI&#40;&#41;` method's switch.)

[//]: # ()
[//]: # (> **Note:** To add an item to the enemy's drop table, follow the instructions of `Bundles` in [Adding Items]&#40;adding_items.md&#41;.)

[//]: # ()
[//]: # (## Creating NPCs)

## Ordering Conventions


There's a convention to create `Room` boxes, which is to add them in this order:

1. Setup

    i. Define worldWidth and worldHeight as integers. Keep in mind a box with boxScale (1, 1) is around (20, 20) of the world unit and the screen is (512, 288).

    ii. Use the method setupWorld(width, height) to fill with grass and define the world borders.

2. Structural Boxes

    i. Add walls and structures using addBox and lineAddBox methods as you'd like.

    ii. Modify them whenever needed, removing or modifying them.

    iii. Typically, more complex and internal structures, such as houses or monuments, are left to the bottom of this section.  

3. Chests

    i. The type of the chest should be specified.

    ii. Read [Creating Chests](#creating-chests) for better understanding.

4. Spawnpoints

    i. There should ideally be only exactly 0 or 1 spawnpoints per room.

    ii. Read [Creating Spawnpoints](#creating-spawnpoints) for better understanding.

5. Travel Boxes

    i. First you must add the boxes, usually a line of them, copying the amount of boxes added to an integer variable. Directly afterward, modify them to have the correct TravelTransition.

    ii. Read [Creating Travel Boxes](#creating-travel-boxes) for better understanding.

6. Enemies

    i. Valid enemy types are specified in `world/enemies/Enemy`'s EnemyType enum.

    ii. Magnification affects only MAX HP and DAMAGE. Default is 1.

    iii. Read [Creating Enemies](#creating-enemies) for better understanding.

7. NPCs

    i. Valid NPC types are specific in `world/npcs/NPC`'s InteractionType enum.
    
    ii. Read [Creating NPCs](#creating-npcs) for better understanding.