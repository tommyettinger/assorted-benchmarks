package rlforj.los;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.github.yellowstonegames.grid.Radius;
import rlforj.math.Line2I;
import rlforj.math.Point2I;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Precise permissive visibility algorithm.
 * 
 * Refer to
 * <a href="https://roguebasin.roguelikedevelopment.org/index.php?title=Precise_Permissive_Field_of_View">this page</a>.
 * Copyright (c) 2007, Jonathon Duerig. Licensed under the BSD
 * license. See LICENSE.txt for details.
 * 
 * TODO : Do multitile organism by replacing offsetT(0,1)(1, 0) by offsetT(0,
 * size.y) (size.x, 0). Also need to consider border tiles.
 * 
 * @author sdatta
 * 
 */
public class PrecisePermissive implements IFovAlgorithm, ILosAlgorithm
{

	static class PermissiveMask
	{
		/*
		 * Do not interact with the members directly. Use the provided
		 * functions.
		 */
		int north;

		int south;

		int east;

		int west;

		// int width;
		// int height;
		int[] mask;

		public FovType fovType;

		public int distPlusOneSq;

		ILosBoard board;
	}

	static class FovState
	{
		Point2I source = new Point2I(0, 0);

		PermissiveMask mask = new PermissiveMask();

		Point2I quadrant = new Point2I(0, 0);

		Point2I extent = new Point2I(0, 0);

		Point2I dest = new Point2I(0, 0);

		public int quadrantIndex;

		ILosBoard board;

		public boolean isLos = false;

		// System.out.println("calcfovq called");
		LinkedList<Bump> steepBumps = new LinkedList<>();
		LinkedList<Bump> shallowBumps = new LinkedList<>();
		// activeFields is sorted from shallow-to-steep.
		LinkedList<Field> activeFields = new LinkedList<>();

	};

	static class Bump
	{
		public Bump()
		{
		}

		Point2I location;

		Bump parent = null;
		
		public String toString() {
			return location.toString()+" p( "+parent+" ) ";
		}
	}

	static class Field
	{
		public Field(Field f)
		{
			steep = new Line2I(new Point2I(f.steep.near.x, f.steep.near.y),
					new Point2I(f.steep.far.x, f.steep.far.y));
			shallow = new Line2I(
					new Point2I(f.shallow.near.x, f.shallow.near.y),
					new Point2I(f.shallow.far.x, f.shallow.far.y));
			steepBump = f.steepBump;
			shallowBump = f.shallowBump;
		}

		public Field()
		{
			// TODO Auto-generated constructor stub
		}

		Line2I steep = new Line2I(new Point2I(0, 0), new Point2I(0, 0));

		Line2I shallow = new Line2I(new Point2I(0, 0), new Point2I(0, 0));

		Bump steepBump;

		Bump shallowBump;
		
		public String toString() {
			return "[ steep "+steep+",  shallow "+shallow+"]";
		}
	}

	private Vector<Point2I> path;
	private ILosAlgorithm fallBackLos=new BresLos(true);
	private final FovState state = new FovState();

	final Point2I quadrants[] = { new Point2I(1, 1), new Point2I(-1, 1),
			new Point2I(-1, -1), new Point2I(1, -1) };


