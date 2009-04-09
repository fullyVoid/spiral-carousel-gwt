package com.reveregroup.client.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Spiral implements EntryPoint {
  public Carousel carousel;
  private List<Photo> photos;
	public void onModuleLoad() {
	photos = new ArrayList<Photo>(10);
	for(int i = 0; i < 10;i++){
		photos.add(new Photo(GWT.getModuleBaseURL()+"img"+(i+1+".bmp")));		
	}
    carousel = new Carousel();
    carousel.setPhotos(photos);
    MouseTracker mouseTracker = new MouseTracker(carousel);
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
    
  }
}
