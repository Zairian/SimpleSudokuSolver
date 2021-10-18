package com.example.sudokusolver.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.midi.MidiOutputPort;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;

public class SudokuBoardView extends View{
    private Paint thickBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint thinBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectedCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint conflictingCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint invalidCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint insertedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int sqrtSize = 3;
    private int size = 9;

    private @Nullable int[][] cells = null;
    private @Nullable boolean[][] invalidCells = null;
    private @Nullable boolean[][] insertedCells = null;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private @Nullable SudokuBoardView.OnTouchListener listener = null;

    private float cellSizePixels = 0F;

    public SudokuBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        thickBorderPaint.setStyle(Paint.Style.STROKE);
        thickBorderPaint.setColor(Color.BLACK);
        thickBorderPaint.setStrokeWidth(4F);

        thinBorderPaint.setStyle(Paint.Style.STROKE);
        thinBorderPaint.setColor(Color.BLACK);
        thinBorderPaint.setStrokeWidth(2F);

        selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectedCellPaint.setColor(Color.parseColor("#78ba41"));

        conflictingCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        conflictingCellPaint.setColor(Color.parseColor("#d1d1d1"));

        invalidCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        invalidCellPaint.setColor(Color.parseColor("#ba4141"));

        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(72F);

        insertedTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        insertedTextPaint.setColor(Color.parseColor("#2196F3"));
        insertedTextPaint.setTextSize(72F);
    }

    // Pixel measurments used when drawing
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizePixels, sizePixels);
    }

    // Contains objects to be drawn in the view
    @Override
    protected void onDraw(Canvas canvas) {
        cellSizePixels = ((float)getWidth() / size);
        fillCells(canvas);
        drawInvalidCells(canvas);
        drawLines(canvas);

        drawText(canvas);
    }

    // Fills the cells with paint to show selected cells
    private void fillCells(Canvas canvas){
        if(selectedRow == -1 || selectedCol == -1){
            return;
        }
        for (int i=0;  i < size; i++ ){
            for(int k=0; k < size; k++){
                if(i == selectedRow && k == selectedCol){
                    fillCell(canvas, i, k, selectedCellPaint);
                }   else if(i == selectedRow || k == selectedCol){
                    fillCell(canvas, i, k, conflictingCellPaint);
                }   else if(i / sqrtSize == selectedRow / sqrtSize && k / sqrtSize == selectedCol / sqrtSize){
                    fillCell(canvas, i, k, conflictingCellPaint);
                }
            }
        }
    }

    // Helper method to fill cell with selected paint
    private void fillCell(Canvas canvas, int row, int col, Paint paint){
        canvas.drawRect(col * cellSizePixels, row * cellSizePixels, (col + 1) * cellSizePixels, (row+1) * cellSizePixels, paint);
    }

    // Draws lines of the sudoku board
    private void drawLines(Canvas canvas){
        canvas.drawRect(0F,0F, (float)getWidth(), (float)getHeight(), thickBorderPaint);

        for (int i=0; i<size; i++){
            if(i % sqrtSize == 0){
                canvas.drawLine(i * cellSizePixels,
                        0F,
                        i * cellSizePixels,
                        (float)getHeight(),
                        thickBorderPaint);

                canvas.drawLine(0F,
                        i* cellSizePixels,
                        (float)getWidth(),
                        i*cellSizePixels,
                        thickBorderPaint);
            } else{
                canvas.drawLine(i * cellSizePixels,
                        0F,
                        i * cellSizePixels,
                        (float)getHeight(),
                        thinBorderPaint);

                canvas.drawLine(0F,
                        i* cellSizePixels,
                        (float)getWidth(),
                        i*cellSizePixels,
                        thinBorderPaint);
            }
        }
    }

    // Draws the text for the cells of the sudoku board
    private void drawText(Canvas canvas){
        for(int i=0; i<size;i++){
            for(int k=0; k<size; k++){
                if(cells != null){
                    if(cells[i][k] != 0){
                        String valueString = Integer.toString(cells[i][k]);

                        Rect textBounds = new Rect();
                        textPaint.getTextBounds(valueString, 0, valueString.length(), textBounds);
                        float textWidth = textPaint.measureText(valueString);
                        float textHeigth = textBounds.height();

                        if(insertedCells[i][k] == true){
                            canvas.drawText(valueString, (k * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                                    (i * cellSizePixels) + cellSizePixels / 2 + textHeigth / 2, textPaint);
                        }   else{
                            canvas.drawText(valueString, (k * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                                    (i * cellSizePixels) + cellSizePixels / 2 + textHeigth / 2, insertedTextPaint);
                        }

                    }
                }
            }
        }
    }

    // Fills the invalid cells with red paint if any invalid cells exists
    private void drawInvalidCells(Canvas canvas){
        for(int i=0; i<size;i++){
            for(int k=0; k<size; k++){
                if(invalidCells != null){
                    if(invalidCells[i][k] == true){
                        fillCell(canvas, i, k, invalidCellPaint);
                    }
                }
            }
        }
    }

    // Handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            handleTouchEvent(event.getX(), event.getY());
            return true;
        }   else{
            return false;
        }
    }

    // Helper method for handling of touch event
    private void handleTouchEvent(Float x, Float y){
        int possibleSelectedRow = (int)(y / cellSizePixels);
        int possibleSelectedCol = (int)(x / cellSizePixels);
        listener.onCellTouched(possibleSelectedRow, possibleSelectedCol);
    }

    // Updates the selected cell data
    public void updateSelectedCellUI(Integer row, Integer col){
        selectedRow = row;
        selectedCol = col;
        invalidate();
    }

    // Updates the cell data
    public void updateCells(int[][] cells){
        this.cells = cells;
        invalidate();
    }

    // Updates the invalid cell data
    public void updateInvalidCells(boolean[][] invalidCells){
        this.invalidCells = invalidCells;
        invalidate();
    }

    // Updates the user inserted cell data
    public void updateInsertedCells(boolean[][] insertedCells){
        this.insertedCells = insertedCells;
        invalidate();
    }

    // Registers the listener
    public void registerListener(SudokuBoardView.OnTouchListener listener){
        this.listener = listener;
    }

    public interface OnTouchListener{
        void onCellTouched(Integer row, Integer col);
    }
}
