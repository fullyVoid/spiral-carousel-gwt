package com.logan.friend.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface UserServiceAsync {
	void updateUser(User user, AsyncCallback<Void> callback);
	void fetchUsers(AsyncCallback<List<User>> callback);
	void whoAmI(AsyncCallback<User> callback);
	void clearUsers(AsyncCallback<Void> callback);
}
