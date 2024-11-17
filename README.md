# Misle

### Prerequisites

- **Java Development Kit (JDK) version**: Requires JDK 21 or higher.

### Usage

To open the game, navigate to the directory `misle-java-master/src` in a terminal and copy paste the following lines of code:

In `misle-java-master/`:

```bash
rm -rf out
mkdir -p out
javac -d out $(find . -name "*.java")
cp -r com/ded/misle/resources out/com/ded/misle/
cp -r com/ded/misle/items out/com/ded/misle/
cd out
java com.ded.misle.Launcher
```

Alternatively, you can use your preferred IDE with a compiler to run the code automatically, always using `Launcher` as the base.

### Controls

- **Arrow keys:** Move the player (white square).
- **Escape key:** Pause and unpause the game.
- **I:** Open inventory.
- **\[** and **\]:** Test keys with functionality that varies by version; these can be customized in KeyHandler.java at the end of the updateKeys() method.

### Settings

To modify default settings, edit `misle-java-master/src/com/ded/misle/resources/settings.config`.

Each setting uses the format:  
`parameterName = value`

#### Available Parameters:
- **`screenSize`** (default=`medium`): Adjust the game's screen size.
    - Options: `small`, `medium`, `big`, `huge`, `tv-sized`, `comical`

- **`isFullscreen`** (default=`false`): Enable or disable fullscreen mode.
    - Options: `true`, `false`

- **`fullscreenMode`** (default=`windowed`): Choose fullscreen type.
    - Options: `windowed`, `exclusive`

- **`frameRateCap`** (default=`60`): Set the maximum frames per second.
    - Range: `1` to `144`

- **`displayFPS`** (default=`false`): Display FPS on-screen (currently non-functional).

- **`language`** (default=`en_US`): Set the language for the game.
    - Options: `de_DE`, `el_GR`, `en_US`, `es_ES`, `mi_PM`, `pt_BR`, `ru_RU` (what these mean is explained below).
    - If the selected language file is missing keys, English is used as a fallback.

Text in parentheses is ignored, and only the value after the " = " sign is used. These values can be of types like STRING, INTEGER, or BOOLEAN. However, since the options do not exist, default value would apply.

At the language section, those parameters are the region codes, which translate to:
- de_DE: Deutsch / German
- el_GR: Ελληνικά / Greek
- en_US: English / U.S. English
- es_ES: Español / Spanish
- mi_PM: \[---------\]
- pt_BR: Português / Brazilian Portuguese
- ru_RU: Русский / Russian

> **Note:** The `displayFPS` function currently does not work and was only available in initial tests, but it will be reimplemented later.

Example usage of `settings.config`:

```properties
screenSize = big
isFullscreen = false
fullscreenMode = exclusive
frameRateCap = 120
language = mi_PM
```

### How-to of adding new items

> **TL;DR:**
>
> Assuming you're in the directory `src/com/ded/misle`:
> 1. In `items/items.json`, create a new entry by following the same pattern.
> 2. In `resources/lang/`, choose a language and write down your item name, item description and item effect following the same pattern.
> 3. In `resources/images/items/`, insert your item icons named after same item's resourceID.

Inside of the `src/com/ded/misle` directory, locate the folder `items` and reach for the JSON file `items.json`. All the item data is stored inside of this JSON. An example entry is:

```json
    {
        "id": 3,
        "resourceID": 3,
        "name": "Small Health Potion",
        "countLimit": 100,
        "rarity": "F",
        "heal": 25,
        "type": "potion",
        "subtype": "heal",
        "kind": "self",
        "size": "small",
        "potionDelay": 1.6,
        "bundles": "mountain|8|75",
        "nameColor": "#FF6969"
    }
```

To add an item, create a new JSON entry by copy-pasting the default at the bottom of the file with ID -1 and editing the upper copy. Make sure to look at the previous ID and resourceID to increment to them instead of replacing an existing value. They should always increase by 1, unless you intend to create a mod to be compatible with updates and other mods. In which case, you should adopt big, specific numbers and make sure they hadn't been used.

The dummy entry is:

