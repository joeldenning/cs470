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
	    Color color = new Color(3, 1, 48);
	    g.setColor(color);
        g.fillRect(curPosition.x, curPosition.y, 2, 2);

        for( int millis=100; millis<500; millis = millis+100 ) {
            Point point = kalmanAgent.getEnemyPosition(millis);
	        color = new Color(color.getRed()+40, color.getGreen()+40, color.getBlue()+40);
	        g.setColor(color);
	        g.fillRect(point.x, point.y, 2, 2);
        }
    }

}