package GUI.Widgets;

import GUI.Animator;
import GUI.Color;
import Main.Project;

import static GUI.Layout.*;

/**
 * Notification widget for SQL operations
 * Author: Stefan
 */
public class LoadingWidget extends Widget{
    private static final int ANIM_SHOW = 3;
    private static final int ANIM_HIDE = 2;

    private boolean isLoading;
    private ProgressBar pb;
    private int animState;
    private Animator animator;

    /**
     * LoadingWidget constructor
     * @param main Reference to the main class
     */
    public LoadingWidget(Project main) {
        super(main, MARGIN_LEFT, WINDOW_HEIGHT - LOADING_HEIGHT, WINDOW_WIDTH - MARGIN_LEFT - MARGIN_RIGHT, LOADING_HEIGHT);
        this.isLoading = true;
        animator = new Animator(Animator.NOTIFICATION_DURATION);
        pb = new ProgressBar(main, x + PADDING, y + height / 2, width - (PADDING * 2), LOADING_PB_HEIGHT);
        startLoading();
    }

    /**
     * Draws the LoadingWidget
     * Author: Stefan
     */
    public void draw() {
        if(isLoading || animState == ANIM_HIDE) {
            int yOffset = height;
            double progress = animator.getProgress();
            if(animState == ANIM_SHOW) {
                yOffset -= progress * height;
            } else if(animState == ANIM_HIDE) {
                yOffset -= height - (progress * height);
            }

            // Background
            main.fill(Color.BACKGROUND_DIMMED);
            main.stroke(Color.GRAY_DARK);
            main.strokeWeight(STROKE_DEFAULT);
            main.rect(x, y + yOffset, width, height, BORDER_RADIUS);

            // Status
            main.fill(Color.PRIMARY);
            main.textAlign(main.LEFT);
            main.textFont(main.FontLarge);
            main.text(main.SQL.getStatusMessage(), x + PADDING, y + 2 * (PADDING) + yOffset);

            // Progress bar
            pb.drawWithOffset(0, yOffset);
        }
    }

    /**
     * @return Whether the loading widget is displaying a loading notification
     */
    public boolean isLoading() {
        return isLoading || animState == ANIM_HIDE;
    }

    /**
     * Makes the notification appear on the screen, regardless of page
     */
    public void startLoading() {
        animState = ANIM_SHOW;
        animator = new Animator(Animator.NOTIFICATION_DURATION);
        isLoading = true;
    }

    /**
     * Makes the notification hide on the screen, regardless of page
     */
    public void doneLoading() {
        animState = ANIM_HIDE;
        animator = new Animator(Animator.NOTIFICATION_DURATION);
        isLoading = false;
    }
}
