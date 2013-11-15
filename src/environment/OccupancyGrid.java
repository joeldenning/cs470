package environment;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Joel
 * Date: 9/28/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class OccupancyGrid extends HashMap<OccupancyGrid.Coordinate, Boolean> {

    public static final int SIZE_OF_GRID = 100;

	private boolean occupied;
    private int x, y;

    public OccupancyGrid(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setOccupied(int x, int y, boolean occupied) {
		Coordinate coordinate = new Coordinate(x, y);
		this.put(coordinate, occupied);
	}

	public boolean isOccupied(int x, int y) {
		return this.get(new Coordinate(x, y));
	}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static class Coordinate {
		private int x, y;

		public Coordinate(int x, int y) {
			this.x = x;
			this. y = y;
		}
	}
}
