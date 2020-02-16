package GUI;

import GUI.Pages.AnimationPage;
import GUI.Pages.Page;
import GUI.Pages.SearchPage;
import Main.Project;

/**
 * Class that handles all animations for the project
 * Author: Stefan
 */
public class Animator {
    public static int TYPE_SWIPE_LEFT = 0;
    public static int TYPE_SWIPE_RIGHT = 1;
    public static int TYPE_SHOW_FILTERS = 2;
    public static int TYPE_HIDE_FILTERS = 3;
    public static int TYPE_EXPAND_STORY = 4;

    private static final double SWIPE_DURATION = 0.7;
    private static final double DOCK_DURATION = 0.5;
    public static final double NOTIFICATION_DURATION = 0.5;

    private double animDuration;
    private long startTime;
    private double progress, lastProgress;

    /**
     * Constructor for a time-keeping object
     * @param animDuration Duration of the animation
     */
    public Animator(double animDuration) {
        startTime = System.currentTimeMillis();
        this.animDuration = animDuration * 1000;
    }

    /**
     * Animates the search interface being shown
     * @param main          Reference to the main class
     * @param searchPage    Reference to the search page
     */
    public static void showFilters(Project main, SearchPage searchPage) {
        main.currentPage = new AnimationPage(main, searchPage, searchPage, DOCK_DURATION, TYPE_SHOW_FILTERS);
    }

    /**
     * Animates the search interface being hidden
     * @param main          Reference to the main class
     * @param searchPage    Reference to the search page
     */
    public static void hideFilters(Project main, SearchPage searchPage) {
        main.currentPage = new AnimationPage(main, searchPage, searchPage, DOCK_DURATION, TYPE_HIDE_FILTERS);
    }

    /**
     * Animates two pages being swiped in the given direction
     * @param main          Reference to the main class
     * @param firstPage     Start page
     * @param secondPage    End page
     * @param direction     Animation direction (see constants)
     */
    public static void switchPages(Project main, Page firstPage, Page secondPage, int direction) {
        if(!(main.currentPage instanceof AnimationPage))
            main.currentPage = new AnimationPage(main, firstPage, secondPage, SWIPE_DURATION, direction);
    }

    /**
     * Calculates the animation progress as a number between 0 and 1
     * Progress is the percentage completion since the object's creation
     * @return progress
     */
    public double getProgress() {
        long deltaTime = System.currentTimeMillis() - startTime;
        if(progress == 1) {
            return progress;
        } else {
            progress = ((Math.cos((deltaTime / animDuration) * Math.PI) * -1) + 1) / 2;
            if(progress < lastProgress) {
                progress = 1;
                return progress;
            }
            lastProgress = progress;
            return progress;
        }
    }
}
