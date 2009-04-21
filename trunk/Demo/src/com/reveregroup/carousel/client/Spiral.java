package com.reveregroup.carousel.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.reveregroup.carousel.client.events.PhotoFocusEvent;
import com.reveregroup.carousel.client.events.PhotoFocusHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Spiral implements EntryPoint {
	public Carousel carousel;
	private Label myLabel = new Label();

	public void onModuleLoad() {
		carousel = new Carousel();
		loadPhotos();

		carousel.addPhotoFocusHandler(new PhotoFocusHandler() {
			public void photoFocused(PhotoFocusEvent event) {
				myLabel.setText("Photo " + Integer.toString(event.getPhotoIndex()) + ": " + event.getPhoto().getCaption());
			}
		});

		carousel.setFocusDecoratorWidget(myLabel, DockPanel.SOUTH);

		Button clockwise = new Button("prev");
		clockwise.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				carousel.prev();
			}
		});
		Button counterclockwise = new Button("next");
		counterclockwise.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				carousel.next();
			}
		});
		RootPanel.get("carouselDiv").add(carousel);
		RootPanel.get().add(clockwise);
		RootPanel.get().add(counterclockwise);
		
		//Update Photos button
		Element submitButton = DOM.getElementById("submitPhotoData");
		Event.setEventListener(submitButton, new EventListener() {
			public void onBrowserEvent(Event event) {
				loadPhotos();
			}
		});
		Event.sinkEvents(submitButton, Event.ONCLICK);

	}

	private void loadPhotos() {
		String photoString = TextArea.wrap(DOM.getElementById("photoData")).getValue();
		List<Photo> photos = new ArrayList<Photo>();
		
		int lineStart = 0;
		int lineEnd = photoString.indexOf('\n');
		int urlEnd;
		while (lineEnd != -1) {
			Photo photo = new Photo();
			urlEnd = photoString.indexOf(' ', lineStart);
			if (urlEnd == -1 || urlEnd > lineEnd) {
				urlEnd = lineEnd;
				photo.setCaption("");
			} else {
				photo.setCaption(photoString.substring(urlEnd, lineEnd).trim());
			}
			photo.setUrl(photoString.substring(lineStart, urlEnd).trim());
			if (photo.getUrl().length() > 0)
				photos.add(photo);
			lineStart = lineEnd + 1;
			lineEnd = photoString.indexOf('\n', lineStart);
		}
		lineEnd = photoString.length();
		urlEnd = photoString.indexOf(' ', lineStart);
		
		Photo photo = new Photo();
		if (urlEnd == -1 || urlEnd > lineEnd) {
			urlEnd = lineEnd;
			photo.setCaption("");
		} else {
			photo.setCaption(photoString.substring(urlEnd, lineEnd).trim());
		}
		photo.setUrl(photoString.substring(lineStart, urlEnd).trim());
		if (photo.getUrl().length() > 0)
			photos.add(photo);
		
		carousel.setPhotos(photos);
	}
}
