/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * DisplayGUI Class
 */
import java.awt.BorderLayout;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * The DisplayGUI class is the class which initializes the auction display window.
 * This class connects to the database, and displays all auction information for
 * that specific user.
 * This auction display window has two tabs: one for all of the user's auctions,
 * and another for just the auctions that they favorited.The user can update and 
 * search for more auctions as he/she pleases, as well as favorite and unfavorite 
 * auctions by double clicking any specific item in the table.
 */
public class DisplayGUI extends JFrame {
    //main "ONEline Auctions" frame
    protected static JFrame displayGUI = new JFrame("ONEline Auctions");
    //pane with multiple tabs; one for all auctions, and another for favorites
    protected static JTabbedPane pane = new JTabbedPane(); 
    //two separate panels for each tab
    protected static JPanel all = new JPanel(); //all auctions panel
    protected static JPanel favorites = new JPanel(); //favorite auctions panel
    //tables that store and display all auction information
    protected static JTable tableAll = new JTable(); //all auctions table 
    protected static JTable tableFavorites = new JTable(); //favorite auctions table
    //the model and layout of the two tables
    protected static DefaultTableModel modelAll; //all auctions model
    protected static DefaultTableModel modelFavorites; //favorite auctions model
    //vectors to store data as retrieved from database
    protected static Vector columnNames = new Vector(); //vector for column names
    protected static Vector dataAll = new Vector(); //vector for all auction data
    protected static Vector dataFavorites = new Vector(); //vector for favorite auction data
    //button to refresh user's auctions
    protected static JButton refreshButton = new JButton("Refresh");
    //sql used to execude updates
    protected static String sql;
    //used to execute updates of favorited and unfavorited auctions
    protected static Statement statement;
    /**
     * The initializeDisplayGUI() method initializes the GUI window, displaying 
     * two separate tabs, one with all auctions table, and another with favorite
     * auctions table.
     * This method connects to the database, and displays all auction information for
     * that specific user. The user can update and search for more auctions as he/she 
     * pleases, as well as favorite and unfavorite auctions by double clicking any 
     * specific item in the table
     * 
     * @return void: Initializes GUI and its components. No return type.
     */
    public static void initializeDisplayGUI() {
        displayGUI.setSize(500,500); //sets window size
        displayGUI.getContentPane().add(pane); //adds pane to window
        //adds separate tabs: one to display all auctions, the other for favorites
        pane.addTab("All Auctions", all); //all auctions tab
        pane.addTab("Favorite Auctions", favorites); //favorites auctions tab
        //retrieves and refreshes all information from the db and displays in GUI
        refresh();
        //sets the jtable to un-editable. Only displays information, doesn't edit.
        modelAll = new DefaultTableModel(dataAll, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }//isCellEditable
        };//DefaultTableModel
        //sets the jtable to un-editable. Only displays information, doesn't edit.
        modelFavorites = new DefaultTableModel(dataFavorites, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }//isCellEditable
        };//DefaultTableModel
        //action listener to place or remove auction in favorites if double clicked
        tableAll.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow() +1;
                    int column = target.getSelectedColumn();
                    try { 
                        sql = "SELECT CASE WHEN favorites = 0 AND ROW_NUMBER() = "+row+" THEN 1 ELSE 0 "
                                + "END AS result1 FROM items";
                            statement = DatabaseConnection.connection.createStatement();
                            statement.executeQuery(sql);
                            System.out.println("Auction at row " +row+ " was saved.");
                        }//try
                    catch (Exception err) {
                    }//catch
                } //if
            } //mouseClicked
        }); //MouseListener
        //action listener to place or remove auction in favorites if double clicked
        tableFavorites.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    try { 
                        sql = "SELECT CASE WHEN favorites = 1 AND ROW_NUMBER() = "+row+" THEN 0 ELSE 1 "
                                + "END AS result1 FROM items";
                            statement = DatabaseConnection.connection.createStatement();
                            statement.executeQuery(sql);
                            System.out.println("Auction at row " +row+ " was removed.");
                        }//try
                    catch (Exception err) {
                    }//catch
                } //if
            } //mouseClicked
        }); //MouseListener
        //refreshes the current information displayed in the jtable
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refresh(); //retrieves new information and data
                modelAll.fireTableDataChanged(); //refreshes the all auction table
                modelFavorites.fireTableDataChanged(); //refreshes the favorites auction table
            } //actionPerformed
        }); //action listener
        //set the tables with specific models
        tableAll.setModel(modelAll); //tableAll model
        tableFavorites.setModel(modelFavorites); //tableFavorites model
        //adds components of tables, panes, and scrollpanes to GUI
        all.add(new JScrollPane(tableAll)); //add tableAll to all auctions pane
        favorites.add(new JScrollPane(tableFavorites)); //add tableFavorites to favorites auction pane
        displayGUI.add(refreshButton, BorderLayout.SOUTH); //place the refresh button on the bottom
        displayGUI.add(pane); //add the tabbed pane to the main displayGUI

        displayGUI.setVisible(true); //allow the window to be seen
    } //initializeDisplayGUI

    public static void refresh() {
        //clear current vector data to retrieve new data
        columnNames.clear(); //clear column names
        dataAll.clear(); //clear all auction data
        dataFavorites.clear(); //clear favorites auction data
        /*
         * The try block attempts to form a statesment from the current connection 
         * with the db. Once we establish a connection, we retrieve all user auction 
         * information.
         */
        try { 
            //create new statement from current connection
            Statement statement = DatabaseConnection.connection.createStatement(); 
            //result to select all items from current user ID
            ResultSet res = statement.executeQuery("SELECT * FROM items WHERE userID = "
                    + ""+DatabaseConnection.currentUserID+"");
            ResultSetMetaData rsmd = res.getMetaData(); //get metadata
            int column = rsmd.getColumnCount(); //get column count
            //adds column names to table
            columnNames.addElement("User ID"); //add User ID column
            columnNames.addElement("Item ID"); //add Item ID column
            columnNames.addElement("Description"); //add Description column
            columnNames.addElement("Link"); //add Link column
            columnNames.addElement("Price"); //add Price column
            columnNames.addElement("Date Ends"); //add Date Ends column
            //while there are more results to retrieve from database
            while(res.next()) {
                Vector row = new Vector(column); //create new row of information
                for(int i=1; i<=column; i++) //traverse through all columns
                    row.addElement(res.getObject(i)); //add information to each column
                dataAll.addElement(row); //store the information back into the All table
            } //while
          //result to select all FAVORITE items from current user ID 
            res = statement.executeQuery("SELECT * FROM items WHERE favorites=1 AND userID = "
                    + ""+DatabaseConnection.currentUserID+"");
            //while there are more results to retrieve from database
            while(res.next()) {
                Vector row = new Vector(column); //create new row of information
                for(int i = 1; i <= column; i++) //traverse through all columns
                    row.addElement(res.getObject(i)); //add information to each column
                dataFavorites.addElement(row); //store the information into the Favorites table
            }//while
        }//try
        catch (Exception e) { //catch any thrown exceptions
            System.err.println("Cannot refresh data at this time");
        }//catch
    }//refresh
}//DisplayGUI