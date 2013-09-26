package environment;

public class Action {

	private Type type;
	
	public String toBZFlagString() {
		return null;
	}
	
	private enum Type {
		SHOOT, SPEED, ANG_VEL
	}
}
