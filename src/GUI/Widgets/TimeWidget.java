package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Project;
import processing.core.PVector;

/**
 * Author Adam Ogorek
 * a Widget to display the differences in popularity of different hours.
 */

public class TimeWidget extends Widget {
    private int[] postsPerHour;
    private int maxAmount;
    private int minAmount;
    private float radius;

    public TimeWidget(Project main, int x, int y, int width, int height, int[] postsPerHour) {
        super(main, x, y, width, height);
        this.postsPerHour = postsPerHour;
        radius = Layout.TIME_WIDGET_WIDTH / 2 - 25;
        maxAmount = 0;
        minAmount = Integer.MAX_VALUE;
        for(int count : postsPerHour) {
            if(count > maxAmount)
                maxAmount = count;
            if(count < minAmount)
                minAmount = count;
        }
    }

    public void draw() {
        main.fill(Color.BACKGROUND_DIMMED);
        main.rect(x, y, width / 2, height, Layout.BORDER_RADIUS);
        main.pushMatrix();
        main.translate(x + (width / 4), y + height / 2);
        main.fill(Color.TEXT_DEFAULT);
        main.textSize(12);
        main.textAlign(main.CENTER);
        main.text("Time", 0, 0);
        main.text("Most active hours", 0, -200 / 2 + 15);
        //main.rect(0,0,width,height);
        for(int i = 0; i < 24; i++) {
            if(i % 2 == 0)
                main.stroke(Color.PRIMARY);
            else
                main.stroke(Color.SECONDARY);
            main.strokeWeight(5);
            float len = main.map(postsPerHour[i], minAmount, maxAmount, 8, 25);
            PVector firstPoint = new PVector();
            PVector secondPoint = new PVector();
            float angle = i * main.TWO_PI / 24 - main.HALF_PI;
            firstPoint.x = radius * main.cos(angle);
            firstPoint.y = radius * main.sin(angle);
            secondPoint.x = (radius + len) * main.cos(angle);
            secondPoint.y = (radius + len) * main.sin(angle);
            main.line(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
            if(i % 6 == 0)
                main.text(i, firstPoint.x-(20*main.cos(angle)), firstPoint.y-(20*main.sin(angle)));
        }
        main.popMatrix();
    }
}
