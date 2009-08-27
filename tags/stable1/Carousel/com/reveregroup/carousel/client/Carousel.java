package com.reveregroup.carousel.client;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.reveregroup.carousel.client.events.PhotoClickEvent;
import com.reveregroup.carousel.client.events.PhotoClickHandler;
import com.reveregroup.carousel.client.events.PhotoFocusHandler;
import com.reveregroup.carousel.client.events.PhotoToFrontEvent;
import com.reveregroup.carousel.client.events.PhotoToFrontHandler;
import com.reveregroup.carousel.client.events.PhotoUnfocusHandler;

public class Carousel extends Composite {
	private List<Photo> photos;
	private CarouselImage[] images;

	// Panels and label for the UI
	private DockPanel carouselDock;

	private AbsolutePanel imagePanel;

	private Label caption;

	private double currentRotation = 0.0;

	private int currentPhotoIndex = 0; // the photo that is currently in front

	private int carouselSize = 9;

	private int preLoadSize = 3;

	private boolean mouseMoved = false;

	private MouseBehavior mouseBehavior;

	private FocusBehavior focusBehavior;

	public Carousel() {
		// Set up UI structure
		carouselDock = new DockPanel();
		imagePanel = new AbsolutePanel();
		imagePanel.setSize("100%", "100%");
		caption = new Label();
		carouselDock.add(caption, DockPanel.SOUTH);
		carouselDock.add(imagePanel, DockPanel.NORTH);
		carouselDock.setCellHeight(caption, "15");
		carouselDock.setCellHeight(imagePanel, "100%");
		carouselDock.setCellHorizontalAlignment(caption, DockPanel.ALIGN_CENTER);
		Utils.preventSelection(carouselDock.getElement());
		imagePanel.getElement().getStyle().setProperty("overflow", "hidden");
		carouselDock.setStyleName("photoCarousel");
		caption.setStyleName("photoCarouselCaption");

		// Set up images
		images = new CarouselImage[this.carouselSize + (this.preLoadSize * 2)];
		for (int i = 0; i < images.length; i++) {
			images[i] = new CarouselImage();
			images[i].setSize("1", "1");
			Utils.preventDrag(images[i]);
			Utils.preventSelection(images[i].getElement());
			images[i].getElement().getStyle().setProperty("display", "none");
			images[i].addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (mouseMoved)
						return; // make sure a photo click is not registered
					// when the mouse is dragged.
					Image img = (Image) event.getSource();
					for (int i = 0; i < carouselSize; i++) {
						if (images[i + preLoadSize] == img) {
							int pIndex = i - 4 + currentPhotoIndex;
							pIndex = Utils.modulus(pIndex, photos.size());
							// fire off photo clicked event
							PhotoClickEvent pcEvent = new PhotoClickEvent();
							pcEvent.setPhotoIndex(pIndex);
							pcEvent.setPhoto(photos.get(pIndex));
							fireEvent(pcEvent);
							break;
						}
					}
				}
			});
			images[i].addLoadHandler(new LoadHandler() {
				public void onLoad(LoadEvent event) {
					if (!"none".equals(((CarouselImage) event.getSource()).getElement().getStyle().getProperty(
							"display"))) {
						placeImages();
					}
				}
			});
			imagePanel.add(images[i]);
		}
		this.initWidget(carouselDock);

		// Sync caption with front-most photo.
		addPhotoToFrontHandler(new PhotoToFrontHandler() {
			public void photoToFront(PhotoToFrontEvent event) {
				caption.setText(event.getPhoto().getCaption());
			}
		});

		// Rotate when mouse is dragged
		mouseBehavior = new MouseBehavior(this);
		// Focus when current photo clicked
		focusBehavior = new FocusBehavior(this);

		// These are used to help make sure a photo click is not registered when
		// the mouse is dragged.
		addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				mouseMoved = false;
			}
		});
		addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				mouseMoved = true;
			}
		});
	}

	/**
	 * If a focus decorator widget is set, the widget is displayed with the
	 * image popup when an image is brought into focus. The position parameter
	 * determines whether it shows up above, below, on the left or on the right
	 * of the image.
	 * 
	 * Use the PhotoFocus event to update the widget with information or options
	 * for the currently focused photo.
	 */
	public void setFocusDecoratorWidget(Widget widget, DockLayoutConstant position) {
		if (focusBehavior != null)
			focusBehavior.setFocusDecoratorWidget(widget, position);
	}

	private void placeImages() {
		// Places images in the correct spots
		int offsetWidth = imagePanel.getOffsetWidth();
		int offsetHeight = imagePanel.getElement().getClientHeight();

		double imgHeight = offsetHeight * 3.0 / 8.0;
		double imgWidth = imgHeight * 1.2;
		double xRadius = (offsetWidth - imgWidth) / 2.0;
		double yRadius = imgHeight / 3.0;
		double spiralSpread = yRadius * .5;

		double degreeOffset = 0.0;
		double rotationDecimal = currentRotation - Math.round(currentRotation);
		degreeOffset = -(rotationDecimal * ((Math.PI) / 4));
		for (int i = 0; i < carouselSize; i++) {
			CarouselImage image = images[i + preLoadSize];
			double finalDegree = ((i * Math.PI) / 4) + degreeOffset;
			double scale = 0.0;
			double x = -Math.sin(finalDegree);
			double y = -Math.cos(finalDegree);
			scale = Math.pow(2, y);
			int zindex = (int) (y * 10);
			zindex += 10;
			images[i + preLoadSize].getElement().getStyle().setProperty("zIndex", Integer.toString(zindex));
			image.sizeToBounds((int) (scale * imgWidth), (int) (scale * imgHeight));

			int xcoord = (int) Math.round((x * xRadius) + (offsetWidth - image.getWidth()) / 2.0);
			int ycoord = (int) Math.round((y * yRadius) + offsetHeight - imgHeight - yRadius - image.getHeight() / 2.0
					- Math.round(spiralSpread * (i - 4 - rotationDecimal)));
			imagePanel.setWidgetPosition(image, xcoord, ycoord);
			if (i == 0) {
				image.setOpacity(.5 - rotationDecimal);
			} else if (i == carouselSize - 1) {
				image.setOpacity(.5 + rotationDecimal);
			} else {
				image.setOpacity(1.0);
			}
		}
	}

	/**
	 * Set a list of photos to be displayed in the carousel.
	 */
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
		currentPhotoIndex = Utils.modulus(currentPhotoIndex, photos.size());
		for (int i = 0; i < images.length; i++) {
			int pIndex = i - preLoadSize - 4 + currentPhotoIndex;
			pIndex = Utils.modulus(pIndex, photos.size());
			images[i].setUrl(photos.get(pIndex).getUrl());
		}
		for (int i = 0; i < carouselSize; i++) {
			images[i + preLoadSize].getElement().getStyle().setProperty("display", "");
		}
		placeImages();
		PhotoToFrontEvent evt = new PhotoToFrontEvent();
		evt.setPhotoIndex(currentPhotoIndex);
		evt.setPhoto(photos.get(currentPhotoIndex));
		fireEvent(evt);
	}

	private void setCurrentPhotoIndex(int photoIndex) {
		if (this.currentPhotoIndex == photoIndex)
			return;
		photoIndex = Utils.modulus(photoIndex, photos.size());
		if (this.currentPhotoIndex == photoIndex) {
			return;
		} else {
			int shiftOffset = photoIndex - this.currentPhotoIndex;
			if (shiftOffset < -(photos.size() / 2)) {
				shiftOffset += photos.size();
			} else if (shiftOffset > (photos.size() / 2)) {
				shiftOffset -= photos.size();
			}
			if (shiftOffset > 0) {
				// Next
				// Creating temp array of images to hold shifted images
				CarouselImage[] temps = new CarouselImage[shiftOffset];
				for (int j = 0; j < temps.length; j++) {
					temps[j] = images[j];
				}
				for (int i = 0; i < images.length - (shiftOffset); i++) {
					images[i] = images[i + (shiftOffset)];
				}
				// update from large array
				for (int k = 0; k < temps.length; k++) {
					int pIndex = photoIndex - 4 + carouselSize + preLoadSize - shiftOffset + k;
					pIndex = Utils.modulus(pIndex, photos.size());
					images[k + images.length - shiftOffset] = temps[k];
					temps[k].setUrl(photos.get(pIndex).getUrl());
				}
			} else if (shiftOffset < 0) {
				shiftOffset *= -1;
				// Prev
				CarouselImage[] temps = new CarouselImage[shiftOffset];
				for (int j = 0; j < temps.length; j++) {
					temps[j] = images[j + images.length - shiftOffset];
				}
				for (int i = images.length - 1; i >= shiftOffset; i--) {
					images[i] = images[i - shiftOffset];
				}
				// update from large array
				for (int k = 0; k < temps.length; k++) {
					int pIndex = photoIndex - 4 - preLoadSize + k;
					pIndex = Utils.modulus(pIndex, photos.size());
					images[k] = temps[k];
					temps[k].setUrl(photos.get(pIndex).getUrl());
				}
			}
			for (int i = 0; i < preLoadSize; i++) {
				images[i].getElement().getStyle().setProperty("display", "none");
				images[images.length - i - 1].getElement().getStyle().setProperty("display", "none");
			}
			for (int i = 0; i < carouselSize; i++) {
				images[i + preLoadSize].getElement().getStyle().setProperty("display", "");
			}
			this.currentPhotoIndex = photoIndex;
		}
	}

	boolean timerOn;
	double velocity;
	Timer timer = new CTimer();
	long lastTime;

	double acceleration = .998;
	double velocityThreshold = .00002;

	/**
	 * Acceleration determines how fast the carousel slows down or speeds up as
	 * it rotates. A value of 1.0 will cause the carousel to rotate at a
	 * constant speed. A value greater than 1.0 will cause it to speed up over
	 * time. A value less than 1.0 will cause it to slow down.
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * Acceleration determines how fast the carousel slows down or speeds up as
	 * it rotates. A value of 1.0 will cause the carousel to rotate at a
	 * constant speed. A value greater than 1.0 will cause it to speed up over
	 * time. A value less than 1.0 will cause it to slow down.
	 */
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	private class CTimer extends Timer {
		public void run() {
			long currentTime = System.currentTimeMillis();
			int ticks = (int) (currentTime - lastTime);
			lastTime = currentTime;
			
			if (acceleration == 1.0) {
				setCurrentRotation(currentRotation + ticks * velocity);
			} else {
				double newVelocity = velocity * Math.pow(acceleration, ticks);
				if (newVelocity < velocityThreshold && newVelocity > -velocityThreshold) {
					setCurrentRotation(currentRotation + Utils.distanceFromStartingVelocity(velocity, acceleration, velocityThreshold));
					setVelocity(0.0);
				} else {
					setCurrentRotation(currentRotation + Utils.distanceForXTicks(velocity, acceleration, ticks));
					setVelocity(velocity * Math.pow(acceleration, ticks));
				}
			}
		}
	}

	/**
	 * Set the speed for the carousel to rotate.
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
		if (velocity > -velocityThreshold && velocity < velocityThreshold) {
			if (timerOn) {
				timer.cancel();
				timerOn = false;
			}
			this.velocity = 0;
		} else if (!timerOn) {
			lastTime = System.currentTimeMillis();
			timer.scheduleRepeating(33);
			timerOn = true;
			timer.run();
		}
	}

	/**
	 * The current rotational position of the carousel. Rotation is based on
	 * indices in the photo list. So if the 3rd photo in the list is in the
	 * front of the carousel, currentRotation will be 2.0 (indicies are 0
	 * based).
	 */
	public double getCurrentRotation() {
		return currentRotation;
	}

	/**
	 * The current rotational position of the carousel. Rotation is based on
	 * indices in the photo list. So if the 3rd photo in the list is in the
	 * front of the carousel, currentRotation will be 2.0 (indicies are 0
	 * based).
	 */
	private void setCurrentRotation(double value) {
		int pi = getCurrentPhotoIndex();
		currentRotation = Utils.modulus(value, photos.size());
		setCurrentPhotoIndex((int) Math.round(currentRotation));
		if (pi != getCurrentPhotoIndex()) {
			PhotoToFrontEvent event = new PhotoToFrontEvent();
			event.setPhoto(photos.get(getCurrentPhotoIndex()));
			event.setPhotoIndex(getCurrentPhotoIndex());
			fireEvent(event);
		}
		placeImages();
	}

	/**
	 * Start an animated rotation to the given position. Position is based on
	 * indices in the photo list. So to rotate to the 3rd photo in the list,
	 * pass 2.0 (indicies are 0 based) as the position.
	 */
	public void rotateTo(double position) {
		if (acceleration >= 1.0) {
			setCurrentRotation(position);
			return;
		}
		double distance = Utils.modulus(position, photos.size()) - currentRotation;
		if (distance > photos.size() / 2) {
			distance -= photos.size();
		} else if (distance < photos.size() / -2) {
			distance += photos.size();
		}
		setVelocity(Utils.velocityForDistance(distance, acceleration, velocityThreshold));
	}

	/**
	 * Start an animated rotation that will rotate the carousel by the given
	 * distance. A distance of 1.0 is equivalent to moving by one photo in the
	 * carousel, and 2.0 is two photos, etc.
	 */
	public void rotateBy(double distance) {
		if (acceleration >= 1.0) {
			setCurrentRotation(currentRotation + distance);
			return;
		}
		setVelocity(Utils.velocityForDistance(distance, acceleration, velocityThreshold));
	}

	/**
	 * Start an animated rotation to the previous photo.
	 */
	public void prev() {
		rotateTo(getCurrentPhotoIndex() - 1.0);
	}

	/**
	 * Start an animated rotation to the next photo.
	 */
	public void next() {
		rotateTo(getCurrentPhotoIndex() + 1.0);
	}

	/**
	 * Returns the zero-based index in the photo list of the photo that is
	 * currently in front.
	 */
	public int getCurrentPhotoIndex() {
		return currentPhotoIndex;
	}

	/**
	 * This handler is fired each time a photo rotates to the front, becoming
	 * the current photo.
	 */
	public HandlerRegistration addPhotoToFrontHandler(PhotoToFrontHandler handler) {
		return addHandler(handler, PhotoToFrontEvent.getType());
	}

	/**
	 * This handler is fired when any photo in the carousel is clicked.
	 */
	public HandlerRegistration addPhotoClickHandler(PhotoClickHandler handler) {
		return addHandler(handler, PhotoClickEvent.getType());
	}

	/**
	 * This handler is fired when a photo is focused (i.e. it is clicked and
	 * opens full-size).
	 */
	public HandlerRegistration addPhotoFocusHandler(PhotoFocusHandler handler) {
		if (focusBehavior == null)
			return null;
		return focusBehavior.addPhotoFocusHandler(handler);
	}

	/**
	 * This handler is fired when a photo is un-focused (i.e. when the expanded
	 * photo box is closed).
	 */
	public HandlerRegistration addPhotoUnfocusHandler(PhotoUnfocusHandler handler) {
		if (focusBehavior == null)
			return null;
		return focusBehavior.addPhotoUnfocusHandler(handler);
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
