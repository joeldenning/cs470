package agent.potentialfields;

import environment.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 10/4/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PotentialField {

    protected Tank myself;

    public PotentialField(Tank myself) {
        this.myself = myself;
    }

    public abstract TankVector toTankVector();
}
