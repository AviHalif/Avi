import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ImageSlide extends Application {

    // Width and height of image in pixels
    private final double IMG_WIDTH = 600;
    private final double IMG_HEIGHT = 300;

    private final int NUM_OF_IMGS = 3;
    private final int SLIDE_FREQ = 5; // in secs

    @Override
    public void start(Stage window){

        //final SwingNode swingNode = new SwingNode();
        //createAndSetSwingContent(swingNode);

        //stage.initStyle(StageStyle.UNDECORATED);

        BorderPane layout = new BorderPane(); // לבחור לאייאווט אחר שיאפשר מיקום שונה של הדברים
        Scene scene = new Scene(layout, 1400, 700);
        Button button = new Button("dsfsdf");
        layout.setBottom(button);
        button.setPrefSize(300,70);

        window.setScene(scene);
        window.setTitle("C A S T R O - W E L C O M E");
        window.show();


        Pane clipPane = new Pane();

        clipPane.setMaxSize(IMG_WIDTH, IMG_HEIGHT);
        layout.setCenter(clipPane);
        //clipPane.setClip(new Rectangle(IMG_WIDTH, IMG_HEIGHT));


    }


    private void createAndSetSwingContent(SwingNode swingNode) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Swing application");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Create JavaFX panel.
                JFXPanel javafxPanel = new JFXPanel();
                frame.getContentPane().add(javafxPanel, BorderLayout.CENTER);
            }
        });
    }


    //start animation
    private void startAnimation(final HBox hbox) {
        //error occured on (ActionEvent t) line
        //slide action
        EventHandler<ActionEvent> slideAction = (ActionEvent t) -> {
            TranslateTransition trans = new TranslateTransition(Duration.seconds(1.5), hbox);
            trans.setByX(-IMG_WIDTH);
            trans.setInterpolator(Interpolator.EASE_BOTH);
            trans.play();
        };
        //eventHandler
        EventHandler<ActionEvent> resetAction = (ActionEvent t) -> {
            TranslateTransition trans = new TranslateTransition(Duration.seconds(1), hbox);
            trans.setByX((NUM_OF_IMGS - 1) * IMG_WIDTH);
            trans.setInterpolator(Interpolator.EASE_BOTH);
            trans.play();
        };

        List<KeyFrame> keyFrames = new ArrayList<>();
        for (int i = 1; i <= NUM_OF_IMGS; i++) {
            if (i == NUM_OF_IMGS) {
                keyFrames.add(new KeyFrame(Duration.seconds(i * SLIDE_FREQ), resetAction));
            } else {
                keyFrames.add(new KeyFrame(Duration.seconds(i * SLIDE_FREQ), slideAction));
            }
        }
//add timeLine
        Timeline anim = new Timeline(keyFrames.toArray(new KeyFrame[NUM_OF_IMGS]));

        anim.setCycleCount(Timeline.INDEFINITE);
        anim.playFromStart();
    }

    //call main function
    public static void main(String[] args) {
        launch(args);
    }
}