# Misle

![License: MISLE 1.2](https://img.shields.io/badge/license-MISLE%201.2-blueviolet)

### About the game

Misle is a free and open-source, turn-based tactical RPG featuring a fully original world, developed with a custom-built Java engine.

This is a game of a project I've had for over three years now. It started as a world I built for storytelling and writing a book. However, over time, my writing hobby faded, and I couldn’t keep up with it as much as I’d like. Thus, I had the idea to create a game based on it. The lore I have planned is huge, but I'm a new developer, so the lore may have to wait until I become more experienced with coding instead of writing text. Up until now, I've tried to make the systems work as intended and create new features, until I reach a point I can safely make the game itself. I'd like to thank everyone who cares and has read this far, as I wholeheartedly agree the community is crucial to the development of things. Thank you, and take care.

As of the game itself: it is a top-down 2D RPG, with distinct turn-based and real-time strategy gameplay. The game (will!) feature very extensive lore, although still friendly to those who just skip it— Don't be shy about it, I'm fine if you just want the gameplay and barely any storytelling. But to those who do enjoy storytelling, you will love it!

I hope you enjoy discovering this world as much as I’ve enjoyed creating it!!

<sup><sub> Also... I've heard there’s some hidden lore tucked away somewhere, just waiting to be deciphered. You're certainly gonna need linguists for it. </sub></sup>

### Prerequisites

- **Java Development Kit (JDK) version**: Requires JDK 21 or higher.

### Usage

#### Building on Linux/macOS

To build and run the game from source, open a terminal in the desired directory and run:

```bash
git clone https://github.com/dedetive/misle-java/
cd misle-java/
bash build.sh
```

This will compile the game, copy the necessary resources, and create a `Misle.jar` file in the root directory. Once built, you can run the game by double-clicking the `Misle.jar` file or using:

```bash
java -jar Misle.jar
```

#### Building on Windows

For Windows users, a `build.bat` script is also included. Simply double-click `build.bat` or run the following commands in Command Prompt:

```bash
git clone https://github.com/dedetive/misle-java/
cd misle-java/
build.bat
```

This will compile the game, prepare resources, create the `Misle.jar`, and give instructions on how to run it.

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
