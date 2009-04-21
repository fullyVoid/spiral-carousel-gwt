package com.reveregroup.carousel.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;

import com.reveregroup.carousel.client.events.PhotoClickEvent;
import com.reveregroup.carousel.client.events.PhotoClickHandler;
import com.reveregroup.carousel.client.events.PhotoFocusEvent;
import com.reveregroup.carousel.client.events.PhotoFocusHandler;
import com.reveregroup.carousel.client.events.PhotoUnfocusEvent;
import com.reveregroup.carousel.client.events.PhotoUnfocusHandler;

public class FocusBehavior {
	protected Carousel target;
	protected HandlerManager handlerManager;

	protected PopupPanel popup;
	protected Image testPanel;

	protected Widget focusDecoratorWidget = null;
	

	public FocusBehavior(Carousel carousel) {
		this.target = carousel;
		handlerManager = new HandlerManager(this);
		
		carousel.addPhotoClickHandler(new PhotoClickHandler() {
			public void photoClicked(PhotoClickEvent event) {
				if (event.getPhotoIndex() == target.getCurrentPhotoIndex()) {
					PhotoFocusEvent evt = new PhotoFocusEvent();
					evt.setPhoto(event.getPhoto());
					evt.setPhotoIndex(event.getPhotoIndex());
					handlerManager.fireEvent(evt);
				}
			}
		});
		
		addPhotoFocusHandler(new PhotoFocusHandler() {
			public void photoFocused(PhotoFocusEvent event) {
				// TODO Auto-generated method stub
			    DockPanel tray = new DockPanel();
			    Element lightbox = (Element)DOM.getElementById("lightbox");		
				lightbox.getStyle().setProperty("backgroundColor", "black");
				lightbox.getStyle().setProperty("zIndex", "500");
				lightbox.getStyle().setProperty("display", "block");
			    tray.getElement().getStyle().setProperty("zIndex", "150");
			    popup = new PopupPanel(true,true);
				Image eventImage = new Image(event.getPhoto().getUrl());
			    tray.add(eventImage, DockPanel.CENTER);
			    tray.add(focusDecoratorWidget, DockPanel.SOUTH);			  
			    popup.getElement().getStyle().setProperty("zIndex", "550");
			    popup.add(tray);			    
				popup.center();
				popup.addCloseHandler(new CloseHandler<PopupPanel>(){
					public void onClose(CloseEvent<PopupPanel> event) {
						// TODO Auto-generated method stub
						Element lightbox = (Element)DOM.getElementById("lightbox");
						lightbox.getStyle().setProperty("backgroundColor", "white");						
						lightbox.getStyle().setProperty("zIndex", "-50");
					}
				});
			}
		});
	}
	
	public HandlerRegistration addPhotoFocusHandler(PhotoFocusHandler handler) {
		return handlerManager.addHandler(PhotoFocusEvent.getType(), handler);
	}
	
	public HandlerRegistration addPhotoUnfocusHandler(PhotoUnfocusHandler handler) {
		return handlerManager.addHandler(PhotoUnfocusEvent.getType(), handler);
	}
	
	public void setFocusDecoratorWidget(Widget widget, DockLayoutConstant position) {
		focusDecoratorWidget = widget;
	}
	public static native String getUserAgent() /*-{
	return navigator.userAgent.toLowerCase();
	}-*/;
}
