package GUI.Widgets;

import GUI.Color;
import Main.Project;
import processing.core.PApplet;

import static GUI.Layout.BORDER_RADIUS;

/**
 * Scroll bar for list widgets
 * Author: Adam
 */
public class ScrollBar extends Widget {
    int height;
    private int sliderLength;
    private float sliderY;
    private boolean active;

    public ScrollBar(Project main, int x, int y, int width, int height, int sliderLength) {
        super(main, x, y, width, height);
        this.sliderLength = sliderLength;
        this.height = height;
        active = false;
        sliderY = y;
    }

    public void draw() {
        main.fill(Color.GRAY_DARK);
        main.noStroke();
        main.rect(x, y, width, height);
        main.fill(Color.PRIMARY);
        main.rect(x, sliderY, width, sliderLength, BORDER_RADIUS / 2);
    }

    public void mouseScrolled(float moveDirection) {
        sliderY += 15 * moveDirection;
        sliderY = PApplet.max(0, sliderY);
        sliderY = PApplet.min(sliderY, height - sliderLength);
    }

    public void mousePressed() {
        if(main.mouseX > x && main.mouseX < x + width && main.mouseY > y && main.mouseY < y + height) {
            active = true;
            sliderY = PApplet.max(main.mouseY - sliderLength / 2, 0);
            sliderY = PApplet.min(height - sliderLength, sliderY);
        }
    }

    public void drag() {
        if(active) {
            sliderY = PApplet.max(main.mouseY - sliderLength / 2, 0);
            sliderY = PApplet.min(height - sliderLength, sliderY);
        }
    }

    public void deactivate() {
        active = false;
    }

    /**
     * Gets the percentage height of the slider as a ratio of its height and the scrollBar length.
     * @return
     */
    public float getPercentage() {
        return (sliderY / (float) (height - sliderLength));
    }

    public void setPercentage(float newPercentage) {
        sliderY = newPercentage * (height - sliderLength);
    }
}
