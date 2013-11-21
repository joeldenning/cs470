package agent.gridvisualization;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 11/20/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridVisualizationThread extends Thread {

	private GridComponent gridComponent;
	private JFrame frame;

	@Override
	public synchronized void start() {
		Dimension dimension = new Dimension(800, 800);
		frame = new JFrame();
		frame.setSize(dimension);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		gridComponent = new GridComponent();
		gridComponent.setPreferredSize(dimension);
		gridComponent.setMinimumSize(dimension);
		gridComponent.setSize(dimension);
		gridComponent.setBounds(0, 0, 800, 800);
		frame.getContentPane().add(gridComponent);

		frame.setVisible(true);
	}

	public synchronized void updateGrid(double[][] grid) {
		gridComponent.setGrid(grid);
		frame.repaint();
	}
}
