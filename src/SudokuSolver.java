/**
 * Author: Steven Chaney
 * Class: CS4491 - Artificial Intelligence
 * Instructor: Franklin
 * Description: This program will read in a specifically formatted CSV file which contains information for a
 *              sudoku puzzle, display it in the console and graphically in a JFrame, and then attempt to solve
 *              it through "human-like" reasoning (not mathematically).
 */

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class SudokuSolver extends JPanel{
    static File file;
    static int[][] curState = new int[9][9];
    static List<Integer>[][] cellOptions = new ArrayList[9][9];
    static JFrame window = new JFrame("Sudoku Solver");
    static GameBoard game = new GameBoard();
    static boolean solved = false;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[][] gameStartState = new int[9][9];

        System.out.println("Current directory: " + new File(".").getAbsoluteFile());
        System.out.print("Please enter a game file: ");
        file = new File(in.nextLine());
        loadGame(file, gameStartState);
        System.out.println("------------------------------------------");
        initGame(gameStartState);
        System.out.println("------------------------------------------");
        solveGame();
        gameOver(2, -1);
    }

    public static void loadGame(File file, int[][] emptyArray) {
        String[] row;
        BufferedReader reader;
        String line;
        String separator = ",";
        int r = 0;

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (r > 8)
                    err("File format error. Column size is greater than 9!");
                row = line.split(separator);
                if (row.length > 9)
                    err("File format error. Row size is greater than 9!");
                for (int i = 0; i <= row.length-1; i++) {
                    if (row[i].isEmpty())
                        row[i] = "0";
                    emptyArray[r][i] = Integer.parseInt(row[i]);
                }
                r++;
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File: " + file.getAbsolutePath() + "\nExists: " + file.exists() + " | Readable: " + file.canRead());
            err("File not found!");
        } catch (IOException e) {
            err("File read error!");
        }
    }

    public static void initGame(int[][] gameStartState) {

        // Set up the working game board and cells
        System.arraycopy(gameStartState, 0, curState, 0, gameStartState.length);
        initCellOptions();

        // Initialize the graphics
        window.setSize(480, 480);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(game);

        // Display the game in the console and open the graphics window
        displayGame();
        window.setVisible(true);
    }

    public static void displayGame() {
        // Display the game as text in the console
        for (int i = 0; i <= curState.length-1; i++) {
            for (int j = 0; j <= curState.length - 1; j++)
                System.out.print(curState[i][j] + " ");
            System.out.println();
        }
    }

    public static void solveGame() {
        think(2);
        updateCellOptions();
        int numsPlaced = 0;
        int level = 1;
        while(!solved) {
            makemove:
            for (int i = 0; i <= cellOptions.length - 1; i++) {
                for (int j = 0; j <= cellOptions[i].length - 1; j++) {
                    switch(level) {
                        case 1:
                            // searching for cells with only one playable option
                            //System.out.println("Searching for cells with one option..." + "\nlevel = " + level);
                            if (cellOptions[i][j].size() == 1) {
                                curState[i][j] = cellOptions[i][j].get(0);
                                updateCellOptions();
                                think(1);
                                solved = checkSolved();
                                numsPlaced++;
                                break makemove;
                            } else if (j == cellOptions[i].length-1 && i == cellOptions.length-1) {
                                level = 2;
                            }
                            break;
                        case 2:
                            // searching and comparing cells with two playable options
                            boolean firstFound = false;
                            boolean secondFound = false;
                            // System.out.println("Searching for cells with two options..." + "\nlevel = " + level);
                            if (cellOptions[i][j].size() == 2) {
                                // check row
                                for (int c = 0; c <= curState[i].length-1; c++) {
                                    if (cellOptions[i][c].contains(cellOptions[i][j].get(0)) && cellOptions[i][c].size() == 2) {
                                        firstFound = true;
                                    } else if (cellOptions[i][c].contains(cellOptions[i][j].get(1)) && cellOptions[i][c].size() == 2) {
                                        secondFound = true;
                                    }
                                }
                                // check column
                                for (int r = 0; r <= curState.length-1; r++)
                                    if (cellOptions[r][j].contains(cellOptions[i][j].get(0)) && cellOptions[i][r].size() == 2) {
                                        firstFound = true;
                                    } else if (cellOptions[r][j].contains(cellOptions[i][j].get(1)) && cellOptions[i][r].size() == 2) {
                                        secondFound = true;
                                    }
                                // check ninette
                                if (i < 3 && j < 3) {
                                    // top left ninette
                                    for (int sr = 0; sr < 3; sr++) {
                                        for (int sc = 0; sc < 3; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if (i < 3 && (j > 2 && j < 6)) {
                                    // top center ninette
                                    for (int sr = 0; sr < 3; sr++) {
                                        for (int sc = 3; sc < 6; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if (i < 3 && j > 5) {
                                    // top right ninette
                                    for (int sr = 0; sr < 3; sr++) {
                                        for (int sc = 6; sc < 9; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if ((i > 2 && i < 6) && j < 3) {
                                    // left middle ninette
                                    for (int sr = 3; sr < 6; sr++) {
                                        for (int sc = 0; sc < 3; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if ((i > 2 && i < 6) && (j > 2 && j < 6)) {
                                    // center ninette
                                    for (int sr = 3; sr < 6; sr++) {
                                        for (int sc = 3; sc < 6; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if ((i > 2 && i < 6) && j > 5) {
                                    // right middle ninette
                                    for (int sr = 3; sr < 6; sr++) {
                                        for (int sc = 6; sc < 9; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if (i > 5 && j < 3) {
                                    // bottom left ninette
                                    for (int sr = 6; sr < 9; sr++) {
                                        for (int sc = 0; sc < 3; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if (i > 5 && (j > 2 && j < 6)) {
                                    // bottom center ninette
                                    for (int sr = 6; sr < 9; sr++) {
                                        for (int sc = 3; sc < 6; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                } else if (i > 5 && j > 5) {
                                    // bottom right ninette
                                    for (int sr = 6; sr < 9; sr++) {
                                        for (int sc = 6; sc < 9; sc++) {
                                            if (cellOptions[sr][sc].contains(cellOptions[i][j].get(0)) && cellOptions[sr][sc].size() == 2) {
                                                firstFound = true;
                                            } else if (cellOptions[sr][sc].contains(cellOptions[i][j].get(1)) && cellOptions[sr][sc].size() == 2) {
                                                secondFound = true;
                                            }
                                        }
                                    }
                                }
                                if (!firstFound) {
                                    curState[i][j] = cellOptions[i][j].get(0);
//                                    cellOptions[i][j].clear();
//                                    cellOptions[i][j].add(0);
                                    updateCellOptions();
                                    think(1);
                                    solved = checkSolved();
                                    numsPlaced++;
                                    level = 1;
                                    break makemove;
                                } else if (!secondFound) {
                                    curState[i][j] = cellOptions[i][j].get(1);
//                                    cellOptions[i][j].clear();
//                                    cellOptions[i][j].add(0);
                                    updateCellOptions();
                                    think(1);
                                    solved = checkSolved();
                                    numsPlaced++;
                                    level = 1;
                                    break makemove;
                                } else if (firstFound && secondFound) {
                                    continue;
                                } else if (j == cellOptions[i].length-1 && i == cellOptions.length-1) {
                                    level = 3;
                                }
                            }
                            break;
                        case 3:
                            // searching and comparing cells with more than two playable options
                            // System.out.println("Searching for cells with more than two options..." + "\nlevel = " + level);
                            level = 4;
                            break;
                        case 4:
                            // cannot solve puzzle. end game.
                            gameOver(1, numsPlaced);
                            break;
                        default: break;
                    }
                }
            }
        }
        gameOver(0, numsPlaced);
    }

    public static void updateCellOptions() {
        for (int i = 0; i <= curState.length-1; i++) {
            for (int j = 0; j <= curState.length-1; j++) {
                if (!cellOptions[i][j].contains(0)) {
                    // check row
                    for (int c = 0; c <= curState[i].length-1; c++) {
                        if (cellOptions[i][c].contains(curState[i][j])) {
                            cellOptions[i][c].remove((Integer) curState[i][j]);
                            //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + i + "][" + c + "]");
                        }
                    }
                    // check column
                    for (int r = 0; r <= curState.length-1; r++)
                        if (cellOptions[r][j].contains(curState[i][j])) {
                            cellOptions[r][j].remove((Integer) curState[i][j]);
                            //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + r + "][" + j + "]");
                        }
                    // check ninette
                    if (i < 3 && j < 3) {
                        // top left ninette
                        for (int sr = 0; sr < 3; sr++) {
                            for (int sc = 0; sc < 3; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if (i < 3 && (j > 2 && j < 6)) {
                        // top center ninette
                        for (int sr = 0; sr < 3; sr++) {
                            for (int sc = 3; sc < 6; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if (i < 3 && j > 5) {
                        // top right ninette
                        for (int sr = 0; sr < 3; sr++) {
                            for (int sc = 6; sc < 9; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if ((i > 2 && i < 6) && j < 3) {
                        // left middle ninette
                        for (int sr = 3; sr < 6; sr++) {
                            for (int sc = 0; sc < 3; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if ((i > 2 && i < 6) && (j > 2 && j < 6)) {
                        // center ninette
                        for (int sr = 3; sr < 6; sr++) {
                            for (int sc = 3; sc < 6; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if ((i > 2 && i < 6) && j > 5) {
                        // right middle ninette
                        for (int sr = 3; sr < 6; sr++) {
                            for (int sc = 6; sc < 9; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if (i > 5 && j < 3) {
                        // bottom left ninette
                        for (int sr = 6; sr < 9; sr++) {
                            for (int sc = 0; sc < 3; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if (i > 5 && (j > 2 && j < 6)) {
                        // bottom center ninette
                        for (int sr = 6; sr < 9; sr++) {
                            for (int sc = 3; sc < 6; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    } else if (i > 5 && j > 5) {
                        // bottom right ninette
                        for (int sr = 6; sr < 9; sr++) {
                            for (int sc = 6; sc < 9; sc++) {
                                if (cellOptions[sr][sc].contains(curState[i][j])) {
                                    cellOptions[sr][sc].remove((Integer) curState[i][j]);
                                    //System.out.println("Removed " + curState[i][j] + " from cellOptions[" + sr + "][" + sc + "]");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void initCellOptions() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (curState[i][j] > 0)
                    cellOptions[i][j] = new ArrayList<>(Arrays.asList(0));
                else
                    cellOptions[i][j] = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
            }
        }
    }

    public static void printCellOptions() {
        for (int i = 0; i <= cellOptions.length-1; i++) {
            for (int j = 0; j <= cellOptions[i].length-1; j++) {
                System.out.println("Cell [" + (i+1) + "][" + (j+1) + "] options: " + cellOptions[i][j].toString());
            }
        }
    }

    public static boolean checkSolved() {
        boolean check = true;
        for (int i = 0; i <= curState.length-1; i++) {
            for (int j = 0; j <= curState[i].length-1; j++) {
                if (!cellOptions[i][j].isEmpty())
                    check = false;
            }
        }
        return check;
    }

    public static void think(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void gameOver (int status, int numsPlaced) {
        System.out.println("------------------------------------------");
        displayGame();
        if (status == 0) {
            System.out.println("FINISHED!!!!");
        } else {
            System.out.println("Unable to solve puzzle.");
        }
        System.out.println("Numbers placed: " + numsPlaced +
                            "\nStatus: " + status);
        System.out.println("------------------------------------------");
        think(60);
        System.exit(0);
    }
    public static void err(String msg){
        System.out.println(msg);
        System.exit(1);
    }
}
