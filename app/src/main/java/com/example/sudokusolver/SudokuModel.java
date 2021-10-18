package com.example.sudokusolver;

public class SudokuModel implements SudokuSolver {
    private final int[][] sudokuMatrix;

    /**
     * Creates a sudoku object with a board represented on a 9*9 matrix.
     */
    public SudokuModel(){
            sudokuMatrix = new int[9][9];
    }
    
    /**
     * Sets the value of the specified cell.
     * @param num the value to set
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     */
    public void setValue(int num, int row, int col) {
        sudokuMatrix[row][col] = num;
    }

    /**
     * Returns the value of the cell
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     * @return value of cell
     */
    public int getValue(int row, int col){
        return sudokuMatrix[row][col];
    }

    /**
     * Clears the board of all values.
     */
    public void clear(){
        for(int i=0; i < 9; i++){
            for(int k=0; k < 9; k++){
                sudokuMatrix[i][k] = 0;
            }
        }
    }

    /**
     * Checks that the given cell and number adheres to the sudoku ruleset.
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     * @param num number that will be controlled against ruleset
     * @return true if no broken rules, false if broken rule exists
     */
    private boolean checkRules(int row, int col, int num){
        for(int r=0; r<=8; r++){
            if(sudokuMatrix[r][col] == num && r != row){
                return false;
            }
        }

        for(int c=0; c<=8; c++){
            if(sudokuMatrix[row][c] == num && c != col){
                return false;
}
        }

        final int startRow = row-row%3;
        final int startCol = col-col%3;
        for(int i = startRow; i < startRow+3; i++){
            for(int k = startCol; k < startCol+3; k++){
                if(sudokuMatrix[i][k] == num && i != row && k != col){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tries to solve the sudoku.
     * @return true if solved, false if unsolvable
     */
    public boolean solve(){
        return solve(0,0);
    }

    /**
     * Recursively tries to solve the sudoku.
     * @param row row coordinate of the cell
     * @param col collumn coordinate of cell
     * @return true if cell is the last cell in matrix and is solvable or if next cell returned true,
     *         false if cell is unsolvable for all numbers
     */
    private boolean solve(int row, int col){
        if(sudokuMatrix[row][col] == 0){
            for(int num=1; num <= 9; num++){
                if(checkRules(row, col, num)){
                    sudokuMatrix[row][col] = num;
    
                    if(row == 8 && col == 8){
                        return true;
                    }else if(col == 8){
                        if(solve(row+1, 0)){
                            return true;
                        }
                        
                    }else{
                        if(solve(row, col+1)){
                            return true;
                        }
                    }
                }
            } 
            sudokuMatrix[row][col] = 0;
            return false;
        } else{
            if(checkRules(row, col, sudokuMatrix[row][col])){
                if(row == 8 && col == 8){
                    return true;
                }else if(col == 8){
                    return solve(row+1, 0);
                }else{
                    return solve(row, col+1);
                }
            }else{
                return false;
            }
        }
    }
}
