package GUI.Widgets;


import Data.User;
import GUI.Color;
import Main.Project;
import processing.core.PApplet;

/**
 * Widget for showing graphical information about user
 * Authors: Nixon
 */

public class PieChartWidget extends Widget {
    Project main;
    User user;
    BarChart barChart;
    int[] colours = {Color.PRIMARY, Color.ACCENT, Color.GRAY_LIGHT, Color.WHITE};
    double[] data = {user.getComments().size(), user.getStories().size(), user.getScore()};
    String[] data_name = {"Comments (" + data[2] + ")", "Posts (" + data[1] + ")", "score (" + data[0] + ")"};
    double angle_1 = 0;
    double angle_2 = data[0];
    double num = 0;


    public PieChartWidget(Project main, Data.User user, int x, int y, int width, int height) {
        super(main, x, y, width, height);
        this.main = main;
        this.user = user;

    }

    /*
    code used to setup information that would be used with the bar chart.
     */

    //public void setup(){
    //    barChart = new BarChart(main);
    //    barChart.setData(new float[] {(user.getScore()), 0.24, 0.39, 0.18, 0.20, 0.6});

     //   barChart.showValueAxis(true);
     //   main.barChart.showCategoryAxis(true);

    //    main.barChart.setBarColour(Color.PRIMARY);
    //    main.barChart.setBarLabels(new String[] {"User", "#1","#2","#3","#4", "#5"});
    //}



    public void draw() {
        main.noLoop(); // prevents piechart from turning
        String heading1 = "User Spotlight";
        String heading2 = "user: " + user.getName();
        String Title = "Statistics";

        /*
        code for creating a bar chart comparing users score to the score of the top five users
        not used.
         */

       // main.fill(120);
       // main.textSize(20);
       // main.text("Total Activity: User Vs Top Five", 15, height - 400);
       // main.textSize(11);
       // main.text("Total User activity = users comments and posts", 15, height - 370);
       // main.barChart.draw(15, height - 350, 300, 300);

        main.textAlign(main.CENTER);
        main.textSize(30);
        main.fill(0);
        main.text(Title, width / 2, 50);


        main.textSize(25);
        main.fill(0);
        main.text(heading1, width / 2, height - 100);

        main.textSize(20);
        main.fill(25);
        main.text(heading2, width / 2, height - 70);

        main.textAlign(main.LEFT);
        main.textSize(14);


        main.noStroke();
        for(double datum : data)
            num += datum;

        num = 360 / num;

        int h = height / 2 - 100;
        for(int i = 0; i < data.length; i++) {
            angle_2 = angle_1;
            angle_1 += data[i] * num;
            int sectorColour = colours[i];


            main.fill(sectorColour);
            main.arc(width / 2 - 100, height / 2, 300, 300, PApplet.radians((float) angle_2), PApplet.radians((float) angle_1));

            main.fill(sectorColour);
            main.ellipse(width / 2 + 120, h, 20, 20);

            main.fill(0);
            main.text(data_name[i], width / 2 + 140, h + 2);
            h += 40;

        }

    }

}
