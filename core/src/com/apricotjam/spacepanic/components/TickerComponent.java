package com.apricotjam.spacepanic.components;

import com.apricotjam.spacepanic.interfaces.EventInterface;
import com.badlogic.ashley.core.Component;

public class TickerComponent implements Component {
	public boolean tickerActive = false;
	public boolean finishActive = false;
	
	public float duration = 0f;
	public float interval = 1f;
	public float totalTimeLeft = 0f;
	public float tickTimeLeft = 0f;
	
	public EventInterface ticker;
	
	public EventInterface finish;
	
	public void start() {
		tickerActive = true;
		finishActive = true;
		totalTimeLeft = duration;
		tickTimeLeft = interval;
	}
	
	public void stop() {
		tickerActive = false;
		finishActive = false;
		totalTimeLeft = 0;
		tickTimeLeft = 0;
	}
	
}
