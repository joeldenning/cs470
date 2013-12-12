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

        Point bulletIntersection = kalmanAgent.getBulletIntersection();
        bulletIntersection.x += 400;
        bulletIntersection.y += 400;
        if( bulletIntersection.x >= 0 && bulletIntersection.y >= 0 ) {
            g.setColor(Color.GREEN);
            g.fillRect(bulletIntersection.x, bulletIntersection.y, 5, 5);
        }

        Point pointTurningTowards = kalmanAgent.getPointTurningTowards();
        pointTurningTowards.x += 400;
        pointTurningTowards.y *= -1;
        pointTurningTowards.y += 400;
        if( pointTurningTowards.x >= 0 && pointTurningTowards.y >= 0 ) {
            g.setColor(Color.RED);
            g.fillRect(pointTurningTowards.x, pointTurningTowards.y, 5, 5);
        }

        Point ourPosition = kalmanAgent.getMyPosition();
        ourPosition.x += 400;
        ourPosition.y *= -1;
        ourPosition.y += 400;
        if( ourPosition.x >= 0 && ourPosition.y >= 0 ) {
            g.setColor(Color.BLACK);
            g.fillRect(ourPosition.x, ourPosition.y, 5, 5);
        }

    }

}