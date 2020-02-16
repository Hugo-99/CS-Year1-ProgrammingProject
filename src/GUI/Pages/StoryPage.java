package GUI.Pages;

import Data.Comment;
import Data.Story;
import GUI.Color;
import GUI.Layout;
import GUI.Widgets.*;
import Main.Event;
import Main.Project;

import java.util.ArrayList;
import java.util.Collections;

import static GUI.Layout.*;


/*
 *  Page that shows the full story title and url and its comments
 *  Author: Koh, Stefan
 */
public class StoryPage extends Page {
    private Story story;

    /**
     * Constructor for the story page
     * @param main  Reference to the main class
     * @param story Story to display
     */
    public StoryPage(Project main, Story story) {
        super(main);
        this.story = story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    @Override
    public void onDBUpdated() {
        // Clear the widgetList in the beginning
        widgetList.clear();
        // Get the comments
        ArrayList<Comment> comments = main.SQL.getStoryComments(story);

        ArrayList<Widget> listWidgets = new ArrayList<>();
        if(comments.size() == 1 && comments.get(0) == null)
            listWidgets.add(new CommentWidget(main, MARGIN_LEFT, MARGIN_TOP + STORY_HEADER_HEIGHT, CONTENT_WIDTH, STORY_COMMENT_HEIGHT));
        else
            addCommentsRecursively(listWidgets, comments, 0);
        listWidgets.add(new StoryHeader(main, MARGIN_LEFT, MARGIN_TOP, CONTENT_WIDTH, STORY_HEADER_HEIGHT, story));
        Collections.reverse(listWidgets);

        Widget toolbarCover = new Widget(main, 0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT);
        toolbarCover.widgetColor = Color.BACKGROUND;

        ScrollBar sB = new ScrollBar(main, WINDOW_WIDTH - SB_WIDTH, 0, SB_WIDTH, WINDOW_HEIGHT, 30);
        List scrollableStories = new List(main, MARGIN_LEFT, TOOLBAR_HEIGHT, LIST_SPACING, listWidgets, false);
        scrollableStories.setDrawReverse();

        Button BackTab = new Button(main, WINDOW_WIDTH - MARGIN_RIGHT - Layout.BACK_BUTTON_WIDTH, TOOLBAR_HEIGHT, Layout.BACK_BUTTON_WIDTH, BUTTON_HEIGHT, "Back", Event.SWITCH_TO_STORIES);
        widgetList.add(scrollableStories);
        widgetList.add(toolbarCover);
        widgetList.add(new NavBar(main, 1));
        if(!(comments.size() == 1 && comments.get(0) == null))
            widgetList.add(sB);
        widgetList.add(BackTab);
    }

    public void draw() {
        for(Widget widget : widgetList)
            widget.draw();
    }

    /**
     * Adds comments to the page recursively
     * Author: Stefan
     *
     * @param listWidgets   ArrayList of widgets on the List widget
     * @param comments      ArrayList of comments to add recursively
     * @param currentDepth  Current indentation (reply level)
     */
    private void addCommentsRecursively(ArrayList<Widget> listWidgets, ArrayList<Comment> comments, int currentDepth) {
        // Draw each comment in the list
        // if it has replies, add the replies with a lower depth
        int previousExtraHeight = 0;
        if(comments.size() == 1 && comments.get(0) == null)
            return;
        for(int i = 0; i < comments.size(); i++) {
            Comment currentComment = comments.get(i);
            if(currentComment != null) {
                CommentWidget sampleStory = new CommentWidget(main, currentComment, MARGIN_LEFT + (currentDepth * Layout.COMMENT_INDENTATION), MARGIN_TOP + previousExtraHeight + (i * (100 + LIST_SPACING)), CONTENT_WIDTH - (currentDepth * Layout.COMMENT_INDENTATION), 100 + currentComment.getExtraHeight());
                addCommentsRecursively(listWidgets, currentComment.getReplies(), currentDepth + 1);
                previousExtraHeight = currentComment.getExtraHeight();
                listWidgets.add(sampleStory);
            }
        }
    }
}
