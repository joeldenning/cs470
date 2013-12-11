package agent.gridvisualization;

import agent.KalmanAgent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KalmanPanel extends JPanel {

    private KalmanAgent kalmanAgent;

    public KalmanPanel(KalmanAgent kalmanAgent) {
        this.kalmanAgent = kalmanAgent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.

        Point curPosition = kalmanAgent.getEnemyPosition(0);
        curPosition.x = curPosition.x + 400;
        curPosition.y = -curPosition.y + 400;
	    Color color = new Color(3, 100, 200);
	    g.setColor(color);
        g.fillRect(curPosition.x, curPosition.y, 4, 4);
//        System.out.println(curPosition);
        final int timeStep = 200;
        final int iters = 10;
        final int colorStep = 20;
        for( int iter = 1; iter <= iters; iter++ ) {
            Point point = kalmanAgent.getEnemyPosition(iter * timeStep);
            point.x = point.x + 400;
            point.y = -point.y + 400;

	        color = new Color(color.getRed()+colorStep, color.getGreen(), color.getBlue());
	        g.setColor(color);
	        g.fillRect(point.x, point.y, 4, 4);
//            System.out.println(point);
        }
        System.out.println("\n");
    }

}