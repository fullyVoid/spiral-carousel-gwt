package com.reveregroup.carousel.client;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class CarouselImage extends Image{
	private int originalHeight = 0;
	private int originalWidth = 0;
	public CarouselImage(){
		super();
		addLoadHandler(new LoadHandler(){
			public void onLoad(LoadEvent event) {
				// TODO Auto-generated method stub
				setSize("", "");
				String oldDisplay = getElement().getStyle().getProperty("display");
				getElement().getStyle().setProperty("display", "");
				originalHeight = getHeight();
				originalWidth = getWidth();
				getElement().getStyle().setProperty("display", oldDisplay);
			}
		});
	}
	
	@Override
	public void setUrl(String url) {
		// TODO Auto-generated method stub
		originalHeight = 0;
		originalWidth = 0;
		super.setUrl(url);
	}
	
	public void sizeToBounds(int maxHeight, int maxWidth) {
		if (originalWidth == 0) {
			setSize("", "");
			originalHeight = getHeight();
			originalWidth = getWidth();
			if (originalWidth == 0) {
				setSize(Integer.toString(maxWidth), Integer.toString(maxHeight));
				return;
			}
		}
		
		double aspectRatio = originalHeight / originalWidth;
		double containerAR = ((double)maxHeight) / ((double)maxWidth);
		
		if (aspectRatio >= containerAR){
			//limit height
			setSize("", Integer.toString(maxHeight));
		} else {
			//limit width
			setSize(Integer.toString(maxWidth), "");
		}
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public int getOriginalWidth() {
		return originalWidth;
	}
	
	
}
