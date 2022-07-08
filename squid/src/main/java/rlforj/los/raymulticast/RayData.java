package rlforj.los.raymulticast;

/**
 * A RayData object encapsulates information regarding a 2D ray from the origin, 
 * where the 'Loc' fields represent their respective components of the ray.
 * 
 * A RayData object also encodes data allowing it to propagate visibility 
 * (or lack of) to other rays. The obscurity effect vector is carried by the 
 * {@code xObsc} and {@code yObsc}. Information about the error from the vector 
 * is carried by the {@code xErrObsc} and {@code yErrObsc} fields. 
 * 
 * The Input fields store references to the input data, from which the rest of 
 * the data can be generated. These aren't necessary, since one could look them up 
 * elsewhere (perhaps from an array), but they are convenient.
 * 
 * The {@link #method obscure()} method contains the visibility (obscurity) function
 * which is somewhat arbitrary. If the {@code ignore} flag is true, then this object 
 * should also be treated as non-visible. 
 * 
 */
public final class RayData {
	int xLoc;
	int yLoc;
	
	int xObsc;
	int yObsc;
	int xErrObsc;
	int yErrObsc;
	
	RayData xInput;
	RayData yInput;

	boolean added; // true if we have added this to the perimeter
	boolean ignore; // true if there is no need to expand this ray

	
	public RayData(int xLoc, int yLoc) {
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}
	
	
	public boolean obscure() {
		return ((xErrObsc > 0) && (xErrObsc <= xObsc)) || 
		       ((yErrObsc > 0) && (yErrObsc <= yObsc));
	}
	
	
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("(" + xLoc + "," + yLoc + ") ");
		data.append(": " + xObsc + "|" + yObsc + "|" + xErrObsc + "|" + yErrObsc);
		return data.toString();
	}
	
	
	/**
	 * <p>A useful method for printing results in text form.</p>
	 * 
	 * @return A character representing the status of this object. 
	 * 'I' -> ignored
	 * 'X' -> obscured from x only
	 * 'Y' -> obscured from y only
	 * 'Z' -> obscured from both x and y
	 * 'A' -> not obscure with recessive x obscurity
	 * 'B' -> not obscure with recessive y obscurity
	 * 'C' -> not obscure with both recessive x and y obscurity
	 */
	public char toChar() {
		boolean xObscure = ((xErrObsc > 0) && (xErrObsc <= xObsc));
		boolean yObscure = ((yErrObsc > 0) && (yErrObsc <= yObsc));
		if(ignore) return 'I';
		else if(xObscure && yObscure) return 'Z';
		else if(xObscure) return 'X';
		else if(yObscure) return 'Y';
		else {
			boolean xRecessive = (xErrObsc <= 0) && (xObsc > 0);
			boolean yRecessive = (yErrObsc <= 0) && (yObsc > 0);
			if(xRecessive && yRecessive) return 'C';
			else if(xRecessive) return 'A';
			else if(yRecessive) return 'B';
			else return 'O';
		}
	}

}

