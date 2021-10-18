package com.example.sudokusolver;

public interface SudokuSolver {

    /**
     * Tries to solve the sudoku.
     * @return true if solved, false if unsolvable
     */
    boolean solve();

    /** Clears the entire sudoku board. */
    void clear();

    /**
     * Sets the value of the specified cell.
     * @param num the value to set
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     */
    void setValue(int num, int row, int col);

    /**
     * Returns the contained value of the cell.
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     * @return value of cell
     */
    int getValue(int row, int col);
}
