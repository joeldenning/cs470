package agent.gridvisualization;

import agent.Rect;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 12/8/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class KalmanPanelTest {
	@Test
	public void test() throws InterruptedException {
		GridVisualizationThread thread = new GridVisualizationThread();
		thread.setKalmanAgent(new MockKalmanAgent(1));
		thread.start();
        while( true )
    		thread.updateKalman();

	}
}