	void calculateFovQuadrant(final FovState state)
	{
		state.shallowBumps.clear();
		state.steepBumps.clear();
		state.activeFields.clear();
		Field active = new Field();
		active.shallow.near.x = 0;
		active.shallow.near.y = 1;
		active.shallow.far.x = state.extent.x;
		active.shallow.far.y = 0;
		active.steep.near.x = 1;
		active.steep.near.y = 0;
		active.steep.far.x = 0;
		active.steep.far.y = state.extent.y;
		state.activeFields.add(active);

		state.dest.x = 0;
		state.dest.y = 0;


		// Visit the source square exactly once (in quadrant 1).
		if (state.quadrant.x == 1 && state.quadrant.y == 1)
		{
			actIsBlocked(state, state.dest);
		}

		CLikeIterator<Field> currentField = new CLikeIterator<>(
				state.activeFields.listIterator());
		int maxI = state.extent.x + state.extent.y;
		// For each square outline
		for (int i = 1; i <= maxI && !state.activeFields.isEmpty(); ++i)
		{
			int startJ = max(0, i - state.extent.x);
			int maxJ = min(i, state.extent.y);
			// System.out.println("Startj "+startJ+" maxj "+maxJ);
			// Visit the nodes in the outline
			for (int j = startJ; j <= maxJ && !currentField.isAtEnd(); ++j)
			{
				// System.out.println("i j "+i+" "+j);
				state.dest.x = i - j;
				state.dest.y = j;
				visitSquare(state, state.dest, currentField);
			}
			// System.out.println("Activefields size "+activeFields.size());
			currentField = new CLikeIterator<Field>(state.activeFields
					.listIterator());
		}
	}

	void visitSquare(final FovState state, final Point2I dest,
					 CLikeIterator<Field> currentField)
	{
//		System.out.println("-> "+steepBumps+" - "+shallowBumps);
		// System.out.println("visitsq called "+dest);
		// The top-left and bottom-right corners of the destination square.
		Point2I topLeft = new Point2I(dest.x, dest.y + 1);
		Point2I bottomRight = new Point2I(dest.x + 1, dest.y);

		// Field currFld=null;

		while (!currentField.isAtEnd()
				&& currentField.getCurrent().steep
						.isBelowOrContains(bottomRight))
		{
//			System.out.println("currFld.steep.isBelowOrContains(bottomRight) "
//					+ currentField.getCurrent().steep
//							.isBelowOrContains(bottomRight));
			// case ABOVE
			// The square is in case 'above'. This means that it is ignored
			// for the currentField. But the steeper fields might need it.
			// ++currentField;
			currentField.gotoNext();
		}
		if (currentField.isAtEnd())
		{
//			System.out.println("currentField.isAtEnd()");
			// The square was in case 'above' for all fields. This means that
			// we no longer care about it or any squares in its diagonal rank.
			return;
		}

		// Now we check for other cases.
		if (currentField.getCurrent().shallow.isAboveOrContains(topLeft))
		{
			// case BELOW
			// The shallow line is above the extremity of the square, so that
			// square is ignored.
//			System.out.println("currFld.shallow.isAboveOrContains(topLeft) "
//					+ currentField.getCurrent().shallow);
			return;
		}
		// The square is between the lines in some way. This means that we
		// need to visit it and determine whether it is blocked.
		boolean isBlocked = actIsBlocked(state, dest);
		if (!isBlocked)
		{
			// We don't care what case might be left, because this square does
			// not obstruct.
			return;
		}

		if (currentField.getCurrent().shallow.isAbove(bottomRight)
				&& currentField.getCurrent().steep.isBelow(topLeft))
		{
			// case BLOCKING
			// Both lines intersect the square. This current field has ended.
			currentField.removeCurrent();
		} else if (currentField.getCurrent().shallow.isAbove(bottomRight))
		{
			// case SHALLOW BUMP
			// The square intersects only the shallow line.
			addShallowBump(topLeft, currentField.getCurrent(), state.shallowBumps);
			checkField(currentField);
		} else if (currentField.getCurrent().steep.isBelow(topLeft))
		{
			// case STEEP BUMP
			// The square intersects only the steep line.
			addSteepBump(bottomRight, currentField.getCurrent(), state.steepBumps);
			checkField(currentField);
		} else
		{
			// case BETWEEN
			// The square intersects neither line. We need to split into two
			// fields.
			Field steeperField = currentField.getCurrent();
			Field shallowerField = new Field(currentField.getCurrent());
			currentField.insertBeforeCurrent(shallowerField);
			addSteepBump(bottomRight, shallowerField, state.steepBumps);
			currentField.gotoPrevious();
			if (!checkField(currentField)) // did not remove
				currentField.gotoNext();// point to the original element
			addShallowBump(topLeft, steeperField, state.shallowBumps);
			checkField(currentField);
		}
	}

