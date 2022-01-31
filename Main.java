package battleship;

public class Main {

    public static void main(String[] args) {
        Board playerA = new Board();
    }
}
enum Ships {

    AIRCRAFT_CARRIER(5, "Aircraft Carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer");

    final int length;
    final String name;

    Ships(int shipLength, String shipName) {
        this.length = shipLength;
        this.name = shipName;
    }

    public int getLength () {
        return this.length;
    }

    public String getName() {
        return this.name;
    }
}

class Board {

    final int length = 11;
    String[][] board = new String[length][length];


    public Board() {
        char abc = 'A';
        board[0][0] = " ";
        for (int i = 1; i < length; i++) {
            board[i][0] = String.valueOf(abc++);
            board[0][i] = String.valueOf(i);
            for (int j = 1; j < length; j++) {
                board[i][j] = "~";
            }
        }
        print(false);
        setupShips();
        theGame();
    }

    private void print(boolean hideShips) {
        System.out.println("");
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(hideShips && board[i][j].equals("O") ? "~ " : board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void setupShips() {
        for (Ships ship : Ships.values()) {
            System.out.println("\nEnter the coordinates of the " + ship.getName()
                    + " (" + ship.getLength() + " cells):\n");
            enterTheCoordinates(ship);
            print(false);
        }
    }

    private void enterTheCoordinates(Ships ship) {
        int xa, x1;
        int xb, x2;
        int ya, y1;
        int yb, y2;
        do {
            boolean continues = false;
            String input = new java.util.Scanner(System.in).nextLine();
            if (input.matches("[A-J]([1-9]|10)\\s[A-J]([1-9]|10)$")) {

                xa = Integer.parseInt(input.split("\\s")[0].replaceAll("[A-J]", ""));
                ya = input.split("\\s")[0].charAt(0) - 64;
                xb = Integer.parseInt(input.split("\\s")[1].replaceAll("[A-J]", ""));
                yb = input.split("\\s")[1].charAt(0) - 64;
                x1 = Math.min(xa, xb);
                x2 = Math.max(xa, xb);
                y1 = Math.min(ya, yb);
                y2 = Math.max(ya, yb);

            } else {
                System.out.println("\nError! Wrong ship location! Try again:\n");
                continue;
            }
            if (x2 - x1 + 1 == ship.getLength() && y1 == y2) {
                for (int i = 0;  i < ship.getLength(); i++) {
                    if(isHit(x1 + i, y1) || isHit(x1 + i + 1, y1) || isHit(x1 + i - 1, y1) ||
                            isHit(x1 + i, y1 - 1) || isHit(x1 + i, y1 + 1)) {
                        System.out.println("\nError! You placed it too close to another one. Try again:\n");
                        continues = true;
                        break;
                    }
                }
                if (continues) continue;
                for (int i = 0; i < ship.getLength(); i++) {
                    board[y1][x1 + i] = "O";

                }
                break;
            } else if (y2 - y1 + 1 == ship.getLength() && x1 == x2) {
                for (int i = 0;  i < ship.getLength(); i++) {
                    if(isHit(x1, y1 + i) || isHit(x1 + 1, y1 + i) || isHit(x1 - 1, y1 + i) ||
                            isHit(x1, y1 + i - 1) || isHit(x1, y1 + i + 1)) {
                        System.out.println("\nError! You placed it too close to another one. Try again:\n");
                        continues = true;
                        break;
                    }
                }
                if (continues) continue;
                for (int i = 0; i < ship.getLength(); i++) {
                    board[y1 + i][x1] = "O";
                }
                break;
            } else if (y2 - y1 + 1 != ship.getLength() && x1 == x2 || x2 - x1 + 1 != ship.getLength() && y1 == y2) {
                System.out.println("\nError! Wrong length of the " + ship.getName() + "! Try again:\n");
            } else {
                System.out.println("\nError! Wrong ship location! Try again:\n");
            }
        } while (true);
    }

    private boolean isHit(int x, int y) {
        if (x > 0 && x <= 10 && y > 0 && y <= 10) {
            return (board[y][x].equals("O"));
        }
        return false;
    }

    private boolean wasHit(int x, int y) {
        if (x > 0 && x <= 10 && y > 0 && y <= 10) {
            return (board[y][x].equals("X"));
        }
        return false;
    }

    private void theGame() {
        System.out.println("\nThe game starts!");
        print(true);
        System.out.println("\nTake a shot!\n");
        int x = 0;
        int y = 0;
        do {
            boolean continues = false;
            String input = new java.util.Scanner(System.in).nextLine();
            if (input.matches("[A-J]([1-9]|10)")) {
                x = Integer.parseInt(input.replaceAll("[A-J]", ""));
                y = input.charAt(0) - 64;
            } else {
                System.out.println("\nError! You entered the wrong coordinates! Try again:\n");
                continue;
            }

            if (isHit(x, y) || wasHit(x, y)) {
                board[y][x] = "X";
                print(false);                                       // ! ! ! ! ! ! ! !
                if (endOfGame()) {
                    System.out.println("\nYou sank the last ship. You won. Congratulations!");
                    break;
                } else if (sunk(x, y)) {
                    System.out.println("\nYou sank a ship! Specify a new target:\n");
                } else {
                    System.out.println("\nYou hit a ship! Try again:\n");
                }
            } else {
                board[y][x] = "M";
                print(false);                                       // ! ! ! ! ! ! ! !
                System.out.println("\nYou missed. Try again:\n");
            }
        } while (true);
    }

    private boolean endOfGame() {
        for (int i = 1; i < length; i++) {
            for (int j = 1; j < length; j++) {
                if (board[i][j].equals("O")) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean sunk(int x, int y) {
        return !(sunkL(x + 1, y) || sunkR(x - 1, y) || sunkD(x, y + 1) || sunkU(x, y - 1));
    }

    private boolean sunkL(int x, int y) {
        if (x > 0 && x <= 10 && y > 0 && y <= 10) {
            if (board[y][x].equals("O")) {
                return true;
            } else if (board[y][x].equals("X")) {
                return sunkL(x + 1, y);
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean sunkR(int x, int y) {
        if (x > 0 && x < 10 && y > 0 && y <= 10) {
            if (board[y][x].equals("O")) {
                return true;
            } else if (board[y][x].equals("X")) {
                return sunkR(x - 1, y);
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean sunkD(int x, int y) {
        if (x > 0 && x <= 10 && y > 0 && y <= 10) {
            if (board[y][x].equals("O")) {
                return true;
            } else if (board[y][x].equals("X")) {
                return sunkD(x, y + 1);
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean sunkU(int x, int y) {
        if (x > 0 && x <= 10 && y > 0 && y <= 10) {
            if (board[y][x].equals("O")) {
                return true;
            } else if (board[y][x].equals("X")) {
                return sunkU(x, y - 1);
            } else {
                return false;
            }
        }
        return false;
    }
}
