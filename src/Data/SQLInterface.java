package Data;

import Main.Event;
import Main.Project;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;

import static Main.Utils.*;

/**
 * Class that manages all data passed between the program and the SQL server
 *
 * Stefan Hutanu: Embedded SQL startup
 *                SQL Connection
 *                JSON File reading
 *                User data creation
 *                Author parsing and SQL insertion
 *
 * Adam Ogorek:   Comment parsing
 *                Dictionary population
 *                Getting most popular websites, number of all stories and number of posts per hour.
 */
public class SQLInterface {
    private static final boolean DISABLE_AUTOCOMPLETE = true;
    private Project main;
    private double loadProgress;
    private final String DATA_URL = "small.json";
    private final String DB_NAME = "pp";

    private final int STATE_CONNECTING_TO_MYSQL = 0;
    private final int STATE_STARTING_EMBEDDED_SERVER = 1;
    private final int STATE_INSERTING_DATA = 2;
    private final int STATE_GENERATING_USER_DATA = 3;
    private final int STATE_CREATING_INDICES = 4;
    private final int STATE_IDLE = 5;

    public ArrayList<User> users;

    private Connection connection;
    private Dictionary dictionary;
    private Dictionary countUrls;
    private int state;
    private long startTime;
    private int totalLines;
    private int currentLineNumber;

    /**
     * Constructor for SQLInterface, called only once during the whole program
     * Author: Stefan
     *
     * @param main          Reference to the main class
     * @param dictionary    Dictionary reference for setting up autocomplete
     */
    public SQLInterface(Project main, Dictionary dictionary) {
        this.main = main;
        this.dictionary = dictionary;
        loadProgress = 0;

        countUrls = new Dictionary();
        startTime = System.currentTimeMillis();
        totalLines = 0;
        currentLineNumber = 1;
    }

    /**
     * Connects the SQLInterface to a MySQL database
     *      If a database is not available, it will start an embedded server and connect to that
     * Author: Stefan
     */
    public void connect() {
        try {
            // Try to connect to the server if it's already running
            boolean usingEmbeddedServer = false;
            state = STATE_CONNECTING_TO_MYSQL;
            try {
                String db = "jdbc:mysql://localhost:3306/pp?rewriteBatchedStatements=true";
                String user = "root";
                String password = "";
                connection = DriverManager.getConnection(db, user, password);
                Event.LAST = Event.DATABASE_UPDATED;
            } catch(Exception ignored) {
                // If the program reaches here, then no server is running
                state = STATE_STARTING_EMBEDDED_SERVER;
                DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
                configBuilder.setPort(3306); // OR, default: setPort(0); => autom. detect free port
                DB db = DB.newEmbeddedDB(configBuilder.build());
                db.start();
                db.createDB(DB_NAME);
                connection = DriverManager.getConnection(configBuilder.getURL(DB_NAME), "root", "");

                usingEmbeddedServer = true;
            }

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `stories` (`ID` int(1) NOT NULL, `Title` varchar(300) NOT NULL, `Author` varchar(100) NOT NULL, `Date` int(1) NOT NULL, `URL` varchar(500) NOT NULL, `Comments` varchar(800) NOT NULL, `Score` int(1) NOT NULL, `CommentCount` int(1) NOT NULL, `DescendantCount` int(1) NOT NULL, PRIMARY KEY (`ID`))");
            statement.execute("CREATE TABLE IF NOT EXISTS `comments` (`ID` int(1) NOT NULL, `Text` varchar(10000) NOT NULL, `Author` varchar(300) NOT NULL, `Date` bigint(1) NOT NULL, `Replies` varchar(1000) NOT NULL, `ReplyCount` int(1) NOT NULL, `Parent` int(1) NOT NULL, PRIMARY KEY (`ID`))");
            statement.execute("CREATE TABLE IF NOT EXISTS `users` (`ID` int(1) NOT NULL, `Name` varchar(300) NOT NULL, `Stories` varchar(10000) NOT NULL, `Comments` varchar(10000) NOT NULL, `Score` int(1) NOT NULL, PRIMARY KEY (`ID`))");

            // Check if the database's empty
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM stories");
            rs.next();

            if(usingEmbeddedServer) {
                Event.LAST = Event.RELOAD_DATA;
                reloadData();
            } else if(rs.getInt(1) == 0)
                reloadData();
            else
                getUserData();
        } catch(Exception e) {
            System.out.println("Error starting server:");
            e.printStackTrace();
        }
        state = STATE_IDLE;
    }

