
    
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.sun.glass.ui.Window.Level;
import com.sun.media.jfxmedia.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;



public class HangMan extends Application {	
	
	
    private static final int APP_W = 900;
    private static final int APP_H = 500;
    private static final Font DEFAULT_FONT = new Font("Courier", 36);

    private static final int POINTS_PER_LETTER = 100;
    private static final float BONUS_MODIFIER = 0.2f;

    private static MediaPlayer a ;
    
    private static MediaPlayer donkeyPlayer;
    
    private static MediaPlayer victoryPlayer;
    private static MediaPlayer lossPlayer;
    
    /**
     * The word to guess
     */
    private SimpleStringProperty word = new SimpleStringProperty();
    private SimpleStringProperty question = new SimpleStringProperty();

    /**
     * How many letters left to guess
     */
    private SimpleIntegerProperty lettersToGuess = new SimpleIntegerProperty();
    private String[] wordQuestion = {null,null};
    /**
     * Current score
     */
    private SimpleIntegerProperty score = new SimpleIntegerProperty();

    /**
     * How many points next correct letter is worth
     */
    private float scoreModifier = 1.0f;

    /**
     * Is game playable
     */
    private SimpleBooleanProperty playable = new SimpleBooleanProperty();

    /**
     * List for letters of the word {@link #word}
     * It is backed up by the HBox children list,
     * so changes to this list directly affect the GUI
     */
    private ObservableList<Node> letters;

    /**
     * K - characters [A..Z] and '-'
     * V - javafx.scene.Text representation of K
     */
    private HashMap<Character, Text> alphabet = new HashMap<Character, Text>();

    private HangmanImage hangman = new HangmanImage();

    private WordReader wordReader = new WordReader("Words.txt" , "Questions.txt");
    
    private Text q;
	private HBox rowQuestions;
	private static boolean dSound = false;
    

