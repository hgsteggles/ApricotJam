package com.apricotjam.spacepanic.components.helmet;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;

public class ResourcePipeComponent implements Component {
	public Resource resource = Resource.OXYGEN;
	public float minCount = 0;
	public float currCount = 0;
}
