# Virtual Ball Game

## Overview
This project implements a **socket-based client-server system** for a virtual ball game. It was developed during coursework on a module on advanced programming at the University of Essex during the 2021-2022 academic session.

The objective was to design and implement a reliable, multi-threaded application using Java sockets. Players (clients) pass a virtual ball between themselves and can join or leave the game at any time.

---

## Features
- **Multi-threaded Server**: Handles multiple client connections and manages the game state.
- **Client Application**: Displays the game state and provides functionality for passing the ball.
- **Robust Communication**: Uses a custom protocol for reliable messaging between clients and the server.
- **Fault Tolerance**: Handles player disconnections gracefully, allowing the game to continue without uninterruptions.

---

## Implementation Overview
- **Server**:
  - Manages client connections.
  - Assigns unique player IDs.
  - Handles the ball state and broadcasts updates.
- **Client**:
  - Displays the current game state (players, ball owner).
  - Prompts the user holding the ball for input to pass it to a specific player.
- **Protocol**:
  - Custom message-based communication for reliability and simplicity.
  - Handles edge cases, such as disconnections.

---

## Requirements
- **Java 8+**
- Command-line interface (CLI) for execution.

---

## Usage
### Running the Server
1. Compile the server code:
   ```bash
   javac src/game/server/*.java
2. Run the server:
   ```bash
   java -cp src game.server.ServerProgram
   
### Running the Client
1. Open another terminal and compile the client code:
   ```bash
   javac src/game/player/*.java
2. Run the client:
   ```bash
   java -cp src game.player.PlayerProgram

### Running Multiple Clients
To test the game with multiple clients, open additional terminals and repeat the steps in "Running the Client" for each new client. All clients will connect to the same server instance.
