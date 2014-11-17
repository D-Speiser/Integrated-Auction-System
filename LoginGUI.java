/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * LoginGUI Class
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * This class, LoginGUI, is the MAIN class. The LoginGUI class initializes the
 * General User Interfaces, or GUI, and allows the user to log into the database (db).
 * The components of the GUI include a JFrame (the display window), text labels, 
 * username and password input fields, and a login and register button. If the user has
 * an account, they can enter their information and log on. Otherwise, the user must register
 * (as will be informed if the account the user tries to log in with does not exist), and 
 * must click on the "Register" button. If they click on the register button, the LoginGUI will
 * close, and LoginGUI will call upon the RegisterGUI to open.
 * 
 * The default "enter" button is to Login.
 */
public class LoginGUI extends JFrame {
    //initialize window and panel components
    protected static JFrame loginGUI = new JFrame("User Login"); //main window named "User Login".
    protected static JPanel panel = new JPanel(); //panel within window to display information
    protected static JLabel username = new JLabel("Username:"); //text label displaying "Username:"
    protected static JLabel password = new JLabel("Password:"); //text label displaying "Password:"
    //initialize button components
    protected static JButton loginButton = new JButton("Login"); //button that displays "Login"
    protected static JButton registerButton = new JButton("Register"); //button that displays "Register"
    /*
     * Initializes text and password fields which allow the user to input information
     * Text field displays the input information as is
     * Password field displays a "*" for every character, to prevent security and privacy issues.
     */
    protected final static JTextField usernameTextField = new JTextField(); //username text field
    protected final static JPasswordField passwordTextField = new JPasswordField(); //password field
    /**
     *   The main method initializes the loginGUI. This method adds and 
     *   sets the layout of the different GUI components. This includes 
     *   the different text areas and buttons necessary in order for the user to be able to 
     *   login and register.  
     *   
     *   @param String [] args Default main parameter
     *   @return void Initializes and displays GUI. No return value.
     */
    public static void main(String [] args) {
        //forms a connection with the database in order for the user to log in
        DatabaseConnection.createDatabase();
        
        //sets size, location, and default operations of main window
        loginGUI.setSize(400, 175); //sets size
        loginGUI.setLocationRelativeTo(null); //centers the window
        loginGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits when hit "x" or exit button
        loginGUI.setContentPane(panel); //sets the panel as the window's main display
        
        //sets sizes of username text and field, and adds them to be displayed
        loginGUI.add(username); //add "Username:" text label
        usernameTextField.setPreferredSize(new Dimension(300, 40)); //sets text box size
        panel.add(usernameTextField); //add text field to panel
        
        //sets sizes of password text and field, and adds them to be displayed
        loginGUI.add(password); //add "Password:" text label
        passwordTextField.setPreferredSize(new Dimension(300, 40)); //sets password field size
        panel.add(passwordTextField); //add password field to panel
        
        //Set loginButton size, and add to GUI
        loginButton.setPreferredSize(new Dimension(180, 50)); //set button size
        panel.add(loginButton); //add button to panel for display
        //sets loginButton as the "default enter button"  
        loginGUI.getRootPane().setDefaultButton(loginButton); //loginButton KeyListener (enter button)
        
        //Set registerButton size, and add to GUI
        registerButton.setPreferredSize(new Dimension(180, 50)); //set button size
        panel.add(registerButton); //add button to panel for display
        /*
         * This is the action listener for the loginButton. Whenever the "Login"
         * button is pressed, whether via pressing the "enter" button, or by clicking it, 
         * the action performed will be to connect to the database, and attempt to log in
         * as the specified user. The loginAsUser method inside the DatabaseConnection class
         * will handle erroneous input, or faulty connections.
         */
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //password fields are stored as char arrays. This converts it to a string.
                String password = new String(passwordTextField.getPassword());
                //calls the loginAsUser method to try to login with the given information
                DatabaseConnection.loginAsUser(usernameTextField.getText() , password);
            }
        });
        /*
         * This is the action listener for the registerButton. Whenever the "Register"
         * button is CLICKED, the action performed will be to initialize the registerGUI.
         * This allows the registerGUI to open and display, and allows the user to register
         * an account. The loginGUI window is then hidden (closed). 
         */
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //calls upon RegisterGUI's initializeRegisterGUI method to display register window
                RegisterGUI.initializeRegisterGUI(); 
                loginGUI.setVisible(false); //closes login window
            }
        });
        //makes the window visible to the user once components are initialized and set
        loginGUI.setVisible(true);
    }//Main
}//loginGUI