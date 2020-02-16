package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Project;

import static GUI.Layout.WINDOW_HEIGHT;
import static GUI.Layout.WINDOW_WIDTH;

/**
 *  Superclass of all widgets
 *  Author: Koh
 */
public class Widget {
    protected Project main;

    public int x, y, width, height;
    public int widgetColor;
    boolean hovering;

    public Widget(Project main, int x, int y, int width, int height) {
        this.main = main;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hovering = false;
        this.widgetColor = Color.BACKGROUND;
    }

    public void draw(){
        if(x > WINDOW_WIDTH || x + width < 0 || y > WINDOW_HEIGHT || y + width < 0)
            return;
        main.fill(widgetColor);
        main.noStroke();
        main.rect(x, y, width, height);
    }

    /**
     * Draws the widget with the specified offset
     * Author: Stefan
     * @param x X offset
     * @param y Y offset
     */
    public void drawWithOffset(int x, int y) {
        this.x += x;
        this.y += y;
        draw();
        this.x -= x;
        this.y -= y;
    }

    void move(int yOffset) {
        this.y = yOffset;
    }

    public void mouseMoved() {
        hovering = cursorInBounds();
    }

    public void mousePressed() {

    }

    public boolean cursorInBounds() {
        return main.mouseX > x && main.mouseX < x + width
                && main.mouseY > y && main.mouseY < y + height;
    }
}
