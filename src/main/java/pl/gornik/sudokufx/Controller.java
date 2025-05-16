package pl.gornik.sudokufx;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class Controller {

    @FXML
    private GridPane sudokuGrid;
    @FXML
    private Label lblNumber;

    private int[][] gameNumbersArr = new int[9][9];

    private int[][] userNumbersArr = new int[9][9];

    private int chosenNumber;

    private String chosenColor="#FFF";

    public void initialize(){
        generateNumbers();
        generateBoard();
    }

    public void generateBoard(){
        for (int i = 0; i < 81; i++) {
            StackPane cell = new StackPane();
            sudokuGrid.getChildren().add(cell);
            int row = i/9;
            int col = i%9;
            GridPane.setColumnIndex(cell,col);
            GridPane.setRowIndex(cell,row);
            if(row%3==0 && row!=0){
                cell.setStyle("-fx-border-color: black");
                cell.setStyle("-fx-border-style: solid hidden hidden hidden;-fx-border-width: 3 0 0 0");
            }
            if(col%3==0 && col!=0){
                cell.setStyle("-fx-border-color: black");
                cell.setStyle("-fx-border-style: hidden hidden hidden solid;-fx-border-width: 0 0 0 3");
            }
            if(col%3==0 && row%3==0 && row!=0 && col!=0){
                cell.setStyle("-fx-border-color: black");
                cell.setStyle("-fx-border-style: solid hidden hidden solid;-fx-border-width: 3 0 0 3");
            }
            Label number = new Label();
            number.setStyle("-fx-border-color: #000;");
            number.setMaxWidth(Double.MAX_VALUE);
            number.setMaxHeight(Double.MAX_VALUE);
            number.setAlignment(Pos.CENTER);
            number.setTextAlignment(TextAlignment.CENTER);
            cell.getChildren().add(number);
            cell.setOnMousePressed(mouseEvent -> {
                if (((Label) cell.getChildren().get(0)).equals("") || !((Label)cell.getChildren().get(0)).getText().equals(String.valueOf(chosenNumber))) {
                    ((Label)cell.getChildren().get(0)).setText(String.valueOf(chosenNumber));
                    ((Label)cell.getChildren().get(0)).setBackground(Background.fill(Paint.valueOf(chosenColor)));
                    ((Label)cell.getChildren().get(0)).setTextFill(Paint.valueOf("#FFF"));
                }else{
                    ((Label)cell.getChildren().get(0)).setText("");
                    ((Label)cell.getChildren().get(0)).setBackground(Background.fill(Paint.valueOf("#FFF")));
                    ((Label)cell.getChildren().get(0)).setTextFill(Paint.valueOf("#000"));
                }
            });
        }
    }

    public void generateNumbers() {
        // Clear grid
        for (int i = 0; i < 9; i++)
            Arrays.fill(gameNumbersArr[i], 0);

        // Fill using backtracking
        fillGrid(0, 0);
    }

    private boolean fillGrid(int row, int col) {
        if (row == 9) return true; // Finished

        int nextRow = (col == 8) ? row + 1 : row;
        int nextCol = (col + 1) % 9;

        List<Integer> numbers = getShuffledNumbers();

        for (int num : numbers) {
            if (isSafe(row, col, num)) {
                gameNumbersArr[row][col] = num;
                if (fillGrid(nextRow, nextCol)) return true;
                gameNumbersArr[row][col] = 0; // backtrack
            }
        }

        return false; // trigger backtracking
    }

    private boolean isSafe(int row, int col, int num) {
        // Row and column check
        for (int i = 0; i < 9; i++) {
            if (gameNumbersArr[row][i] == num || gameNumbersArr[i][col] == num)
                return false;
        }

        // 3x3 box check
        int boxStartRow = (row / 3) * 3;
        int boxStartCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (gameNumbersArr[boxStartRow + i][boxStartCol + j] == num)
                    return false;

        return true;
    }

    private List<Integer> getShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers);
        return numbers;
    }

    public void selectNumber(ActionEvent event) {
        chosenNumber=Integer.parseInt(((Button)event.getSource()).getText());
        lblNumber.setText(((Button)event.getSource()).getText());
        switch (((Button)event.getSource()).getText()){
            case "1"-> chosenColor="pink";
            case "2"-> chosenColor="cyan";
            case "3"-> chosenColor="yellow";
            case "4"-> chosenColor="red";
            case "5" -> chosenColor="lime";
            case "6" -> chosenColor="magenta";
            case "7" -> chosenColor="brown";
            case "8"-> chosenColor="gray";
            case "9"-> chosenColor="black";
        }
        lblNumber.setStyle("-fx-border-color: #000;-fx-text-fill: #FFF;-fx-background-color: "+chosenColor);
    }
}
