package environment;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.lang.IndexOutOfBoundsException;

public class Base {
	private List<Point2D.Double> corners;
	
	public Base(List<Point2D.Double> corners) {
		this.corners = new ArrayList<Point2D.Double>(corners);
	}
	
	public int getNumOfCorners() {
		return corners.size();
	}
	
	public Point2D.Double getCorner(int i) {
		if (i < corners.size() && i >= 0) {
			return corners.get(i);
		}
		throw new IndexOutOfBoundsException();
	}
	/*public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}*/
}
