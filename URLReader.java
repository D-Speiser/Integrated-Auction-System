/**
 * ONEline Auctions
 * 
 * CS370 - Project
 * Summer Session 2014
 * Professor Goldberg
 * 
 * @author Daniel Speiser
 * 
 * URLReader Class
 */
import java.net.*;
import java.sql.*;
import java.util.regex.*;
import java.io.*;

import javax.swing.*;
/**
 * The URLReader class takes in a URL, gets its HTML code, then stores it as a string. It then
 * performs a regex search for matching string to retrieve information about the auctions. As it retrieves
 * the information, it stores/updates it into the database.
 */
public class URLReader {
    
    protected static String html = ""; //used to store the html code for searches
    protected static String sql; //used to store the sequel commands
    protected static int numPages; //used to store the number of pages resulting from the search
    /**
     * The regex method takes in a URL, gets its HTML code, then stores it as a string. It then
     * performs a regex search for matching string to retrieve information about the auctions. As it retrieves
     * the information, it stores/updates it into the database.
     * 
     * @param URL: The given URL to be extracted into HTML and searched
     * @return void: Searches through the HTML code. No return type.
     */
    public static void regex(String URL) throws Exception {
        URL goodwill = new URL(URL); //passed URL that the HTML will be extracted from
        //buffered reader to retrieve all HTML code from the specified website
        BufferedReader in = new BufferedReader(new InputStreamReader(goodwill.openStream()));
        //reads through each line of code from the URL
        String inputLine; //line of code/input read from the URL
        while ((inputLine = in.readLine()) != null) //while there is still another line to read
            html += inputLine; //builts the HTML string to eventually be searched
        in.close(); //closes the URL connection/stream once there are no more lines to read
        
        //Pattern to find auction ID
        Pattern idPattern = Pattern.compile(">([0-9]{8})</th>");
        
        //Pattern to find auction link
        Pattern linkPattern = Pattern.compile("<a href=\"(http://www.shopgoodwill.com/auctions/.*?html)\">");
        
      //Pattern to find auction title
        Pattern titlePattern = Pattern.compile(".html\">(.*?)</a>");
        
        //Pattern to find auction price
        Pattern pricePattern = Pattern.compile("<b>(\\$[0-9]*.[0-9]{2})</b>");
        
        //Pattern for auction date and time (US standard)
        Pattern dateEndsPattern = Pattern.compile("<th scope=\"row\">((0?[1-9]|1[012])/"
                + "(0?[1-9]|1[0-9]|2[0-9]|3[01]).*?)</th>");
        /*
         * Pattern for date ending soon. ending soon is used whenever the remaining auction
         * time is an hour or less the date is no longer displayed, only a time.
         * This is needed only for those specific auctions.
         */
        Pattern endingSoonPattern = Pattern.compile("<font color=red>(in [0-9]{1,2} minutes)</font>");
       
        /*
         * Pattern to find the number of items from a specific search. This allows us to know how many
         * pages of results there are, as the website displays 25 auctions per page.
         */
        Pattern numItemsPattern = Pattern.compile("<h1 class=\"whitetext\">([0-9]*?.*[0-9].*) items found, showing ");
        
        //Matchers look for the specified patterns
        Matcher idMatcher = idPattern.matcher(html); //Find auction ID
        Matcher linkMatcher = linkPattern.matcher(html); //find auction link
        Matcher titleMatcher = titlePattern.matcher(html); //find auction title
        Matcher priceMatcher = pricePattern.matcher(html); //find auction price
        Matcher dateEndsMatcher = dateEndsPattern.matcher(html); //find auction date ending
        Matcher endingSoonMatcher = endingSoonPattern.matcher(html); //find auctions ending soon
        //find number of pages to retrieve information from
        Matcher numItemsMatcher = numItemsPattern.matcher(html);
        /*
         * The try block attempts to form a connection with the database, and 
         * attempts to find matches to the patterns. Once it finds matches, it
         * outputs the results, and stores them in the database. This allows them
         * to be read and displayed onto the DisplayGUI.
         */
        try {
            //form a statement from the current connection
            Statement statement = DatabaseConnection.connection.createStatement();  
            int count = 0; //count is used to determine if there are no matches on the first attempt
            while(true) {
                //Current user ID stored in the db with the soon to be retrieved information  
                Integer userID = DatabaseConnection.currentUserID; 
                Integer itemID = null; //default itemID is null if not found
                String link = null; //default link is null if not found
                String title = null; //default title is null if not found
                String price = null; //default price is null if not found
                String dateEnds = null; //default date ends is null if not found
                //Attempts to find id patterns to be saved to db
                if (idMatcher.find()) { //if there exists an id pattern
                    itemID = Integer.parseInt(idMatcher.group(1)); //parse and store itemID to Integer 
                    System.out.print(idMatcher.group(1)); //output the match to console
                } //if 
                else { //if there are no matches found, matches are completed, or none were found
                    if (count == 0) { //if no matches were found on first attempt, our search yielded no matches
                        JOptionPane.showMessageDialog(null, "Your search yields no matches."); //output no match
                    } //if 
                    return; //return to attempt another search
                } //else
                
                if (linkMatcher.find()) { //if there exists a link pattern
                    link = linkMatcher.group(1); //store link in local variable to be stored in db
                    System.out.print("\t" + linkMatcher.group(1)); //output the match to console
                } //if
                
                if (titleMatcher.find()) {
                    /*
                     * SQL injection escape removes any potential escape characters
                     */
                    //store title in local variable to be stored in db
                    title = titleMatcher.group(1).replaceAll("[',`,=,\\\\]", "");
                    System.out.print("\t" + titleMatcher.group(1));//output the match to console
                } //if
                
                if (priceMatcher.find()) {
                    price = priceMatcher.group(1); //store price in local variable to be stored in db
                    System.out.print("\t" + priceMatcher.group(1));//output the match to console
                } //if
                
                if (endingSoonMatcher.find()) {
                    dateEnds = endingSoonMatcher.group(1); //store time in local variable to be stored in db
                    System.out.println("\t" + endingSoonMatcher.group(1));//output the match to console
                } //if
                //else if pattern is not an ending soon match, check for regular date ends match
                else if (dateEndsMatcher.find()) {
                    dateEnds = dateEndsMatcher.group(1);//store date in local variable to be stored in db
                    System.out.println("\t" + dateEndsMatcher.group(1));//output the match to console
                } //else if
                
                else //otherwise break
                    break;
                /*
                 * Stores the auction matches into the database. The values passed to the db are the
                 * user ID, item ID, title, link, price, end date, and a default value of 0 for favorites. 
                 * Favorites could later be changed if the user wants to save a specific auction.
                 */
                sql = "REPLACE INTO `items` " + 
                        "VALUES ('"+userID+"', '"+itemID+"', '"+title+"', '"+link+"', '"+price+"', '"+dateEnds+"', 0)";
                //execute the sql string statement, as described above;
                statement.executeUpdate(sql);
                count++; //increment the count, so we know that the match yielded results
            }//while
            /*
             * We must check how many pages of results there are, and extract all auction
             * information from every page. The default number of auction items listed per
             * page is 25. We divide the number of items found, divide by 25, and take the 
             * ceiling function of the number to round up to the next page.   
             */
            numItemsMatcher.find(); //find number of total auction items from the search
            //find number of items, and remove unnecessary commas from larger numbers
            int numItems = Integer.parseInt(numItemsMatcher.group(1).replaceAll(",",""));
            numPages = (int)Math.ceil((double)numItems/25); //find out the number of pages
        }//try
        /*
         * Catches any errors thrown while attempting to connect to the db, store to the db,
         * or during pattern searching.
         */
        catch (Exception e) { 
            System.err.println("You cannot update at this time.");
            e.printStackTrace();
        }//catch 
    }//Regex
}//URLReader