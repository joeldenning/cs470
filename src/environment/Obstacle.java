package environment;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Obstacle {

private List<Point2D.Double> corners;
	
	public Obstacle(List<Point2D.Double> corners) {
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
}
