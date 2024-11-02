# Misle

### Usage

To open the game, navigate to the directory `misle-java-master/src` in a terminal and copy paste the following lines of code:

In `misle-java-master/`:

```bash
rm -rf out
mkdir -p out
javac -d out $(find . -name "*.java")
# Copy resources and the items folder
cp -r com/ded/misle/resources out/com/ded/misle/
cp -r com/ded/misle/items out/com/ded/misle/
cd out
java com.ded.misle.Launcher
```

 Alternatively, you can use your preferred IDE with a compiler to run the code automatically, always using `Launcher` as the base.

Currently, the controls are the arrow keys to move the white square, which represents the player, the escape key, which pauses the game and 'I', that opens the inventory. Additionally, there are test keys [ and ] whose functionality varies from version to version. These can be customized in `GamePanel.java`, at the end of the `updateKeys()` method.

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
