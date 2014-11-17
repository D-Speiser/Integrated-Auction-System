/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * DatabaseConnection Class
 */
import java.sql.*;
import java.util.Calendar;

import javax.swing.*;
/**
 * The DatabaseConnection class allows the user to connect, update, and retrieve 
 * valuable information from the database. This includes creating the database (if
 * it does not exist), storing and updating information in the database, and pulling 
 * information from the database. The user must create an account, or have a current
 * account in order to do so. Their information is personal, and cannot be accessed by
 * anyone other than themselves. 
 * 
 *                          **db is short for database**
 */
public class DatabaseConnection { 
    //connection is the variable used when forming a connection with the db 
    protected static Connection connection;
    //used to store the sequel commands
    protected static String sql;
    //the current user with an established connection
    protected static int currentUserID;
    //used to skip unnecessary steps if db already exists
    /**
     * This method checks whether the db exists before the user is 
     * allowed to connect or attempt to manipulate any information.
     * If the db exists, the user is logged in and connected.
     * If the db DOES NOT exist, the db is created, along with the 
     * necessary user and items tables. 
     * 
     * The String variables are information to connect to the mysql db server, such as
     * the url, db name, driver, username, and password.
     * 
     * @return Prints created new db, and or connection successful
     */
    public static void createDatabase() {
        String url = "jdbc:mysql://localhost:3306/"; //connection host
        String dbName = "ONEline"; //db name
        String driver = "com.mysql.jdbc.Driver"; //connection driver
        String userName = "root"; //server username information
        String password = "root"; //server password information 
        /*
         * The try block attempts to form a connection with the db. Once we
         * establish a connection, check if the db exists. If not, create one,
         * else, just connect normally.
         */
        try { 
            Class.forName(driver).newInstance(); //creates driver instance
            //connects to the mysql server using the given host, username, and password
            connection = DriverManager.getConnection(url,userName,password);
            //creates a statement to allow SEQUEL commands to be executed
            Statement statement = connection.createStatement(); 
            /* checks whether the database "ONEline" exists
             * If it doesn't, the database is created. If it does, it forms a connection.
             */ 
            sql = "CREATE DATABASE IF NOT EXISTS "+dbName+"";
            statement.executeUpdate(sql); //executes database sql
            
            //refresh connection and statement with database included
            connection = DriverManager.getConnection(url+dbName,userName,password);
            statement = connection.createStatement();
            /* Once the database definitely exists (regardless of if it was just
             * created, or if it existed prior to this method call), we check to
             * make sure two tables exits, and if not, they are created. These tables
             * are "users" and "items". The table "users" contains valuable information
             * about a user, such as their ID, username, and password. The table "items"
             * contains all of their auction searches, including the current user ID, 
             * the auction or item ID, the item info, link, price, end date, and if it 
             * was favorited.
             */
            sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id int NOT NULL AUTO_INCREMENT,"
                    + "username varchar(255),"
                    + "password varchar(255),"
                    + "PRIMARY KEY (id)"
                    + ");";
            statement.executeUpdate(sql); //execute "users" table sql
            sql = "CREATE TABLE IF NOT EXISTS items ("
                    + "userID int NOT NULL DEFAULT 0,"
                    + "itemID int NOT NULL DEFAULT 0,"
                    + "itemInfo varchar(255) DEFAULT null,"
                    + "itemLink varchar(255) DEFAULT null,"
                    + "itemPrice varchar(255) DEFAULT null,"
                    + "itemEndDate varchar(255) DEFAULT null,"
                    + "favorites bool DEFAULT 0,"
                    + "PRIMARY KEY (itemID),"
                    + "FOREIGN KEY (userID) REFERENCES users(id)"
                    + ")";
            statement.executeUpdate(sql); //execute "items" table sql
            //informing the user of a successful connection.
            System.out.println("Database Connection Successful.");
        }//try
        //The catch block handles any db connection errors. 
        catch (Exception e) { 
            System.err.println("You cannot connect to the database at this time.");
            e.printStackTrace(); 
        }//catch
    }//createDatabase
    /**
     * This method connect() allows the user to connect to the db in order
     * to log in and search for auctions. 
     * 
     * The String variables are information to connect to the mysql db server, such as
     * the url, db name, driver, username, and password.
     * 
     * @return void Connects to db. No return value. 
     */
    public static void connect() {
        String url = "jdbc:mysql://localhost:3306/"; //connection host
        String dbName = "ONEline"; //db name
        String driver = "com.mysql.jdbc.Driver"; //connection driver
        String userName = "root"; //server username information
        String password = "root"; //server password information 
        //the try block attemps to connect to the db
        try { 
            Class.forName(driver).newInstance(); //creates driver instance
            //connects to the mysql server using the given host, username, and password
            connection = DriverManager.getConnection(url+dbName,userName,password);
        }//try
        //if cannot connect, throw error
        catch (Exception e) {
            System.err.println("Could not connect to the dataase at this time.");
        }//catch
    }//connect
    /**
     * The method createUser is used to create a user, and update his/her
     * information into the db. This method is called when the user, from within the
     * RegisterGUI, hits the register button. There is a check as to whether the 
     * username already exists, and whether the password matches the confirmation
     * password. If no username exists and the passwords match, the account is created.
     * Otherwise there is an error message displaying what went wrong, such as "Username 
     * already exists", or "Passwords don't match". Once the user successfully registers,
     * they are returns to the LoginGUI in order to connect and be allowed to search for
     * auctions. 
     * 
     * @param user: the username that was entered into the registration text box
     * @param password: the password that was entered into registration password box
     * @param confirmPassword: the password is reentered to confirm its accuracy
     * @return void: informs the user of faulty input, or when they are properly connected
     */
    public static void createUser (String user, String password, String confirmPassword)  {
        //make sure there is user input before trying to create an account 
        if (user.equals("") || password.equals("")) { //if any textField is blank
            JOptionPane.showMessageDialog(null, "Please enter a username and password.");
            return;
        }
        
        //db connect
        //connect();///////////////////////////////////////*******************************************************
        
        /*
         * Within this try/catch block, we check the db's existing users, and compare
         * them with the user's input. If the user exits, we inform the user and ask them
         * to try again, otherwise the new username is created. The try is needed while attempting
         * to form the connection and retrieve information from the db. If any errors are thrown,
         * the catch block handles it.  
         */
        try {
            Statement statement = connection.createStatement();
            //selects all username values to be compared with the user input
            ResultSet result = statement.executeQuery("SELECT * FROM users"); 
            //while there is still another username in the db
            while (result.next()) { 
                //if the username already exists, inform the user and allow them to try again
                if (result.getString("username").equalsIgnoreCase(user)) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please try again.");
                    return;
                }//if
            }//while
            
            /*
             * otherwise (if the username doesn't exist), compare the two passwords.
             * IF they match, the user is created, and a message informing them is displayed.
             * ELSE, the user is informed that the passwords don't match and is asked to try again.
             */
            if (password.equals(confirmPassword)) {
                statement.executeUpdate("INSERT INTO users (username, password) VALUES ('"+user+"','"+password+"')");
                JOptionPane.showMessageDialog(null, "Account created successfully. You can now log in.");
                RegisterGUI.registerGUI.setVisible(false); //register gui exits
                LoginGUI.loginGUI.setVisible(true);
            }//if
            //if passwords do NOT match
            else 
                JOptionPane.showMessageDialog(null, "Passwords don't match. Try again."); 
        }//try
        //handles thrown errors from the try block.
        catch (Exception e) { 
            System.out.println("You cannot register at this time.");
            e.printStackTrace();
        }//catch
    }//createUser
    
    /**
     * The method loginAsUser allows the user to properly log in (if their account exists).
     * This method is called when the user, from within the loginGUI, hits the login button.
     * It checks whether there is a valid input, and if there is, it compares the entered
     * username with the usernames in the database. Once the username is found, it then
     * compares the entered password. If they match, the user is logged on. Otherwise the
     * user is informed that they don't match, and to try again. Once the user is properly 
     * logged in, the loginGUI is closed, and the SearchSelectionGUI is opened, allowing
     * the user to begin searching for auctions.
     *  
     * @param user: the username that was entered into the login text box
     * @param password: the password that was entered into login password box
     * @return void: informs the user of faulty input, or when they are properly connected
     */
    public static void loginAsUser (String user, String password) {
        //checks if there is user input before attempting to log in
        if (user.equals("") || password.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter a username and password.");
            return;
        }
        //form a connection with the database
        connect();
        /*
         * Within this try/catch block, we check the db's existing users, and compare
         * them with the user's input. If the user exits, then check the user entered password
         * with the user's password in the db. If they match, it is the correct user, and they can
         * log on. The db is connected to, and the search GUI opens.
         */
        try {
            Statement statement = connection.createStatement(); 
            ResultSet result = statement.executeQuery("SELECT * FROM users"); 
            //bool exists is used to inform the user whether their input exists
            boolean exists = false;
            //while there is still more db usernames to check
            while (result.next()) { 
                //if the user's input exists, set exists to true, and check password
                if (result.getString("username").equalsIgnoreCase(user)) {
                    exists = true;
                    //if user's password matches the information in the db, log in
                    if (result.getString("password").equals(password)) {
                        JOptionPane.showMessageDialog(null, "Login Successful");
                        //get id of current user to store db information properly
                        currentUserID = result.getInt("id");
                        LoginGUI.loginGUI.setVisible(false); //exit login screen
                        SearchSelectionGUI.initializeSearchSelectionGUI(); //open search screen
                    }//if
                    //if user input password doesn't match db password
                    else
                        JOptionPane.showMessageDialog(null, "Invalid password. Please try again.");
                }//if   
            }//while
            //if the user does not exist, inform the user
            if (!exists) 
                JOptionPane.showMessageDialog(null, "Invalid username. Please register.");
        }//try
        //handles any connection and db errors
        catch (Exception e) { 
            System.err.println("You cannot login at this time.");
            e.printStackTrace();
        }//catch
        //The session is automatically stored.
    }//loginAsUser
    /**
     * This method updateAuctions calls upon URLReader's method regex, in order
     * to search for the auctions that the user specifies from the given URL.
     * 
     * @param URL Website that the user is searching on 
     */
    public static void updateAuctions(String URL) {
        //already connected to db, no need to reconnect
        //the try block attempts to access the given URL
        try { 
        URLReader.regex(URL);
        }//try
        //handles any exceptions if the connection fails
        catch (Exception e) {
            System.err.println("Cannot update information from " + URL + " at this time.");
            e.printStackTrace();
        }//catch 
    }//updateAuctions   
    /**
     * This method exportDbToFile() is used to back up any and all information from
     * our database. This method stores each update into a separate file with a current timestamp.
     * This is extremely important in the case of a system crash, a database
     * crash, or if there are recent update errors within a db. This backup method  
     * would then allow all of our user's and table's items to be recovered from our backed-up
     * .txt files. (Recovery method not included).
     * 
     * @param void Saves db information into back-up .txt file
     */
    public static void exportDbToFile () {
        String url = "jdbc:mysql://localhost:3306/"; //connection host
        String dbName = "ONEline"; //db name
        String driver = "com.mysql.jdbc.Driver"; //connection driver
        String userName = "root"; //server username information
        String password = "root"; //server password information 
        /*
         * Knowing the time a file was created means you know exactly when the system
         * was backed up. This is important in case a system crashes, we know the most
         * recent file has the most recent data. Also, if recent data is malformed and 
         * is causing issues, these multiple back-up files allows us to update our db
         * to a prior working state (some information might be lost in this case,
         * but it is better than losing ALL information).
         */
        Calendar calendar = Calendar.getInstance(); //get calandar date for timestamp
        Timestamp time = new Timestamp(calendar.getTime().getTime()); //get current time
        
        //name of file where table "users" will be backed up, including current timestamp
        String filenameUsers = "ONEline_Users_" + time + ".txt";
        //name of file where table "items" will be backed up, including current timestamp
        String filenameItems = "ONEline_Items_"+ time + ".txt"; 
        String tablenameUsers = "users"; //name of table to be backed up to .txt file
        String tablenameItems = "items"; //name of table to be backed up to .txt file
        /*
         * The try block attempts to form a connection with the db. Once we
         * establish a connection, we then try to take all existing information
         * and add it to a .txt file with the current timestamp.
         */
        try { 
            Class.forName(driver).newInstance(); //creates driver instance
            //connects to the mysql server using the given host, db name, username, and password
            connection = DriverManager.getConnection(url+dbName,userName,password);
            //creates a statement to allow SEQUEL commands to be executed
            Statement statement = connection.createStatement(); 
            //retrieve all information from "users" table, and back up into .ONEline_Users_timestamp.txt file
            statement.executeQuery("SELECT * INTO OUTFILE \"" + filenameUsers + "\" FROM " + tablenameUsers);
            //retrieve all information from "items" table, and back up into .ONEline_Items_timestamp.txt file
            statement.executeQuery("SELECT * INTO OUTFILE \"" + filenameItems + "\" FROM " + tablenameItems);
        }//try
        /*
         * The catch block catches errors thrown if a db cannot be connected to, or if 
         * there is any error backing-up the db. 
         */
        catch (Exception e) {
            System.err.println("Could not back-up information to .txt at this time." );
            System.err.println("Check that the selected save directory \"/usr/local/var/mysql\" is correct");
            e.printStackTrace();
        }//catch
    }//exportDbToFile
}//DatabaseConnection
