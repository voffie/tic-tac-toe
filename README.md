<p align="center">
  <img align="center" src="https://www.ithsdistans.se/pluginfile.php/1/core_admin/favicon/64x64/1723553478/ITHS_LOGO_SMALL.png" />
</p>
<h1 align="center">
  <strong>Tic-Tac-Toe</strong>
</h1>
<p align="center">
  A JavaFX implementation using sockets of the classic game Tic-Tac-Toe
</p>
<p align="center">
  <b>Built with</b>
  <br />
  <img alt="Java" width="30px" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original.svg" />
</p>

## About

This project was built for an assignment when I studied at [IT-Högskolan](https://www.iths.se/). The goal was to use JavaFX to create a clone of the classic game Tic-Tac-Toe.
The project allows you to play against a computer and across the network against another player.

## Technologies & Libraries

- **Java** - Core programming language used.
- **JavaFX** - Library used to create the graphical user interface.
- **[Awaitility](https://mvnrepository.com/artifact/org.awaitility/awaitility)** - For testing asynchronous requests.

## What I learned

This project allowed me to gain experience in several new areas:

- **Sockets** - Improved understanding of socket-based communication.
- **MVC Design Pattern** - Applied the Model-View-Controller (MCV) pattern within a graphical application.
- **Asynchronous Testing** - Learned to use Awaitility to test asynchronous requests.

## Installation and Setup

1. Clone the repository:
   
   ```bash
   git clone https://github.com/voffie/tic-tac-toe.git
   cd tic-tac-toe
   ```
3. Ensure you have Java installed (this project is developed with JDK 23)
4. Open the project in [IntelliJ IDEA](https://www.jetbrains.com/idea/):
   - Go to **File > Open** and select the project folder
   - Intellij should detect the project structure and automatically set up the necessary configurations.
5. Run the server:
   - Open `network/GameServer.java`, and select the **Run 'GameServer'** option.
6. Run the client:
   - Open `TicTacToe.java`, and select the **Run TicTacToe'** option.
7. Connect to the Game Server:
   - Make sure the Game server is up and running
   - Select one of the options displayed on the client, and you're set to go!

## Possible Upgrades

If I were to continue building on this project these are a few enhancements I would implement:

- **Improved Validation** - Add more robust validations on both the client and the server sides to prevent errors or cheating.
- **Multiple Game Instances** - Allow multiple game sessions to run concurrently, supporting more than one pair of players
- **Game Reset Control** - Implement a feature where either both players or the host must agree to reset the game
- **Enhanced Styling** - Add player customizability and polished styles to the game
