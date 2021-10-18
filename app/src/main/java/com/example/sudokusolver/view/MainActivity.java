package com.example.sudokusolver.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.example.sudokusolver.R;
import com.example.sudokusolver.view.custom.SudokuBoardView;
import com.example.sudokusolver.viewmodel.SolveSudokuViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SudokuBoardView.OnTouchListener{
    private SolveSudokuViewModel viewModel;

    private SudokuBoardView s;

    private List<Button> buttons;

    // Thread for handling solve calculation
    private class DoCalculation extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog progressDialog;

        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this, R.style.ProgressTheme);
            progressDialog.setMessage("Calculating...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return viewModel.solve();
        }

        protected void onPostExecute(Boolean result){
            if(result == false){
                findViewById(R.id.unSolveableWarning).setVisibility(View.VISIBLE);
            }
            viewModel.update();
            progressDialog.dismiss();
        }
    }

    //Objects created on view creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar theToolbar = findViewById(R.id.theToolbar);
        setSupportActionBar(theToolbar);

        s = findViewById(R.id.sudokuBoardView);
        buttons = new ArrayList<Button>();
        buttons.add(findViewById(R.id.oneButton));
        buttons.add(findViewById(R.id.twoButton));
        buttons.add(findViewById(R.id.threeButton));
        buttons.add(findViewById(R.id.fourButton));
        buttons.add(findViewById(R.id.fiveButton));
        buttons.add(findViewById(R.id.sixButton));
        buttons.add(findViewById(R.id.sevenButton));
        buttons.add(findViewById(R.id.eightButton));
        buttons.add(findViewById(R.id.nineButton));
        buttons.add(findViewById(R.id.solveButton));
        buttons.add(findViewById(R.id.deleteButton));
        buttons = Collections.unmodifiableList(buttons);


        s.registerListener(this);


        viewModel = new ViewModelProvider(this).get(SolveSudokuViewModel.class);

        int counter = 1;

        // Button onClickListener setup
        for (Button b: buttons
        ) {
            int finalCounter = counter;
            b.setOnClickListener((View v) -> {
                int row = viewModel.getSelectedRow();
                int col = viewModel.getSelectedCol();
                if (row == -1 || col == -1){
                    return;
                } else if(finalCounter <= 9){
                    viewModel.setValue(finalCounter, row, col);
                    viewModel.setInsertedValue(true, row, col);
                    if(viewModel.checkRules(row, col, finalCounter) == false){
                        viewModel.setInvalidCellMatrix(true, row, col);
                    }
                    viewModel.recheckInvalidCells();
                } else if(finalCounter == 10){
                        if(!viewModel.checkInvalidExists()){
                            new DoCalculation().execute();
                        }else{
                            findViewById(R.id.conflictWarning).setVisibility(View.VISIBLE);
                        }
                } else if(finalCounter == 11){
                    viewModel.setValue(0, row, col);
                    viewModel.setInvalidCellMatrix(false, row, col);
                    viewModel.setInsertedValue(false, row, col);
                    viewModel.recheckInvalidCells();

                    if(!viewModel.checkInvalidExists()){
                        findViewById(R.id.conflictWarning).setVisibility(View.INVISIBLE);
                    }

                }
                viewModel.update();
            });
            counter++;
        }

        // LiveData observers
        viewModel.selectedCellLiveData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                updateSelectedCellUI((Pair<Integer, Integer>) o);
            }
        });

        viewModel.cellsLiveData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                updateCells((int[][])o);
            }
        });

        viewModel.invalidCellsLiveData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                updateInvalidCells((boolean[][])o);
            }
        });
        viewModel.insertedCellsLiveData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                updateInsertedCells((boolean[][])o);
            }
        });
    }

    // Calls the board view to show currently selected cell
    private void updateSelectedCellUI(@Nullable Pair<Integer, Integer> cell){
        if(cell != null){
            s.updateSelectedCellUI(cell.first, cell.second);
        }
    }

    // Updates the board view cell data
    private void updateCells(@Nullable int[][] cells){
        if(cells != null){
            s.updateCells(cells);
        }
    }

    // Updates the board view invalid cell data
    private void updateInvalidCells(@Nullable boolean[][] invalidCells){
        if(invalidCells != null){
            s.updateInvalidCells(invalidCells);
        }
    }

    // Updates the board view user inserted cell data
    private void updateInsertedCells(@Nullable boolean[][] insertedCells){
        if(insertedCells != null){
            s.updateInsertedCells(insertedCells);
        }
    }

    // Updates the board view selected cell data with touched cell
    @Override
    public void onCellTouched(Integer row, Integer col) {
        findViewById(R.id.unSolveableWarning).setVisibility(View.INVISIBLE);
        viewModel.updateSelectedCell(row, col);
    }

    // Creates the top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu, menu);
        return true;
    }

    // Handles events when top menu items gets selcted
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear:
                viewModel.clear();
                findViewById(R.id.conflictWarning).setVisibility(View.INVISIBLE);
                s.invalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}