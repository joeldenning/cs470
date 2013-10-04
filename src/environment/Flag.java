package environment;

public class Flag {

	private String possessingColor;
	private double x, y;

	public Flag(String possessingColor, double x, double y) {
		this.possessingColor = possessingColor;
		this.x = x;
		this.y = y;
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
}
