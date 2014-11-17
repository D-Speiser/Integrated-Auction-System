/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * RegisterGUI Class
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * The RegisterGUI class initializes the registration form. This registration form
 * allows the user to create an account with our software. This form checks whether the 
 * user input already exists within the database, and if not, an account is created (if the
 * password and confirm passwords match,and other criteria are met). This class is called 
 * by the loginGUI, once the user hits the registration button. Once the user registers
 * with the db, the RegisterGUI window is closed, and the user is allowed to log in with
 * their new account.
 *
 * The default "enter" button is to Register.
 */
public class RegisterGUI extends JFrame {
    //initialize window and panel components
    protected static JFrame registerGUI = new JFrame("Registration Form"); //main window named "Registration Form"
    protected static JPanel panel = new JPanel(); //panel within window to display information
    protected static JLabel username = new JLabel(); //text label displaying "Username:"
    protected static JLabel password = new JLabel(); //text label displaying "Password:"
    protected static JLabel confirmPassword = new JLabel(); //text label displaying "Confirm Password:"
    //initialize button components
    protected static JButton registerButton = new JButton("Create Account"); //button that displays "Create Account"
    //button allows user to leave registration form and go back to login window
    protected static JButton backButton = new JButton("Back to Login"); //button that displays "Back to Login"
    /*
     * Initializes text and password fields which allow the user to input information
     * Text field displays the input information as is
     * Password fields display a "*" for every character, to prevent security and privacy issues.
     */
    protected final static JTextField usernameTextField = new JTextField(); //username text field
    protected final static JPasswordField passwordTextField = new JPasswordField(); //password field
    protected final static JPasswordField confirmPasswordTextField = new JPasswordField(); //confirm password field
    /**
     *   This method, initializeRegisterGUI, initializes the registration form. 
     *   This method adds and sets the layout of the different GUI components. This includes 
     *   the different text areas and buttons necessary in order for the user to be able to 
     *   register to the db, and to eventually log in and search for auctions.  
     *   
     *   @return void Initializes and displays register GUI. No return value.
     */
    public static void initializeRegisterGUI() {
        //sets size, location, and default operations of main window
        registerGUI.setSize(440, 225); //sets size
        registerGUI.setLocationRelativeTo(null); //centers the window
        registerGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits when hit "x" or exit button
        registerGUI.setContentPane(panel); //sets the panel as the window's main display
        
        //sets sizes of username text and field, and adds them to be displayed
        username.setText("             Username:"); //set text label to display "Username:"
        registerGUI.add(username);  //add "Username:" text label to panel
        usernameTextField.setPreferredSize(new Dimension(300, 40)); //sets size
        panel.add(usernameTextField); //add username text field to panel
        
        //sets sizes of password text and field, and adds them to be displayed
        password.setText("             Password:"); //set text label to display "Password:"
        registerGUI.add(password); //add password label to panel
        passwordTextField.setPreferredSize(new Dimension(300, 40)); //sets size
        panel.add(passwordTextField); //add password field to panel
        
        //sets password confirmation text and field
        confirmPassword.setText("Confirm Password:"); //set text label to display "Confirm Password:"
        registerGUI.add(confirmPassword); //add confirm password label to panel
        confirmPasswordTextField.setPreferredSize(new Dimension(300, 40)); //sets size
        panel.add(confirmPasswordTextField); //add confirm password field to panel
           
        //Set registerButton size, and add to GUI
        registerButton.setPreferredSize(new Dimension(200, 55)); //set button size
        panel.add(registerButton); //add button to panel for display
        //sets registerButton as the "default enter button"
        registerGUI.getRootPane().setDefaultButton(registerButton); //registerButton KeyListener (enter button)
        /*
         * This is the action listener for the registerButton. Whenever the "Register"
         * button is pressed, whether via pressing the "enter" button, or by clicking it, 
         * the action performed will be to connect to the database, and attempt to register
         * the user with the input information. The create User method inside the DatabaseConnection 
         * class will handle erroneous input, or faulty connections.
         */
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              //password fields are stored as char arrays. This converts it to a string.
                String password = new String(passwordTextField.getPassword()); //password string
                String confirmPassword = new String(confirmPasswordTextField.getPassword()); //confirmPassword String
              //calls the createUser method to try to register an account with the given information
                DatabaseConnection.createUser(usernameTextField.getText() , password, confirmPassword);
            }
        });
        //Set backButton size, and add to GUI
        backButton.setPreferredSize(new Dimension(200, 55)); //set button size
        panel.add(backButton); //add button to panel for display
        /*
         * This is the action listener for the backButton. When the backButton is pressed,
         * the user will be sent back to the login form. This is there in case a user forgot
         * that he/she has previously signed up, or if they accidentally hit the register button. 
         */
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerGUI.setVisible(false); //closes registration form window
                LoginGUI.loginGUI.setVisible(true); //opens login window
            }
        });
        //makes the window visible to the user once components are initialized and set
        registerGUI.setVisible(true); 
    }//Main
}//registerGUI
