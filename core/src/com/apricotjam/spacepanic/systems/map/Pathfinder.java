package com.apricotjam.spacepanic.systems.map;

import java.util.ArrayList;

import com.badlogic.gdx.math.GridPoint2;

public class Pathfinder {

	private class TreeNode {
		public GridPoint2 pos;
		public int distance;
		public TreeNode par = null;
		public ArrayList<TreeNode> children = new ArrayList<TreeNode>();

		public TreeNode(GridPoint2 pos, int distance) {
			this.pos = pos;
			this.distance = distance;
		}
	}

	private static final int[] XOFFSET = {-1, +1, 0, 0};
	private static final int[] YOFFSET = {0, 0, -1, +1};

	private int width;
	private int height;
	private int maxDistance;

	public void setOffset(GridPoint2 offset) {
		this.offset = offset;
	}

	private GridPoint2 offset = new GridPoint2();

	public Pathfinder(int width, int height, int maxDistance) {
		this.width = width;
		this.height = height;
		this.maxDistance = maxDistance;
	}

	public ArrayList<GridPoint2> calculatePath(int[][] maze, GridPoint2 offsetStart, GridPoint2 offestEnd) {
		GridPoint2 start = new GridPoint2(offsetStart.x - offset.x, offsetStart.y - offset.y);
		GridPoint2 end = new GridPoint2(offestEnd.x - offset.x, offestEnd.y - offset.y);
		if (!pathable(maze, end.x, end.y)) {
			return new ArrayList<GridPoint2>();
		}
		TreeNode root = new TreeNode(new GridPoint2(end.x, end.y), 0);
		boolean[][] visited = new boolean[width][height];
		ArrayList<TreeNode> toCheck = new ArrayList<TreeNode>();
		addNeighbours(root, maze, visited, toCheck);
		while (toCheck.size() > 0) {
			TreeNode nextNode = toCheck.get(0);
			if (nextNode.pos.x == start.x && nextNode.pos.y == start.y) {
				return getPath(nextNode);
			}
			if (nextNode.distance < maxDistance) {
				addNeighbours(nextNode, maze, visited, toCheck);
			}
			toCheck.remove(nextNode);
		}
		return new ArrayList<GridPoint2>();
	}

	private void addNeighbours(TreeNode node, int[][] maze, boolean[][] visited, ArrayList<TreeNode> toCheck) {
		for (int i = 0; i < 4; i++) {
			int x = node.pos.x + XOFFSET[i];
			int y = node.pos.y + YOFFSET[i];
			if (x < 0 || x >= width || y < 0 || y >= height) {
				continue;
			}
			if (pathable(maze, x, y) && !visited[x][y]) {
				TreeNode newNode = addNode(node, new GridPoint2(x, y));
				toCheck.add(newNode);
				visited[x][y] = true;
			}
		}
	}

	private TreeNode addNode(TreeNode par, GridPoint2 childPoint) {
		TreeNode child = new TreeNode(childPoint, par.distance + 1);
		par.children.add(child);
		child.par = par;
		return child;
	}

	private ArrayList<GridPoint2> getPath(TreeNode node) {
		ArrayList<GridPoint2> path = new ArrayList<GridPoint2>();
		TreeNode inode = node;
		while (inode.par != null) {
			path.add(new GridPoint2(inode.par.pos.x + offset.x, inode.par.pos.y + offset.y));
			inode = inode.par;
		}
		return path;
	}

	private boolean pathable(int[][] maze, int x, int y) {
		return maze[x][y] != 1;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
}
