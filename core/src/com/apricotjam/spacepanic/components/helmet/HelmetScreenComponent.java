package com.apricotjam.spacepanic.components.helmet;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetWorld;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

public class HelmetScreenComponent implements Component {
	public ObjectMap<Resource, Integer> resourceCount = new ObjectMap<Resource, Integer>();
	public ObjectMap<Resource, Integer> maxCount = new ObjectMap<Resource, Integer>();
	public Queue<HelmetSystem.LED_Message> messages = new Queue<HelmetSystem.LED_Message>();
	
	public HelmetScreenComponent() {
		maxCount.put(Resource.OXYGEN, 4*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		maxCount.put(Resource.OIL, 3*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		maxCount.put(Resource.RESOURCE2, 2*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		maxCount.put(Resource.RESOURCE3, 1*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		
		resourceCount.put(Resource.OXYGEN, 4*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		resourceCount.put(Resource.OIL, 3*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		resourceCount.put(Resource.RESOURCE2, 2*HelmetWorld.FLUID_SEG_RSRC_COUNT);
		resourceCount.put(Resource.RESOURCE3, 1*HelmetWorld.FLUID_SEG_RSRC_COUNT);
	}
}
