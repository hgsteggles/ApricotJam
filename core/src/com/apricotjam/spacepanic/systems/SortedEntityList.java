package com.apricotjam.spacepanic.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class SortedEntityList implements EntityListener {
	private Family family;
	private Array<Entity> sortedEntities;
	private final ImmutableArray<Entity> entities;
	private boolean shouldSort;
	private Comparator<Entity> comparator;

	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
	 *
	 * @param family     The family of entities iterated over in this System
	 * @param comparator The comparator to sort the entities
	 * @param priority   The priority to execute this system with (lower means higher priority)
	 */
	public SortedEntityList(Family family, Comparator<Entity> comparator) {
		this.family = family;
		sortedEntities = new Array<Entity>(false, 16);
		entities = new ImmutableArray<Entity>(sortedEntities);
		this.comparator = comparator;
	}

	/**
	 * Call this if the sorting criteria have changed. The actual sorting will be delayed until the entities are processed.
	 */
	public void forceSort() {
		shouldSort = true;
	}

	private void sort() {
		if (shouldSort) {
			sortedEntities.sort(comparator);
			shouldSort = false;
		}
	}

	public void addedToEngine(Engine engine) {
		ImmutableArray<Entity> newEntities = engine.getEntitiesFor(family);
		sortedEntities.clear();
		if (newEntities.size() > 0) {
			for (int i = 0; i < newEntities.size(); ++i) {
				sortedEntities.add(newEntities.get(i));
			}
			sortedEntities.sort(comparator);
		}
		shouldSort = false;
		engine.addEntityListener(family, this);
	}

	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		sortedEntities.clear();
		shouldSort = false;
	}

	@Override
	public void entityAdded(Entity entity) {
		sortedEntities.add(entity);
		shouldSort = true;
	}

	@Override
	public void entityRemoved(Entity entity) {
		sortedEntities.removeValue(entity, true);
		shouldSort = true;
	}

	public Array<Entity> getSortedEntities() {
		sort();
		return sortedEntities;
	}

	/**
	 * @return set of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities() {
		sort();
		return entities;
	}

	/**
	 * @return the Family used when the system was created
	 */
	public Family getFamily() {
		return family;
	}
}