    public Parent createContent() {
    	URL resource = getClass().getResource("background music.wav");
    	 a =new MediaPlayer(new Media(resource.toString()));
   	 a.setOnEndOfMedia(new Runnable() {
   	       public void run() {
   	         a.seek(Duration.ZERO);
   	       }
   	   });
   	  a.play();
    	 
   	  	rowQuestions =new HBox(5);
    	rowQuestions.setAlignment(Pos.CENTER);
    	
        q = new Text(String.valueOf(""));
        q.setFont(DEFAULT_FONT);
        rowQuestions.getChildren().add(q);
        
        HBox rowLetters = new HBox();
        rowLetters.setAlignment(Pos.BASELINE_CENTER);
        letters = rowLetters.getChildren();
        rowLetters.setSpacing(10);

        playable.bind(hangman.lives.greaterThan(0).and(lettersToGuess.greaterThan(0)));
        playable.addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                stopGame();
        });
        
        Button btnAgain = new Button("NEW GAME");
        btnAgain.setOnAction(event -> {
        	
        	if(dSound == true) {
        			donkeyPlayer.stop(); //problem
        			dSound = false;
        	
        	}
        	
        	rowQuestions.getChildren().removeAll(rowQuestions.getChildren());
        	Play();
        	});
        
        Button btnAgain2 = new Button("Reveal");
        btnAgain2.setTooltip(new Tooltip("Display Answer"));
        btnAgain2.setOnAction(event -> stopGame());
        
        Button btnAgain3 = new Button("Instructions");
        btnAgain3.setTooltip(new Tooltip("How to play"));
        btnAgain3.setOnAction(event -> instructions());
        	
        
    

        // layout
       

        HBox rowAlphabet = new HBox(5);
        rowAlphabet.setAlignment(Pos.CENTER);
        for (char c = 'A'; c <= 'Z'; c++) {
            Text t = new Text(String.valueOf(c));
            t.setFont(DEFAULT_FONT);
            alphabet.put(c, t);
            rowAlphabet.getChildren().add(t);
        }

        Text hyphen = new Text("-");
        hyphen.setFont(DEFAULT_FONT);
        alphabet.put('-', hyphen);
        rowAlphabet.getChildren().add(hyphen);

        Text textScore = new Text();
        textScore.textProperty().bind(score.asString().concat(" Points"));

        HBox rowHangman = new HBox(10, btnAgain, textScore, hangman);
        rowHangman.setAlignment(Pos.CENTER);
        HBox rowHangman2 = new HBox(10, btnAgain2,btnAgain3, textScore, hangman);
        rowHangman2.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(10);
        // vertical layout
        vBox.setAlignment(Pos.BOTTOM_CENTER);
        vBox.getChildren().addAll(
                
        		rowQuestions,
                rowLetters,
                rowAlphabet,
                rowHangman,
                rowHangman2);
        return vBox;
    }

    private void stopGame() {
        for (Node n : letters) {
            Letter letter = (Letter) n;
            letter.show();
        }
    }
    public void instructions(){
    	
        Label secondLabel = new Label("1. Click New Game to genate new questions.");
        Label secondLabe2 = new Label("2. Select a letter of the alphabet using keypad.");
        Label secondLabe3 = new Label("3. If the letter is contained it will be revealed.");
        Label secondLabe4 = new Label("4. If the letter is not contained in the word/phrase,a portion of the hangman is added.");
        Label secondLabe5 = new Label("5. The game continues until:");
        Label secondLabe6 = new Label("The word/phrase is guessed (all letters are revealed) – WINNER or,");
        Label secondLabe7 = new Label("All the parts of the hangman are displayed – LOSER");
        VBox vBox = new VBox(10);
        
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(
                
        		secondLabel,
        		secondLabe2,
        		secondLabe3,
        		secondLabe4,
        		secondLabe5,
        		secondLabe6,
        		secondLabe7);
        
        
        Scene secondScene = new Scene(vBox, 600, 300);

        Stage secondStage = new Stage();
        secondStage.setTitle("Instructions");
        secondStage.setScene(secondScene);
        
        //Set position of second window, related to primary window.
        
        secondStage.setResizable(false);
        secondStage.show();
    }
    private void Play() {
    	
        for (Text t : alphabet.values()) {
            t.setStrikethrough(false);
            t.setFill(Color.BLACK);
        }
        
         wordQuestion = wordReader.getRandomWord();
         q = new Text(String.valueOf(wordQuestion[1]));
         q.setFont(DEFAULT_FONT);
         rowQuestions.getChildren().add(q);
        hangman.reset();
        word.set(wordQuestion[0].toUpperCase());
        lettersToGuess.set(word.length().get());

        letters.clear();
        for (char c : word.get().toCharArray()) {
            letters.add(new Letter(c));
        }
     
    }

    private static class HangmanImage extends Parent {
        private static final int SPINE_START_X = 400;
        private static final int SPINE_START_Y = 20;
        private static final int SPINE_END_X = SPINE_START_X;
        private static final int SPINE_END_Y = SPINE_START_Y + 50;

        /**
         * How many lives left
         */
        private SimpleIntegerProperty lives = new SimpleIntegerProperty();

        public HangmanImage() {
        	
            Circle head = new Circle(20);
            head.setTranslateX(SPINE_START_X);

            Line spine = new Line();
            spine.setStartX(SPINE_START_X);
            spine.setStartY(SPINE_START_Y);
            spine.setEndX(SPINE_END_X);
            spine.setEndY(SPINE_END_Y);

            Line leftArm = new Line();
            leftArm.setStartX(SPINE_START_X);
            leftArm.setStartY(SPINE_START_Y);
            leftArm.setEndX(SPINE_START_X + 40);
            leftArm.setEndY(SPINE_START_Y + 10);

            Line rightArm = new Line();
            rightArm.setStartX(SPINE_START_X);
            rightArm.setStartY(SPINE_START_Y);
            rightArm.setEndX(SPINE_START_X - 40);
            rightArm.setEndY(SPINE_START_Y + 10);

            Line leftLeg = new Line();
            leftLeg.setStartX(SPINE_END_X);
            leftLeg.setStartY(SPINE_END_Y);
            leftLeg.setEndX(SPINE_END_X + 25);
            leftLeg.setEndY(SPINE_END_Y + 50);

            Line rightLeg = new Line();
            rightLeg.setStartX(SPINE_END_X);
            rightLeg.setStartY(SPINE_END_Y);
            rightLeg.setEndX(SPINE_END_X - 25);
            rightLeg.setEndY(SPINE_END_Y + 50);

            getChildren().addAll(head, spine, leftArm, rightArm, leftLeg, rightLeg);
            lives.set(getChildren().size());
        }
       

        public void reset() {
            getChildren().forEach(node -> node.setVisible(false));
            lives.set(getChildren().size());
        }

        public void takeAwayLife() {
        	
            for (Node n : getChildren()) {
                if (!n.isVisible()) {
                    n.setVisible(true);
                    lives.set(lives.get() - 1);
if(lives.get() == 0) {
	URL donkeySound = getClass().getResource("donkey.wav");
   donkeyPlayer = new MediaPlayer(new Media(donkeySound.toString()));
   a.pause();
   dSound=true;
	donkeyPlayer.play();
	
	donkeyPlayer.setOnStopped(() -> a.play());
}
                    break;
                }
            }
        }
    }

    private static class Letter extends StackPane {
        private Rectangle bg = new Rectangle(50, 70);
        private Text text;

        public Letter(char letter) {
            bg.setFill(letter == ' ' ? Color.DARKSEAGREEN : Color.WHITE);
            bg.setStroke(Color.BLACK);

            text = new Text(String.valueOf(letter).toUpperCase());
            text.setFont(DEFAULT_FONT);
            text.setVisible(false);

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
            
            
        }

        public void show() {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), bg);
            rt.setAxis(Rotate.Y_AXIS);
            rt.setToAngle(180);
            rt.setOnFinished(event -> text.setVisible(true));
            rt.play();
        }

        public boolean isEqualTo(char other) {
            return text.getText().equals(String.valueOf(other).toUpperCase());
        }
    }
  
   
   //move this body and use override to enable
   
   
    //Scene 1
    
    
    public void start(Stage primaryStage2) {
            
    primaryStage2.setTitle("Hangman");
    	Image image = new Image("file:HangMan.jpg");
    	ImageView mv = new ImageView(image);
    	
    	Group root = new Group();
    	root.getChildren().addAll(mv);
    	
        Scene scene = new Scene(createContent());
        
        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getText().isEmpty())
                return;

            char pressed = event.getText().toUpperCase().charAt(0);
            if ((pressed < 'A' || pressed > 'Z') && pressed != '-')
                return;

            if (playable.get()) {
                Text t = alphabet.get(pressed);
                if (t.isStrikethrough())
                    return;

                // mark the letter 'used'
                t.setFill(Color.BLUE);
                t.setStrikethrough(true);

                boolean found = false;

                for (Node n : letters) {
                    Letter letter = (Letter) n;
                    if (letter.isEqualTo(pressed)) {
                        found = true;
                        score.set(score.get() + (int)(scoreModifier * POINTS_PER_LETTER));
                        lettersToGuess.set(lettersToGuess.get() - 1);
                        letter.show();
                    }
                }

                if (!found) {
                	
                    hangman.takeAwayLife();
                    scoreModifier -= BONUS_MODIFIER;
                    URL lossSound = getClass().getResource("lose sound.wav");
                    lossPlayer = new MediaPlayer(new Media(lossSound.toString()));
                    a.pause();
                 	lossPlayer.play();
                 	lossPlayer.setOnEndOfMedia(() -> a.play());
                }
                else {
                    scoreModifier += BONUS_MODIFIER;
                    URL victorySound = getClass().getResource("victory sound.wav");
                    victoryPlayer = new MediaPlayer(new Media(victorySound.toString()));
                    a.pause();
                 	victoryPlayer.play();
                 	victoryPlayer.setOnEndOfMedia(() -> a.play());
                }
                
            }
           
          
           
        });
       
        primaryStage2.setResizable(false);
        primaryStage2.setWidth(APP_W);
        primaryStage2.setHeight(APP_H);
        primaryStage2.setTitle("Hangman");
        primaryStage2.setScene(scene);
        primaryStage2.show();
        Play();
        
        
        
    
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
