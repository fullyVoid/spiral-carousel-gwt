package com.logan.friend.client.facebook;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Facebook {
	private static Facebook instance;
	
	public static Facebook create(String APIKey) {
		if (instance != null) {
			if (APIKey.equals(instance.getAPIKey())) {
				return instance;
			} else {
				throw new RuntimeException("Only one instance of Facebook can be created.");
			}
		}
		instance = new Facebook(APIKey);
		return instance;
	}

	public static Facebook get() {
		return instance;
	}


	private String APIKey;

	private Facebook(String APIKey) {
		this.APIKey = APIKey;
	}

	public String getAPIKey() {
		return APIKey;
	}

	public void showFeedDialog(Long templateBundleId, TemplateData templateData, RequireConnect requireConnect,
			AsyncCallback<String> callback) {
		init();
		
		String templateStringId = NumberFormat.getFormat("#").format(templateBundleId.doubleValue());

		if (requireConnect == null)
			requireConnect = RequireConnect.DO_NOT_REQUIRE;
		
		showFeedDialog(templateStringId, templateData.getTemplateDataAsJavaScriptObject(), templateData
				.getTargetsAsJSArray(), templateData.getBodyGeneral(), requireConnect.getJSName(), templateData
				.getPrompt());
	}

	private native void showFeedDialog(String templateBundleId, JavaScriptObject templateData, JsArrayString targetIds,
			String bodyGeneral, String requireConnect, String userMessagePrompt) /*-{
		$wnd.FB_RequireFeatures(["Connect"], function() {
			$wnd.FB.Connect.showFeedDialog(templateBundleId, templateData, targetIds, bodyGeneral, null, $wnd.FB.RequireConnect[requireConnect], null, userMessagePrompt);
		});
	}-*/;

	public enum RequireConnect {
		DO_NOT_REQUIRE("doNotRequire"), REQUIRE("require"), PROMPT_CONNECT("promptConnect");

		RequireConnect(String jsName) {
			this.jsName = jsName;
		}

		private String jsName;

		String getJSName() {
			return jsName;
		}
	}
	
	public native void init() /*-{
		if (!$wnd.FB.Facebook) {
			$wnd.FB.init(this.@com.logan.friend.client.facebook.Facebook::APIKey, 'xd_receiver.htm');
			$wnd.FB_RequireFeatures(["XFBML"], function() {
				$wnd.FB.XFBML.Host.autoParseDomTree = false;
			});
		}
	}-*/;
}
