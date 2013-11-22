package agent.gridvisualization;

import agent.Rect;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 11/20/13
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridComponent extends JPanel {

	private double[][] grid;
    private Set<Rect> obstacles, lastObstacles;
    private boolean obstaclesChanged = false;

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
//                        System.out.println("probability = "+grid[x][y]+", rgb = "+rgb);
                        g.drawRect(x, 800-y, 1, 1);
                    }
                }
            }
        }
        if (obstaclesChanged) {
            obstaclesChanged = false;
            if (lastObstacles != null) {
                g.setColor(Color.BLACK);
                for (Rect obstacle : lastObstacles) {
                    g.drawRect(obstacle.llx, 800 - obstacle.ury, obstacle.urx - obstacle.llx, obstacle.ury - obstacle.lly);
                }
            }
            if (obstacles != null) {
                g.setColor(Color.GREEN);
                for (Rect obstacle : obstacles) {
                    g.drawRect(obstacle.llx, 800 - obstacle.ury, obstacle.urx - obstacle.llx, obstacle.ury - obstacle.lly);
                }
            }
        }
    }

    public void setObstacles(Set<Rect> obstacles) {
        lastObstacles = this.obstacles;
        this.obstacles = obstacles;
        obstaclesChanged = true;
    }
}
