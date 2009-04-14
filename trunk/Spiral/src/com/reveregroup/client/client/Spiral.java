package com.reveregroup.client.client;

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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Spiral implements EntryPoint {
  public Carousel carousel;
  private List<Photo> photos;
	public void onModuleLoad() {
	photos = new ArrayList<Photo>(10);
	for(int i = 0; i < 10;i++){
		photos.add(new Photo(GWT.getModuleBaseURL()+(i+1+".JPG")));		
	}
    carousel = new Carousel();
    carousel.setPhotos(photos);
    MouseTracker mouseTracker = new MouseTracker(carousel);
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
    RootPanel.get("carouselDiv").add(carousel);
    RootPanel.get().add(clockwise);
    RootPanel.get().add(counterclockwise);
    
    //new Playground(carousel);
    
  }
}
