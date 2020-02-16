package GUI.Widgets;

import GUI.Color;
import Main.Project;
import processing.core.PFont;

import java.util.ArrayList;

import static GUI.Layout.*;

/**
 * Widget for both check boxes and Radio buttons
 * Authors: Koh, Stefan
 */
public class CheckBoxWidget extends Widget{
    private ArrayList<String> options;
    private ArrayList<Boolean> selected;
    public boolean radio;
    private String label;

    /**
     * Constructor for a CheckBoxWidget
     * @param main          Reference to the main class
     * @param x             x coordinate of the top-left corner of the widget
     * @param y             y coordinate of the top-left corner of the widget
     * @param width         CheckBoxWidget width
     * @param height        CheckBoxWidget height
     * @param label         CheckBoxWidget title
     * @param options       ArrayList of options to be displayed
     */
    public CheckBoxWidget(Project main, int x, int y, int width, int height, String label, ArrayList<String> options) {
        super(main, x, y, width, height);
        this.label = label;
        this.widgetColor = Color.PRIMARY;
        this.options = options;
        this.radio = false;
        this.selected = new ArrayList<>(options.size());
        for (String option : options)
            this.selected.add(false);
    }

    /**
     * Draws the CheckBoxWidget
     */
    public void draw() {
        main.textFont(main.FontRegular);
        main.noStroke();
        main.fill(Color.TEXT_DEFAULT);
        main.textAlign(main.LEFT);
        main.text(label, x, y);
        for(int i = 0; i < options.size(); i++) { // Drawing each option
            main.fill(Color.PRIMARY);
            int verticalOffset = y + APPROX_TEXT_HEIGHT + i * (CHECK_BOX_SIZE + BUTTON_PADDING);
            int ellipseFix = CHECK_BOX_SIZE / 2;
            main.ellipse(x + ellipseFix, verticalOffset + ellipseFix, CHECK_BOX_SIZE, CHECK_BOX_SIZE);

            main.fill(Color.TEXT_DEFAULT);
            main.text(options.get(i), x + CHECK_BOX_SIZE + BUTTON_PADDING, verticalOffset + BUTTON_PADDING + (CHECK_BOX_SIZE - APPROX_TEXT_HEIGHT) / 2);
            if (selected.get(i)) {    // Drawing the selected option
                main.fill(Color.WHITE);
                main.ellipse(x + ellipseFix, verticalOffset + ellipseFix, BUTTON_SELECTED_SIZE, BUTTON_SELECTED_SIZE);
            }
        }
    }

    /**
     * Changes the state of the option last clicked
     */
    public void mousePressed() {
        int selectedOption = -1;
        for (int i = 0; i < options.size(); i++) { // Checking if the cursor is over any of the buttons
            if (main.mouseX > x && main.mouseX < x + CHECK_BOX_SIZE &&
                    main.mouseY > y + (i * (CHECK_BOX_SIZE)) &&            // lower button border
                    main.mouseY < y + ((i + 1) * (CHECK_BOX_SIZE + BUTTON_PADDING))) { // upper button border
                selectedOption = i;
                break;
            }
        }

        if (selectedOption == -1)
            return;

        System.out.println("Selected option: " + selectedOption + ", Radio: " + radio);
        for (int i = 0; i < selected.size(); i++)
            if (radio)
                if (i == selectedOption)
                    selected.set(i, true);
                else
                    selected.set(i, false);
            else
                if (i == selectedOption)
                    selected.set(i, !selected.get(i));
    }

    /**
     * Gets the selected options
     * Author: Stefan
     *
     * @return      Boolean array of selected options
     */
    public boolean[] getSelectedArray() {
        boolean[] selectedArray = new boolean[options.size()];
        for (int i = 0; i < options.size(); i++)
            selectedArray[i] = selected.get(i);

        return selectedArray;
    }

    /**
     * Gets the selected option for radio CheckBoxes
     * Author: Stefan
     *
     * @return  Index of the first selected option
     */
    public int getSelectedInt() {
        for (int i = 0; i < selected.size(); i++)
            if (selected.get(i))
                return i;

        return 0;
    }

    /**
     * Determines if the cursor is over any of the options
     * @return boolean: Whether the cursor is over any of the buttons
     */
    public boolean cursorInBounds() {
        for(int i = 0; i < options.size(); i++)
            if(main.mouseX > x && main.mouseX < x + CHECK_BOX_SIZE &&
                    main.mouseY > y + (i * (CHECK_BOX_SIZE)) &&            // lower button border
                    main.mouseY < y + ((i + 1) * (CHECK_BOX_SIZE + BUTTON_PADDING)))
                return true;

        return false;
    }

    /**
     * Sets the option with the given index to true for radio CheckBoxes
     * Author: Stefan
     *
     * @param option Index of the option to set to true
     */
    public void check(int option) {
        selected.set(option, true);
    }

    /**
     * Sets the option with the given index to true
     * Author: Stefan
     *
     * @param option Index of the option to set to true
     */
    public void set(int option) {
        if(radio)
            selected.set(option, true);
    }
}
