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
	private final int maxDist = 30;
	
	private Carousel target;
	int lastXValue;
	boolean mouseDown = false;
	boolean mouseMoved = false;

	public MouseBehavior(Carousel carousel) {
		this.target = carousel;
		
		//rotate when mouse dragged
		target.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
				mouseMoved = false;
				if ((event.getNativeButton() & NativeEvent.BUTTON_LEFT) != 0) {
					lastXValue = event.getX();
					target.setVelocity(0.0);
				}
			}
		});
		target.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (mouseDown == true) {
					int distance = event.getX() - lastXValue;
					if (distance > maxDist)
						distance = maxDist;
					if (distance < -maxDist)
						distance = -maxDist;
					target.setVelocity(distance / -50.0);
					lastXValue = event.getX();
					mouseMoved = true;
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
				if (!mouseMoved)
					target.rotateTo(event.getPhotoIndex());
			}
		});
	}
}