	boolean checkField(CLikeIterator<Field> currentField)
	{
		// If the two slopes are colinear, and if they pass through either
		// extremity, remove the field of view.
		Field currFld = currentField.getCurrent();
		boolean ret = false;

		if (currFld.shallow.doesContain(currFld.steep.near)
				&& currFld.shallow.doesContain(currFld.steep.far)
				&& (currFld.shallow.doesContain(new Point2I(0, 1)) || currFld.shallow
						.doesContain(new Point2I(1, 0))))
		{
//			System.out.println("removing "+currentField.getCurrent());
			currentField.removeCurrent();
			ret = true;
		}
		// System.out.println("CheckField "+ret);
		return ret;
	}

	void addShallowBump(final Point2I point, Field currFld,
			LinkedList<Bump> shallowBumps)
	{
//		System.out.println("Adding shallow "+point);
		// First, the far point of shallow is set to the new point.
		currFld.shallow.far = point;
		// Second, we need to add the new bump to the shallow bump list for
		// future steep bump handling.
		Bump bump = new Bump();
		bump.location = point;
		bump.parent = currFld.shallowBump;
		shallowBumps.add(bump);
		currFld.shallowBump = bump;
		// Now we have too look through the list of steep bumps and see if
		// any of them are below the line.
		// If there are, we need to replace near point too.
		Bump currentBump = currFld.steepBump;
		while (currentBump != null)
		{
			if (currFld.shallow.isAbove(currentBump.location))
			{
				currFld.shallow.near = currentBump.location;
			}
			currentBump = currentBump.parent;
		}
	}

	void addSteepBump(final Point2I point, Field currFld,
			LinkedList<Bump> steepBumps)
	{
//		System.out.println("Adding steep "+point);
		currFld.steep.far = point;
		Bump bump = new Bump();
		bump.location = point;
		bump.parent = currFld.steepBump;
		steepBumps.add(bump);
		currFld.steepBump = bump;
		// Now look through the list of shallow bumps and see if any of them
		// are below the line.
		Bump currentBump = currFld.shallowBump;
		while (currentBump != null)
		{
			if (currFld.steep.isBelow(currentBump.location))
			{
				currFld.steep.near = currentBump.location;
			}
			currentBump = currentBump.parent;
		}
	}

	boolean actIsBlocked(final FovState state, final Point2I pos)
	{
		Point2I adjustedPos = new Point2I(pos.x * state.quadrant.x
				+ state.source.x, pos.y * state.quadrant.y + state.source.y);

		if(!state.board.contains(adjustedPos.x, adjustedPos.y))
			return false;//we are getting outside the board
		
		// System.out.println("actIsBlocked "+adjustedPos.x+" "+adjustedPos.y);

		// if ((state.quadrant.x * state.quadrant.y == 1
		// && pos.x == 0 && pos.y != 0)
		// || (state.quadrant.x * state.quadrant.y == -1
		// && pos.y == 0 && pos.x != 0)
		// || doesPermissiveVisit(state.mask, pos.x*state.quadrant.x,
		// pos.y*state.quadrant.y) == 0)
		// {
		// // return result;
		// }
		// else
		// {
		// board.visit(adjustedPos.x, adjustedPos.y);
		// // return result;
		// }
		/*
		 * ^ | 2 | <-3-+-1-> | 4 | v
		 * 
		 * To ensure all squares are visited before checked ( so that we can
		 * decide obstacling at visit time, eg walls destroyed by explosion) ,
		 * visit axes 1,2 only in Q1, 3 in Q2, 4 in Q3
		 */
		if (state.isLos // In LOS calculation all visits allowed
				|| state.quadrantIndex == 0 // can visit anything from Q1
				|| (state.quadrantIndex == 1 && pos.x != 0) // Q2 : no Y axis
				|| (state.quadrantIndex == 2 && pos.y != 0) // Q3 : no X axis
				|| (state.quadrantIndex == 3 && pos.x != 0 && pos.y != 0)) // Q4 : no X or Y axis
			if (doesPermissiveVisit(state.mask, pos.x * state.quadrant.x, pos.y
					* state.quadrant.y) == 1)
			{
				state.board.visit(adjustedPos.x, adjustedPos.y,
						Radius.CIRCLE.radius(state.source.x, state.source.y, adjustedPos.x, adjustedPos.y));
			}
		return state.board.isObstacle(adjustedPos.x, adjustedPos.y);
	}

