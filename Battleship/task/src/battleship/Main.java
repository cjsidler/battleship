package battleship;

import java.util.Scanner;

class BattleshipBoard {
    public enum Player {
        PLAYER, ATTACKER
    }

    char[][] playerBoard = new char[10][10];
    char[][] attackBoard = new char[10][10];

    int totalHitsReceived = 0;     // 17 hits received == loss
    int aircraftCarrierHits = 0;
    int battleshipHits = 0;
    int submarineHits = 0;
    int cruiserHits = 0;
    int destroyerHits = 0;

    public BattleshipBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.playerBoard[i][j] = '~';
                this.attackBoard[i][j] = '~';
            }
        }
    }

    public int getTotalHitsReceived() {
        return totalHitsReceived;
    }

    public char[][] getPlayerBoard() {
        return this.playerBoard;
    }

    public char[][] getAttackBoard() {
        return this.attackBoard;
    }

    public void updateCell(Player player, int row, int col, char newCellContent) {
        if (player.equals(Player.PLAYER)) {
            this.playerBoard[row][col] = newCellContent;
        } else {
            this.attackBoard[row][col] = newCellContent;
        }
    }

    public void printSingleBoard(Player player) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        char letter = 'A';
        for (char[] row : player.equals(Player.PLAYER) ? this.playerBoard : this.attackBoard) {
            String strRow = String.join(" ", String.valueOf(row).split(""));
            StringBuilder strRowWithOs = new StringBuilder(strRow);
            for (int i = 0; i < strRowWithOs.length(); i++) {
                if (strRowWithOs.charAt(i) == 'A' ||
                        strRowWithOs.charAt(i) == 'B' ||
                        strRowWithOs.charAt(i) == 'S' ||
                        strRowWithOs.charAt(i) == 'C' ||
                        strRowWithOs.charAt(i) == 'D') {
                    strRowWithOs.setCharAt(i, 'O');
                }
            }
            System.out.printf("%c %s%n", letter, strRowWithOs);
            letter++;
        }
        // System.out.println();
    }

    public void printBoardWithShipChars(Player player) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        char letter = 'A';
        for (char[] row : player.equals(Player.PLAYER) ? this.playerBoard : this.attackBoard) {
            String strRow = String.join(" ", String.valueOf(row).split(""));
            System.out.printf("%c %s%n", letter, strRow);
            letter++;
        }
        System.out.println();
    }

    public void promptShip(String shipName, int numCells) {
        System.out.printf("Enter the coordinates of the %s (%d cells):%n%n", shipName, numCells);

        int[] coordinates;

        // Validate the coordinates
        while (true) {
            coordinates = this.getShipPlacementCoordinates();

            // Validate that the coordinates are within the board's dimensions
            if (this.coordsNotWithinDimensions(coordinates)) {
                System.out.println("\nError! Wrong ship location! Try again:\n");
                continue;
            }

            // Validate that the coordinates are the correct length
            if (this.coordsWrongLength(coordinates, numCells)) {
                System.out.printf("%nError! Wrong length of the %s! Try again:%n%n", shipName);
                continue;
            }

            // Validate that the coordinates are in the same column or same row
            if (this.coordsNotHorizOrVert(coordinates)) {
                System.out.println("\nError! Wrong ship location! Try again:\n");
                continue;
            }

            // Validate that the cells in the range are empty
            if (this.coordsNotEmpty(coordinates)) {
                System.out.println("\nError! Wrong ship location! Try again:\n");
                continue;
            }

            // Validate that the cells' neighbors are empty
            if (this.coordsNeighborsNotEmpty(coordinates)) {
                System.out.println("\nError! You placed it too close to another one. Try again:\n");
                continue;
            }

            break;
        }

        switch (shipName) {
            case "Aircraft Carrier" -> {
                this.placeShip(coordinates, 'A');
            }
            case "Battleship" -> {
                this.placeShip(coordinates, 'B');
            }
            case "Submarine" -> {
                this.placeShip(coordinates, 'S');
            }
            case "Cruiser" -> {
                this.placeShip(coordinates, 'C');
            }
            case "Destroyer" -> {
                this.placeShip(coordinates, 'D');
            }
        }

        System.out.println();
    }

    private boolean coordsWrongLength(int[] coordinates, int numCells) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        boolean horizontal = row1 == row2;

        if (horizontal) {
            return Math.abs(col1 - col2) + 1 != numCells;
        } else {
            return Math.abs(row1 - row2) + 1 != numCells;
        }
    }

    public boolean coordsNotWithinDimensions(int[] coordinates) {
        for (int coord : coordinates) {
            if (coord < 0 || coord >= 10) {
                return true;
            }
        }
        return false;
    }

    private boolean coordsNotHorizOrVert(int[] coordinates) {
        // Either the row numbers or col numbers for both sets of coords must be the same
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        return row1 != row2 && col1 != col2;
    }

    private boolean coordsNotEmpty(int[] coordinates) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        boolean horizontal = row1 == row2;

        if (horizontal) {
            for (int i = Math.min(col1, col2); i <= Math.max(col1, col2); i++) {
                if (this.playerBoard[row1][i] == 'O') {
                    return true;
                }
            }
        } else {
            for (int i = Math.min(row1, row2); i <= Math.max(row1, row2); i++) {
                if (this.playerBoard[i][col1] == 'O') {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean coordsNeighborsNotEmpty(int[] coordinates) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        boolean horizontal = row1 == row2;

        if (horizontal) {
            for (int i = Math.min(col1, col2); i <= Math.max(col1, col2); i++) {
                if (coordsSingleNeighborsNotEmpty(row1, i)) {
                    return true;
                }
            }
        } else {
            for (int i = Math.min(row1, row2); i <= Math.max(row1, row2); i++) {
                if (coordsSingleNeighborsNotEmpty(i, col1)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean coordsSingleNeighborsNotEmpty(int x, int y) {
        if (0 <= x + 1 && x + 1 < 10 && this.playerBoard[x + 1][y] != '~') {
            return true;
        }
        if (0 <= x - 1 && x - 1 < 10 && this.playerBoard[x - 1][y] != '~') {
            return true;
        }
        if (0 <= y + 1 && y + 1 < 10 && this.playerBoard[x][y + 1] != '~') {
            return true;
        }
        if (0 <= y - 1 && y - 1 < 10 && this.playerBoard[x][y - 1] != '~') {
            return true;
        }
        return false;
    }

    private void placeShip(int[] coordinates, char ship) {
        int row1 = coordinates[0];
        int col1 = coordinates[1];
        int row2 = coordinates[2];
        int col2 = coordinates[3];

        boolean horizontal = row1 == row2;

        if (horizontal) {
            for (int i = Math.min(col1, col2); i <= Math.max(col1, col2); i++) {
                this.playerBoard[row1][i] = ship;
            }
        } else {
            for (int i = Math.min(row1, row2); i <= Math.max(row1, row2); i++) {
                this.playerBoard[i][col1] = ship;
            }
        }
    }

    public void fillBoard(int player) {
        System.out.printf("Player %d, place your ships on the game field%n%n", player);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
        this.promptShip("Aircraft Carrier", 5);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
        this.promptShip("Battleship", 4);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
        this.promptShip("Submarine", 3);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
        this.promptShip("Cruiser", 3);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
        this.promptShip("Destroyer", 2);
        this.printSingleBoard(Player.PLAYER);
        System.out.println();
    }

    public int[] getShipPlacementCoordinates() {
        Scanner scanner = new Scanner(System.in);

        String alpha = "ABCDEFGHIJ";

        String startCoord = scanner.next();
        String endCoord = scanner.next();

        char startRowLetter = startCoord.charAt(0);
        char endRowLetter = endCoord.charAt(0);

        int startRow = alpha.indexOf(startRowLetter);
        int endRow = alpha.indexOf(endRowLetter);

        int startCol = Integer.parseInt(startCoord.substring(1)) - 1;
        int endCol = Integer.parseInt(endCoord.substring(1)) - 1;

        return new int[]{startRow, startCol, endRow, endCol};
    }
}

class BattleshipGame {
    BattleshipBoard player1Board = new BattleshipBoard();
    BattleshipBoard player2Board = new BattleshipBoard();
    boolean gameOver = false;
    boolean player1Won = false;
    boolean player2Won = false;

    public BattleshipBoard getPlayer1Board() {
        return player1Board;
    }

    public BattleshipBoard getPlayer2Board() {
        return player2Board;
    }

    public boolean isGameOver() {
        if (this.player1Board.getTotalHitsReceived() == 17) {
            this.gameOver = true;
            this.player2Won = true;
            return true;
        } else if (this.player2Board.getTotalHitsReceived() == 17) {
            this.gameOver = true;
            this.player1Won = true;
            return true;
        }
        return false;
    }

    public void attack(int playerNumber) {
        BattleshipBoard board = playerNumber == 1 ? this.getPlayer1Board() : this.getPlayer2Board();

        // board.printSingleBoard(BattleshipBoard.Player.ATTACKER);
        System.out.println("Take a shot!\n");

        int[] attackCoords;
        while (true) {
            attackCoords = this.getAttackCoordinates();
            if (board.coordsNotWithinDimensions(attackCoords)) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
                continue;
            }
            System.out.println();
            break;
        }

        int row = attackCoords[0];
        int col = attackCoords[1];

        char attackedCell = board.getPlayerBoard()[row][col];

        if (board.getPlayerBoard()[row][col] == '~') {
            board.updateCell(BattleshipBoard.Player.PLAYER, row, col, 'M');
            board.updateCell(BattleshipBoard.Player.ATTACKER, row, col, 'M');
        } else {
            if (attackedCell != 'X') {
                board.totalHitsReceived++;
            }
            board.updateCell(BattleshipBoard.Player.PLAYER, row, col, 'X');
            board.updateCell(BattleshipBoard.Player.ATTACKER, row, col, 'X');
        }


        switch (attackedCell) {
            case 'A' -> {
                // hit Aircraft Carrier
                board.aircraftCarrierHits++;
                if (board.totalHitsReceived == 17) {
                    return;
                }
                if (board.aircraftCarrierHits == 5) {
                    System.out.println("You sank a ship! Specify a new target:\n");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case 'B' -> {
                // hit Battleship
                board.battleshipHits++;
                if (board.totalHitsReceived == 17) {
                    return;
                }
                if (board.battleshipHits == 4) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case 'S' -> {
                // hit Submarine
                board.submarineHits++;
                if (board.totalHitsReceived == 17) {
                    return;
                }
                if (board.submarineHits == 3) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case 'C' -> {
                // hit Cruiser
                board.cruiserHits++;
                if (board.totalHitsReceived == 17) {
                    return;
                }
                if (board.cruiserHits == 3) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case 'D' -> {
                // hit Destroyer
                board.destroyerHits++;
                if (board.totalHitsReceived == 17) {
                    return;
                }
                if (board.destroyerHits == 2) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case 'X' -> {
                System.out.println("You hit a ship!");
            }
            default -> {
                // switch to '~' case later
                // miss
                System.out.println("You missed.");
            }
        }
    }

    public int[] getAttackCoordinates() {
        String alpha = "ABCDEFGHIJ";
        Scanner scanner = new Scanner(System.in);
        String attackCoords = scanner.next();

        char rowLetter = attackCoords.charAt(0);
        int row = alpha.indexOf(rowLetter);
        int col = Integer.parseInt(attackCoords.substring(1)) - 1;

        return new int[]{row, col};
    }

    public void startGame() {
        System.out.println("The game starts!\n");
    }

    public void passTurn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
    }
}

public class Main {

    public static void main(String[] args) {
        BattleshipGame battleshipGame = new BattleshipGame();
        battleshipGame.getPlayer1Board().fillBoard(1);
        battleshipGame.passTurn();
        battleshipGame.getPlayer2Board().fillBoard(2);
        battleshipGame.startGame();

        boolean flag = true;
        while (!battleshipGame.isGameOver()) {
            battleshipGame.passTurn();
            if (flag) {
                battleshipGame.getPlayer2Board().printSingleBoard(BattleshipBoard.Player.ATTACKER);
                System.out.println("---------------------");
                battleshipGame.getPlayer1Board().printSingleBoard(BattleshipBoard.Player.PLAYER);
                System.out.println();
                battleshipGame.attack(2);
            } else {
                battleshipGame.getPlayer1Board().printSingleBoard(BattleshipBoard.Player.ATTACKER);
                System.out.println("---------------------");
                battleshipGame.getPlayer2Board().printSingleBoard(BattleshipBoard.Player.PLAYER);
                System.out.println();
                battleshipGame.attack(1);
            }
            flag = !flag;
        }

        if (battleshipGame.player1Won) {
            battleshipGame.getPlayer2Board().printSingleBoard(BattleshipBoard.Player.ATTACKER);
            System.out.println("---------------------");
            battleshipGame.getPlayer1Board().printSingleBoard(BattleshipBoard.Player.PLAYER);
            System.out.println();
        } else {
            battleshipGame.getPlayer1Board().printSingleBoard(BattleshipBoard.Player.ATTACKER);
            System.out.println("---------------------");
            battleshipGame.getPlayer2Board().printSingleBoard(BattleshipBoard.Player.PLAYER);
            System.out.println();
        }
        System.out.println("You sank the last ship. You won. Congratulations!");
    }
}
