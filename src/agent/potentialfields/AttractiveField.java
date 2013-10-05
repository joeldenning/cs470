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

    public AttractiveField(Flag flag, Tank myself) {
        super(myself);

    }

    public AttractiveField(Base base, Tank myself) {
        super(myself);
    }

    @Override
    public TankVector toTankVector() {
        //TODO Brian
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