    /**
     * Parses the data in the JSON file and loads its contents to the MySQL server
     * Authors: Adam and Stefan
     */
    public void reloadData() {
        users = new ArrayList<>();
        state = STATE_INSERTING_DATA;
        try {
            // Scanner to read data from the file
            BufferedReader fileReader = new BufferedReader(new FileReader(main.dataPath(DATA_URL)));

            // Number of lines in the file (used for progress bar)
            String lastLine;
            while(fileReader.readLine() != null)
                totalLines++;

            fileReader = new BufferedReader(new FileReader(main.dataPath(DATA_URL)));

            String storySQL = "INSERT IGNORE INTO stories (id, title, author, date, url, comments, score, commentCount, descendantCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String commentSQL = "INSERT IGNORE INTO comments (id, text, author, date, replies, replyCount, parent) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement storyQuery = connection.prepareStatement(storySQL);
            PreparedStatement commentQuery = connection.prepareStatement(commentSQL);

            users = new ArrayList<>();

            currentLineNumber = 1;
            int queryCount = 0, batchSize = totalLines / 10;
            long START = System.currentTimeMillis();
            connection.setAutoCommit(false);
            while((lastLine = fileReader.readLine()) != null) {
                // Parse the JSON object
                try {
                    JSONObject currentJSON = new JSONObject(lastLine);

                    // Read the keys common to stories and comments
                    int id = currentJSON.getInt("id");
                    String type = "";
                    if(currentJSON.has("type"))
                        type = currentJSON.getString("type");
                    String author = "";
                    if(currentJSON.has("by"))
                        author = currentJSON.getString("by");

                    int authorIndex = -1;
                    for(int j = 0; j < users.size(); j++) {
                        User currentUser = users.get(j);
                        if(currentUser.name.equalsIgnoreCase(author)) {
                            authorIndex = j;
                            break;
                        }
                    }
                    User currentUser;
                    if(authorIndex == -1) {
                        currentUser = new User(main, users.size(), author, "", "", 0);
                        users.add(currentUser);
                    } else {
                        currentUser = users.get(authorIndex);
                    }

                    int[] kids = new int[]{0};
                    if(currentJSON.has("kids"))
                        kids = JSONArrayToIntArray(currentJSON.getJSONArray("kids"));

                    // Multiplying the time by 1000 as the time here is in seconds, while Date expects miliseconds.
                    long timestamp = 0;
                    if(currentJSON.has("time"))
                        timestamp = currentJSON.getLong("time");

                    // Read the specific keys
                    if(type.equalsIgnoreCase("story")) {
                        String title = "";
                        if(currentJSON.has("title"))
                            title = currentJSON.getString("title");
                        String url = "";
                        if(currentJSON.has("url"))
                            url = currentJSON.getString("url");
                        int score = 0;
                        if(currentJSON.has("score"))
                            score = currentJSON.getInt("score");
                        int descendantCount = 0;
                        if(currentJSON.has("descendants"))
                            descendantCount = currentJSON.getInt("descendants");

                        currentUser.score += score;
                        currentUser.storyIDs += id + ",";

                        // Add it to our list of results
                        if(!DISABLE_AUTOCOMPLETE) {
                            dictionary.insert(author);
                            String[] parts = title.split(" ");
                            for(String word : parts)
                                dictionary.insert(word);
                            parts = url.split("/");
                            if(parts.length > 2) {
                                dictionary.insert(parts[2]);
                                countUrls.insert(parts[2]);
                            }
                        }

                        // SQL HERE
                        storyQuery.setInt(1, id);
                        storyQuery.setString(2, formatSQL(title));
                        storyQuery.setString(3, formatSQL(author));
                        storyQuery.setLong(4, timestamp);
                        storyQuery.setString(5, formatSQL(url));
                        storyQuery.setString(6, intArrayToCommaSeparatedString(kids));
                        storyQuery.setInt(7, score);
                        storyQuery.setInt(8, kids.length);
                        storyQuery.setInt(9, descendantCount);

                        storyQuery.addBatch();
                    } else if(type.equalsIgnoreCase("comment")) {
                        String text = "";
                        if(currentJSON.has("text"))
                            text = currentJSON.getString("text");
                        int parent = 0;
                        if(currentJSON.has("parent"))
                            parent = currentJSON.getInt("parent");

                        if(!DISABLE_AUTOCOMPLETE)
                            dictionary.insert(author);

                        currentUser.commentIDs += id;

                        // SQL HERE
                        commentQuery.setInt(1, id);
                        commentQuery.setString(2, formatSQL(text));
                        commentQuery.setString(3, formatSQL(author));
                        commentQuery.setLong(4, timestamp);
                        commentQuery.setString(5, intArrayToCommaSeparatedString(kids));
                        commentQuery.setInt(6, kids.length);
                        commentQuery.setInt(7, parent);

                        commentQuery.addBatch();
                    }
                } catch(EmptyStackException e) {
                    // Thrown when too many JSONExceptions are thrown, causes Processing to crash
                    System.out.println("Too many exceptions thrown before reading line " + currentLineNumber);
                } catch(JSONException e) {
                    // System.out.println("Problem with line: " + currentLineNumber + "\t" + e.getMessage());
                } catch(Exception e) {
                    e.printStackTrace();
                    return;
                }

                currentLineNumber++;
                // Calculate the new progress (divided by four since we'll need to add comments and store everything in the database)
                loadProgress = currentLineNumber * 100d / totalLines;

                queryCount++;
                if(queryCount > batchSize) {
                    queryCount = 0;
                    storyQuery.executeBatch();
                    storyQuery.clearBatch();
                    storyQuery.clearParameters();
                    commentQuery.executeBatch();
                    commentQuery.clearBatch();
                    commentQuery.clearParameters();
                }
            }
            fileReader.close();

            // Add the users to the database
            state = STATE_GENERATING_USER_DATA;
            System.out.println("Size: " + users.size());
            for(User user : users) {
                String userSQL = "INSERT IGNORE INTO users (id, name, stories, comments, score) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement userStatement = connection.prepareStatement(userSQL);
                userStatement.setInt(1, user.id);
                userStatement.setString(2, user.name);
                userStatement.setString(3, user.storyIDs);
                userStatement.setString(4, user.commentIDs);
                userStatement.setInt(5, user.score);
                userStatement.execute();
            }

            // Creating indices for table columns
            createIndices();

            connection.setAutoCommit(true);
            double duration = (System.currentTimeMillis() - START) / 1000;
            System.out.println(duration + " seconds to execute " + totalLines + " queries, average of " +
                    (duration * 1000 / totalLines) + " milliseconds per query");
        } catch(IOException e) {
            System.out.println("Could not open data file: " + main.dataPath(DATA_URL));
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        loadProgress = 100;
        Event.LAST = Event.DATABASE_UPDATED;
        state = STATE_IDLE;
    }

    /**
     * Queries the database for all the information regarding all the users
     * Author: Stefan
     */
    private void getUserData() {
        state = STATE_GENERATING_USER_DATA;
        try {
            users = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users`");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String storyIDs = rs.getString(3);
                String commentIDs = rs.getString(4);
                int score = rs.getInt(5);
                User currentUser = new User(main, id, name, storyIDs, commentIDs, score);
                users.add(currentUser);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates indices for all the tables in the database allowing for quicker querying
     * Author: Stefan
     */
    private void createIndices() {
        state = STATE_CREATING_INDICES;
        // Clearing any previous indices
        try {
            PreparedStatement existsStoriesIndex = connection.prepareStatement("SELECT COUNT(1) IndexIsThere FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name='stories' AND index_name='index_stories'");
            existsStoriesIndex.executeQuery();

            try {
                PreparedStatement dropIndex = connection.prepareStatement("ALTER TABLE stories DROP INDEX index_stories");
                dropIndex.execute();
            } catch(Exception ignored) {
            }

            PreparedStatement indexStories = connection.prepareStatement("CREATE INDEX index_stories ON stories(id, date, score, commentcount)");
            indexStories.execute();

            PreparedStatement existsCommentsIndex = connection.prepareStatement("SELECT COUNT(1) IndexIsThere FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name='comments' AND index_name='index_comments'");
            existsCommentsIndex.executeQuery();
            try {
                PreparedStatement dropIndex = connection.prepareStatement("ALTER TABLE comments DROP INDEX index_comments");
                dropIndex.execute();
            } catch(Exception ignored) {
            }

            PreparedStatement indexComments = connection.prepareStatement("CREATE INDEX index_comments ON comments(id, date, replycount, parent)");
            indexComments.execute();

            PreparedStatement existsUsersIndex = connection.prepareStatement("SELECT COUNT(1) IndexIsThere FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name='users' AND index_name='index_users'");
            existsUsersIndex.executeQuery();
            try {
                PreparedStatement dropIndex = connection.prepareStatement("ALTER TABLE users DROP INDEX index_users");
                dropIndex.execute();
            } catch(Exception ignored) {
            }
            PreparedStatement indexUsers = connection.prepareStatement("CREATE INDEX index_users ON users(id, name, score)");
            indexUsers.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current loading progress for the loading notification
     * Author: Stefan
     *
     * @return Current loading progress
     */
    public double getProgress() {
        return loadProgress;
    }

    /**
     * Returns the results of a particular query from the database
     * Authors: Adam and Stefan
     *
     * @param query     Query to run on the SQL server
     * @return          ArrayList of DBElements (Stories, Comments, or Users)
     */
    public ArrayList<DBElement> query(Query query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            ResultSet resultSet = statement.executeQuery();
            ArrayList<DBElement> results = new ArrayList<>();

            if(query.tables[Query.TABLE_STORIES]) {
                while(resultSet.next()) {
                    Story lastStory = new Story(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            new Date(resultSet.getLong(4) * 1000L),
                            resultSet.getString(5),
                            resultSet.getInt(7),
                            commaSeparatedStringToIntArray(resultSet.getString(6)),
                            resultSet.getInt(8));
                    results.add(lastStory);
                }
                return results;
            }

            if(query.tables[Query.TABLE_COMMENTS]) {
                while(resultSet.next()) {
                    Comment lastComment = new Comment(main,
                            resultSet.getString(2),
                            resultSet.getString(3),
                            new Date(resultSet.getLong(4) * 1000L),
                            resultSet.getInt(7),
                            resultSet.getInt(1),
                            commaSeparatedStringToIntArray(resultSet.getString(6)));
                    results.add(lastComment);
                }
                return results;
            }

            if(query.tables[Query.TABLE_USERS]) {
                while(resultSet.next()) {
                    User lastUser = new User(main,
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getInt(5));
                    users.add(lastUser);
                }
            }
            query.queryComplete();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all the stories written by a particular user
     * Author: Stefan
     *
     * @param user  User to get stories for
     * @return      ArrayList of stories
     */
    ArrayList<DBElement> getStoriesFromUser(User user) {
        ArrayList<DBElement> results = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE `Name` = '" + user.getName() + "' ORDER BY `Name` ASC");
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                String storyIDs = rs.getString(3);
                String formattedStoryIDs = storyIDs.substring(0, storyIDs.length() - 2);
                String query = "SELECT * FROM stories WHERE id IN(" + formattedStoryIDs + ")";
                System.out.println(query);
                statement = connection.prepareStatement(query);
                rs = statement.executeQuery();
                while(rs.next()) {
                    Story currentStory = new Story(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            new Date(rs.getLong(4) * 1000L),
                            rs.getString(5),
                            rs.getInt(7),
                            commaSeparatedStringToIntArray(rs.getString(6)),
                            rs.getInt(8));
                    results.add(currentStory);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Returns a string describing the current state of the SQLInterface
     * Author: Stefan
     *
     * @return String describing the current state of the SQLInterface
     */
    public String getStatusMessage() {
        switch(state) {
            case STATE_CONNECTING_TO_MYSQL:
                return "Connecting to local MySQL server...";
            case STATE_STARTING_EMBEDDED_SERVER:
                return "No MySQL server found, starting embedded server...";
            case STATE_INSERTING_DATA:
                String message = "Loading JSON data into MySQL server, this may take a while (";
                double averageRate = (((double) (System.currentTimeMillis() - startTime)) / 1000) / currentLineNumber;
                int linesRemaining = totalLines - currentLineNumber;
                double timeLeft = linesRemaining * averageRate;

                message += String.format(String.format("%.0f", timeLeft)) + " seconds remaining)";
                return message;
            case STATE_GENERATING_USER_DATA:
                return "Generating user data...";
            case STATE_CREATING_INDICES:
                return "Creating data indices, this may take a while...";
            case STATE_IDLE:
                return "SQL server idle";
            default:
                return "I have no idea what I'm doing";
        }
    }

    /**
     * Uses SQL to return the amount of post per each hour.
     * Author: Adam Ogorek
     *
     * @return Array of 24 integers, each representing the amount of posts posted at the corresponding hour.
     */
    public int[] getPostsPerHour() {
        int[] results = new int[24];
        try {
            PreparedStatement statement = connection.prepareStatement("Select storiesHourCount.Hour, (commentsHourCount.number+storiesHourCount.number) AS totalCount \n" +
                    "FROM ((SELECT Hour,Count(*) as number\n" +
                    "FROM (SELECT ((MOD(date,24*60*60)) DIV (60*60)) AS Hour\n" +
                    "FROM comments) AS commentHours\n" +
                    "GROUP BY Hour\n" +
                    "ORDER BY Hour) AS commentsHourCount,\n" +
                    "(SELECT Hour,Count(*) as number\n" +
                    "FROM (SELECT ((MOD(date,24*60*60)) DIV (60*60)) AS Hour\n" +
                    "FROM stories) AS storiesHours\n" +
                    "GROUP BY Hour\n" +
                    "ORDER BY Hour) AS storiesHourCount)\n" +
                    "WHERE storiesHourCount.Hour=commentsHourCount.Hour\n" +
                    "ORDER BY storiesHourCount.Hour");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                int hour = rs.getInt(1);
                results[hour] = rs.getInt(2);

            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * This function uses SQL to find the most used websites from the database.
     * Author Adam Ogorek
     * @return Array of WordFrequency, representing the urls with the amount of their uses.
     */
    public WordFrequency[] getMostPopularWebsites() {
        ArrayList <WordFrequency> results= new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT truncatedURL,COUNT(truncatedURL) as number FROM ((SELECT (IF(LOCATE('/',url,8)!=0,SUBSTRING(url,8,LOCATE('/',url,8)-8),SUBSTRING(url,8))) AS truncatedURL FROM stories WHERE CHAR_LENGTH(url)!=0) AS subquery) GROUP BY truncatedURL ORDER BY number DESC");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                results.add(new WordFrequency(rs.getString(1),rs.getInt(2)));

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        WordFrequency [] result = new WordFrequency[results.size()];
        results.toArray(result);
        return result;

    }

    /**
     * returns the number of all stories in the database.
     * Author Adam Ogorek
     */
    public int getCountAllWebsites() {
        int result = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM stories WHERE CHAR_LENGTH(url)!=0");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This function uses SQL to return an arraylist representing the number of posts at each day of the week.
     * Author Adam Ogorek
     * @return ArrayList <Integer>
     */
    public ArrayList<Integer> getActivityPerDayOfWeek() {
        ArrayList<Integer> days = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT DAYOFWEEK(FROM_UNIXTIME(date)) AS day, Count(*)\n" +
                    "FROM stories\n" +
                    "GROUP BY day");
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                days.add(rs.getInt(2));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * Returns an ArrayList of all the comments for a given story
     * Author: Stefan
     *
     * @param story Story to get comments for
     * @return      Comments from that story
     */
    public ArrayList<Comment> getStoryComments(Story story) {
        if(story == null)
            return new ArrayList<>();

        try {
            PreparedStatement getStoryStatement = connection.prepareStatement("SELECT * FROM stories WHERE id = " + story.getID());
            ResultSet rs = getStoryStatement.executeQuery();
            if(rs.next()) {
                // SQL query successful, story found
                String comments = rs.getString(6);
                int[] commentArray = commaSeparatedStringToIntArray(comments);

                ArrayList<Comment> commentList = new ArrayList<>();
                for(int commentID : commentArray) {
                    commentList.add(getSubcommentsRecursively(commentID));
                }
                return commentList;
            }
        } catch(SQLException e) {
            System.out.println("Error trying to get all stories for " + story.getTitle());
        }

        return new ArrayList<>();
    }

    /**
     * Recursive function for getting replies to a comment
     * Author: Stefan
     *
     * @param commentID Top-level comment
     * @return          Top-level comment including replies
     */
    public Comment getSubcommentsRecursively(int commentID) {
        // Go to every reply
        // For each reply, return a Comment object whose children are that comment's replies
        // Base case:
        // If we've reached a comment that has no replies, return a comment object with no children
        try {
            PreparedStatement getCommentStatement = connection.prepareStatement("SELECT * FROM comments WHERE id = " + commentID);
            ResultSet comment = getCommentStatement.executeQuery();
            if(comment.next()) {
                String repliesString = comment.getString(5);
                if(repliesString.equals("0,")) {
                    // Base case, return a comment with no replies
                    Comment currentComment = new Comment(main, comment.getString(2),
                            comment.getString(3),
                            new Date(comment.getLong(4)),
                            comment.getInt(6),
                            comment.getInt(1),
                            (int[]) null);
                    return currentComment;
                }
                int[] replyIDs = commaSeparatedStringToIntArray(repliesString);

                ArrayList<Comment> replies = new ArrayList<>();
                for(int replyID : replyIDs) {
                    Comment currentReply = getSubcommentsRecursively(replyID);
                    replies.add(currentReply);
                }
                Comment headComment = new Comment(main,
                        comment.getString(2),
                        comment.getString(3),
                        new Date(comment.getLong(4)),
                        comment.getInt(6),
                        comment.getInt(1),
                        replies);
                return headComment;
            }
        } catch(SQLException e) {
            System.out.println("Failed query to get comment " + commentID);
        }

        return null;
    }
}
