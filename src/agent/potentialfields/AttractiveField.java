package agent.potentialfields;

import environment.Base;
import environment.Flag;
import environment.Tank;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttractiveField extends PotentialField {

	private static final double FLAG_CONST = 1.25;
	private static final double BASE_CONST = 1.5;
	private double radius;
	private double goalX;
	private double goalY;
	private double alpha;
	
    public AttractiveField(Flag flag, Tank myself) {
        super(myself);
        radius = 1;//What should this be?
        goalX = flag.getX();
        goalY = flag.getY();
        alpha = FLAG_CONST;
    }

    public AttractiveField(Point2D point, Tank myself) {
        super(myself);
        goalX = point.getX();
        goalY = point.getY();
        alpha = FLAG_CONST;
    }

    public AttractiveField(Base base, Tank myself) {
        super(myself);
        
        //Find average of corners for center location
        int numOfCorners = base.getNumOfCorners();
        goalX = 0;
        goalY = 0;
        for (int i = 0; i < numOfCorners; i++) {
        	goalX += base.getCorner(i).x;
        	goalY += base.getCorner(i).y;
        }
        goalX = goalX/numOfCorners;
        goalY = goalY/numOfCorners;
        
        //Rough estimate on radius
        radius = Math.abs((base.getCorner(0).x - base.getCorner(1).x)/2 + (base.getCorner(0).y - base.getCorner(1).y)/2) - 30;
        
        alpha = BASE_CONST;
    }

    @Override
    public TankVector toTankVector() {
    	double distance = Math.sqrt(Math.pow(goalX-myself.getX(), 2) + Math.pow(goalY-myself.getY(), 2));
    	if (distance < radius) {
    		return new TankVector(0,myself.getAngle());
    	}
    	//				This will return angle from x-axis
    	double angle = Math.atan2(goalY-myself.getY(), goalX-myself.getX());
    	double magnitude = alpha*(distance-radius) + 15;
    	magnitude = Math.min(600, magnitude);

    	return new TankVector(magnitude,angle);
    }
}
