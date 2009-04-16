package com.reveregroup.carousel.client;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.reveregroup.carousel.client.events.PhotoClickEvent;
import com.reveregroup.carousel.client.events.PhotoClickHandler;
import com.reveregroup.carousel.client.events.PhotoFocusEvent;
import com.reveregroup.carousel.client.events.PhotoFocusHandler;
import com.reveregroup.carousel.client.events.PhotoToFrontEvent;
import com.reveregroup.carousel.client.events.PhotoToFrontHandler;

public class Carousel extends Composite {
	private List<Photo> photos;
	private CarouselImage[] images;

	//Panels and label for the UI
	private DockPanel carouselDock;
	
	private AbsolutePanel imagePanel;
	
	private Label caption;
	
	private double currentRotation = 0.0;
	
	private int currentPhotoIndex = 0; //the photo that is currently in front
	
	private int photoIndex = 0; //the current offset in the photo list
	
	private int carouselSize = 8;
	
	private int preLoadSize = 3;
	
	private boolean focused;
	
	public Carousel() {
		carouselDock = new DockPanel();
		carouselDock.setSize("800", "400");		
		imagePanel = new AbsolutePanel();
		imagePanel.setSize("100%", "100%");
		caption = new Label();
		carouselDock.add(caption, DockPanel.SOUTH);
		carouselDock.add(imagePanel, DockPanel.NORTH);
		carouselDock.setCellHeight(caption, "15");
		carouselDock.setCellHeight(imagePanel, "100%");
		carouselDock.setCellHorizontalAlignment(caption, DockPanel.ALIGN_CENTER);
		Utils.preventSelection(carouselDock.getElement());
		images = new CarouselImage[this.carouselSize+(this.preLoadSize*2)];
		for (int i = 0; i < images.length; i++) {
			images[i] = new CarouselImage();
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
								if(focused){
									PhotoFocusEvent focusevent= new PhotoFocusEvent();
									focusevent.setPhoto(photos.get(pIndex));
									focusevent.setPhotoIndex(getCurrentPhotoIndex());
									fireEvent(event);
								}
								placeImages();
							}else{
								rotateTo(pIndex);
							}
							break;
						}
					}
				}
			});
			imagePanel.add(images[i]);
		}
		this.initWidget(carouselDock);
		addPhotoToFrontHandler(new PhotoToFrontHandler(){
			public void photoToFront(PhotoToFrontEvent event) {
				caption.setText(event.getPhoto().getCaption());
			}
		});
	}

	private void placeImages() {
		int offsetWidth = imagePanel.getOffsetWidth();
		int offsetHeight = imagePanel.getOffsetHeight();
		
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
			CarouselImage image = images[i+preLoadSize];
			if(i == frontImage && focused){
				
				if(image.getOriginalHeight() > offsetHeight || image.getOriginalWidth() > offsetWidth){
					image.sizeToBounds(offsetHeight, offsetWidth);
				} else {
					image.setSize("", "");
				}
				image.getElement().getStyle().setProperty("zIndex", "21");
				int xcoord = (int)(offsetWidth - images[i+preLoadSize].getWidth())/2;
				int ycoord = (int)(offsetHeight - images[i+preLoadSize].getHeight())/2;
				imagePanel.setWidgetPosition(image, xcoord, ycoord);		
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
				image.sizeToBounds((int)(scale * 80), (int)(scale * 80));
				
				int xcoord = (int) (x * 300) + (offsetWidth - image.getWidth()) / 2;
				int ycoord = (int) (y * 75) + (offsetHeight - image.getHeight()) / 2 - 40;
				imagePanel.setWidgetPosition(image, xcoord, ycoord);
			}
		}
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
				CarouselImage[] temps = new CarouselImage[shiftOffset];
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
				CarouselImage[] temps = new CarouselImage[shiftOffset];
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
	private void setCurrentRotation(double value) {
		int pi = getCurrentPhotoIndex();
		currentRotation = Utils.modulus(value, photos.size());
		currentPhotoIndex = Utils.modulus((int)Math.round(currentRotation + 4.0),photos.size());
		if (pi != getCurrentPhotoIndex()) {
			PhotoToFrontEvent event = new PhotoToFrontEvent();
			event.setPhoto(photos.get(getCurrentPhotoIndex()));
			event.setPhotoIndex(getCurrentPhotoIndex());
			//caption.setText(photos.get(getCurrentPhotoIndex()).getCaption());
			fireEvent(event);
			if(focused){
				PhotoFocusEvent focusedEvent = new PhotoFocusEvent();
				event.setPhoto(photos.get(getCurrentPhotoIndex()));
				event.setPhotoIndex(getCurrentPhotoIndex());
				fireEvent(focusedEvent);
			}
		}
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
	
	public HandlerRegistration addPhotoFocusHandler(PhotoFocusHandler handler){
		return addHandler(handler, PhotoFocusEvent.getType());
	}
	public HandlerRegistration addPhotoToFrontHandler(PhotoToFrontHandler handler){
		return addHandler(handler,PhotoToFrontEvent.getType());
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
		return currentPhotoIndex;
	}
	public HandlerRegistration addPhotoClickedHandler(PhotoClickHandler handler){
		return addHandler(handler, PhotoClickEvent.getType());
	}
}
