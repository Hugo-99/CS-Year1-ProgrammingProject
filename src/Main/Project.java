package Main;

import Data.Dictionary;
import Data.SQLInterface;
import GUI.Color;
import GUI.Pages.*;
import GUI.Widgets.LoadingWidget;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import static GUI.Layout.*;

/**
 * Starting point for the project
 * Authors: Adam, Koh, and Stefan
 */
public class Project extends PApplet {
    public PFont FontBold, FontRegular, FontThin, FontLarge, FontHeading;
    protected Page storyListingPage,  searchPage,  analysisPage, storyPage;
    public Page currentPage;

    public SQLInterface SQL;
    LoadingWidget loadingBar;
    public Dictionary dictionary;

    /**
     * Sets up colors, the autocomplete dictionary, the sql interface, fonts, and pages
     * Authors: Adam, Koh, and Stefan
     */
    public void setup() {
        // Initialize colors and the dictionary
        Color.initialize(this);
        dictionary = new Dictionary();

        // Initialize the SQLInterface
        SQL = new SQLInterface(this, dictionary);
        new Thread(new Runnable() {
            public void run() {
                SQL.connect();
            }
        }).start();

        // Load fonts
        FontRegular = createFont("Montserrat-Regular.ttf", 16);
        FontBold = createFont("Montserrat-Bold.ttf", 16);
        FontHeading = createFont("Montserrat-Bold.ttf", 28);
        FontLarge = createFont("Montserrat-Regular.ttf", 20);
        FontThin = createFont("Montserrat-Thin.ttf", 16);

        // Initialize pages
        storyListingPage = new StoryListingPage(this);
        searchPage = new SearchPage(this);
        analysisPage = new AnalysisPage(this);
        storyPage = new StoryPage(this, null);
        currentPage = storyListingPage;

        // Initialize SQL notification
        loadingBar = new LoadingWidget(this);
    }

    public void settings() {
        if(FULLSCREEN) {
            WINDOW_WIDTH = displayWidth;
            WINDOW_HEIGHT = displayHeight;
            MARGIN_LEFT = (int) (0.1 * WINDOW_WIDTH);
            MARGIN_RIGHT = MARGIN_LEFT;
            CONTENT_WIDTH = WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;
            fullScreen();
        } else {
            size(WINDOW_WIDTH, WINDOW_HEIGHT);
        }
    }

    /**
     * Draw loop for the GUI
     */
    @Override
    public void draw() {
        background(Color.BACKGROUND);

        fill(Color.PRIMARY);
        textFont(FontRegular);
        text("FPS: " + String.format("%,.0f", frameRate), 30, 30);
        Event.handleEvents(this);

        // Draw the current page
        if(currentPage != null)
            currentPage.draw();
        if(loadingBar.isLoading())
            loadingBar.draw();
    }

    /**
     * Project's starting point
     * @param args Command line arguments
     */
    public static void main(String... args) {
        PApplet.main("Main.Project");
    }

    /**
     * Notifies each page that the database has been updated
     * Author: Stefan
     */
    void onDBUpdated() {
        storyListingPage.onDBUpdated();
        searchPage.onDBUpdated();
        analysisPage.onDBUpdated();
        storyPage.onDBUpdated();
    }

    // INPUT HANDLERS

    public void mouseMoved() {
        currentPage.mouseMoved();
    }

    public void keyPressed() {
        currentPage.keyPressed();
    }

    public void mouseWheel(MouseEvent event) {
        currentPage.mouseScrolled(event.getCount());
    }

    public void mousePressed() {
        currentPage.mousePressed();
    }

    public void mouseDragged() {
        currentPage.mouseDragged();
    }

    public void mouseReleased() {
        currentPage.mouseReleased();
    }
}
