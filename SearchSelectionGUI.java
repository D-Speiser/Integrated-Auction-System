/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * SearchSelectionGUI
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * The searchSelectionGUI class initializes the search and display windows. This searchSelection GUI
 * allows the user to select which auction websites he/she would like to search from, as well
 * as to enter their desired search. This search method allows the user to search, display, and save
 * their favorite auctions. 
 *
 * The default "enter" button is the search button.
 */
public class SearchSelectionGUI extends LoginGUI {
    //initialize window and panel components
    protected static JFrame searchSelectionGUI = new JFrame("ONEline Auctions"); //main window named "ONEline Auctions"
    protected static JPanel panel = new JPanel(); //panel within window to display information
    //the checkbox allows you to select one or more websites to search from
    protected static JCheckBox checkBox1 = new JCheckBox("Goodwill"); //checkbox named "Goodwill"
    protected static JLabel moreToCome = new JLabel("More to come."); //text label displaying "More to come."
    //initialize button components
    protected static JButton searchButton = new JButton("Search"); //button that displays "Search"
    /*
     * Initializes text and password fields which allow the user to input information
     * Text field displays the input information as is
     * Password field displays a "*" for every character, to prevent security and privacy issues.
     */
    protected final static JTextField searchTextField = new JTextField();
    /**
     *   This method, initializeSearchSelectionGUI, initializes the auction search GUI,
     *   and calls upon the displayGUI to be initialized. This allows whatever the user
     *   searched to be updated within the db, as well as to be displayed for the user to
     *   search and favorite auctions.  
     *   This method adds and sets the layout of the different GUI components. This includes 
     *   the different text areas, checkboxes, and buttons necessary in order for the user to 
     *   be able to search and display items.  
     *   
     *   @return void Initializes and displays both the searchSelectionGUI and displayGUI. No return value.
     */
    public static void initializeSearchSelectionGUI() {
        //sets size, location, and default operations of main window
        searchSelectionGUI.setSize(300, 125); //sets size
        searchSelectionGUI.setLocationRelativeTo(null); //centers the window
        searchSelectionGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits when hit "x" or exit button
        searchSelectionGUI.setContentPane(panel); //sets the panel as the window's main display
        
        //set checkBoxes and their location on the panel
        panel.add(checkBox1); //add goodwill checkbox1 to panel
        panel.add(moreToCome); //add "More to come." label to panel
        //sets the checkbox layout to be aligned left, with checkboxes vertical to each other
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        
        //sets sizes of password text and field, and adds them to be displayed
        searchTextField.setPreferredSize(new Dimension(300, 40));
        panel.add(searchTextField); //add password field to panel
        
        //Set searchButton size, and add to GUI
        searchButton.setPreferredSize(new Dimension(100, 40)); //sets button dimensions
        panel.add(searchButton); //add button to panel for display
        //sets searchButton as the "default enter button"
        searchSelectionGUI.getRootPane().setDefaultButton(searchButton); //searchButton KeyListener (enter button)
        /*
         * This is the action listener for the searchButton. Whenever the "Search"
         * button is pressed, whether via pressing the "enter" button, or by clicking it, 
         * the action performed will be to search the selected websites, update the information
         * in the db, and also display the fetched information. This action listener calls upon the 
         * updateAuctions method within the DatabaseConnection class, which eventually calls upon the 
         * regex method within the URLReader class.
         */
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //make sure the display window is open
                DisplayGUI.displayGUI.setVisible(true);
                if (checkBox1.isSelected()) { //search specific websites based on which checkbox is selected
                    //their plaintext search uses "+" for space, so the search string is updated accordingly
                    String search = searchTextField.getText().replaceAll(" ", "+"); //updates proper search string
                    DatabaseConnection.updateAuctions("http://shopgoodwill.com/search/SearchKey.asp?itemTitle="
                            + search +"&catid=0&sellerID=all&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&"
                                    + "SortOrder=a&showthumbs=on");
                }//if
                else //if no boxes are selected, inform the user that they must select a website to search
                    JOptionPane.showMessageDialog(null, "No checkboxes selected. Please select a website to search from.");
                //checks if there is more than one page to search
                if (URLReader.numPages > 1) {
                    for (int i = 2; i <= URLReader.numPages; i++) { //already searched page one. Start at page two.
                        //searches the all auctions related to the input search within the searchTextField
                        String search = searchTextField.getText().replaceAll(" ", "+");
                        DatabaseConnection.updateAuctions("http://shopgoodwill.com/search/SearchKey.asp?itemTitle="
                                + search +"&catid=0&sellerID=all&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&"
                                + "SortOrder=a&showthumbs=on&page=" + i);
                    }//for            
                }//if
                //updates the tables after the search is complete
                DisplayGUI.refresh();
                //save new updated db information to .txt. file 
                DatabaseConnection.exportDbToFile();
            }//actionPerformed
        });
        //makes the window visible to the user once components are initialized and set
        searchSelectionGUI.setVisible(true); //allows the user to see the gui after it's initialized
        //initialized the DisplayGUI, allowing the searched auctions to be displayed
        DisplayGUI.initializeDisplayGUI();
    }//initializeSearchSelectionGUI
}//SearchSelectionGUI
