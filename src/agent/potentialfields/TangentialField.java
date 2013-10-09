package agent.potentialfields;

import environment.Obstacle;
import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TangentialField extends PotentialField{

	private static final double OBSTACLE_CONST = -1;
	private static final double TANK_CONST = -1;
	private double fieldReach;
	private double badX;
	private double badY;
	private double alpha;
	
    public TangentialField(Obstacle obstacle, Tank myself) {
        super(myself);
        
        //Find average of corners for center location
        int numOfCorners = obstacle.getNumOfCorners();
        badX = 0;
        badY = 0;
        for (int i = 0; i < numOfCorners; i++) {
        	badX += obstacle.getCorner(i).x;
        	badY += obstacle.getCorner(i).y;
        }
        badX = badX/numOfCorners;
        badY = badY/numOfCorners;
        
        //Rough estimate on radius
        fieldReach = 60;
        
        alpha = OBSTACLE_CONST;
    }

    public TangentialField(Tank otherTank, Tank myself) {
        super(myself);
        
        fieldReach = 20;
        badX = otherTank.getX();
        badY = otherTank.getY();
        alpha = TANK_CONST;
    }

    @Override
    public TankVector toTankVector() {
        //TODO Brian
    	double distance = Math.sqrt(Math.pow(badX-myself.getX(), 2) + Math.pow(badY-myself.getY(), 2));
    	if (distance > fieldReach) {
    		return new TankVector(0,myself.getAngle());
    	}
    	//				This will return angle from x-axis
    	double angle = Math.atan2(badY-myself.getY(), badX-myself.getX()) + Math.PI/2;
    	double magnitude = alpha*(fieldReach - distance);
    	magnitude = Math.max(magnitude,-1);
    	
    	return new TankVector(magnitude,angle);
    }
}
