package com.logan.friend.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
	String name;
	Long id;
	String friendConnectId;
	String phrase;

	String loginName;
	String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFriendConnectId() {
		return friendConnectId;
	}

	public void setFriendConnectId(String friendConnectId) {
		this.friendConnectId = friendConnectId;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User::name:" + name + ", login:" + loginName + ", password:" + password + ", FCid:" + friendConnectId;
	}
	
	

}
