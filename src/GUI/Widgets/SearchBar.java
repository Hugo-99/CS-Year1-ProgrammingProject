package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Event;
import Main.Project;
import processing.core.PApplet;

import java.util.Arrays;

import static GUI.Layout.BORDER_RADIUS;
import static GUI.Layout.PADDING;

/**
 * Text entry widget
 * Author: Koh, Adam
 */
public class SearchBar extends Widget {
    private String text1;
    private String text2;
    private boolean active;
    private float fontSize;
    private boolean show;
    private int count;

    public SearchBar(Project main, int x, int y, int width, int height) {
        super(main, x, y, width, height);

        this.text1 = "";
        this.text2 = "";
        this.active = false;
        this.fontSize = 0;
        this.show = true;
        this.count = 0;
    }

    public void draw() {
        if (!active)
            main.stroke(Color.GRAY_DARK);
        else
            main.stroke(Color.PRIMARY);

        main.fill(Color.BACKGROUND_DIMMED);
        main.rect(x, y, width, height, BORDER_RADIUS);
        main.fill(0);
        main.textSize(16);
        main.textAlign(main.LEFT);
        main.textFont(main.FontRegular);
        main.text(text1, x + PADDING, y + PADDING, width, height);
        main.text(text2, x + PADDING + main.textWidth(text1), y + PADDING, width, height);
        count++;

        if (count == 27) {
            show = !show;
            count = 0;
        }

        if (active && show) {
            main.fill(20);
            main.strokeWeight(Layout.TEXT_CURSOR_THICKNESS);
            main.line(x + fontSize + PADDING, y + PADDING, x + fontSize + PADDING, y + height - PADDING);
        }
        main.strokeWeight(1);
    }

    public void add(char k) {
        if (active) {
            main.textSize(16);
            text1 += k;
            fontSize = main.textWidth(text1);
        }
    }

    public void backSpace() {
        text1 = text1.substring(0, PApplet.max(0, text1.length() - 1));
        fontSize = main.textWidth(text1);
    }

    public void enter() {
        text1 = text1 + text2;
        text2 = "";
        fontSize = main.textWidth(text1);
        active = false;
        Event.LAST = Event.HIDE_FILTERS;
    }

    public String getText() {
        return text1 + text2;
    }
    /**
     * arrow method
     * Returns none, filter the keyCode and enable the user to change the cursor with arrow keys in the search bar to edit the text
     * Parameter: int keyCode
     * Author: Koh
     */
    public void arrow(int keyCode) {
        char[] before1 = text1.toCharArray();
        char[] before2 = text2.toCharArray();

        if (keyCode == main.LEFT && before1.length != 0) {
            char[] after1 = Arrays.copyOfRange(before1, 0, before1.length - 1);
            text1 = String.valueOf(after1);
            System.out.println(text1);
            char[] after2 = new char[before2.length + 1];
            System.arraycopy(before1, before1.length - 1, after2, 0, 1);
            System.arraycopy(before2, 0, after2, 1, before2.length);
            text2 = String.valueOf(after2);
            fontSize = main.textWidth(text1);
        }

        if (keyCode == main.RIGHT && before2.length != 0) {
            char[] after1 = new char[before1.length + 1];
            System.arraycopy(before1, 0, after1, 0, before1.length);
            System.arraycopy(before2, 0, after1, before1.length, 1);
            text1 = String.valueOf(after1);
            System.out.println(text1);
            char[] after2 = Arrays.copyOfRange(before2, 1, before2.length);
            text2 = String.valueOf(after2);
            fontSize = main.textWidth(text1);
        }
    }
    /**
     * mousePressed method
     * Returns none, enable the user to change the cursor with mouse in the search bar to edit the text
     * Parameter: int keyCode
     * Author: Koh
     */
    public void mousePressed() {
        active = hovering;

        char[] before1 = text1.toCharArray();
        char[] before2 = text2.toCharArray();
        float count = 0;

        for (int i = 0; i < before1.length; i++) {
            count += main.textWidth(before1[i]);
            if (i + 1 < before1.length) {
                if (count + main.textWidth(before1[i + 1]) > (main.mouseX - x)) {
                    fontSize = count;
                    char[] after1 = Arrays.copyOfRange(before1, 0, i + 1);
                    text1 = String.valueOf(after1);
                    char[] after2 = Arrays.copyOfRange(before1, i + 1, before1.length);
                    text2 = String.valueOf(after2);
                    i = before1.length;
                }
            }
        }

        if ((main.mouseX - x) < main.textWidth(text1) + main.textWidth(text2) && (main.mouseX - x) > main.textWidth(text1)) {
            count = main.textWidth(text1);
            for (int i = 0; i < before2.length; i++) {
                count += main.textWidth(before2[i]);
                if (i + 1 < before2.length) {
                    if (count + main.textWidth(before2[i + 1]) > (main.mouseX - x)) {
                        fontSize = count;
                        char[] after3 = new char[before1.length + i];
                        System.arraycopy(before1, 0, after3, 0, before1.length);
                        System.arraycopy(before2, 0, after3, before1.length, i);
                        text1 = String.valueOf(after3);
                        char[] after4 = new char[before2.length - i];
                        System.arraycopy(before2, i, after4, 0, before2.length - i);
                        text2 = String.valueOf(after4);

                        i = before2.length;
                    }
                }
            }
        }

        if ((main.mouseX - x) < main.textWidth(text1)) {
            count = 0;
            for (int i = 0; i < before1.length; i++) {
                count += main.textWidth(before1[i]);
                if (i + 1 < before1.length) {
                    if (count + main.textWidth(before1[i + 1]) > (main.mouseX - x)) {
                        fontSize = count;
                        char[] after5 = Arrays.copyOfRange(before1, 0, i + 1);
                        text1 = String.valueOf(after5);
                        char[] after6 = new char[(before1.length - (i + 1)) + before2.length];
                        System.arraycopy(before1, i + 1, after6, 0, before1.length - (i + 1));
                        System.arraycopy(before2, 0, after6, before1.length - (i + 1), before2.length);
                        text2 = String.valueOf(after6);
                        i = before1.length;
                    }
                }
            }
        }

        if (main.textWidth(text1) + main.textWidth(text2) < (main.mouseX - x)) {
            fontSize = main.textWidth(text1) + main.textWidth(text2);
            text1 = text1 + text2;
            text2 = "";
        }
    }
}
