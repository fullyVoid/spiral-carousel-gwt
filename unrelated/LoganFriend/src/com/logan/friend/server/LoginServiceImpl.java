package com.logan.friend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logan.friend.client.LoginService;
import com.logan.friend.client.User;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	public User login(String loginName, String password) {
		User user = UserManager.get().getByLogin(loginName, password);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}

	public User loginWithFriendConnect(Long friendConnectId) {
		User user = UserManager.get().getFC(friendConnectId);
		//TODO This is where it's at.
		return user;
	}

	public User register(User user) {
		UserManager.get().add(user);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}
}
