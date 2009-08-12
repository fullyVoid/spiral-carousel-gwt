package com.logan.friend.client.facebook;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class LoginButton extends Widget {
	public LoginButton(Facebook facebook) {
		setElement(DOM.createDiv());
		facebook.init();
		addElement(getElement());
	}
	
	private native void addElement(Element element) /*-{
		$wnd.FB_RequireFeatures(["XFBML"], function() {
			$wnd.FB.XFBML.Host.addElement(new $wnd.FB.XFBML.LoginButton(element));
		});
	}-*/;
}
