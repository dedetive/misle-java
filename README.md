# Misle

![License: MISLE 1.1](https://img.shields.io/badge/license-MISLE%201.1-blueviolet)

### About the game

This is a game of a project I've had for over two years now. It used to be just a world I built for the sake of creating stories and writing a book. However, my writing hobby had died down, and I couldn't keep up with writing as much as I'd like. Thus, I had the idea to create a game based on it. The lore I have planned is huge, but I'm a new developer, so the lore may have to wait until I become more experienced with coding instead of writing text. Up until now, I've tried to make the systems work as intended and create new features, until I reach a point I can safely make the game itself. I'd like to thank everyone who cares and has read this far, as I wholeheartedly agree the community is crucial to the development of things. Thank you, and take care.

### Prerequisites

- **Java Development Kit (JDK) version**: Requires JDK 21 or higher.

### Usage

To install the game, copy and paste the following in a bash terminal, in the directory that you want to install the game in:

```bash
git clone https://github.com/dedetive/misle-java/
cd misle-java/
mkdir out
javac -d out $(find . -name "*.java")
cp -r src/com/ded/misle/resources out/com/ded/misle/
cp -r src/com/ded/misle/items out/com/ded/misle/
cd out
java com.ded.misle.Launcher
```

This process might take some time, usually around a few seconds. The game will automatically launch, but this behavior can be disabled by removing the last bash line.

Alternatively, you can use your preferred IDE with a compiler to run the source-code yourself, always using `Launcher` as the starting point or a preferred Test class.

### Donations

To donate to the project and help me and my team, which consists of solely me and Glacy, the graphical artist, use the following link:
https://pixie.gg/ded/

> **Note:** Donating will not grant you special benefits. This is not a paid product, and there are no paid in-game bonuses or bonus content that are the result of donations.

Again, I profoundly thank you and everyone who is reading this.

### General Helpful Documentation

- [Controls](docs/controls.md)
- [Settings](docs/settings.md)
- [License](LICENSE.md)

### License

This project is licensed under the **MISLE 1.1 (Misle Independent Software License & Enforcement)**.
It is a **non-commercial license** that grants the right to use, modify, and redistribute the software for personal or educational purposes, but **prohibits commercialization and reuse of core artistic or narrative content** outside this game.
Redistributions and derivative works **must include the full, unmodified text** of the MISLE License and follow all its terms.
Creating and monetizing media content such as videos, streams, or screenshots involving the game is allowed under this license, as long as it does not redistribute or repackage the game itself.

See the full license in [LICENSE.md](LICENSE.md).

### Additional Developer Documentation
- [Known Issues](docs/known_issues.md)
- [How to Add New Items](docs/adding_items.md)
- [How to Add New Rooms](docs/adding_rooms.md)
