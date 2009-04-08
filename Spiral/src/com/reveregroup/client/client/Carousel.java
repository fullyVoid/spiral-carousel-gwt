package com.reveregroup.client.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class Carousel extends AbsolutePanel {
	private List<Photo> photos;

	private Image[] images;

	private final int totalRotations;

	private int rotationIncrement;

	private Timer timer;

	private int direction;
	
	private int photoIndex = 0;

	public Carousel() {
		direction = 0;
		timer = new CarouselTimer();		
		this.setWidth("800");
		this.setHeight("400");
		this.getElement().setAttribute("style", "z-index:" + "100");
		this.rotationIncrement = 0;
		this.totalRotations = 5;
		images = new Image[8];
		for (int i = 0; i < images.length; i++) {
			images[i] = new Image();
			this.add(images[i]);
		}
	}

	private void placeImages() {
		// Places images in the correct spots
		double degreeOffset = 0.0;
		degreeOffset = direction
				* (((Math.PI / 4) / totalRotations) * rotationIncrement);
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
				placeImages();
			} else {
				rotationIncrement = 0;
				timer.cancel();
				if (direction == -1) {
					// Next
					Image temp = images[0];
					for (int i = 0; i < images.length - 1; i++) {
						images[i] = images[i + 1];
					}
					//update from large array
					photoIndex = (photoIndex+1) % photos.size();
					int pIndex = (photoIndex+7)%10;
					temp.setUrl(photos.get(pIndex).getUrl());
					images[images.length-1] = temp;
				} else if (direction == 1) {
					// Previous
					Image temp = images[images.length-1];
					for (int i = images.length - 1; i > 0; i--) {
						images[i] = images[i - 1];
					}
					//update from large array
					
					photoIndex = (photoIndex-1);
					if(photoIndex < 0){
						photoIndex+=photos.size();
					}
					temp.setUrl(photos.get(photoIndex).getUrl());
					images[0] = temp;					
				}
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
}
