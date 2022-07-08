package rlforj.los;

import java.util.List;
import java.util.Vector;

import rlforj.math.Point2I;
import rlforj.util.BresenhamLine;

/**
 * Bresenham LOS.
 * Tries to reach destination along first path. If 
 * obstacled, shifts to alternate path. If that is blocked,
 * shift to first path again. Fails only if both are blocked
 * at a point.
 * @author sdatta
 *
 */
public class BresOpportunisticLos implements ILosAlgorithm
{

	private Vector<Point2I> path;
	
	public boolean existsLineOfSight(ILosBoard b, int startX, int startY,
			int x1, int y1, boolean calculateProject)
	{
		int dx=startX-x1, dy=startY-y1;
		int adx=dx>0?dx:-dx, ady=dy>0?dy:-dy;
		int len=(adx>ady?adx:ady) + 1;
		
		if(calculateProject)
			path=new Vector<Point2I>(len);
		
		int[] px=new int[len], py=new int[len];
		int[] px1=null, py1=null;
		px1=new int[len]; py1=new int[len];
		
		//Compute both paths
		BresenhamLine.plot(startX, startY, x1, y1, px, py);
		BresenhamLine.plot(x1, y1, startX, startY, px1, py1);

		boolean los=false;
		boolean alternatePath=false;
		for(int i=0; i<len; i++) {
			// Have we reached the end ? In that case quit
			if(px[i]==x1 && py[i]==y1) {
				if(calculateProject){
					path.add(new Point2I(px[i], py[i]));
				}
				los=true;
				break;
			}
			// if we are on alternate path, is the path clear ?
			if(alternatePath && !b.isObstacle(px1[len-i-1], py1[len-i-1])) {
				if(calculateProject)
					path.add(new Point2I(px1[len-i-1], py1[len-i-1]));
				continue;
			} else
				alternatePath=false;//come back to ordinary path
			
			//if on ordinary path, or alternate path was not clear
			if(!b.isObstacle(px[i], py[i])) {
				if(calculateProject) {
					path.add(new Point2I(px[i], py[i]));
				}
				continue;
			}
			//if ordinary path wasnt clear
			if(!b.isObstacle(px1[len-i-1], py1[len-i-1])) {
				if(calculateProject)
					path.add(new Point2I(px1[len-i-1], py1[len-i-1]));
				alternatePath=true;//go on alternate path
				continue;
			}
			if(calculateProject)
				path.add(new Point2I(px1[len-i-1], py1[len-i-1]));
			break;
		}
		
		return los;
	}

	public List<Point2I> getProjectPath()
	{
		return path;
	}

}
