# How-to of adding new items

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

To add an item, create a new JSON entry by copy-pasting the default at the bottom of the file with ID -1 and editing the upper copy. Make sure to check the previous ID and resourceID and increment them, rather than replacing existing values. They should always increase by 1, unless you intend to create a mod to be compatible with updates and other mods. In which case, you should adopt big, specific numbers and make sure they hadn't been used.

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

Additionally, you need to go to `resources/lang/` and choose the languages for which you want to add support for this item. I acknowledge not everyone knows all these languages, so I'd recommend prioritizing adding it to English, as it falls back to English.
Each item requires the following parameters, all in snake_case except for the additional _DESC and _EFFECT:
- "item_name" -> The name of the item in natural language.
- "item_name_DESC" -> The description of the item shown in the tooltip.
- "item_name_EFFECT" -> What the item does shown in the tooltip.

And finally, add the .png icon for the item in `resources/images/items/`. The name of the file must be the resourceID previously used in the creation of the item JSON entry. IDs and resourceIDs are separate because I plan on items that change textures without changing the item itself.

## Additional notes:

- Mandatory Fields:
    - The following fields must be included for every item:
        - ID
        - resourceID
        - name
        - rarity
        - type
- Default Values:
    - If optional fields are not specified, the following defaults apply:
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
    - The accepted values for rarity are:
        - F, E, D, C, B, A, S.
        - Invalid values default to F.
- Translation names must follow snake_case naming convention.
- Count limit:
    - The field `countLimit` allows for variables, set in the `itemLoader` class. Current officially supported variables are `material` and `consumable`.
    - When `countLimit` is set to 1 or isn't specified, max stack size multiplier does not have effect in it, never going above 1. If it's any number above 1, max stack size multiplier will take effect.

## Example:

1. Edit items.json:
    1. Locate the file `src/com/ded/misle/items/items.json`
    2. Copy the ID -1 template and modify it:
   ```json
    {
        "id": 512,
        "resourceID": 512,
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
4. Add functionality to the item, which depends heavily on the item itself. In this case, a new section should be added in the Java file Inventory, to the method useItem(). As-is, the item would be just an empty image with no functionality.
5. Testing item:
    1. Try and get it from a chest.
    2. You can also go to the KeyHandler class, and inside the debug keys section, comment out or remove the current functionality of the debug keys and add the following line:
    ```java
    if (player.keys.keyPressed.get("debug1")) {
        player.inv.addItem(createItem(512));
        player.keys.keyPressed.put("debug1", false);
    }
    ```
    3. Press "[" in-game. The item should appear in your inventory.
    4. Note the createItem(12). That means it's creating an item with the ID 12. To add more than 1 of the item, just add a new integer parameter, like so:
   ```java
        player.inv.addItem(createItem(512, 3));
   ```
    5. Make sure the item allows more than 1 count limit, otherwise your inventory might become cluttered with the item.