package agent.potentialfields;

import environment.Obstacle;
import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepulsiveField extends PotentialField {
	
	private static final double OBSTACLE_CONST = 2;
	private static final double TANK_CONST = -.4;
	private double fieldReach;
	private double badX;
	private double badY;
	private double alpha;
	
    public RepulsiveField(Obstacle obstacle, Tank myself) {
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
        fieldReach = 85;
        
        alpha = OBSTACLE_CONST;
    }

    public RepulsiveField(Tank otherTank, Tank myself) {
        super(myself);
        
        fieldReach = 3;
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
    	double angle = Math.atan2(badY-myself.getY(), badX-myself.getX());
    	double magnitude = alpha*(fieldReach - distance);

    	return new TankVector(magnitude,angle);
    }
}
