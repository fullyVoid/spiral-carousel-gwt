package com.logan.friend.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface LoginServiceAsync {
	void login(String loginName, String password, AsyncCallback<User> callback);
	void loginWithFriendConnect(String friendConnectId, AsyncCallback<User> callback);
	void register(User user, AsyncCallback<User> callback);
}
