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
	private final double maxVelocity = .3;
	
	private Carousel target;
	int lastXValue;
	boolean mouseDown = false;

	public MouseBehavior(Carousel carousel) {
		this.target = carousel;
		
		//rotate when mouse dragged
		target.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				mouseDown = true;
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
					double velocity = distance / ((double)target.getOffsetWidth()) * -4.0;
					if (velocity > maxVelocity)
						velocity = maxVelocity;
					if (velocity < -maxVelocity)
						velocity = -maxVelocity;
					target.setVelocity(velocity);
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
