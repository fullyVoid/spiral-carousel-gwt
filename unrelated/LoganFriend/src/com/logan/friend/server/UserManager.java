package com.logan.friend.server;

import java.util.ArrayList;
import java.util.List;

import com.logan.friend.client.User;

public class UserManager {
	private static UserManager instance;

	public static UserManager get() {
		if (instance == null)
			instance = new UserManager();
		return instance;
	}
	
	private List<User> users = new ArrayList<User>();
	private long nextId = 1;
	
	public void add(User user) {
		users.add(user);
		user.setId(nextId++);
	}
	
	public User get(Long id) {
		for (User u : users) {
			if (u.getId().equals(id)) {
				return u;
			}
		}
		return null;
	}
	
	public User getFC(String id) {
		for (User u : users) {
			if (id.equals(u.getFriendConnectId())) {
				return u;
			}
		}
		return null;
	}

	public User getFB(Long id) {
		for (User u : users) {
			if (id.equals(u.getFacebookId())) {
				return u;
			}
		}
		return null;
	}
	
	public User merge(User user) {
		User u = get(user.getId());
		u.setFriendConnectId(user.getFriendConnectId());
		u.setName(user.getName());
		u.setPhrase(user.getPhrase());
		return u;
	}
	
	public User getByLogin(String loginName, String password) {
		for (User u : users) {
			if (loginName.equals(u.getLoginName()) && password.equals(u.getPassword())) {
				return u;
			}
		}
		return null;		
	}
	
	public List<User> all() {
		return users;
	}
	
	public void clear() {
		users.clear();
	}
}
