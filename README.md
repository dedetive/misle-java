# Misle

### Usage

To open the game, navigate to the directory `misle-java-master/` in a terminal and execute `javac src/com/ded/misle/*.java src/com/ded/misle/boxes/*.java src/com/ded/misle/player/*.java` to compile the project. Then, use `java -cp src com.ded.misle.Launcher` to launch the compiled project. Alternatively, you can use your preferred IDE with a compiler to run the code automatically, always using `Launcher` as the base.

In `misle-java-master/`:

```bash
javac src/com/ded/misle/*.java src/com/ded/misle/boxes/*.java src/com/ded/misle/player/*.java
java -cp src com.ded.misle.Launcher
```

Currently, the controls are the arrow keys to move the white square, which represents the player, and the escape key, which pauses the game. Additionally, there are test keys [ and ] whose functionality varies from version to version. These can be customized in `GamePanel.java`, at the end of the `updateKeys()` method.

### Settings

To change the default game settings, navigate to the file `misle-java-master/src/com/ded/misle/resources/settings.config` and adjust the values. The default value is used when the specified value differs from any possible option. The available options are:

```sql
screenSize (small, default=medium, big, huge, tv-sized, comical)
isFullscreen (default=false, true)
fullscreenMode (default=windowed, exclusive)
frameRateCap (1..144, default=60)
displayFPS (default=false, true)
language (default=en, pt, ppm)
```

What’s in parentheses is ignored, and only what follows the = sign is considered. The `displayFPS` function currently does not work and was only available in initial tests, but it will be reimplemented later.
