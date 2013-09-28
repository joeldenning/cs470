package environment;

public class Tank {

	private int index;
	private String callSign;
	private Status status;
	private String shotsAvailable;
	private double timeToReload;
	private String flag;
	private double x;
	private double y;
	private double angle;
	private double vx;
	private double vy;
	private double angularVelocity;

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setStatus(String status) {
		this.status = Status.valueOf(status);
	}

	public Status getStatus() {
		return status;
	}

	public void setShotsAvailable(String shotsAvailable) {
		this.shotsAvailable = shotsAvailable;
	}

	public String getShotsAvailable() {
		return shotsAvailable;
	}

	public void setTimeToReload(double timeToReload) {
		this.timeToReload = timeToReload;
	}

	public double getTimeToReload() {
		return timeToReload;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getAngle() {
		return angle;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVx() {
		return vx;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public double getVy() {
		return vy;
	}

	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public double getAngularVelocity() {
		return angularVelocity;
	}

	private enum Status {

	}
}
