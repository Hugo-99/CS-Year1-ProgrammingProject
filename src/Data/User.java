package Data;

import GUI.Widgets.CommentWidget;
import GUI.Widgets.StoryWidget;
import GUI.Widgets.Widget;
import Main.Project;

import java.util.ArrayList;

import static Data.Query.SORT_BY_LATEST;
import static GUI.Layout.*;

/**
 * Data structure for storing users
 * Author: Koh
 */
public class User extends DBElement {
    int id;
    public String name;
    String storyIDs;
    String commentIDs;
    int score;
    public Project main;

    User(Project main, int id, String name, String storyIDs, String commentIDs, int score) {
        super(id, name);
        this.id = id;
        this.name = name;
        this.storyIDs = storyIDs;
        this.commentIDs = commentIDs;
        this.score = score;
        this.main=main;
    }

    public ArrayList<Widget> getStories(){
        boolean[] filters = new boolean[4];
        filters[Query.TABLE_STORIES] = true;
        filters[Query.TABLE_COMMENTS] = false;
        filters[Query.TABLE_DATES] = false;
        filters[Query.TABLE_USERS] = false;

        ArrayList<DBElement> results = main.SQL.getStoriesFromUser(this);

        ArrayList<Widget> stories = new ArrayList<>();
        for(int i = 0; i < results.size(); i++) {
            Story currentStory = (Story) results.get(i);
            StoryWidget sampleStory = new StoryWidget(main, currentStory, MARGIN_LEFT, MARGIN_TOP + (i * (100 + LIST_SPACING)), WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 100);
            stories.add(sampleStory);
        }
        return stories;
    }

    public ArrayList<Widget> getComments() {
        // Get the stories
        boolean[] filters = new boolean[4];
        filters[Query.TABLE_STORIES] = false;
        filters[Query.TABLE_COMMENTS] = true;
        filters[Query.TABLE_DATES] = false;
        filters[Query.TABLE_USERS] = false;

        Query initialQuery = new Query("", SORT_BY_LATEST, filters);
        ArrayList<DBElement> posts = main.SQL.query(initialQuery);

        ArrayList<Widget> comments = new ArrayList<>();
        int previousExtraHeight = 0;
        for(int i = 0; i < posts.size(); i++) {
            Comment currentComment = (Comment) posts.get(i);
            CommentWidget sampleStory = new CommentWidget(main,currentComment, MARGIN_LEFT, MARGIN_TOP + previousExtraHeight + (i * (100 + LIST_SPACING)), WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, 100 + currentComment.getExtraHeight());
            previousExtraHeight = currentComment.getExtraHeight();
            comments.add(sampleStory);
        }
        return comments;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

}
