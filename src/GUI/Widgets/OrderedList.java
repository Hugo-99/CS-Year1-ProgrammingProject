package GUI.Widgets;

import Data.WordFrequency;
import GUI.Color;
import GUI.Layout;
import Main.Project;
import processing.core.PConstants;

/**
 * List of top 5 most popular websites
 * Author:Adam
 */
public class OrderedList extends Widget {
    private int totalAmountOfStories;
    private WordFrequency[] mostPopularStories;

    public OrderedList(Project main, int x, int y, int width, int height, WordFrequency[] mostPopularStories, int totalAmountOfStories) {
        super(main, x, y, width, height);
        this.totalAmountOfStories = totalAmountOfStories;
        this.mostPopularStories = mostPopularStories;
    }

    @Override
    public void draw() {
        main.stroke(Color.GRAY_DARK);
        main.strokeWeight(1);
        float maxTextLength = 0;
        main.textAlign(main.LEFT);
        for(int i = 0; i < 5; i++)
            if(main.textWidth(i + ". " + mostPopularStories[i]) > maxTextLength)
                maxTextLength = main.textWidth(i + ". " + mostPopularStories[i]);
        main.fill(Color.BACKGROUND_DIMMED);
        main.rect(x, y, Layout.ORDERED_LIST_WIDTH, height, Layout.BORDER_RADIUS);

        main.fill(Color.TEXT_DEFAULT);
        main.text("Most popular websites", x + 5, y + 30);
        float maxPercentage = ((float) mostPopularStories[0].frequency) / totalAmountOfStories;
        for(int i = 1; i <= 5; i++) {
            float percentage = ((float) mostPopularStories[i - 1].frequency) / totalAmountOfStories;
            float rectLength = main.map(percentage, 0, maxPercentage, 5, 30);
            main.fill(Color.TEXT_DEFAULT);
            main.text(i + ". " + mostPopularStories[i - 1], x + Layout.PADDING, y + (i + 1) * 30);
            main.text(String.format("%.2f\u2030", percentage * 10), x + 40 + maxTextLength, y + (i + 1) * 30);
            main.fill(Color.PRIMARY);
            main.rect(x + maxTextLength + 10, y + (i + 1) * 30 - 10, rectLength, 10);
        }
    }
}
