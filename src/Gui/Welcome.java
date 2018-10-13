package Gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

public class Welcome extends JFrame { //extends Application {

    public static final String FRAME_NAME = "CASTRO - WELCOME";
    public static final String OPEN_PHOTO = "/src/images/openphoto.png";
    public static final String BUTTON_ENTER_LOGO = "/src/images/enterLogo.png";
    public static final String CASTRO_LOGO = "/src/images/castroLogoJPG.png";
    public static final String CREATORS_LOGO = "/src/images/Creators.png";
    public static final String CASTRO_ICON = "/src/images/icon.png";
    public static final String JOPTIONPANE_MESSAGE = "Are you sure you want to close the terminal?";
    public static final String JOPTIONPANE_TITLE = "CASTRO - CLOSE TERMINAL";
    public static final int DELAY_TIME  = 30;
    public static final int ENTER_BUTTON_WIDTH = 500;
    public static final int ENTER_BUTTON_HEIGHT = 75;

    private int colorIdx = 255;
    private Timer timer;
    private Dimension screenSize;

    private JPanel jPanelData;
    private SpringLayout springLayout;
    private JLabel castroLogoLabel, creatorsLogoLabel, openPhotoLabel;
    private ImageIcon castroLogoJPG, enterLogoJPG, creatorsLogoJPG, openPhotoJPG;
    private JButton jButtonEnter;


    public Welcome(){

        SetGUIComponents();
    }

    private void SetGUIComponents() {

        jPanelData = new JPanel();
        springLayout = new SpringLayout();

        castroLogoJPG = new ImageIcon(getClass().getResource(CASTRO_LOGO));
        castroLogoLabel = new JLabel(castroLogoJPG);

        openPhotoJPG = new ImageIcon(getClass().getResource(OPEN_PHOTO));
        openPhotoLabel = new JLabel(openPhotoJPG);

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

        springLayout.putConstraint(SpringLayout.WEST,jButtonEnter,(screenSize.width)/2 - 270,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH,jButtonEnter,(screenSize.height)/2 + 280,SpringLayout.NORTH,jPanelData);

        springLayout.putConstraint(SpringLayout.WEST,castroLogoLabel,-190,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.NORTH,castroLogoLabel,-40,SpringLayout.NORTH,jPanelData);

        springLayout.putConstraint(SpringLayout.WEST,creatorsLogoLabel,3,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.SOUTH,creatorsLogoLabel,3,SpringLayout.SOUTH,jPanelData);

        springLayout.putConstraint(SpringLayout.WEST,openPhotoLabel,500,SpringLayout.WEST,jPanelData);
        springLayout.putConstraint(SpringLayout.SOUTH,openPhotoLabel,0,SpringLayout.SOUTH,jPanelData);
    }

    private void GUIDefineLayoutAndAddComponentsOnJPanel() {

        jPanelData.setLayout(springLayout);
        jPanelData.add(jButtonEnter);
        jPanelData.add(castroLogoLabel);
        jPanelData.add(creatorsLogoLabel);
        jPanelData.add(openPhotoLabel);
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
