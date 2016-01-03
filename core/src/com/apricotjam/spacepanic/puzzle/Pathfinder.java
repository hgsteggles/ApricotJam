package com.apricotjam.spacepanic.puzzle;

import java.awt.*;
import java.util.ArrayList;

public class Pathfinder {

	private class TreeNode {
		public Point pos;
		public int distance;
		public TreeNode par = null;
		public ArrayList<TreeNode> children = new ArrayList<TreeNode>();

		public TreeNode(Point pos, int distance) {
			this.pos = pos;
			this.distance = distance;
		}
	}

	private static final int[] XOFFSET = {-1, +1, 0, 0};
	private static final int[] YOFFSET = {0, 0, -1, +1};

	private int width;
	private int height;
	private int maxDistance;

	public Point getOffset() {
		return offset;
	}

	public void setOffset(Point offset) {
		this.offset = offset;
	}

	public void setOffset(int x, int y) {
		this.offset.move(x, y);
	}

	private Point offset = new Point();

	public Pathfinder(int width, int height, int maxDistance) {
		this.width = width;
		this.height = height;
		this.maxDistance = maxDistance;
	}

	public ArrayList<Point> calculatePath(int[][] maze, Point offsetStart, Point offestEnd) {
		Point start = new Point(offsetStart.x - offset.x, offsetStart.y - offset.y);
		Point end = new Point(offestEnd.x - offset.x, offestEnd.y - offset.y);
		if (maze[end.x][end.y] == 1) {
			return new ArrayList<Point>();
		}
		TreeNode root = new TreeNode(new Point(end.x, end.y), 0);
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
		return new ArrayList<Point>();
	}

	private void addNeighbours(TreeNode node, int[][] maze, boolean[][] visited, ArrayList<TreeNode> toCheck) {
		for (int i = 0; i < 4; i++) {
			int x = node.pos.x + XOFFSET[i];
			int y = node.pos.y + YOFFSET[i];
			if (x < 0 || x >= width || y < 0 || y >= height) {
				continue;
			}
			if (maze[x][y] == 0 && !visited[x][y]) {
				TreeNode newNode = addNode(node, new Point(x, y));
				toCheck.add(newNode);
				visited[x][y] = true;
			}
		}
	}

	private TreeNode addNode(TreeNode par, Point childPoint) {
		TreeNode child = new TreeNode(childPoint, par.distance + 1);
		par.children.add(child);
		child.par = par;
		return child;
	}

	private ArrayList<Point> getPath(TreeNode node) {
		ArrayList<Point> path = new ArrayList<Point>();
		TreeNode inode = node;
		while (inode.par != null) {
			path.add(new Point(inode.par.pos.x + offset.x, inode.par.pos.y + offset.y));
			inode = inode.par;
		}
		return path;
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
