package agent.gridvisualization;

import agent.KalmanAgent;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 12/3/13
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class KalmanPanel extends JPanel {


    private KalmanAgent kalmanAgent;


    public KalmanPanel(KalmanAgent kalmanAgent) {
        this.kalmanAgent = kalmanAgent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.


        Point curPosition = kalmanAgent.getEnemyPosition(0);
        //TODO draw current position circle

        for( int millis=100; millis<500; millis = millis+100 ) {
            Point point = kalmanAgent.getEnemyPosition(millis);
            //TODO draw future circle
        }
    }
}
