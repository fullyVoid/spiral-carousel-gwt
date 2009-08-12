package com.logan.friend.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	User login(String loginName, String password);
	User loginWithFriendConnect();
	User loginWithFacebook();
	User register(User user);
	void logout();
}
