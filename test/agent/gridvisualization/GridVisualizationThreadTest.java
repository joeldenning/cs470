package agent.gridvisualization;

import org.junit.Test;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 11/20/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridVisualizationThreadTest {
	@Test
	public void test() {
		GridVisualizationThread thread = new GridVisualizationThread();
		thread.start();
		final int size = 800;
		double[][] grid = new double[size][size];
		Random rand = new Random();
		for( int x=0; x<size; x++ ) {
			for( int y=0; y<size; y++ ) {
//				grid[x][y] = Math.round(rand.nextDouble());
                grid[x][y] = rand.nextDouble();
			}
		}
		thread.updateGrid(grid);
		while( true ) {}
	}
}
