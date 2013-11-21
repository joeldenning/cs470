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
    private static final int ZOOM_FACTOR = 1;

	public void setGrid(double[][] grid) {
		this.grid = grid;
	}

	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(grid != null) {
            for (int x = 0; x < grid.length; x++) {
                if (grid[0] != null) {
                    for (int y = 0; y < grid[0].length; y++) {
                        double inverseProb = 1 - grid[x][y];
                        int rgb = (int) Math.round(inverseProb * 255d);
                        Color gray = new Color(rgb, rgb, rgb);
                        g.setColor(gray);
                        int pixelX = ZOOM_FACTOR * x;
                        int pixelY = 800 - ZOOM_FACTOR * y;
//                        System.out.println("probability = "+grid[x][y]+", rgb = "+rgb);
                        g.drawRect(pixelX, pixelY, ZOOM_FACTOR, ZOOM_FACTOR);
                    }
                }
            }
        }
    }
}
