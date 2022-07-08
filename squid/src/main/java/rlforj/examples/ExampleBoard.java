package rlforj.examples;

import rlforj.los.ILosBoard;
import rlforj.math.Point2I;
import squidpony.ArrayTools;
import squidpony.squidmath.GreasedRegion;

public class ExampleBoard implements ILosBoard  {

	public int w, h;
	
	public boolean[][] obstacles;
	public boolean[][] visited;

	public char visibleFloor='.', invisibleFloor=' ', invisibleWall=' ';
	
	public ExampleBoard(int w, int h) {
		this.w=w;
		this.h=h;
		
		obstacles = new boolean[w][h];
		visited = new boolean[w][h];
	}

	public ExampleBoard(GreasedRegion region) {
		this.w=region.width;
		this.h=region.height;

		obstacles = region.decode();
		visited = new boolean[w][h];
	}

	public void resetVisited()
	{
		visited = new boolean[w][h];
//		ArrayTools.fill(visited, false);
	}

	public void setObstacle(int x, int y) {
		obstacles[x][y]=true;
	}
	
	public boolean contains(int x, int y)
	{
		return x>=0 && y>=0 && x<w && y<h;
	}

	public boolean isObstacle(int x, int y)
	{
		return obstacles[x][y];
	}

	public void visit(int x, int y)
	{
		visited[x][y]=true;
	}
	
	public void print(int ox, int oy) {
		Point2I p=new Point2I(0, 0);
		for(int j=0; j<h; j++) {
			for(int i=0; i<w; i++) {
				p.x = i;
				p.y = j;
				if (i == ox && j == oy)
					System.out.print('@');
				else
					System.out.print(visited[i][j] ? (obstacles[i][j] ? '#'
							: visibleFloor) : (obstacles[i][j] ? invisibleWall
							: invisibleFloor));
			}
			System.out.println();
		}
	}

	public boolean wasVisited(int i, int j)
	{
		return visited[i][j];
	}
	
}