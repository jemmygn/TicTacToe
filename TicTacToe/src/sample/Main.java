package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {


    private char currentPlayer = 'x';
    private Label statusMessage = new Label("X must play!");
    private int rowColumn;
    private Cell[][] cell;
    private int winStreak = 0 ;
    private Stage stage = new Stage();
    @Override


    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();

        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("TicTacToe");
        dialog.setHeaderText("Welcome to TicTacToe");
        dialog.setContentText("Please enter how many rows and columns to play: ");


        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        TextField inputField = dialog.getEditor();
        BooleanBinding isInvalid = Bindings.createBooleanBinding(() -> isInvalid(inputField.getText()), inputField.textProperty());
        okButton.disableProperty().bind(isInvalid);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(rowColumns -> rowColumn = Integer.parseInt(rowColumns));

        cell =  new Cell[rowColumn][rowColumn];

        for(int i = 0; i< rowColumn; i++)
        {
            for(int j = 0; j< rowColumn; j++)
            {
                cell[i][j] = new Cell();
                gPane.add(cell[i][j], i, j);
            }
        }

        bPane.setCenter(gPane);
        bPane.setBottom(statusMessage);

        Scene scene = new Scene(bPane, 800,800,Color.CADETBLUE);
        primaryStage.setMaxHeight(800);
        primaryStage.setMaxWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;


    }

    public boolean isInvalid(String input)
    {
        try{
            double d = Double.parseDouble(input);

            if(d == 0 || d == 1)
            {
                return true;
            }
        }
        catch(NumberFormatException | NullPointerException nfe)
        {
            return true;
        }

        return false;
    }

    public boolean isBoardFull()
    {
        for(int i = 0; i < rowColumn; i++ )
        {
            for(int j = 0; j< rowColumn; j++)
            {
                if(cell[i][j].getPlayer()== ' ')
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasWon(char player, Cell[][] cell)
    {

        //Horizontal Check
        for(int i = 0; i < rowColumn; i++)
        {
            for(int j=0; j < rowColumn; j++)
            {
                if (cell[j][i].getPlayer() == player) {
                    winStreak++;
                    if(winStreak == rowColumn)
                    {
                        return true;
                    }
                }
            }
            winStreak = 0;
        }

        //Vertical Check
        for(int i = 0; i < rowColumn; i++)
        {
            for(int j=0; j < rowColumn; j++)
            {
                if (cell[i][j].getPlayer() == player) {
                    winStreak++;
                    if(winStreak == rowColumn)
                    {
                        return true;
                    }
                }
            }
            winStreak = 0;
        }


        int i;

        //Down Right Diagonal Check
        for (i = 0; i < rowColumn; i++) {
            if (cell[i][i].getPlayer() != player) {
                break;
            }
        }
        if ( i == rowColumn) {
            return true;
        }

        //Down Left Diagonal Check
        for (i = 0; i < rowColumn; i++) {
            if (cell[i][(rowColumn - 1) - i].getPlayer() != player) {
                break;
            }
        }
        if ( i == rowColumn) {
            return true;
        }

        return false;
    }

    public class Cell extends Pane
    {
        private char player = ' ';

        public Cell()
        {
            setStyle("-fx-border-color: black");
            this.setPrefSize(300,300);
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    handleClick();
                }
            });

        }

        private void handleClick()
        {
            if(player == ' ' && currentPlayer != ' ')
            {
                setPlayer(currentPlayer);

                if(hasWon(currentPlayer, cell))
                {
                    statusMessage.setText(currentPlayer + " won!");
                    for(int i = 0; i< rowColumn; i++)
                    {
                        for(int j = 0; j< rowColumn; j++)
                        {
                            cell[i][j].setOnMouseClicked(null);
                        }
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("TicTacToe");
                    alert.setHeaderText(Character.toUpperCase(currentPlayer) + " won!");
                    alert.setContentText("Do you want to play again?");


                    ButtonType buttonTypeOne = new ButtonType("Yes");
                    ButtonType buttonTypeTwo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne)
                    {
                        Platform.runLater( () -> {
                            try {
                                stage.close();
                                new Main().start( new Stage() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    else if (result.get() == buttonTypeTwo)
                    {
                        System.exit(0);
                    }
                }
                else if(isBoardFull())
                {
                    statusMessage.setText("Draw!");
                }
                else
                {
                    currentPlayer = (currentPlayer == 'x') ? 'o' : 'x';
                    statusMessage.setText(currentPlayer + "'s turn to play!");
                }
            }
        }

        public char getPlayer()
        {
            return player;
        }

        public void setPlayer(char c)
        {
            player = c;

            if(player == 'x')
            {
                Line line1 = new Line(10, 10, this.getWidth()-10, this.getHeight()-10);
                line1.endXProperty().bind(this.widthProperty().subtract(10));
                line1.endYProperty().bind(this.heightProperty().subtract(10));

                Line line2 = new Line(10, this.getWidth()-10, this.getHeight()-10, 10);
                line2.endXProperty().bind(this.widthProperty().subtract(10));
                line2.startYProperty().bind(this.heightProperty().subtract(10));

                getChildren().addAll(line1,line2);
            }
            else if (player == 'o')
            {
                Ellipse ellipse = new Ellipse(
                        this.getWidth()/2,
                        this.getHeight()/2,
                        this.getWidth()/2-10,
                        this.getHeight()/2-10);

                ellipse.centerXProperty().bind(this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
                ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.TRANSPARENT);

                getChildren().add(ellipse);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
