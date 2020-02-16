package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Project;
import processing.core.PConstants;
import processing.core.PShape;

import java.util.ArrayList;
/**
 * DropDown widget
 * Author: Koh and Stefan
 */
public class DropDownWidget extends Widget {
    private final String function;
    private final String[] choices;
    private boolean clicked;
    private static ArrayList<PShape> displayRect;

    /**
     * Constructor for a dropdown widget
     * @param main      Reference to the main class
     * @param x         x coordinate of the top-left corner of the dropdown widget
     * @param y         y coordinate of the top-left corner of the dropdown widget
     * @param width     Widget width
     * @param height    Widget height
     * @param function  Label
     * @param choices   Dropdown options
     */
    DropDownWidget(Project main, int x, int y, int width, int height, String function, String[] choices) {
        super(main, x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.function = function;
        this.choices = choices;
        this.clicked = false;
    }

    /**
     * Draws the dropdown widget
     */
    public void draw() {
        displayRect = this.setChoices();
        main.stroke(Color.GRAY_DARK);
        main.fill(Color.BACKGROUND_DIMMED);
        if(clicked)
            main.rect(x, y, width, height, Layout.BORDER_RADIUS, Layout.BORDER_RADIUS, 0, 0);
        else
            main.rect(x, y, width, height, Layout.BORDER_RADIUS);

        main.fill(Color.PRIMARY);
        main.textAlign(PConstants.CENTER);
        main.text(function, x + Layout.PADDING * 3, (float) (y + Layout.PADDING * 1.75));
        main.triangle(x + Layout.PADDING / 2 + width - 30, y + Layout.PADDING / 2, x + Layout.PADDING / 2 + width - Layout.PADDING, y + Layout.PADDING / 2, x + Layout.PADDING / 2 + width - 20, y + Layout.PADDING + (height - 15));
        if(clicked) {
            for(int i = 0; i < choices.length; i++) {
                main.shape(displayRect.get(i));
                main.textAlign(main.LEFT);
                main.text(choices[i], x + Layout.PADDING, y + Layout.PADDING * 2 + height * (i + 1));
            }
        }
    }

    /**
     * setChoices method
     * Returns an ArrayList of PShape
     * Parameter: none
     * Author: Koh
     */
    public ArrayList<PShape> setChoices() {
        ArrayList<PShape> choicesDisplay = new ArrayList<>();
        for(int i = 0; i < choices.length; i++) {
            PShape eachRect;
            if(i == choices.length - 1)
                eachRect = main.createShape(PConstants.RECT, x, y + height * (i + 1), width, height, 0, 0, Layout.BORDER_RADIUS, Layout.BORDER_RADIUS);
            else
                eachRect = main.createShape(PConstants.RECT, x, y + height * (i + 1), width, height);
            eachRect.setFill(Color.BACKGROUND_DIMMED);
            eachRect.setStroke(Color.GRAY_DARK);
            choicesDisplay.add(eachRect);
        }
        return choicesDisplay;
    }

    /**
     * Mouse click handler
     */
    public void mousePressed() {
        if(cursorInBounds())
            clicked = !clicked;
        else
            clicked = false;
    }
}
