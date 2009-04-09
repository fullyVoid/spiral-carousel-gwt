package com.reveregroup.client.client;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class Carousel extends AbsolutePanel {
	private List<Photo> photos;
	private Image[] images;

	private final int totalRotations;

	private double currentRotation;
	
	private int rotationIncrement;

	private Timer timer;

	private int direction;
	
	private int photoIndex = 0;

	public Carousel() {
		direction = 0;
		currentRotation = 0.0;
		timer = new CarouselTimer();		
		this.setWidth("800");
		this.setHeight("400");
		//this.getElement().setAttribute("style", "z-index:" + "100");
		this.rotationIncrement = 0;
		this.totalRotations = 5;
		this.getElement().getStyle().setProperty("MozUserSelect", "none");
		this.getElement().setAttribute("unselectable", "on");
		this.getElement().setAttribute("onselectstart", "return false;");
	
		images = new Image[8];
		for (int i = 0; i < images.length; i++) {
			images[i] = new Image();
			Utils.preventDrag(images[i]);
			Utils.preventSelection(images[i].getElement());
			this.add(images[i]);
		}
	}

	private void placeImages() {
		// Places images in the correct spots
		double degreeOffset = 0.0;
		double rotationDecimal = currentRotation - Math.floor(currentRotation);
		int wholeMovements = (int)Math.floor(currentRotation);
		this.setPhotoIndex(wholeMovements);		
		degreeOffset = -(rotationDecimal * ((Math.PI) / 4));
		//degreeOffset = direction * (((Math.PI / 4) / totalRotations) * rotationIncrement);
		for (int i = 0; i < images.length; i++) {
			double finalDegree = ((i * Math.PI) / 4) + degreeOffset;
			double scale = 0.0;
			double x = Math.sin(finalDegree);
			double y = -Math.cos(finalDegree);
			scale = Math.pow(2, y);
			int zindex = (int) (y * 10);
			zindex += 10;		
			images[i].getElement().getStyle().setProperty("zIndex",
					Integer.toString(zindex));
			// images[i].getElement().setAttribute("style","z-index:"+Integer.toString(zindex));
			images[i].setSize(Double.toString((80 * scale)), Double
					.toString(80 * scale));
			int xcoord = (int) (x * 300) + 400;
			xcoord -= 40 * scale;
			int ycoord = (int) (y * 75) + 100;
			ycoord -= 40 * scale;
			this.setWidgetPosition(images[i], xcoord, ycoord);
		}
	}

	public void prev() {
		direction = 1;
		timer.scheduleRepeating(5);
	}

	public void next() {
		direction = -1;
		timer.scheduleRepeating(5);
	}

	private class CarouselTimer extends Timer {
		public void run() {
			if ((rotationIncrement + 1) < totalRotations) {
				rotationIncrement++;
				currentRotation = ((double)photoIndex) + -((double)direction) * ((double)(rotationIncrement) / (double)(totalRotations));
				if(currentRotation < 0){
					currentRotation +=photos.size();					
				}
				placeImages();
			} else {
				rotationIncrement = 0;
				timer.cancel();
				currentRotation = photoIndex + -direction;
				if(currentRotation < 0){
					currentRotation +=photos.size();					
				}
//				if (direction == -1) {
//					// Next
//					Image temp = images[0];
//					for (int i = 0; i < images.length - 1; i++) {
//						images[i] = images[i + 1];
//					}
//					//update from large array
//					photoIndex = (photoIndex+1) % photos.size();
//					int pIndex = (photoIndex+7)%10;
//					temp.setUrl(photos.get(pIndex).getUrl());
//					images[images.length-1] = temp;
//				} else if (direction == 1) {
//					// Previous
//					Image temp = images[images.length-1];
//					for (int i = images.length - 1; i > 0; i--) {
//						images[i] = images[i - 1];
//					}
//					//update from large array
//					
//					photoIndex = (photoIndex-1);
//					if(photoIndex < 0){
//						photoIndex+=photos.size();
//					}
//					temp.setUrl(photos.get(photoIndex).getUrl());
//					images[0] = temp;					
//				}
				placeImages();
			}
		}
	}
	public void setPhotos(List<Photo> photos){
		this.photos = photos;
		for (int i = 0; i < images.length; i++) {
			images[i].setUrl(photos.get(i).getUrl());
		}
		placeImages();
	}

	private void setPhotoIndex(int photoIndex) {
		if(this.photoIndex == photoIndex){
			return;
		}else{
			this.photoIndex = photoIndex;
			//Loop through and switch out all of the new images
			for(int i = 0; i < images.length;i++){
				images[i].setUrl(photos.get((photoIndex+i)%photos.size()).getUrl());
			}
		}
	}

	public double getCurrentRotation() {
		return currentRotation;
	}
	public void rotateTo(double rotations){
		if(rotations < 0){
			rotations +=photos.size();					
		}
		if(rotations >= photos.size()){
			rotations -=photos.size();
		}
		currentRotation = rotations;
		placeImages();
	}
	public void rotateBy(double rotations){
		rotateTo(currentRotation+rotations);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {		
		return addDomHandler(handler, ClickEvent.getType());
	}
	
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
	
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}
	
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}
	
}
