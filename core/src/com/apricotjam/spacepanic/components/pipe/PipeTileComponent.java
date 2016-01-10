package com.apricotjam.spacepanic.components.pipe;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class PipeTileComponent implements Component {
	public byte mask = 0;
	
	public Entity[] neighbours = new Entity[]{null, null, null, null};
}