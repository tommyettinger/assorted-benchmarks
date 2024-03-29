package rlforj.math;

import java.io.Serializable;

/**
 * A class encapsulating a 2D point, as integers
 * 
 * (Reason for existance: java.awt.Point uses double 
 * and I wanted speed.)
 * @author sdatta
 *
 */
public class Point2I implements Serializable
{
	public int x;

	public int y;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1360560915480181893L;

	public Point2I(int x, int y)
	{
		this.x=x; this.y=y;
	}

	/**
	 * Uses x*127+y as hash; different from what was originally here.
	 */
	public int hashCode()
	{
		return (x<<7)-x+y;//x*prime+y
	}
	
	public String toString()
	{
		return "Point2I[ "+x+", "+y+" ]";
	}
	public int getIx()
	{
		return x;
	}

	public int getIy()
	{
		return y;
	}
}
