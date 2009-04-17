package com.reveregroup.carousel.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

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
//	for(int i = 0; i < 10;i++){
//		photo = new Photo(GWT.getModuleBaseURL()+"img"+(i+".bmp"));
		photo.setCaption("This is photo caption number: " + i);
		photo.setPhotoId(i);
		photos.add(photo);
	}
    carousel = new Carousel();
    carousel.setPhotos(photos);
    Button clockwise = new Button("prev");
    clockwise.addClickHandler(new ClickHandler(){
		public void onClick(ClickEvent event) {
			carousel.prev();
		}	
    });
    Button counterclockwise = new Button("next");
    counterclockwise.addClickHandler(new ClickHandler(){
    	public void onClick(ClickEvent event) {
    		carousel.next();
    	}	
    });
    RootPanel.get("carouselDiv").add(carousel);
    RootPanel.get().add(clockwise);
    RootPanel.get().add(counterclockwise);
    
    //new Playground(carousel);
    
  }
}
