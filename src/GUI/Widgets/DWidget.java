package GUI.Widgets;

import Main.Project;

/**
 * Double Widget container (used in the analysis page)
 * Widget that draws two widgets equally spaced horizontally
 * Author: Stefan
 */
public class DWidget extends Widget {
    private Widget widget1, widget2;

    /**
     * Double Widget constructor
     * Author: Stefan
     *
     * @param main      Reference to the main class
     * @param x         x coordinate of the top-left corner of the DWidget
     * @param y         y coordinate of the top-left corner of the DWidget
     * @param width     Widget width
     * @param height    Widget height
     * @param widget1   Left widget to draw
     * @param widget2   Right widget to draw
     */
    public DWidget(Project main, int x, int y, int width, int height, Widget widget1, Widget widget2) {
        super(main, x, y, width, height);

        this.widget1 = widget1;
        widget1.x = x;
        widget1.y = y;
        widget1.height = height;
        widget1.width = width / 2;

        this.widget2 = widget2;
        widget2.x = x + (width / 2);
        widget2.y = y;
        widget2.height = height;
        widget2.width = width / 2;
    }

    /**
     * Draws the two widgets side-by-side
     * Author: Stefan
     */
    public void draw() {
        widget1.x = x;
        widget1.y = y;
        widget2.x = x + (width / 2);
        widget2.y = y;
        widget1.draw();
        widget2.draw();
    }
}
