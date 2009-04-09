package com.reveregroup.client.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class MouseTracker {
	private  double accelValue;
	private Carousel carousel;
	private double velocity;
	int lastXValue;
	private CTimer ctimer;
	boolean mouseDown = false;
	boolean timerRun = false;
	public MouseTracker(Carousel theCarousel){		
		this.carousel = theCarousel;
		ctimer = new CTimer();		
		HandlerRegistration  mouseDownHandler= carousel.addMouseDownHandler(new MouseDownHandler(){
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				// TODO Auto-generated method stub
				if((event.getNativeButton() & NativeEvent.BUTTON_LEFT) != 0){
					lastXValue = event.getX();
				}
			}
		});
		HandlerRegistration mouseMoveHandler = carousel.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event) {
				// TODO Auto-generated method stub		
				//log(Integer.toString(event.getNativeButton()));
				if(mouseDown == true){
					int distance = event.getX() - lastXValue;
					System.out.println("Distance: " + distance);
					setVelocity(distance/100.0);
					lastXValue = event.getX();
				}
			}
		});
		HandlerRegistration mouseUpHandler = carousel.addMouseUpHandler(new MouseUpHandler(){
			public void onMouseUp(MouseUpEvent event) {
				// TODO Auto-generated method stub
				mouseDown = false;
				setVelocity(0.0);
			}
		});
	}
	
	private native void log(String s) /*-{
		if(console){
		console.log(s);
		}
	}-*/;
	private class CTimer extends Timer{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			carousel.rotateBy(velocity);
			setVelocity(velocity*.90);
			
		}
	}
	private void setVelocity(double velocity) {
		this.velocity = velocity;
		if(velocity  > -.03 && velocity < .03){
			if(timerRun == true){ctimer.cancel();}
			velocity = 0;
		}else if(timerRun == false){		
			ctimer.scheduleRepeating(33);
		}
	}
}
