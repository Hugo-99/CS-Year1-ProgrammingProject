package GUI.Widgets;

import GUI.Color;
import GUI.Layout;
import Main.Project;
import processing.core.PConstants;
import processing.core.PShape;

import java.util.ArrayList;
/**
 * BarChart widget
 * Author: Koh
 */
public class BarChart extends Widget {
    private final String title;
    private final String xAxisLabel;
    private final String yAxisLabel;
    private final ArrayList<Integer> data;
    private final char[] xAxisPara;
    private static ArrayList<PShape> displayBar;

    public BarChart(Project main, int x, int y, int width, int height, String title, String yAxisLabel, String xAxisLabel, ArrayList<Integer> postsOnEachDayOfTheWeek, char[] xAxisPara) {
        super(main, x, y, width, height);

        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.xAxisPara = xAxisPara;
        this.data = postsOnEachDayOfTheWeek;

    }

    public void draw() {
        displayBar = this.setData();
        // Draw the axes
        main.stroke(Color.GRAY_DARK);
        main.strokeWeight(Layout.BAR_CHART_AXIS_THICKNESS);
        main.line(x + Layout.PADDING, y + Layout.PADDING, x + Layout.PADDING, y + height - Layout.PADDING);
        main.line(x + Layout.PADDING, y + height - Layout.PADDING, x + Layout.PADDING + height, y + height - Layout.PADDING);

        // Draw the vertical text
        main.pushMatrix();
        main.fill(Color.SECONDARY);
        main.translate(x - Layout.PADDING * 2, y + height / 2 - Layout.PADDING);
        main.rotate(-main.HALF_PI);
        main.text(yAxisLabel, 0, 0);
        main.popMatrix();

        // Draw the Title
        main.fill(Color.PRIMARY);
        main.text(title, x + Layout.PADDING + 100, y + Layout.PADDING - 10);

        // Draw the horizontal
        main.fill(Color.SECONDARY);
        main.text(xAxisLabel, x + Layout.PADDING + 100, y + height + Layout.PADDING * 2);

        // Draw the bars
        int spacing = height / data.size();

        for(int j = 0; j < xAxisPara.length; j++) {
            char day = xAxisPara[j];
            main.fill(Color.PRIMARY);
            main.text(day, x + Layout.PADDING * 2 + spacing * j, y + height + Layout.PADDING / 2);
        }

        for(PShape bar : displayBar)
            main.shape(bar);
    }
    /**
     * setData method
     * Returns an ArrayList of PShape
     * Parameter: none
     * Author: Koh
     */
    public ArrayList<PShape> setData() {
        ArrayList<PShape> bars = new ArrayList();
        int barWidth = height / data.size();
        int greatest = 0;
        for(int j = 0; j < data.size(); j++)
            if(greatest < data.get(j))
                greatest = data.get(j);

        for(int i = 0; i < data.size(); i++) {
            PShape eachBar = main.createShape(PConstants.RECT, x + Layout.PADDING + i * barWidth, y + height - Layout.PADDING, barWidth, -data.get(i) * (height-20) / greatest);
            eachBar.setFill(Color.PRIMARY);
            bars.add(eachBar);
        }
        return bars;
    }

}
