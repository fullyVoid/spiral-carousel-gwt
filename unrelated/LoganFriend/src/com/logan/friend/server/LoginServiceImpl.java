package com.logan.friend.server;

import javax.servlet.http.Cookie;

import com.google.appengine.repackaged.com.google.common.base.Log;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logan.friend.client.Constants;
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

	public User loginWithFriendConnect(String friendConnectId) {
		if (friendConnectId == null)
			return null;
		String securityToken = null;
		for (Cookie cookie : getThreadLocalRequest().getCookies()) {
			if (("fcauth" + Constants.FRIEND_CONNECT_ID).equals(cookie.getName())) {
				securityToken = cookie.getValue();
				break;
			}
		}
		if (securityToken == null)
			return null;
		
		//TODO
		
		
		User user = UserManager.get().getFC(friendConnectId);
		return user;
	}

	public User register(User user) {
		UserManager.get().add(user);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}
}
