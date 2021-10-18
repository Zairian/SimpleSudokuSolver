package com.example.sudokusolver.viewmodel;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sudokusolver.SudokuModel;

public class SolveSudokuViewModel extends ViewModel {
    public SudokuModel sudokuSolver = new SudokuModel();

    private final int[][] sudokuMatrix;
    private final boolean[][] invalidCellMatrix;
    private final boolean[][] insertedCellMatrix;

    public MutableLiveData<Pair<Integer, Integer>> selectedCellLiveData = new MutableLiveData<>();
    public MutableLiveData<int[][]> cellsLiveData = new MutableLiveData<>();
    public MutableLiveData<boolean[][]> invalidCellsLiveData = new MutableLiveData<>();
    public MutableLiveData<boolean[][]> insertedCellsLiveData = new MutableLiveData<>();

    private int selectedRow = -1;
    private int selectedCol = -1;

    public SolveSudokuViewModel(){
        sudokuMatrix = new int[9][9];
        invalidCellMatrix = new boolean[9][9];
        insertedCellMatrix = new boolean[9][9];

        selectedCellLiveData.postValue(new Pair<>(selectedRow,selectedCol));
        cellsLiveData.postValue(sudokuMatrix);
        invalidCellsLiveData.postValue(invalidCellMatrix);
        insertedCellsLiveData.postValue(insertedCellMatrix);
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
     * Sets the invalid state of the specified cell.
     * @param state the state to be set. True = cell invalid, False = cell valid.
     * @param row row of cell
     * @param col column of cell
     */
    public void setInsertedValue(boolean state, int row, int col) {
        insertedCellMatrix[row][col] = state;
    }

    /**
     * Returns the currently selected row.
     * @return position of currently selected row
     */
    public int getSelectedRow(){
        return selectedRow;
    }

    /**
     * Returns the currently selected column.
     * @return position of currently selected column
     */
    public int getSelectedCol(){
        return selectedCol;
    }

    /**
     * Sets the invalid state of the specified cell.
     * @param state the state to be set. True = cell invalid, False = cell valid.
     * @param row row of cell
     * @param col column of cell
     */
    public void setInvalidCellMatrix(boolean state, int row, int col){
        invalidCellMatrix[row][col] = state;
    }

    /**
     * Clears the board of all values and resets invalid cells.
     */
    public void clear(){
        for(int i=0; i < 9; i++){
            for(int k=0; k < 9; k++){
                sudokuMatrix[i][k] = 0;
                invalidCellMatrix[i][k] = false;
                insertedCellMatrix[i][k] = false;
                update();
            }
        }
        sudokuSolver.clear();
    }

    /**
     * Tries to solve the sudoku.
     * @return true if solved, false if unsolvable
     */
    public boolean solve(){
        updateSelectedCell(-1, -1);
        boolean temp;
        for(int i=0; i<9; i++){
            for(int k=0; k<9; k++){
                sudokuSolver.setValue(sudokuMatrix[i][k], i, k);
            }
        }
        temp = sudokuSolver.solve();
        for(int i=0; i<9; i++){
            for(int k=0; k<9; k++){
                sudokuMatrix[i][k] = sudokuSolver.getValue(i,k);
            }
        }
        update();
        return temp;
    }

    /**
     * Checks board for invalid cells that are not breaking the rules and sets them to valid.
     * Also checks for valid cells that are breaking the rules and sets them to invalid
     */
    public void recheckInvalidCells(){
        for(int i=0; i<9;i++){
            for(int k=0; k<9; k++){
                if(invalidCellMatrix[i][k]){
                    if(checkRules(i, k, sudokuMatrix[i][k])){
                        invalidCellMatrix[i][k] = false;
                    }
                }else{
                    if(sudokuMatrix[i][k] != 0 && !checkRules(i,k, sudokuMatrix[i][k])){
                        invalidCellMatrix[i][k] = true;
                    }
                }
            }
        }
    }

    /**
     * Checks if any invalid cells exists on the board.
     * @return true if the board contains invalid cells, false if it doesn't
     */
    public boolean checkInvalidExists(){
        for(int i=0; i<9;i++){
            for(int k=0; k<9; k++){
                if(invalidCellMatrix[i][k]){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates the positional value of the currently selected cell.
     * @param row currently selected row
     * @param col currently selected col
     */
    public void updateSelectedCell(int row, int col){
        selectedRow = row;
        selectedCol = col;
        selectedCellLiveData.postValue(new Pair<>(row, col));
    }

    /**
     * Updates the LiveData
     */
    public void update(){
        cellsLiveData.postValue(sudokuMatrix);
        invalidCellsLiveData.postValue(invalidCellMatrix);
        insertedCellsLiveData.postValue(insertedCellMatrix);
    }

    /**
     * Checks that the given cell and number adheres to the sudoku ruleset.
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     * @param num number that will be controlled against ruleset
     * @return true if no broken rules, false if broken rule exists
     */
    public boolean checkRules(int row, int col, int num){
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

        for(int i = row-row%3; i <= row+(2-row%3); i++){
            for(int k = col-col%3; k <= col+(2-col%3); k++){
                if(sudokuMatrix[i][k] == num && i != row && k != col){
                    return false;
                }
            }
        }
        return true;
    }
}
