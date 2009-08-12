package com.logan.friend.client.facebook;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsObject extends JavaScriptObject {
	protected JsObject() {};
	
	public static native JsObject createString(String value) /*-{
		return value;
	}-*/;
	
	public static native JsObject createNonString(String value) /*-{
		return eval('(' + value + ')');
	}-*/;
	
	public static JsObject create() {
		return JavaScriptObject.createObject().cast();
	}
	
	public native void set(String key, JavaScriptObject value) /*-{
		this[key] = value;
	}-*/;
	
	public void setString(String key, String value) {
		set(key, createString(value));
	}
	
	public void setNonString(String key, String value) {
		set(key, createNonString(value));
	}
	
	public native JavaScriptObject get(String key) /*-{
		return this[key];
	}-*/;
}
