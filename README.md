# Misle

![License: MISLE 1.2](https://img.shields.io/badge/license-MISLE%201.2-blueviolet)

### About the game

This is a game of a project I've had for over three years now. It started as a world I built for storytelling and writing a book. However, over time, my writing hobby faded, and I couldn’t keep up with it as much as I’d like. Thus, I had the idea to create a game based on it. The lore I have planned is huge, but I'm a new developer, so the lore may have to wait until I become more experienced with coding instead of writing text. Up until now, I've tried to make the systems work as intended and create new features, until I reach a point I can safely make the game itself. I'd like to thank everyone who cares and has read this far, as I wholeheartedly agree the community is crucial to the development of things. Thank you, and take care.

As of the game itself: it is a top-down 2D RPG, with distinct turn-based and real-time strategy gameplay. The game (will!) feature very extensive lore, although still friendly to those who just skip it— Don't be shy about it, I'm fine if you just want the gameplay and barely any storytelling. But to those who do enjoy storytelling, you will love it!

I hope you enjoy discovering this world as much as I’ve enjoyed creating it!!

<sup><sub> Also... I've heard there’s some hidden lore tucked away somewhere, just waiting to be deciphered. You're certainly gonna need linguists for it. </sub></sup>

### Prerequisites

- **Java Development Kit (JDK) version**: Requires JDK 21 or higher.

### Usage

To install the game, copy and paste the following in a bash terminal, in the directory that you want to install the game in:

```bash
git clone https://github.com/dedetive/misle-java/
cd misle-java/
mkdir out
javac -d out $(find . -name "*.java")
cp -r src/resources out/
cd out
java com.ded.misle.Launcher
```

This process might take some time, usually around a few seconds. The game will automatically launch, but this behavior can be disabled by removing the last bash line.

Alternatively, you can use your preferred IDE with a compiler to run the source-code yourself, always using `Launcher` as the starting point or a preferred Test class.

### Planned Features
- Top-down tactical combat with turn-based system (partially implemented already!)
- Extensive original lore and world-building
- Item and ability system with custom crafting

### Donations

To donate to the project and help me and my team, use the following link:
https://pixie.gg/ded/

> **Note:** Donating will not grant you special benefits. This is not a paid product, and there are no paid in-game bonuses or bonus content that are the result of donations.

Again, I profoundly thank you and everyone who is reading this.

### General Helpful Documentation

- [Controls](docs/controls.md)
- [Settings](docs/settings.md)
- [License](LICENSE.md)

### License

This project is licensed under the **MISLE 1.2 (Misle Independent Software License & Enforcement)**.
It is a **non-commercial license** that grants the right to use, modify, and redistribute the software for personal or educational purposes, but **prohibits commercialization and reuse of core artistic or narrative content** outside this game.
Redistributions and derivative works **must include the full, unmodified text** of the MISLE License and follow all its terms.
Creating and monetizing media content such as videos, streams, or screenshots involving the game is allowed under this license, as long as it does not redistribute or repackage the game itself.

See the full license in [LICENSE.md](LICENSE.md).

### Additional Developer Documentation
- [Known Issues](docs/known_issues.md)
- [How to Add New Items](docs/adding_items.md)
- [How to Add New Rooms](docs/adding_rooms.md)
