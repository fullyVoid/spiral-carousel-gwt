package com.reveregroup.carousel.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.reveregroup.carousel.client.events.PhotoClickEvent;
import com.reveregroup.carousel.client.events.PhotoClickHandler;

public class MouseBehavior {
	private final double maxVelocity = .03;
	
	private Carousel target;
	int lastXValue;
	long lastTime;
	boolean mouseDown = false;
	
	int avgDist;
	int avgTime;

	public MouseBehavior(Carousel carousel) {
		this.target = carousel;
		
		//rotate when mouse dragged
		target.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				if ((event.getNativeButton() & NativeEvent.BUTTON_LEFT) != 0) {
					lastXValue = event.getX();
					lastTime = System.currentTimeMillis();
					avgDist = 0;
					avgTime = 0;
					target.setVelocity(0.0);
				}
			}
		});
		target.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (mouseDown == true) {
					long curTime = System.currentTimeMillis();
					int distance = event.getX() - lastXValue;
					int ticks = (int) (curTime - lastTime);
					lastTime = curTime;
					
					if ((distance < 0 && avgDist > 0) || (distance > 0 && avgDist < 0)) {
						avgDist = distance;
					} else {
						avgDist = (avgDist == 0) ? distance : ((4 * avgDist + distance) / 5);
					}
					avgTime = (avgTime == 0) ? ticks : ((4 * avgTime + ticks) / 5);
					
					//Utils.log(distance + ":" + avgDist + " / " + ticks + ":" + avgTime);

					if (avgTime != 0) {
						double velocity = avgDist / ((double)avgTime) / ((double)target.getOffsetWidth()) * -4.0;
						if (velocity > maxVelocity)
							velocity = maxVelocity;
						if (velocity < -maxVelocity)
							velocity = -maxVelocity;
						target.setVelocity(velocity);
					}
					lastXValue = event.getX();
				}
			}
		});
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONMOUSEUP) {
					mouseDown = false;
				}
			}
		});
		
		//Rotate to an image when clicked.
		target.addPhotoClickHandler(new PhotoClickHandler() {
			public void photoClicked(PhotoClickEvent event) {
				target.rotateTo(event.getPhotoIndex());
			}
		});
	}
}
