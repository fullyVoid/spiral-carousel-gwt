package com.logan.friend.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable{
	String name;
	Long id;
	Long friendConnectId;
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

	public Long getFriendConnectId() {
		return friendConnectId;
	}

	public void setFriendConnectId(Long friendConnectId) {
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

}
