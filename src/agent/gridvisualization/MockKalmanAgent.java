package agent.gridvisualization;

import agent.KalmanAgent;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 12/8/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockKalmanAgent extends KalmanAgent {

	private int original = 10;
	private boolean goingRight = true;
	protected MockKalmanAgent(int tankIndex) {
		super(tankIndex);
	}

	@Override
	public Point getEnemyPosition(long millisIntoFuture) {
		if( original < 10 )
			goingRight = true;
		else if( original > 100 )
			goingRight = false;
		if( goingRight )
			original += 2;
		else
			original -= 2;
		return new Point(original + (int)millisIntoFuture / 15, original );
	}
}
