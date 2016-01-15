package com.apricotjam.spacepanic.systems.map;

import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.LineComponent;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;

public class Path {

	private static final int MAXPATH = 30;

	private ArrayList<Vector2> path = new ArrayList<Vector2>();
	private ArrayList<Entity> pathLines = new ArrayList<Entity>();
	private Entity cross = null;

	private final MapSystem mapSystem;
	private final Pathfinder pathfinder = new Pathfinder(Patch.PATCH_WIDTH * PatchConveyor.PATCHES_X, Patch.PATCH_HEIGHT * PatchConveyor.PATCHES_Y, MAXPATH);
	private final PatchConveyor patchConveyor;

	public Path(PatchConveyor patchConveyor, MapSystem mapSystem) {
		this.patchConveyor = patchConveyor;
		this.mapSystem = mapSystem;
	}

	public void update(Engine engine) {
		if (size() > 0) {
			LineComponent linec = ComponentMappers.line.get(pathLines.get(0));
			linec.start.set(mapSystem.getMapScreenComponent().playerPosition);
		}
	}

	public boolean calculateNew(Engine engine, Point start, Point end) {
		pathfinder.setOffset(patchConveyor.getOffset());
		ArrayList<Point> newPath = pathfinder.calculatePath(patchConveyor.getFullMaze(), start, end);
		if (newPath.size() > 0) {
			path.clear();
			for (Point p : newPath) {
				path.add(new Vector2(p.x, p.y));
			}
			removeFromEngine(engine);
			createLines(engine);
			cross = mapSystem.createCrossGood(end.x, end.y);
			addToEngine(engine);
			return true;
		} else {
			return false;
		}
	}

	private void createLines(Engine engine) {
		pathLines.clear();
		for (int i = 0; i < path.size(); i++) {
			pathLines.add(createLine(i));
		}
	}

	private Entity createLine(int i) {
		Vector2 start;
		if (i == 0) {
			start = mapSystem.getMapScreenComponent().playerPosition;
		} else {
			start = path.get(i - 1);
		}
		Vector2 end = path.get(i);
		return mapSystem.createLine(start, end, 0.1f, MapArt.mapLine);
	}

	public void addToEngine(Engine engine) {
		for (Entity e : pathLines) {
			engine.addEntity(e);
		}
		if (cross != null) {
			engine.addEntity(cross);
		}
	}

	public void removeFromEngine(Engine engine) {
		for (Entity e : pathLines) {
			engine.removeEntity(e);
		}
		if (cross != null) {
			engine.removeEntity(cross);
		}
	}

	public void softStop(Engine engine) {
		if (cross != null) {
			engine.removeEntity(cross);
			cross = null;
		}
		while (path.size() > 1) {
			path.remove(path.size() - 1);
			engine.removeEntity(pathLines.get(pathLines.size() - 1));
			pathLines.remove(pathLines.size() - 1);
		}
	}

	public void hardStop(Engine engine) {
		removeFromEngine(engine);
		pathLines.clear();
		path.clear();
		cross = null;
	}

	public Vector2 getNext() {
		return path.get(0);
	}

	public void legComplete(Engine engine) {
		engine.removeEntity(pathLines.get(0));
		pathLines.remove(0);
		path.remove(0);
		if (path.size() == 0 && cross != null) {
			engine.removeEntity(cross);
			cross = null;
		}
	}

	public int size() {
		return path.size();
	}
}
