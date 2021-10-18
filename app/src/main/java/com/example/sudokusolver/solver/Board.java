package com.example.sudokusolver.solver;

import java.util.List;

public class Board {
    int size;
    List<Cell> cells;

    public Board(int size, List<Cell> cells){
        this.size = size;
        this.cells = cells;
    }

    public Cell getCell(int row, int col){
        return cells.get(row * size + col);
    }
}
