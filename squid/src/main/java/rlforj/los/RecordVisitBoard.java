package rlforj.los;

import java.util.HashSet;
import java.util.Set;

import rlforj.math.Point2I;

/**
 * A LOS board that records points that were visited, while using another 
 * board to decide obstacles.
 * @author sdatta
 *
 */
public class RecordVisitBoard implements ILosBoard, 
			GenericCalculateProjection.VisitedBoard
{

	ILosBoard b;

	int sx, sy, sxy;

	int targetX, targetY;

	// int manhattanDist;
	Set<Point2I> visitedNotObs = new HashSet<Point2I>();

	boolean endVisited = false;

	boolean calculateProject;

	public RecordVisitBoard(ILosBoard b, int sx, int sy, int dx, int dy,
			boolean calculateProject)
	{
		super();
		this.b = b;
		this.sx = sx;
		this.sy = sy;
		sxy = sx + sy;
		this.targetX = dx;
		this.targetY = dy;

		this.calculateProject = calculateProject;
	}

	public boolean contains(int x, int y)
	{
		return b.contains(x, y);
	}

	public boolean isObstacle(int x, int y)
	{
		return b.isObstacle(x, y);
	}

	public void visit(int x, int y)
	{
		//			System.out.println("visited "+x+" "+y);
		if (x == targetX && y == targetY)
			endVisited = true;
		if (calculateProject && !b.isObstacle(x, y))
		{
			int dx = x - sx;
			dx = dx > 0 ? dx : -dx;
			int dy = y - sy;
			dy = dy > 0 ? dy : -dy;
			visitedNotObs.add(new Point2I(dx, dy));
		}
		//DEBUG
//		b.visit(x, y);
	}

	private Point2I visitedCheck=new Point2I(0, 0);
	public boolean wasVisited(int x, int y)
	{
		visitedCheck.x=x; visitedCheck.y=y;
		return visitedNotObs.contains(visitedCheck);
	}

}