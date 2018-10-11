package Gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

public class Welcome extends JFrame { //extends Application {

    public static final String FRAME_NAME = "CASTRO - WELCOME";
    public static final String BUTTON_ENTER_LOGO = "/src/images/enterLogo.png";
    public static final String CASTRO_LOGO = "/src/images/castroLogoJPG.png";
    public static final String CREATORS_LOGO = "/src/images/Creators.png";
    public static final String CASTRO_ICON = "/src/images/icon.png";
    public static final String JOPTIONPANE_MESSAGE = "Are you sure you want to close the terminal?";
    public static final String JOPTIONPANE_TITLE = "CASTRO - CLOSE TERMINAL";
    public static final int DELAY_TIME  = 30;
    public static final int ENTER_BUTTON_WIDTH = 500;
    public static final int ENTER_BUTTON_HEIGHT = 120;

    private int colorIdx = 255;
    private Timer timer;
    private Dimension screenSize;

    private JPanel jPanelData;
    private SpringLayout springLayout;
    private JLabel castroLogoLabel, creatorsLogoLabel;
    private ImageIcon castroLogoJPG, enterLogoJPG, creatorsLogoJPG;
    private JButton jButtonEnter;
  /*
    // Width and height of image in pixels
    private final double IMG_WIDTH = 600;
    private final double IMG_HEIGHT = 300;
    private final int NUM_OF_IMGS = 3;
    private final int SLIDE_FREQ = 5; // in secs
    private StackPane root;
    private Pane clipPane;*/

    public Welcome(){

        SetGUIComponents();
    }

    private void SetGUIComponents() {

        jPanelData = new JPanel();
        springLayout = new SpringLayout();

        castroLogoJPG = new ImageIcon(getClass().getResource(CASTRO_LOGO));
        castroLogoLabel = new JLabel(castroLogoJPG);

        enterLogoJPG = new ImageIcon(getClass().getResource(BUTTON_ENTER_LOGO));
        jButtonEnter = new JButton(enterLogoJPG);
        jButtonEnter.setBorderPainted(false);
        jButtonEnter.setBackground(Color.BLACK);

        jButtonEnter.setPreferredSize(new Dimension(ENTER_BUTTON_WIDTH, ENTER_BUTTON_HEIGHT));

        creatorsLogoJPG = new ImageIcon(getClass().getResource(CREATORS_LOGO));
        creatorsLogoLabel = new JLabel(creatorsLogoJPG);

        jButtonEnter.setVisible(false);
    }

