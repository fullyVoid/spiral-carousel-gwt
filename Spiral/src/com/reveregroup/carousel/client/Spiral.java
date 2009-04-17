package com.reveregroup.carousel.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.reveregroup.carousel.client.events.PhotoClickEvent;
import com.reveregroup.carousel.client.events.PhotoClickHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Spiral implements EntryPoint {
  public Carousel carousel;
  private List<Photo> photos;
	public void onModuleLoad() {
	photos = new ArrayList<Photo>(10);
	Photo photo;
	for(int i = 0; i < 25;i++){
		photo = new Photo(GWT.getModuleBaseURL()+"sample"+(i+1+".jpg"));
		photo.setCaption("This is photo caption number: " + i);
		photo.setPhotoId(i);
		photos.add(photo);
	}
    carousel = new Carousel();
    carousel.setPhotos(photos);
    //carousel set phototray with panel passed in
    //create panel and it will have hello world
    //custom panel that extends panel implements photoId setter
    //inside carousel call tray.set photoID
    //
    carousel.getElement().getStyle().setProperty("backgroundColor", "silver");
    Button clockwise = new Button("prev");
    clockwise.addClickHandler(new ClickHandler(){
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			carousel.prev();
		}	
    });
    Button counterclockwise = new Button("next");
    counterclockwise.addClickHandler(new ClickHandler(){
    	public void onClick(ClickEvent event) {
    		// TODO Auto-generated method stub
    		carousel.next();
    	}	
    });    
    carousel.addPhotoClickHandler(new PhotoClickHandler(){
		public void photoClicked(PhotoClickEvent event) {
			// TODO Auto-generated method stub
			
		}
    });
    RootPanel.get("carouselDiv").add(carousel);
    RootPanel.get().add(clockwise);
    RootPanel.get().add(counterclockwise);
    
    //new Playground(carousel);
    
  }
}
