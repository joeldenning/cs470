package environment;

public class Flag {

	private String possessingColor;
	private double x, y, vx, vy;

	public Flag(String possessingColor, double x, double y, double vx, double vy) {
		this.possessingColor = possessingColor;
		this.x = x;
		this.y = y;
		this.vx = x;
		this.vy = y;
	}

	public String getPossessingColor() {
		return possessingColor;
	}

	public void setPossessingColor(String possessingColor) {
		this.possessingColor = possessingColor;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}
}
