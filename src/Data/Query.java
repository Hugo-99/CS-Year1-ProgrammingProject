package Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Data structure for queries sent to SQLInterface's query method
 * Author: Stefan
 * Adam: Searching by date
 */
public class Query {

    // Sort constants
    public static final int SORT_BY_LATEST = 0;
    private static final int SORT_BY_OLDEST = 1;
    private static final int SORT_BY_HIGHEST_SCORE = 2;

    public static final int TABLE_STORIES = 0;
    public static final int TABLE_COMMENTS = 1;
    public static final int TABLE_USERS = 2;
    public static final int TABLE_DATES = 3;
    private static final int QUERY_RESULT_SIZE = 20;
    public static Query LastQuery;

    private int sortBy;
    public int nextStart;
    boolean[] tables;
    private String searchQuery;

    /**
     * Query Constructor
     * Author: Stefan
     *
     * @param searchQuery   String of keywords to search for in the database
     * @param sortBy        Sort field (defined by the SORT_BY_ constants)
     * @param tables        Array of tables to search for (each index defined by the TABLE_ constants detemines whether
     *                          that table is included)
     */
    public Query(String searchQuery, int sortBy, boolean[] tables) {
        this.searchQuery = searchQuery;
        this.sortBy = sortBy;
        this.tables = tables;
        this.nextStart = 0;
        LastQuery = this;
    }

    /**
     * Returns an SQL query based on the filters passed in the constructor. Can search by title, url, user or date and sort by date or highest score.
     * Authors: Adam and Stefan
     *
     * @return Formatted SQL Query
     */
    public String toString() {
        String output="";
        if(tables[TABLE_STORIES]) {
            if(!tables[TABLE_USERS] && !tables[TABLE_DATES])
                output = "SELECT * FROM stories WHERE title LIKE '%" + searchQuery + "%' OR url LIKE '%" + searchQuery + "%' ORDER BY ";
            else if(tables[TABLE_USERS])
                output = "SELECT * FROM stories WHERE author LIKE '%" + searchQuery + "%' ORDER BY ";
            else if(tables[TABLE_DATES])
                try {
                    String[] dates = searchQuery.split("-");
                    for(String date : dates) {
                        date.trim();
                    }
                    if(dates.length>1)
                        output = "SELECT * FROM stories WHERE date BETWEEN " +  (new SimpleDateFormat("dd/MM/yyyy").parse(dates[0])).getTime()/1000 + " AND " + (((new SimpleDateFormat("dd/MM/yyyy").parse(dates[1])).getTime()/1000)+24*60*60-1) + " ORDER BY ";
                    else if( dates.length == 1)
                        output = "SELECT * FROM stories WHERE date BETWEEN " +  (new SimpleDateFormat("dd/MM/yyyy").parse(dates[0])).getTime()/1000 + " AND " + (((new SimpleDateFormat("dd/MM/yyyy").parse(dates[0])).getTime()/1000)+24*60*60-1) + " ORDER BY ";
                }catch (java.text.ParseException e) {
                    System.out.println("Not a valid date.");
                }
            switch (sortBy) {
                case SORT_BY_LATEST:
                    output += "Date DESC LIMIT 20";
                    break;
                case SORT_BY_OLDEST:
                    output += "Date ASC LIMIT 20";
                    break;
                case SORT_BY_HIGHEST_SCORE:
                    output += "Score DESC LIMIT 20";
                    break;
                default:
                    output += "Date DESC LIMIT 20";
                    break;
            }
        } else {
            if(!tables[TABLE_USERS] && !tables[TABLE_DATES])
                output = "SELECT * FROM comments WHERE text LIKE '%" + searchQuery + "%' ORDER BY ";
            else if(tables[TABLE_USERS])
                output = "SELECT * FROM comments WHERE author LIKE '%" + searchQuery + "%' ORDER BY ";
            else if(tables[TABLE_DATES])
                try {
                    output = "SELECT * FROM comments WHERE date LIKE '%" +  (new SimpleDateFormat("dd-MM-yyyy").parse(searchQuery)).getTime() + "%' ORDER BY ";
                }catch (java.text.ParseException e) {
                    System.out.println("Not a valid date.");
                }
            switch (sortBy) {
                case SORT_BY_LATEST:
                    output += "Date DESC";
                    break;
                case SORT_BY_OLDEST:
                    output += "Date ASC";
                    break;
                case SORT_BY_HIGHEST_SCORE:
                    output += "Score DESC";
                    break;
                default:
                    output += "Date DESC";
                    break;
            }
            output +=  " LIMIT " + nextStart + "," + QUERY_RESULT_SIZE;
        }

        return output;
    }

    /**
     * Increments the start location of the next query
     * Author: Stefan
     */
    public void queryComplete() {
        nextStart += QUERY_RESULT_SIZE;
    }
}