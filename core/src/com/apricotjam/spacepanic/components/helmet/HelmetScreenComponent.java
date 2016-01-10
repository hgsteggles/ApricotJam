package com.apricotjam.spacepanic.components.helmet;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;

public class HelmetScreenComponent implements Component {
	public ObjectMap<Resource, Float> resourceCount = new ObjectMap<Resource, Float>();
	
	public HelmetScreenComponent() {
		resourceCount.put(Resource.OXYGEN, 25.0f);
		resourceCount.put(Resource.OIL, 20.0f);
		resourceCount.put(Resource.RESOURCE2, 15.0f);
		resourceCount.put(Resource.RESOURCE3, 10.0f);
	}
}
