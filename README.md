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
- **\[** and **\]:** Test keys with functionality that varies by version; these can be customized in GamePanel.java at the end of the updateKeys() method.

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

> **Note:** The `displayFPS` function currently does not work and was only available in initial tests, but it will be reimplemented later.

Example usage of `settings.config`:

```properties
screenSize = big
isFullscreen = false
fullscreenMode = exclusve
frameRateCap = 120
language = mi_PM
```
