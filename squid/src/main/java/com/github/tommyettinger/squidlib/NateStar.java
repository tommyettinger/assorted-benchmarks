package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.IntArray;

/**
 * From <a href="https://gist.github.com/NathanSweet/7587981">a gist posted by Nathan Sweet in 2014</a>.
 */
public class NateStar {
	private final int columns, rows;
	private final BinaryHeap<PathNode> open;
	private final PathNode[] nodes;
	int runID;
	private final IntArray path = new IntArray();
	private int targetX, targetY;

	public NateStar(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		open = new BinaryHeap<>(columns * 4, false);
		nodes = new PathNode[columns * rows];
	}

	/**
	 * Returns x,y pairs that are the path from the target to the start.
	 */
	public IntArray getPath(int startX, int startY, int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;

		path.clear();
		open.clear();

		runID++;
		if (runID < 0) runID = 1;

		int index = startY * columns + startX;
		PathNode root = nodes[index];
		if (root == null) {
			root = new PathNode(0);
			root.x = startX;
			root.y = startY;
			nodes[index] = root;
		}
		root.parent = null;
		root.pathCost = 0;
		open.add(root, 0);

		int lastColumn = columns - 1, lastRow = rows - 1;
		while (open.size > 0) {
			PathNode node = open.pop();
			if (node.x == targetX && node.y == targetY) {
				while (node != root) {
					path.add(node.x);
					path.add(node.y);
					node = node.parent;
				}
				break;
			}
			node.closedID = runID;
			int x = node.x;
			int y = node.y;
			if (x < lastColumn) {
				addNode(node, x + 1, y, 10);
				if (y < lastRow) addNode(node, x + 1, y + 1, 14); // Diagonals cost more, roughly equivalent to sqrt(2).
				if (y > 0) addNode(node, x + 1, y - 1, 14);
			}
			if (x > 0) {
				addNode(node, x - 1, y, 10);
				if (y < lastRow) addNode(node, x - 1, y + 1, 14);
				if (y > 0) addNode(node, x - 1, y - 1, 14);
			}
			if (y < lastRow) addNode(node, x, y + 1, 10);
			if (y > 0) addNode(node, x, y - 1, 10);
		}
		return path;
	}

	private void addNode(PathNode parent, int x, int y, int cost) {
		if (!isValid(x, y)) return;

		int pathCost = parent.pathCost + cost;
		float score = pathCost + Math.abs(x - targetX) + Math.abs(y - targetY);

		int index = y * columns + x;
		PathNode node = nodes[index];
		if (node != null && node.runID == runID) { // Node already encountered for this run.
			if (node.closedID != runID && pathCost < node.pathCost) { // Node isn't closed and new cost is lower.
				// Update the existing node.
				open.setValue(node, score);
				node.parent = parent;
				node.pathCost = pathCost;
			}
		} else {
			// Use node from the cache or create a new one.
			if (node == null) {
				node = new PathNode(0);
				node.x = x;
				node.y = y;
				nodes[index] = node;
			}
			open.add(node, score);
			node.runID = runID;
			node.parent = parent;
			node.pathCost = pathCost;
		}
	}

	protected boolean isValid(int x, int y) {
		return true;
	}

	public int getWidth() {
		return columns;
	}

	public int getHeight() {
		return rows;
	}

	private static class PathNode extends BinaryHeap.Node {
		int runID, closedID, x, y, pathCost;
		PathNode parent;

		public PathNode(float value) {
			super(value);
		}
	}
}