	void permissiveFov(int sourceX, int sourceY)
	{
		state.source.x = sourceX;
		state.source.y = sourceY;
		PermissiveMask mask = state.mask;
		state.board = mask.board;
		state.isLos = false;

		final int quadrantCount = 4;


//		final Point2I extents[] = { new Point2I(mask.east, mask.north),
//				new Point2I(mask.west, mask.north),
//				new Point2I(mask.west, mask.south),
//				new Point2I(mask.east, mask.south) };

		for (int quadrantIndex = 0; quadrantIndex < quadrantCount; ++quadrantIndex)
		{
			state.quadrant = quadrants[quadrantIndex];
			state.extent.x = mask.east;
			state.extent.y = mask.north;//extents[quadrantIndex];
			state.quadrantIndex = quadrantIndex;
			calculateFovQuadrant(state);
		}
	}

	int doesPermissiveVisit(PermissiveMask mask, int x, int y)
	{
		if (mask.fovType == FovType.SQUARE)
			return 1;
		else if (mask.fovType == FovType.CIRCLE)
		{
			if (x * x + y * y < mask.distPlusOneSq)
				return 1;
			else
				return 0;
		}
		return 1;
	}

	public void visitFieldOfView(ILosBoard b, int x, int y, int distance)
	{
		PermissiveMask mask = state.mask;
		mask.east = mask.north = mask.south = mask.west = distance;
		mask.mask = null;
		mask.fovType = FovType.CIRCLE;
		mask.distPlusOneSq = (distance+1) * (distance+1);
		mask.board = b;
		permissiveFov(x, y);
	}

