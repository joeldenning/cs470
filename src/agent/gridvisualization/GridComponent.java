package agent.gridvisualization;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 11/20/13
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridComponent extends JPanel {

	private double[][] grid;

	public void setGrid(double[][] grid) {
		this.grid = grid;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paint(g);
		for( int x=0; x<grid.length; x++ ) {
			if (grid[0] != null) {
				for (int y = 0; y <grid[0].length; y++ ) {
					double inverseProb = 1 - grid[x][y];
					long rgb = Math.round(inverseProb*255d);
					g.setColor(new Color(rgb, rgb, rgb));
					g.drawLine(x,y,x,y);
				}
			}
		}
	}
}
