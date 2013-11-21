package agent;

import environment.Action;
import environment.AttemptedAction;
import environment.Environment;
import environment.Obstacle;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbdennin
 * Date: 11/15/13
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GridFilterControllerAgent extends GridFilterAgent {

    protected Set<Obstacle> obstacles = new HashSet<Obstacle>();
    private static double BEL_THRESH = 0.7;

    public GridFilterControllerAgent(int tankIndex) {
        super(tankIndex);
    }

    @Override
    public List<Action> getActions(Environment environment) {
        if( grid == null ) {
            int size = Integer.parseInt(environment.getConstant("worldsize"));
            grid = new double[size][size];
            for (int x = 0; x < size; x++)
            {
            	for (int y = 0; y < size; y++)
                    grid[x][y]=.5;
            }
            truePositive = Double.parseDouble(environment.getConstant("truepositive"));
            trueNegative = Double.parseDouble(environment.getConstant("truenegative"));
        }
        return super.getActions(environment);
    }

    @Override
    public void processAttemptedActions(List<AttemptedAction> attemptedActions) {
        analyzeForObstacles();
        updateOpenGL();
    }

    private void analyzeForObstacles() {
        //TODO Brian
    	//This may need to run through twice to catch leftover rectangles
    	
    	//find largest rectangle, algorithm from http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529
    	//extended to find all rectangles
    	Stack<Rect> bestFound = new Stack<Rect>();//This is a stack as the algorithm moves along x linearly
    	Stack<NewRect> partail_rect = new Stack<NewRect>();
    	int[] cache = new int[grid.length];
    	for (int i = 0; i<cache.length; i++)
    		cache[i] = 0;
    	for (int x = grid.length -1; x >= 0; x--) {
    		update_cache(cache, x);
    		int width = 0;
    		for (int y = 0; y < grid.length; y++) {
    			if (cache[y] > width) { //open rectangle?
    				partail_rect.push(new NewRect(y, width));
    				width = cache[y];
    			}
    			else if (cache[y] < width) { //close rectangle?
    				NewRect popped;
    				do {
    					popped = partail_rect.pop();
    					Rect newRect = new Rect(x,popped.y,x+width-1,y-1);
    					if (bestFound.empty()) {
    						bestFound.push(newRect);
    					}
    					else {
	    					Rect lastRect = bestFound.peek();
	    					if (lastRect.interesect(newRect)) { //does this take any cells used by the last rectangle we found?
	    						if (lastRect.area() < width*(y-popped.y)) {
	    							bestFound.pop();
	    							bestFound.push(newRect);
	    						}
	    					}
	    					else {
	    						bestFound.push(newRect);//we can always push
	    					}
    					}
    					width = popped.width;
    				} while (cache[y] < width);
    				
    				width = cache[y];
    				if (width != 0) {
    					partail_rect.push(new NewRect(popped.y,width));//re-push
    				}
    			}
    		}
    	}
    	
    	//add found rectanlges
    	obstacles.clear();
    	while (!bestFound.empty()) {
    		Rect aRect = bestFound.pop();
    		List<Point2D.Double> corners = new ArrayList<Point2D.Double>();
    		corners.add(new Point2D.Double(aRect.llx-grid.length/2,aRect.lly-grid.length/2));
    		corners.add(new Point2D.Double(aRect.urx-grid.length/2,aRect.lly-grid.length/2));
    		corners.add(new Point2D.Double(aRect.llx-grid.length/2,aRect.ury-grid.length/2));
    		corners.add(new Point2D.Double(aRect.urx-grid.length/2,aRect.ury-grid.length/2));
    		obstacles.add(new Obstacle(corners));
    	}
    }
    
//    private boolean[][] blackWhiteGrid(){
//    	boolean[][] toReturn = new boolean[grid.length][grid.length];
//    	for 
//    	
//    	return toReturn;
//    }

    private void update_cache(int[] cache, int x) {
		for (int y = 0; y < grid.length; y++) {
			if (grid[x][y] > BEL_THRESH) {
				cache[y] += 1;
			}
			else {
				cache[y] = 0;
			}
		}
	}

	private void updateOpenGL() {
        //TODO Joel
    }
    
}