	/**
	 * Algorithm inspired by
	 * http://groups.google.com/group/rec.games.roguelike.development/browse_thread/thread/f3506215be9d9f9a/2e543127f705a278#2e543127f705a278
	 * 
	 * @see rlforj.los.ILosAlgorithm#existsLineOfSight(ILosBoard, int,
	 *      int, int, int, boolean)
	 */
	public boolean existsLineOfSight(ILosBoard b, int startX, int startY,
			int x1, int y1, boolean calculateProject)
	{
		PermissiveMask mask = new PermissiveMask();
		int dx = x1 - startX;
		int adx = dx > 0 ? dx : -dx;
		int dy = y1 - startY;
		int ady = dy > 0 ? dy : -dy;
		RecordQuadrantVisitBoard fb = new RecordQuadrantVisitBoard(b, startX, startY, x1, y1,
				calculateProject);
		mask.east = mask.west = adx;
		mask.north = mask.south = ady;
		mask.mask = null;
		mask.fovType = FovType.SQUARE;
		mask.distPlusOneSq = 0;
		mask.board = fb;

		FovState state = new FovState();
		state.source = new Point2I(startX, startY);
		state.mask = mask;
		state.board = fb;
		state.isLos = true;
		state.quadrant = new Point2I(dx < 0 ? -1 : 1, dy < 0 ? -1 : 1);
		state.quadrantIndex = 0;

		state.steepBumps.clear();
		state.shallowBumps.clear();
		state.activeFields.clear();

		Field active = new Field();
		active.shallow.near.x = 0;
		active.shallow.near.y = 1;
		active.shallow.far.x = adx + 1;
		active.shallow.far.y = 0;
		active.steep.near.x = 1;
		active.steep.near.y = 0;
		active.steep.far.x = 0;
		active.steep.far.y = ady + 1;
		state.activeFields.add(active);

		Point2I dest = new Point2I(0, 0);

		Line2I stopLine = new Line2I(new Point2I(0, 1), new Point2I(adx, ady + 1)), startLine = new Line2I(
				new Point2I(1, 0), new Point2I(adx + 1, ady));

		// Visit the source square exactly once (in quadrant 1).
		actIsBlocked(state, dest);

		CLikeIterator<Field> currentField = new CLikeIterator<>(
				state.activeFields.listIterator());
		int maxI = adx + ady;
		// For each square outline
		int lastStartJ = -1;
		Point2I topLeft = new Point2I(0, 0), bottomRight = new Point2I(0, 0);
		for (int i = 1; i <= maxI && !state.activeFields.isEmpty(); ++i)
		{
			// System.out.println("i "+i);
			int startJ = max(0, i - adx);
			startJ = max(startJ, lastStartJ - 1);
			int maxJ = min(i, ady);

			// System.out.println("Startj "+startJ+" maxj "+maxJ);
			// Visit the nodes in the outline
			int thisStartJ = -1;
			// System.out.println("startJ "+startJ+" maxJ "+maxJ);
			for (int j = startJ; j <= maxJ && !currentField.isAtEnd(); ++j)
			{
				// System.out.println("i j "+i+" "+j);
				dest.x = i - j;
				dest.y = j;
				topLeft.x = dest.x;
				topLeft.y = dest.y + 1;
				bottomRight.x = dest.x + 1;
				bottomRight.y = dest.y;
				// System.out.println(startLine+" "+topLeft+" "+stopLine+"
				// "+bottomRight);
				// System.out.println("isbelow "+startLine.isBelow(topLeft)+"
				// isabove "+stopLine.isAbove(bottomRight));
				if (startLine.isAboveOrContains(topLeft))
				{
					// not in range, continue
					// System.out.println("below start");
					continue;
				}
				if (stopLine.isBelowOrContains(bottomRight))
				{
					// done
					// System.out.println("Above stop ");
					break;
				}
				// in range
				if (thisStartJ == -1)
					thisStartJ = j;
				visitSquare(state, dest, currentField);
			}
			lastStartJ = thisStartJ;
			// System.out.println("Activefields size "+activeFields.size());
			currentField = new CLikeIterator<>(state.activeFields
					.listIterator());
		}

		if (calculateProject)
		{
			if(fb.endVisited)
				path = GenericCalculateProjection.calculateProjecton(startX, startY, x1, y1, fb);
			else {
				fallBackLos.existsLineOfSight(b, startX, startY, x1, y1, true);
				path=(Vector<Point2I>)fallBackLos.getProjectPath();
			}
//			calculateProjecton(startX, startY, adx, ady, fb, state);
		}
		return fb.endVisited;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sid.los.ILosAlgorithm1#getProjectPath()
	 */
	public List<Point2I> getProjectPath()
	{
		return path;
	}

//	public static void main(String[] args)
//	{
//		ILosAlgorithm pp = new PrecisePermissive();
//		TestBoard b = new TestBoard(false);
//		// b.exception.add(new Point2I(10, 11));
//		// b.exception.add(new Point2I(11, 10));
//		// b.exception.add(new Point2I(14, 15));
//		b.exception.add(new Point2I(14, 18));
//		System.out.println(pp.existsLineOfSight(b, -10, -10, -19, -15, true));
//
//		System.out.println(pp.getProjectPathX());
//		System.out.println(pp.getProjectPathY());
//
//		System.out.println(b.visited);
//	}


}
