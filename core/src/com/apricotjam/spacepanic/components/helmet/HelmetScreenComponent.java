package com.apricotjam.spacepanic.components.helmet;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetWorld;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

public class HelmetScreenComponent implements Component {
	public ObjectMap<Resource, Float> resourceCount = new ObjectMap<Resource, Float>();
	public ObjectMap<Resource, Float> maxCount = new ObjectMap<Resource, Float>();
	public Queue<HelmetSystem.LED_Message> messages = new Queue<HelmetSystem.LED_Message>();
	
	public HelmetScreenComponent() {
		maxCount.put(Resource.OXYGEN, 1.0f);
		maxCount.put(Resource.OIL, 1.0f);
		maxCount.put(Resource.RESOURCE2, 1.0f);
		maxCount.put(Resource.RESOURCE3, 1.0f);
		
		resourceCount.put(Resource.OXYGEN, 1.0f);
		resourceCount.put(Resource.OIL, 1.0f);
		resourceCount.put(Resource.RESOURCE2, 1.0f);
		resourceCount.put(Resource.RESOURCE3, 1.0f);
	}
}
