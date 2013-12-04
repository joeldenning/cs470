package agent.gridvisualization;

import agent.KalmanAgent;
import agent.Rect;
import environment.Obstacle;

import javax.swing.*;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 11/20/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridVisualizationThread extends Thread {

	private GridComponent gridComponent;
    private KalmanPanel kalmanPanel;
	private JFrame frame;
    private KalmanAgent kalmanAgent;

    public void setKalmanAgent(KalmanAgent kalmanAgent) {
        this.kalmanAgent = kalmanAgent;
    }

    @Override
	public synchronized void start() {
		Dimension dimension = new Dimension(800, 800);
		frame = new JFrame();
		frame.setSize(dimension);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		gridComponent = new GridComponent();
		gridComponent.setPreferredSize(dimension);
		gridComponent.setMinimumSize(dimension);
		gridComponent.setSize(dimension);
		gridComponent.setBounds(0, 0, 800, 800);

        kalmanPanel = new KalmanPanel(kalmanAgent);
        kalmanPanel.setPreferredSize(dimension);
        kalmanPanel.setMinimumSize(dimension);
        kalmanPanel.setSize(dimension);
        kalmanPanel.setBounds(0, 0, 800, 800);
        
		frame.getContentPane().add(kalmanPanel);

		frame.setVisible(true);

        updateKalman();
	}

    private void updateKalman() {
        while( true ) {
            try {
                Thread.sleep(500l);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            frame.repaint();
        }
    }

    public synchronized void update(double[][] grid, Set<Rect> obstacles) {
		gridComponent.setGrid(grid);
        gridComponent.setObstacles(obstacles);
		frame.repaint();
	}

}
