package com.reveregroup.client.client;

import java.util.List;

import org.apache.commons.el.ModulusOperator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Carousel extends AbsolutePanel {
	private List<Photo> photos;
	private Image[] images;

	private double currentRotation = 0.0;
	
	private int photoIndex = 0;
	
	private int carouselSize = 8;
	
	private int preLoadSize = 3;
	
	private Grid grid;
	
	private boolean focused;
	
	private int height;
	
	private int width;
	
	public Carousel() {
		this.setWidth("800");
		this.width = 800;
		this.setHeight("400");
		this.height = 400;
		this.getElement().getStyle().setProperty("MozUserSelect", "none");
		this.getElement().setAttribute("unselectable", "on");
		this.getElement().setAttribute("onselectstart", "return false;");
		
		images = new Image[this.carouselSize+(this.preLoadSize*2)];
		for (int i = 0; i < images.length; i++) {
			images[i] = new Image();
			Utils.preventDrag(images[i]);
			Utils.preventSelection(images[i].getElement());
			images[i].getElement().getStyle().setProperty("display", "none");
			images[i].addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Image img = (Image) event.getSource();					
					for (int i = 0; i < images.length; i++) {						
						if (images[i] == img) {
							int pIndex = i-preLoadSize + photoIndex;
							pIndex = Utils.modulus(pIndex, photos.size());
							
							//fire off photo clicked event
							PhotoClickEvent pcEvent = new PhotoClickEvent();
							pcEvent.setPhotoIndex(pIndex);
							pcEvent.setPhoto(photos.get(pIndex));
							fireEvent(pcEvent);																
							if(pIndex == getCurrentPhotoIndex()){
								//image is in front create panel to show
								focused = !focused;
								placeImages();
								//Window.alert("HELLO");
							}else{
								rotateTo(pIndex);
							}
							break;
						}
					}
				}
			});
			this.add(images[i]);
		}
		
		
