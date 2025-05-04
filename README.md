# Misle

### About the game

This is a game of a project I've had for over two years now. It used to be just a world I built for the sake of creating stories and writing a book. However, my writing hobby had died down, and I couldn't keep up with writing as much as I'd like. Thus, I had the idea to create a game based on it. The lore I have planned is huge, but I'm a new developer, so the lore may have to wait until I become more experienced with coding instead of writing text. Up until now, I've tried to make the systems work as intended and create new features, until I reach a point I can safely make the game itself. I'd like to thank everyone who cares and has read this far, as I wholeheartedly agree the community is crucial to the development of things. Thank you, and take care.

### Prerequisites

- **Java Development Kit (JDK) version**: Requires JDK 21 or higher.

### Usage

To open the game, navigate to the directory `misle-java-master/src` in a terminal and copy paste the following lines of code:

In `misle-java-master/`, where you can see the folder `com`:

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

### Donations

To donate to the project and help me and my team, which consists of solely me and Glacy, the graphical artist, use the following link:
https://pixie.gg/ded/

> **Note:** Donating will not grant you special benefits. This is not a paid product, and there are no paid in-game bonuses or bonus content.

Again, I profoundly thank you and everyone who is reading this.

### General Helpful Documentation

- [Controls](docs/controls.md)
- [Settings](docs/settings.md)
- [License](LICENSE.md)

### Additional Developer Documentation
- [Known Issues](docs/known_issues.md)
- [How to Add New Items](docs/adding_items.md)
- [How to Add New Rooms](docs/adding_rooms.md)
