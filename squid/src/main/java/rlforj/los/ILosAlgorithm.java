package rlforj.los;

import java.util.List;

import rlforj.math.Point2I;

/**
 * An interface for for LOS and projection
 * @author sdatta
 *
 */
public interface ILosAlgorithm
{

	/**
	 * Calculates if line of sight exists between point startX, startY and
	 * x1, y1. Optionally calculate the path of projection.
	 * 
	 * @param b The board to be visited.
	 * @param startX Starting position:x
	 * @param startY Starting position:y
	 * @param x1 Target location:x
	 * @param y1 Target location:y
	 * @param calculateProject Whether to also calculate the path from the 
	 * source to the target.
	 * @return
	 */
	public boolean existsLineOfSight(ILosBoard b, int startX,
			int startY, int x1, int y1, boolean calculateProject);

	/**
	 * Obtain the path of the projection calculated during the last call 
	 * to existsLineOfSight.
	 * @return
	 */
	public List<Point2I> getProjectPath();

}