```json
    {
        "id": -1,
        "resourceID": -1,
        "name": "",
        "countLimit": -1,
        "rarity": "",
        "type": "",
        "subtype": "",
        "kind": "",
        "bundles": "none|0|0",
        "nameColor": "#FFFFFF"
    }
```

In addition to this, you also have to go to `resources/lang/` and choose the languages you wish to add support for this item. I acknowledge not everyone knows all these languages, so I'd recommend prioritizing adding it to English, as it fallbacks to English.
Each item needs the following parameters added, all in snake_case except for the extra _DESC and _EFFECT:
- "item_name" -> The name of the item in natural language.
- "item_name_DESC" -> The description of the item shown in the tooltip.
- "item_name_EFFECT" -> What the item does shown in the tooltip.

And finally, add the .png icon for the item in `resources/images/items/`. The name of the file must be the resourceID previously used in the creation of the item JSON entry. IDs and resourceIDs are separate because I plan on items that change textures without changing the item itself.

#### Additional notes:

- Mandatory Fields:
  - The following fields must be included for every item:
    - ID
    - resourceID
    - name
    - rarity
    - type
- Default Values:
  - If optional fields are not specified, these defaults apply:
  - countLimit: Defaults to 1.
  - nameColor: Defaults to #FFFFFF (white).
- Bundles Field:
  - Specifies the chest types where the item can be found.
  - Format: bundleName|maxQuantity|Weight.
- Attributes:
  - Any non-standard parameters (e.g., heal, attackDelay, element) are treated as item-specific attributes to define functionality.
- Avoid Duplicates:
  - Ensure that:
    - IDs and names are unique to avoid overwriting existing items.
    - resourceIDs are unique to prevent multiple items from sharing the same texture.
- Rarity Values:
  - Accepted values for rarity:
    - F, E, D, C, B, A, S.
    - Invalid values default to F.
- Translation names must follow snake_case naming convention.

#### Example:

1. Edit items.json:
   1. Locate the file `src/com/ded/misle/items/items.json`
   2. Copy the ID -1 template and modify it:
   ```json
    {
        "id": 12,
        "resourceID": 12,
        "name": "Fireball Wand",
        "countLimit": 1,
        "rarity": "E",
        "type": "weapon",
        "subtype": "ranged",
        "kind": "wand",
        "element": "fire",
        "attackDelay": 0.8,
        "bundles": "mountain|1|10, copperCave|1|15",
        "nameColor": "#CE2020"
    }
   ```
2. Add translations:
   1. Locate the folder `src/com/ded/misle/resources/lang/` and choose a language file (e.g.: messages_en_US.properties for the English file)
   2. Add the following lines:
   ```properties
    fireball_wand=Fireball Wand
    fireball_wand_DESC=Heats you up when you're cold just like a warm blanket! If your blanket is on fire, that is.
    fireball_wand_EFFECT=Shoots a fireball.
    ```
3. Add icon:
   1. Save item's icon as `12.png` (based on its resourceID) in `resources/images/items/`.
4. Add functionality to the item. This heavily depends on the item. In this case, a new section should be added in the Java file Inventory, to the method useItem(). As-is, the item would be just an empty image with no functionality.
5. Testing item:
   1. Try and get it from a chest.
   2. You can also go to KeyHandler class and, inside the debug keys section, remove the current functionality and add the following line:
    ```java
    if (player.keys.keyPressed.get("debug1")) {
        player.inv.addItem(createItem(12));
        player.keys.keyPressed.put("debug1", false);
    }
    ```
   3. Press "[" in-game. The item should appear in your inventory.
   4. Note the createItem(12). That means it's creating an item with the ID 12. To add more than 1 of the item, just add a new integer parameter, like so:
   ```java
        player.inv.addItem(createItem(12, 3));
   ```
   5. Make sure the item allows more than 1 count limit, otherwise your inventory might be filled with the item.



### Known issues

1. Currently, `ru_RU` (Russian) and `el_GR` (Greek) fonts in item tooltips are not working properly due to unsupported characters in the current font.

> **Note:** Please report any issues you encounter during your game development.