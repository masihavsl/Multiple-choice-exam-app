//Masihaali Vesali - student ID : 101350475
//Amir Yektajoo - student ID: 1013367389


package com.example.assignment2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //scene, grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        Label header = new Label("You should answer the following questions. Select a single answer from the four choices.");
        Label studentName = new Label("Student's Name: ");
        TextField nameField = new TextField();
        Label header2 = new Label("Questions");
        Label mark = new Label("Mark: ");
        mark.setMaxWidth(180);
        Label avgMarkOfAll = new Label("Average of all: ");
        mark.setMaxWidth(180);
        HBox field = new HBox();
        field.getChildren().addAll(studentName, nameField);
        //buttons
        Button submitBtn = new Button("Submit");
        submitBtn.setMaxWidth(100);
        Button calcAvgBtn = new Button("Calculate average of all exams");
        submitBtn.setMaxWidth(100);
        Button calcBtn = new Button("Calculate Grade");
        calcBtn.setMaxWidth(180);
        HBox btnsField = new HBox();
        btnsField.getChildren().addAll(submitBtn, calcAvgBtn, calcBtn, mark, avgMarkOfAll);
        btnsField.setSpacing(20);
        grid.add(header, 0, 0, 3,1);
        grid.add(field,0,1);
        grid.add(header2, 0, 3);
        grid.add(btnsField, 0, 32);
        //create a function which gnerates an array with 5 random numbers between 1-20 which are not the same
        Integer[] questionIndexes = randomNumGenerator();

        //create 2 seperate list
        //1: 20 questions stored as string
        //2: answers stored as an list within list
        List[] questionsAndAnswers = readFileAndStructureTheData();
        List questions = questionsAndAnswers[0];
        List answers = questionsAndAnswers[1];
        ToggleGroup[] arrOfToggleGroups = printQuestionsAndAnswers(answers, questions, questionIndexes, grid);
        //show stage
        //Creating the scroll pane
        ScrollPane scroll = new ScrollPane();
        scroll.setPrefSize(595, 600);
        //Setting content to the scroll pane
        scroll.setContent(grid);
        //Setting the stage
        Group root = new Group();
        root.getChildren().addAll(scroll);
        Scene scene = new Scene(root, 595, 600, Color.BEIGE);
        stage.setScene(scene);
        stage.setTitle("Multiple choice exam");
        stage.show();
        //create a seprate array which stores the correct answer to each question based on its question number (1 - 20)
        String[] correctAnswers = {"Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby"
                ,"Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby","Ruby",};
        // create an event listener for the Calculate button
        // {
        //store the selected answers in an array
        //compare the correctAnswers array with the selectedAnswer array
        //add or deduct marks accordingly
        //display the calculated mark on the screen
        //}
        calcBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //create a string for all the selectedAnswers -> x for empty

                int totalMark = calculateMark(setSelectedAnswers(arrOfToggleGroups), correctAnswers, questionIndexes);
                mark.setText("Mark: " + Integer.toString(totalMark));

            }
        });
        //create another event listener for the submit button{
        // which creates a string which creates the following string:
        // name, answers, final results separated by space -> hesam BXACD 7
        // and writes it to the result.txt file
        // }
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (nameField.getText().isEmpty() || nameField.getText().isBlank() || isNumeric(nameField.getText())){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Empty name fields");
                    a.setContentText("Name field must be filled and non numeric value.");
                    a.show();
                }else {
                    String result = "";
                    result += nameField.getText() + " ";
                    result += createAnswersString(answers, setSelectedAnswers(arrOfToggleGroups), questionIndexes) + " ";
                    result += calculateMark(setSelectedAnswers(arrOfToggleGroups), correctAnswers, questionIndexes);
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt", true));
                        writer.write(result + "\n");
                        writer.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

            }
        });
        //create another event listener for the show-average button{
        // go in the results.txt and calculate the average of all student
        // display the avg on the screen
        // }
        calcAvgBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int sum = 0;
                int lineCount = 0;
                try{
                    BufferedReader reader = new BufferedReader(new FileReader("result.txt"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lineCount++;
                        String[] arrOfStrings = line.split(" ", 3);
                        sum += Integer.parseInt(arrOfStrings[2]);
                    }
                    reader.close();

                }catch (IOException e){
                    e.printStackTrace();
                }
                avgMarkOfAll.setText("Average of all: " + (double) sum/lineCount);

            }
        });
    }
    //------------------FUNCTIONS-------------------
    public static Integer[] randomNumGenerator(){
        Integer[] arr = new Integer[5];
        for (int i = 0; i < 5; i++){
            //convert the arr to a list
            //so we can check if the newly generated value already exists in the array
            List<Integer> intList = Arrays.asList(arr);
            int randNum = (int)(Math.random() * (20 - 1 + 1) + 1);
            while (intList.contains(randNum)){
                randNum = (int)(Math.random() * (20 - 1 + 1) + 1);
            }
            arr[i] = randNum;
        }
        return arr;
    }

    //reads the file and produces an array with two elements
    //1: array of questions
    //2: array of answers
    public static List[] readFileAndStructureTheData(){
        List[] questionsAndAnswers= new List[2];
        //one list to store the question
        List<String> listOfQuestion = new ArrayList<String>();
        //one list to store the answers
        List<String> listOfOptions = new ArrayList<String>();
        List<List<String>> listOfAnswers = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("questions.txt"));
            String line;
            int questionNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == 'q'){
                    listOfQuestion.add(line);
                }else if(line.charAt(0) == ','){
                    listOfAnswers.add(listOfOptions);
                    listOfOptions = new ArrayList<String>();
                }
                else {
                    listOfOptions.add(line);
                }
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        questionsAndAnswers[0] = listOfQuestion;
        questionsAndAnswers[1] = listOfAnswers;

        return questionsAndAnswers;
    }

    //takes the question and answers list and adds them to the grid, and it returns the array of toggle groups
    public static ToggleGroup[] printQuestionsAndAnswers(List answers, List questions, Integer[] questionIndexes,GridPane grid){
        int i = 4;
        int j = 0;
        int questionNumber = 1;
        ToggleGroup[] arrOfToggleGroups = new ToggleGroup[5];
        List[] answs = (List[]) answers.toArray(new List[0]);
        for (int num : questionIndexes){
            //labels
            String question = (String) questions.get(num-1);

            Label q = new Label(Integer.toString(questionNumber++) + question.substring(1));
            //radio btns
            RadioButton opt1 = new RadioButton();
            opt1.setText((String) answs[num-1].get(0));
            RadioButton opt2 = new RadioButton();
            opt2.setText((String) answs[num-1].get(1));
            RadioButton opt3 = new RadioButton();
            opt3.setText((String) answs[num-1].get(2));
            RadioButton opt4 = new RadioButton();
            opt4.setText((String) answs[num-1].get(3));
            ToggleGroup tg = new ToggleGroup();
            opt1.setToggleGroup(tg);
            opt2.setToggleGroup(tg);
            opt3.setToggleGroup(tg);
            opt4.setToggleGroup(tg);
            arrOfToggleGroups[j++] = tg;
            //add the btns to the hbox
            VBox toggle = new VBox();
            toggle.getChildren().addAll(opt1,opt2,opt3,opt4);
            //grid layout
            grid.add(q, 0, i++);
            grid.add(opt1, 0, i++);
            grid.add(opt2, 0, i++);
            grid.add(opt3, 0, i++);
            grid.add(opt4, 0, i++);
        }
        return arrOfToggleGroups;

    }

    //calculate the mark based on the selected answers
    public static int calculateMark(String[] selectedAnswers, String[] correctAnswers, Integer[] questionIndexes){
        int totalMark = 0;
        int i = 0;
        for (String answ : selectedAnswers) {
            if (correctAnswers[questionIndexes[i++] - 1 ].equals(answ)){
                totalMark += 20;
            }else if(answ == "empty"){

            }else {
                totalMark -= 5;
            }
        }

        return totalMark;

    }
    //creates the answers string in -> ABCX format based on the selected answers
    public static String createAnswersString(List answers, String[] selectedAnswers, Integer[] questionIndexes){
        String allAnswersShortFormat = "";
        List[] answs = (List[]) answers.toArray(new List[0]);
        for (int n = 0; n < 5; n++) {
            for (int j = 0; j < 4; j++) {
                if (answs[questionIndexes[n]-1].get(j) == selectedAnswers[n]){
                    switch (j){
                        case 0:
                            allAnswersShortFormat += "A";
                            break;
                        case 1:
                            allAnswersShortFormat += "B";
                            break;
                        case 2:
                            allAnswersShortFormat += "C";
                            break;
                        case 3:
                            allAnswersShortFormat += "D";
                            break;
                    }
                } else if (selectedAnswers[n] == "empty"){
                    allAnswersShortFormat += "X";
                    break;
                }

            }
        }
        return allAnswersShortFormat;
    }

    //set sellected answers
    static public String[] setSelectedAnswers(ToggleGroup[] arrOfToggleGroups){
        String[] selectedAnswers = new String[5];

        int i = 0;
        for (ToggleGroup tg : arrOfToggleGroups){
            RadioButton opt = (RadioButton) tg.getSelectedToggle();
            if (opt == null){
                selectedAnswers[i++] = "empty";
            }else {
                selectedAnswers[i++] = opt.getText();
            }
        }
        return selectedAnswers;
    }

    public static boolean isNumeric(String string) {
        int intValue;
        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }
    public static void main(String[] args) {
        launch();
    }
}

