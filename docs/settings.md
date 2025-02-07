# Settings

To modify default settings, either change in-game options or edit `misle-java-master/src/com/ded/misle/resources/settings.config`.

Each setting uses the format:  
`parameterName = value`

## Available Parameters

| Setting              | Explanation                                                  | Options                                                       | Default  |
|----------------------|--------------------------------------------------------------|---------------------------------------------------------------|----------|
| screenSize           | Adjusts the game's screen size                               | small / medium / big / huge / tv-sized / comical              | medium   |
| isFullscreen         | Enables or disables fullscreen                               | true / false                                                  | false    |
| fullscreenMode       | Selects the fullscreen mode                                  | windowed / exclusive                                          | windowed |
| frameRateCap         | Sets the maximum frames per second                           | 30 / 60 / 90 / 120 / 160                                      | 60       |
| displayFPS           | Displays FPS on-screen                                       | true / false                                                  | false    |
| language             | Sets the language for the game                               | de_DE / el_GR / en_US / es_ES / mi_PM / pt_BR / ru_RU / zh_CN | en_US    |
| levelDesigner        | Toggles Level Designer mode (mostly non-functional)          | true / false                                                  | false    | 
| heldItemFollowsMouse | Toggles whether held item follows mouse or walking direction | true / false                                                  | true     |
| antiAliasing         | Toggles Anti-Aliasing                                        | true / false                                                  | true     |
| displayMoreInfo      | Displays HP / Entropy values and other info                  | false / exact / percentage                                    | false    |


Text in parentheses is ignored, and only the value after the " = " sign is used. These values can be of types STRING, INTEGER, or BOOLEAN. However, since the options do not exist, default value would apply.

At the language section, those parameters are the region codes, which translate to:

| Language code | Language  | Language (English)   | State |
|---------------|-----------|----------------------|-------|
| de_DE         | Deutsch   | German               | MS    |
| el_GR         | Ελληνικά  | Greek                | MS    |
| en_US         | English   | U.S. English         |       |
| es_ES         | Español   | Spanish              | MS    |
| mi_PM         | [------]  |                      | MS    |
| pt_BR         | Português | Brazilian Portuguese |       |
| ru_RU         | Русский   | Russian              | MS    |
| zh_CN         | 简体中文      | Simplified Chinese   | MS    |
- MS = Missing translations

Example usage of `settings.config`:

```properties
screenSize = small
isFullscreen = false
fullscreenMode = windowed
displayFPS = false
frameRateCap = 160
language = pt_BR
levelDesigner = false
heldItemFollowsMouse = false
antiAliasing = true
displayMoreInfo = exact
```