//		this.grid = new Grid(1,14);
//		for(int i = 0; i < grid.getColumnCount();i++){
//			grid.setWidget(0, i, new Image());
//			((Image)(grid.getWidget(0, i))).setHeight("50");
//			((Image)(grid.getWidget(0, i))).setWidth("50");
//		}
//		RootPanel.get().add(grid);
	}

	private void placeImages() {
		// Places images in the correct spots
		double degreeOffset = 0.0;
		double rotationDecimal = currentRotation - Math.floor(currentRotation);
		int frontImage = 0;
		if(rotationDecimal < .5){
			frontImage = 4;
		}else{
			frontImage = 5;
		}
		
		int wholeMovements = (int)Math.floor(currentRotation);
		this.setPhotoIndex(wholeMovements);		
		degreeOffset = -(rotationDecimal * ((Math.PI) / 4));
		//degreeOffset = direction * (((Math.PI / 4) / totalRotations) * rotationIncrement);
		for (int i = 0; i < this.carouselSize; i++) {
			if(i == frontImage && focused){
				images[i+preLoadSize].setSize("", "");
				double height = images[i+preLoadSize].getHeight();
				double width = images[i+preLoadSize].getWidth();
				
				double aspectRatio = height / width;
				double containerAR = ((double)this.height) / ((double)this.width);
				
				if (aspectRatio >= containerAR){
					//limit height
					if(height > this.height){
						images[i+preLoadSize].setSize("", Integer.toString(this.height));
					}
				} else {
					//limit width
					if(width > this.width){
						images[i+preLoadSize].setSize(Integer.toString(this.width),"");
					}					
				}
				images[i+preLoadSize].getElement().getStyle().setProperty("zIndex", "21");
				int xcoord = (int)(this.width - width)/2;
				int ycoord = (int)(this.height - height)/2;
				this.setWidgetPosition(images[i+preLoadSize], xcoord, ycoord);				
			} else {
				double finalDegree = ((i * Math.PI) / 4) + degreeOffset;
				double scale = 0.0;
				double x = Math.sin(finalDegree);
				double y = -Math.cos(finalDegree);
				scale = Math.pow(2, y);
				int zindex = (int) (y * 10);
				zindex += 10;		
				images[i+preLoadSize].getElement().getStyle().setProperty("zIndex",
						Integer.toString(zindex));
				// images[i].getElement().setAttribute("style","z-index:"+Integer.toString(zindex));
				images[i+preLoadSize].setSize(Double.toString((80 * scale)), Double
						.toString(80 * scale));
				int xcoord = (int) (x * 300) + 400;
				xcoord -= 40 * scale;
				int ycoord = (int) (y * 75) + 100;
				ycoord -= 40 * scale;
				this.setWidgetPosition(images[i+preLoadSize], xcoord, ycoord);
			}
		}
//		for(int i = 0; i < grid.getColumnCount();i++){
//			((Image)(grid.getWidget(0, i))).setUrl(images[i].getUrl());
//		}
	}

	public void setPhotos(List<Photo> photos){
		this.photos = photos;
		for (int i = 0; i < images.length; i++) {
			int pIndex = photoIndex - preLoadSize + i;
			pIndex = Utils.modulus(pIndex,photos.size());			
			images[i].setUrl(photos.get(pIndex).getUrl());
		}
		for(int i = 0;i < carouselSize;i++){
			images[i+preLoadSize].getElement().getStyle().setProperty("display", "");
		}
		placeImages();
	}

	private void setPhotoIndex(int photoIndex) {
		if(this.photoIndex == photoIndex){
			return;
		}else{			
			int shiftOffset = photoIndex - this.photoIndex;
			if(shiftOffset < -(photos.size()/2)){
				shiftOffset += photos.size();
			}else if(shiftOffset > (photos.size()/2)){
				shiftOffset -= photos.size();
			}
			if (shiftOffset > 0) {				
				// Next
				//Creating temp array of images to hold shifted images
				Image[] temps = new Image[shiftOffset];
				for(int j = 0; j < temps.length; j++){
					temps[j] = images[j];
				}
				for (int i = 0; i < images.length - (shiftOffset); i++) {
					images[i] = images[i + (shiftOffset)];
				}
				//update from large array			
				for(int k = 0; k < temps.length;k++){
					int pIndex = photoIndex + carouselSize +  preLoadSize - shiftOffset + k;
					pIndex = Utils.modulus(pIndex, photos.size());
					images[k+images.length - shiftOffset] = temps[k];
					temps[k].setUrl(photos.get(pIndex).getUrl());
				}
			} else if (shiftOffset < 0) {
				shiftOffset *= -1;
				// Prev
				Image[] temps = new Image[shiftOffset];
				for(int j = 0; j < temps.length; j++){
					temps[j] = images[j+images.length - shiftOffset];
				}
				for (int i = images.length - 1; i >= shiftOffset; i--) {
					images[i] = images[i - shiftOffset];
				}
				//update from large array
				for(int k = 0; k < temps.length;k++){
					int pIndex = photoIndex - preLoadSize + k;
					pIndex = Utils.modulus(pIndex, photos.size());
					images[k] = temps[k];
					temps[k].setUrl(photos.get(pIndex).getUrl());
				}				
			}
			for(int i = 0; i < preLoadSize; i++)
			{
				images[i].getElement().getStyle().setProperty("display", "none");
				images[images.length-i-1].getElement().getStyle().setProperty("display", "none");
			}
			for(int i = 0; i < carouselSize; i++){
				images[i+preLoadSize].getElement().getStyle().setProperty("display", "");
			}
			this.photoIndex = photoIndex;			
		}
	}
	
	boolean timerOn;
	double velocity;
	Timer timer = new CTimer();
	
	double acceleration = .9;
	double velocityThreshold = .01;
	
	private class CTimer extends Timer {
		public void run() {
			setCurrentRotation(currentRotation + Utils.distanceForOneTick(velocity, acceleration));
			setVelocity(velocity * acceleration);
		}
	}
	
	public void setVelocity(double velocity) {
		this.velocity = velocity;
		if (velocity  > -velocityThreshold && velocity < velocityThreshold) {
			if (timerOn){
				timer.cancel(); 
				timerOn = false;
			}
			this.velocity = 0;
		} else if (!timerOn) {		
			timer.scheduleRepeating(33);
			timerOn = true;
			timer.run();
		}
	}

	public double getCurrentRotation() {
		return currentRotation;
	}
	public void setCurrentRotation(double value) {
		currentRotation = Utils.modulus(value, photos.size());
		placeImages();
	}
	
	public void rotateTo(double position) {
		double distance = Utils.modulus(position - 4, photos.size()) - currentRotation;
		if (distance > photos.size() / 2) {
			distance -= photos.size();
		} else if (distance < photos.size() / -2) {
			distance += photos.size();
		}
		setVelocity(Utils.velocityForDistance(distance, acceleration, velocityThreshold));
	}
	
	public void rotateBy(double distance) {
		setVelocity(Utils.velocityForDistance(distance, acceleration, velocityThreshold));
	}
	
	public void prev() {
		rotateTo(Math.round(currentRotation) - 1.0 + 4.0);
	}

	public void next() {
		rotateTo(Math.round(currentRotation) + 1.0 + 4.0);
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
	
	public int getCurrentPhotoIndex(){
		return Utils.modulus((int)Math.round(currentRotation + 4.0),photos.size());	
	}
	public HandlerRegistration addPhotoClickedHandler(PhotoClickHandler handler){
		return addHandler(handler, PhotoClickEvent.getType());
	}
}
