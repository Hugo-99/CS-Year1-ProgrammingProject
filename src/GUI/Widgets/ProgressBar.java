package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Project;

/**
 * Progress Bar Widget
 * Author: Koh
 */
public class ProgressBar extends Widget{
    private final Project main;

    public ProgressBar(Project main, int x, int y, int width, int height) {
        super(main, x, y, width, height);
        this.main = main;
    }

    public void draw() {
        main.fill(Color.PRIMARY);
        main.rect(x, y, (float) (main.SQL.getProgress() / 100) * width, height, Layout.BORDER_RADIUS);
    }
}