/**
 * Author: Steven Chaney
 * Class: CS4491 - Artificial Intelligence
 * Instructor: Franklin
 * Description: This program will read in a specifically formatted CSV file which contains information for a
 *              sudoku puzzle, display it in the console and graphically in a JFrame, and then attempt to solve
 *              it through "human-like" reasoning (not mathematically).
 */

import javax.swing.*;
import java.awt.*;

public class GameBoard extends JComponent{

    int boardWidth = 365;
    int boardHeight = 365;
    int boardX = 50;
    int boardY = 40;
    int cellSize = 40;
    Graphics2D newBoard;

    public GameBoard() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        newBoard = (Graphics2D) g;
        Stroke bold = new BasicStroke(3);
        Stroke reg = new BasicStroke(1);

        newBoard.setStroke(reg);
        newBoard.drawString("File: " + SudokuSolver.file.getAbsolutePath(), boardX, boardY - 5);

        // horizontal
        newBoard.drawLine(boardX, boardY+(cellSize), boardX+boardWidth, boardY+(cellSize));
        newBoard.drawLine(boardX, boardY+(2*cellSize), boardX+boardWidth, boardY+(2*cellSize));
        newBoard.drawLine(boardX, boardY+(4*cellSize), boardX+boardWidth, boardY+(4*cellSize));
        newBoard.drawLine(boardX, boardY+(5*cellSize), boardX+boardWidth, boardY+(5*cellSize));
        newBoard.drawLine(boardX, boardY+(7*cellSize), boardX+boardWidth, boardY+(7*cellSize));
        newBoard.drawLine(boardX, boardY+(8*cellSize), boardX+boardWidth, boardY+(8*cellSize));
        // vertical
        newBoard.drawLine(boardX+(cellSize), boardY, boardX+(cellSize), boardY+boardHeight);
        newBoard.drawLine(boardX+(2*cellSize), boardY, boardX+(2*cellSize), boardY+boardHeight);
        newBoard.drawLine(boardX + (4 * cellSize), boardY, boardX + (4 * cellSize), boardY + boardHeight);
        newBoard.drawLine(boardX + (5 * cellSize), boardY, boardX + (5 * cellSize), boardY + boardHeight);
        newBoard.drawLine(boardX + (7 * cellSize), boardY, boardX + (7 * cellSize), boardY + boardHeight);
        newBoard.drawLine(boardX + (8 * cellSize), boardY, boardX + (8 * cellSize), boardY + boardHeight);

        newBoard.setStroke(bold);
        newBoard.drawRect(boardX, boardY, boardWidth, boardHeight);
        // horizontal
        newBoard.drawLine(boardX, boardY+(3*cellSize), boardX+boardWidth, boardY+(3*cellSize));
        newBoard.drawLine(boardX, boardY+(6*cellSize), boardX+boardWidth, boardY+(6*cellSize));
        // vertical
        newBoard.drawLine(boardX+(3*cellSize), boardY, boardX+(3*cellSize), boardY+boardHeight);
        newBoard.drawLine(boardX + (6 * cellSize), boardY, boardX + (6 * cellSize), boardY + boardHeight);

        newBoard.setFont(new Font("Arial", Font.BOLD, 22));

        for (int i = 0; i <= SudokuSolver.curState.length-1; i++) {
            for (int j = 0; j <= SudokuSolver.curState.length-1; j++)
                if (SudokuSolver.curState[i][j] != 0)
                    newBoard.drawString(Integer.toString(SudokuSolver.curState[i][j]), boardX + (j * cellSize)+15,
                            boardY + (i * cellSize)+30);
        }

        this.repaint();

    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    public int getCellSize() { return cellSize; }
}
