package pl.gornik.sudokufx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    @FXML
    private ComboBox<String> comboDifficulty;

    private int[][] gameNumbersArr = new int[9][9];

    private int[][] userNumbersArr = new int[9][9];

    private int chosenNumber;

    private String chosenColor="#FFF";

    private int displayTiles=40;

    public void initialize(){
        List<String> difficulties = new ArrayList<>();
        difficulties.add("Łatwy");
        difficulties.add("Średni");
        difficulties.add("Trudny");
        comboDifficulty.valueProperty().addListener(observable -> {
            switch (comboDifficulty.valueProperty().getValue()){
                case "Łatwy"->{
                    displayTiles=40;
                }
                case "Średni"->{
                    displayTiles=32;
                }
                case "Trudny"->{
                    displayTiles=26;
                }
            }
        });
        comboDifficulty.setItems(FXCollections.observableList(difficulties));
        generateNumbers();
        for(int[] i : gameNumbersArr){
            System.out.println(Arrays.toString(i));
        }
        sudokuGrid.getChildren().clear();
        generateBoard();
        displayTips();
//        fillNumbers();
    }

    public void displayTips(){
        MouseEvent mouseEvent1 = new MouseEvent(
                MouseEvent.MOUSE_PRESSED,
                0, 0, 0, 0,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false, true,
                false, false, null
        );
        Random random = new Random();
        int index = 0;
        for (int i = 0; i < displayTiles; i++) {
            index = random.nextInt(0,81);
            if(((Label)((StackPane)sudokuGrid.getChildren().get(index)).getChildren().get(0)).getText().equals("")){
                int row = index / 9;
                int col = index % 9;
                selectNumber(gameNumbersArr[row][col]);
                sudokuGrid.getChildren().get(index).fireEvent(mouseEvent1);
                sudokuGrid.getChildren().get(index).setOnMousePressed(null);
                ((Label)((StackPane)sudokuGrid.getChildren().get(index)).getChildren().get(0)).setStyle("-fx-border-color: green;-fx-border-width: 3;");
            }
        }
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
                    userNumbersArr[row][col]=chosenNumber;
                    if(checkForWin()){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Koniec");
                        alert.setHeaderText(null);
                        alert.setContentText("Wygrałeś!");
                        alert.showAndWait();
                    }
                }else{
                    ((Label)cell.getChildren().get(0)).setText("");
                    ((Label)cell.getChildren().get(0)).setBackground(Background.fill(Paint.valueOf("#FFF")));
                    ((Label)cell.getChildren().get(0)).setTextFill(Paint.valueOf("#000"));
                    userNumbersArr[row][col]=0;
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
            if ((gameNumbersArr[row][i] == num && i!=col) || (gameNumbersArr[i][col] == num&&i!=row)){
                return false;
            }
        }


        // 3x3 box check
        int boxStartRow = (row / 3) * 3;
        int boxStartCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameNumbersArr[boxStartRow + i][boxStartCol + j] == num && boxStartRow+i!=row && boxStartCol+j!=col) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isSafeUser(int row, int col, int num) {
        // Row and column check
        for (int i = 0; i < 9; i++) {
            if ((userNumbersArr[row][i] == num && i!=col) || (userNumbersArr[i][col] == num&&i!=row)){
                return false;
            }
        }


        // 3x3 box check
        int boxStartRow = (row / 3) * 3;
        int boxStartCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameNumbersArr[boxStartRow + i][boxStartCol + j] == num && boxStartRow+i!=row && boxStartCol+j!=col) {
                    return false;
                }
            }
        }

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

    public boolean checkForWin(){

        for (int i = 0; i < 81; i++) {
            int row = i/9;
            int col = i%9;
            if(userNumbersArr[row][col]==0) return false;
            if(isSafeUser(row,col,userNumbersArr[row][col])) continue;
            return false;
        }
        return true;
    }

    public void fillNumbers(){
        MouseEvent mouseEvent1 = new MouseEvent(
                MouseEvent.MOUSE_PRESSED,
                0, 0, 0, 0,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false, true,
                false, false, null
        );

        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int col = i % 9;
            selectNumber(gameNumbersArr[row][col]);
            sudokuGrid.getChildren().get(i).fireEvent(mouseEvent1);
        }
    }

    public void selectNumber(int number) {
        chosenNumber=number;
        lblNumber.setText(String.valueOf(number));
        switch (String.valueOf(number)){
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

    public void restart(){
        initialize();
    }
}
