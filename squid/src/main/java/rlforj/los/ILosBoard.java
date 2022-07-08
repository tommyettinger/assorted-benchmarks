package rlforj.los;

/**
 *  An interface board that allows visibility alogithms to 
 * decide which points are in the board, which points are
 * obstacles to this form of visibility, and visit those points
 * on the board.
 * @author sdatta
 *
 */
public interface ILosBoard
{

	/**
	 * Is the location (x, y) inside the board ?
	 * Note: If a point is outside, any radially 
	 * outward points are not checked, so the area must 
	 * be concave.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(int x, int y);
	
	/**
	 * Is the location (x, y) an obstacle
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isObstacle(int x, int y);
	
	/**
	 * Location (x,y) is visible
	 * Visit the location (x,y)
	 * 
	 * This can involve saving the points in a collection,
	 * setting flags on a 2D map etc.
	 * 
	 * @param x
	 * @param y
	 */
	public void visit(int x, int y);
	
}
