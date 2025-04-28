# How-to of adding new rooms

> [!WARNING]
> THIS IS DOCUMENTATION IS DEPRECATED

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

## Creating Chests

Chests are boxes that spawn items when touched with a certain interval. These items are defined by a bundle (a drop table).
Currently, only the preset `mountain_chest` is available for chests, which can only drop potions.
To spawn a `mountain_chest` in your `Room`, use the following:
```java
    addBox(X, Y, "mountain_chest");
```
Whereas:
1. X: The X position the box will be.
2. Y: The Y position the box will be.

If instead you desire to spawn a custom chest, use the following:
```java
    addBox(X, Y, "mountain_chest");
    editLastBox(EFFECT, "{chest, COOLDOWN, BUNDLE}");
```
Whereas:
1. X: The X position the box will be.
2. Y: The Y position the box will be.
3. COOLDOWN: The interval in __seconds__ to reopen it.
4. BUNDLE: The bundle used in the item entry bundles line. See [Adding Items](adding_items.md) for more information on item editing.

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
Whereas:
1. startX: X world unit where travel boxes will start.
2. startY: Y world unit where travel boxes will start.
3. numBoxesInXDirection: How many boxes in the X direction.
4. numBoxesInYDirection: How many boxes in the Y direction. (usually it's just a line, so choose one of them to be equal to 1)
5. TRAVEL_TRANSITION: The exact name of the TravelTransition you chose earlier.
6. The rest must remain unchanged.

## Creating Spawnpoints

Spawnpoints are used whenever the player dies or when entering the game.

To create a new spawnpoint, you'll need to first define the Spawnpoint X and Y world unit coordinates and then spawn the spawnpoint in a `Room`.

Firstly go to `world/player/PlayerPosition` and locate the method `reloadSpawnpoint()`. 
Then, add a new case using the ID of the `Room` you want to create a spawnpoint in, and add a new int[] consisting of the X and Y positions the player will spawn in.

Finally, in `WorldLoader`, choose the `Room` you want to add the spawnpoint box to, and add the following lines:
```java
    addBox(X, Y, "spawnpoint");
```
Whereas:
1. X: X world unit the spawnpoint will be in.
2. Y: Y world unit the spawnpoint will be in.
3. The rest must remain unchanged.

Take note that it is ideal that the X and Y positions of the box are the same as the spawnpoint will spawn the player in. This is not mandatory, however.

## Creating Enemies

Enemies are boxes that can damage the player, drop items, move by themselves, and more. This can be more advanced than the others due to having to write the enemy's AI.

To create a new Enemy, you must go to `world/enemies/Enemy`'s EnemyType and add a new entry. 
Then, go to the `loadEnemy()` method in the same class and add your new EnemyType to the switch.
There, you can modify base `Attributes` (`HP`, `Damage`, `DamageRate`), 
`Structural` changes (`texture`, `collision`, `size`, etc.),
`Drop` changes (`drop table`, `XP`, `coin interval`),
and `Breadcrumb` information, which is basically the memory and how the AI will work if it uses breadcrumbs.

> **Note:** To add an item to the enemy's drop table, follow the instructions related to `Bundles` in [Adding Items](adding_items.md).

After creating the Enemy entry, locate `world/enemies/EnemyAI` and add a new entry to `updateEnemyAI()` method's switch and write the AI code for the enemy.

To add your own textures, in the `Enemy` class's `loadEnemy()`, use `this.setTexture("../characters/enemy/TEXTURE_NAME.png");`. Modify the TEXTURE_NAME as needed.
Then, go to `resources/images/characters/enemy/` directory and add the textures, named exactly as you modified in the last step.

To insert the enemy into a `Room`, use the following:
```java
    new Enemy(X, Y, ENEMY_TYPE, MAGNIFICATION);
```
Whereas:
1. X: X world unit the enemy will spawn in.
2. Y: Y world unit the enemy will spawn in.
3. ENEMY_TYPE: The `EnemyType` you previously set it to.
4. MAGNIFICATION: The multiplier of the Max HP and Damage of the instance.

## Creating NPCs and Dialogs

NPCs are boxes that (currently) can do either nothing or be talked to. They may or may not have HP.

The NPC creation is done entirely inside the `Room` loader. If the NPC is of DIALOG interaction type, then dialog must be added in the language properties file.
In the `Room` you want to add the NPC in, here's how it works for an NPC that does nothing:
```java
    NPC exampleNPC = new NPC(X, Y, NONE);
    exampleNPC.setColor(new Color(0xCOLOR_HEX_CODE));
```
Whereas:
1. X: X world unit the NPC will spawn in.
2. Y: Y world unit the NPC will spawn in.
3. COLOR_HEX_CODE: The hexadecimal code for the color. Can be textures too with `exampleNPC.setTexture(TEXTURE_LOCATION)`.

Now, an NPC with dialog would be:
```java
     NPC exampleNPC = new NPC(X, Y, DIALOG);
     exampleNPC.setColor(new Color(0xCOLOR_HEX_CODE));
     exampleNPC.setDialogID(DIALOG_ID);
     exampleNPC.setName(DIALOG_BOX_NAME);
     exampleNPC.setNameColor(new Color(0xNAME_HEX_CODE));
```
Whereas:
1. X: X world unit the NPC will spawn in.
2. Y: Y world unit the NPC will spawn in.
3. COLOR_HEX_CODE: The hexadecimal code for the color. Can be textures too with `exampleNPC.setTexture(TEXTURE_LOCATION)`.
4. DIALOG_ID: The ID of the dialog. This will be explained later.
5. NAME_IN_DIALOG_BOX: The name to appear in the dialog box.
6. NAME_HEX_CODE: The color of the name in the dialog box.

#### Dialogs

In the NPC creation you should choose a DIALOG ID. This ID is then used in the language properties file, in the format:
`"DIALOG_" + ID + "=" + LAST_SUB_ID`, where those that are contained within quotation marks must be as is, while others are the following variables:
1. ID: The dialog ID chosen earlier.
2. LAST_SUB_ID: The last sub-ID within the ID. Examples will be given later.
> **Note:** This step is necessary to assert the last ID and must be equal to the last sub-ID of the ID, otherwise dialog will be either blank or missing. Be sure to not include anything other than the positive integer number. If this step is ignored, it will default to 0 being the last step, showing only the first dialog.

Next, the dialog itself is done through the format:
`"DIALOG_" + ID + "." + SUB_ID + "=" + CONTENT`, where those that are contained within quotation marks must be as is, while others are the following variables:
1. ID: The dialog ID used earlier.
2. SUB_ID: The order the dialog will take. It starts at 0 and must only be incremented by 1.
3. CONTENT: The message the NPC will convey. Note it automatically wraps.

- Example 1:
```java
DIALOG_1.0=Hello
DIALOG_1.1=Spinach
```
Here, the ID is 1 and the last sub-ID wasn't given. In this example, the NPC will say "Hello", but won't say "Spinach" because the last sub-ID defaults to 0.

- Example 2:
```java
DIALOG_2=2
DIALOG_2.0=3.1415926535
DIALOG_2.1=2.7182818284
DIALOG_2.2=You've played for c{#FF0000, f{totalPlaytimeHours}}h:c{#00FF00, f{totalPlaytimeMinutes}}m:c{#0000FF, f{totalPlaytimeSeconds}}s
```
In this example, the ID is 2 and the last sub-ID was 2. It will first say the first digits of pi, then after a click say the first digits of Euler's number, and only then it will say the player's playtime with the RGB colors.
Notice how I've not only used multiple dialogs within an ID, but also used variables and colors. These will be explained next.

#### Colors and variables

These can be used in dialog through using a letter and color codes.
- For colors, you use `c{#VALID_HEX_COLOR, CONTENT}`.
- For variables, you use `f{VARIABLE_NAME}`. These variables must be valid within `renderer/DialogRenderer`'s `renderDialog()` method.
- For colored constants, such as `STR` or `HP`, you use `r{CONSTANT_NAME}`. Valid codes can be seen within `renderer/ColorManager`'s `StringToColorCode` enum. The codes are the enums entries, and they contain their text value and color in the parenthesis. 

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
    
    ii. Read [Creating NPCs](#creating-npcs-and-dialogs) for better understanding.