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

To change the default game settings, navigate to the file `misle-java-master/src/com/ded/misle/resources/settings.config` and adjust the values. The default value is used when the specified value differs from any possible option. The available options are:

```properties
screenSize (small, default=medium, big, huge, tv-sized, comical) = STRING
isFullscreen (default=false, true) = TRUE OR FALSE BOOLEAN
fullscreenMode (default=windowed, exclusive) = STRING
frameRateCap (1..144, default=60) = INTEGER
displayFPS (default=false, true) = TRUE OR FALSE BOOLEAN
language (de_DE, el_GR, default=en_US, es_ES, mi_PM, pt_BR, ru_RU) = lang_REGION
```

What’s between parentheses is ignored, and only what follows the " = " sign is considered, in this case would be STRING, INTEGER or the others. However, since the options do not exist, default value would apply.

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
> 1. In `items/items.json`, create a new entry by following the same pattern.
> 2. In `resources/lang/`, choose a language and write down your item name, item description and item effect following the same pattern.
> 3. In `resources/images/items/`, insert your item icons named after same item's resourceID.

Inside of the `src/com/ded/misle` directory, locate the folder `items` and reach for the JSON file `items.json`. All the item data is stored inside of this JSON. An example entry is:

```json
[
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
]
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

Extra useful info:

- `ID`, `resourceID`, `name`, `rarity` and `type` are the mandatory parameters.
- `countLimit` is defaulted to 1 and `nameColor` is defaulted to #FFFFFF when not set.
- Non-specified parameters are named "attributes", and they're there to help the item have functionality. In the example case, `heal` is how much HP will be given to whoever takes it, `subtype` here means the action to be evaluated is heal, `kind` here means who will receive the effect, in which case would be whoever takes it. `potionDelay` is the delay it takes until another of the same potion can be taken.
- `ID`s and `ǹame`s should not be repeated. repeated `resourceID`s would cause multiple items to have the same texture.
- `rarity`s are formalized only to be `F`, `E`, `D`, `C`, `B`, `A` and `S`, and anything other than that will be ignored and treated as `F` when checking for rarity.