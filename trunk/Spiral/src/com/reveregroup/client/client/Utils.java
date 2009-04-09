package com.reveregroup.client.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;

public class Utils {
	public static void preventDrag(Image img) {
		img.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
			}
		});
		preventDragIE(img.getElement());
	}
	
	native private static void preventDragIE(Element element) /*-{
		element.ondragstart = function() { return false; };
	}-*/;
	
	native public static void preventSelection(Element element) /*-{
		element.style.MozUserSelect = 'none';
		element.unselectable = 'on';
		element.onselectstart = function() { return false; };
	}-*/;
}
