package com.reveregroup.carousel.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;

public class MouseTracker {
//	private  double accelValue;
	private Carousel carousel;
//	private double velocity;
	int lastXValue;
//	private CTimer ctimer;
	boolean mouseDown = false;
	boolean timerRun = false;
	public MouseTracker(Carousel theCarousel){		
		this.carousel = theCarousel;
		carousel.addMouseDownHandler(new MouseDownHandler(){
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				// TODO Auto-generated method stub
				if((event.getNativeButton() & NativeEvent.BUTTON_LEFT) != 0){
					lastXValue = event.getX();
					carousel.setVelocity(0.0);
				}
			}
		});
		carousel.addMouseMoveHandler(new MouseMoveHandler(){
			public void onMouseMove(MouseMoveEvent event) {
				// TODO Auto-generated method stub		
				//log(Integer.toString(event.getNativeButton()));
				if(mouseDown == true){
					int distance = event.getX() - lastXValue;
					carousel.setVelocity(distance/50.0);
					lastXValue = event.getX();
				}
			}
		});	
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			   public void onPreviewNativeEvent(NativePreviewEvent event) {
			    // TODO Auto-generated method stub
			    if (event.getTypeInt() == Event.ONMOUSEUP) {
			     mouseDown = false;
			    }
			   }
		});		
	}
	
//	private class CTimer extends Timer{
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			carousel.rotateBy(velocity);
//			setVelocity(velocity*.90);
//			
//		}
//	}
//	private void setVelocity(double velocity) {
//		this.velocity = velocity;
//		if (velocity  > -.01 && velocity < .01) {
//			if (timerRun){
//				ctimer.cancel(); 
//				timerRun = false;
//			}
//			this.velocity = 0;
//		} else if (!timerRun) {		
//			ctimer.scheduleRepeating(33);
//			timerRun = true;
//			ctimer.run();
//		}
//	}
}
