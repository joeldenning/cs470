package agent.potentialfields;

import environment.Base;
import environment.Flag;
import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttractiveField extends PotentialField {

	private double radius;
	private double goalX;
	private double goalY;
	
    public AttractiveField(Flag flag, Tank myself) {
        super(myself);
        radius = 1;
        goalX = flag.getX();
        goalY = flag.getY();
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
        
    }

    @Override
    public TankVector toTankVector() {
        //TODO Brian
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