    private void InitializeActions() {


        jButtonEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Login loginWin = null;
                loginWin = new Login( getFrame());

                getFrame().setEnabled(false); // Disable edit this window when you open the next window
                loginWin.setUndecorated(true);
                loginWin.DrawLogin();
                loginWin.setVisible(true);
            }
        });

        getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                askUserBeforeCloseWindow( getFrame());
            }
        });

        timer = new Timer(DELAY_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jButtonEnter.setVisible(true);
                    if(colorIdx==0)return;
                    jPanelData.setBackground(new Color(colorIdx, colorIdx, colorIdx));
                    jButtonEnter.setBackground(new Color(colorIdx, colorIdx, colorIdx));
                    colorIdx--;

            }
        });


    }

    protected void DrawWelcome(){

        GUISettingForJFrame();
        GUISettingForJPanel();
        InitializeActions();
        SetFrameVisibleAndStartTimer();
    }

    private void SetFrameVisibleAndStartTimer() {

        getFrame().setVisible(true);
        getFrame().setLocationRelativeTo(null);
        timer.start();
    }

    private void GUISettingForJPanel() {

        GUIDefineLayoutAndAddComponentsOnJPanel();
        GUIPlaceComponentsOnJPanel();
    }

    private void GUIPlaceComponentsOnJPanel() {

        springLayout.putConstraint(SpringLayout.WEST,jButtonEnter,(screenSize.width)/2 - 250,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonEnter,(screenSize.height)/2 + 200,SpringLayout.NORTH,jPanelData);

        springLayout.putConstraint(SpringLayout.WEST,castroLogoLabel,-190,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH,castroLogoLabel,-40,SpringLayout.NORTH,jPanelData);

        springLayout.putConstraint(SpringLayout.WEST,creatorsLogoLabel,3,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.SOUTH,creatorsLogoLabel,3,SpringLayout.SOUTH,jPanelData);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelData.setLayout(springLayout);
        jPanelData.add(jButtonEnter);
        jPanelData.add(castroLogoLabel);
        jPanelData.add(creatorsLogoLabel);
    }

    private void GUISettingForJFrame() {

        getFrame().setTitle(FRAME_NAME);
        getFrame().setResizable(false);
        getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getFrame().setIconImages(Collections.singletonList(Toolkit.getDefaultToolkit().getImage(getClass().getResource(CASTRO_ICON))));

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        getFrame().setSize(screenSize.width, screenSize.height);
        getFrame().setContentPane(jPanelData);
    }

    private static void askUserBeforeCloseWindow(JFrame currentFrame ){

        if (JOptionPane.showConfirmDialog(currentFrame,
                JOPTIONPANE_MESSAGE, JOPTIONPANE_TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,null) == JOptionPane.YES_OPTION){
                     currentFrame.setVisible(false);
        }

        else{
            currentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    public JFrame getFrame() {
        return this;
    }

    public static void main(String[] args) {

        Welcome welcomeWin = new Welcome();
        welcomeWin.DrawWelcome();
        welcomeWin.setVisible(true);
        welcomeWin.setLocationRelativeTo(null);
        //launch(args);
    }
}

/*
    @Override
    public void start(Stage stage) throws Exception {

        stage.initStyle(StageStyle.UNDECORATED);

        //root code
        StackPane root = new StackPane();

        root.setEffect( new InnerShadow() );

        Pane clipPane = new Pane();

        stage.setX(450);
        stage.setY(370);
        stage.setAlwaysOnTop(true);

        // To center the slide show incase maximized

        clipPane.setMaxSize(IMG_WIDTH, IMG_HEIGHT);
        clipPane.setClip(new Rectangle(IMG_WIDTH, IMG_HEIGHT));

        BorderPane borderPane = new BorderPane(clipPane);
        borderPane.setVisible(false);

        HBox imgContainer = new HBox();

        //image view
        ImageView imgGreen = new ImageView("/src/images/photoLogIn1.png");
        ImageView imgBlue = new ImageView("/src/images/photoLogIn2.png");
        ImageView imgRose = new ImageView("/src/images/photoLogIn3.png");

        imgContainer.getChildren().addAll(imgGreen, imgBlue, imgRose);
        clipPane.getChildren().add(imgContainer);

        root.getChildren().add(clipPane);

        Scene scene = new Scene(root, IMG_WIDTH, IMG_HEIGHT);
        stage.setTitle("Image Slider");
        stage.setScene(scene);
        startAnimation(imgContainer);
        stage.show();
    }



    private void startAnimation(final HBox hbox) {
        //error occured on (ActionEvent t) line
        //slide action
        EventHandler<javafx.event.ActionEvent> slideAction = (javafx.event.ActionEvent t) -> {
            TranslateTransition trans = new TranslateTransition(Duration.seconds(1.5), hbox);
            trans.setByX(-IMG_WIDTH);
            trans.setInterpolator(Interpolator.EASE_BOTH);
            trans.play();
        };
        //eventHandler
        EventHandler<javafx.event.ActionEvent> resetAction = (javafx.event.ActionEvent t) -> {
            TranslateTransition trans = new TranslateTransition(Duration.seconds(1), hbox);
            trans.setByX((NUM_OF_IMGS - 1) * IMG_WIDTH);
            trans.setInterpolator(Interpolator.EASE_BOTH);
            trans.play();
        };

        java.util.List<KeyFrame> keyFrames = new ArrayList<>();
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
}*/
