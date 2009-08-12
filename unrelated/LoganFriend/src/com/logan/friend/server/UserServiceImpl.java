package com.logan.friend.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logan.friend.client.User;
import com.logan.friend.client.UserService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements UserService {

	public List<User> fetchUsers() {
		return UserManager.get().all();
	}

	public void updateUser(User user) {
		User u = UserManager.get().get(user.getId());
		u.setName(user.getName());
		u.setPhrase(user.getPhrase());
	}

	public User whoAmI() {
		return (User) getThreadLocalRequest().getSession().getAttribute("currentUser");
	}

	public void clearUsers() {
		UserManager.get().clear();
	}

	
}
