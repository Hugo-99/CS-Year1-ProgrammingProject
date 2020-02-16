package GUI;

import Main.Project;

/**
 * Class of constants for getting all the colors throughout the project
 * Author: Stefan
 */
public class Color {
    public static int BACKGROUND;
    public static int BACKGROUND_DIMMED;
    public static int WHITE;

    public static int GRAY_LIGHT;
    public static int GRAY_DARK;

    public static int BLACK;
    public static int TEXT_DEFAULT;

    public static int PRIMARY;
    public static int ACCENT;
    public static int SECONDARY;
    public static int SECONDARY_DARK;

    public static void initialize(Project main) {
        BACKGROUND = main.color(255);
        BACKGROUND_DIMMED = main.color(248);
        WHITE = main.color(255);

        GRAY_LIGHT = main.color(235);
        GRAY_DARK = main.color(220);

        BLACK = main.color(0);
        TEXT_DEFAULT = main.color(48, 48, 48);

        PRIMARY = main.color(64, 178, 173);
        ACCENT = main.color(143, 255, 250);
        SECONDARY = main.color(255, 165, 117);
        SECONDARY_DARK = main.color(178, 109, 72);
    }
}
