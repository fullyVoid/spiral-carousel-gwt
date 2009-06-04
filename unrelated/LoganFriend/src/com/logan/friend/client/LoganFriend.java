package com.logan.friend.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LoganFriend implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private final UserServiceAsync userService = GWT.create(UserService.class);
	
	private ListBox userList = new ListBox();
	private TextBox loginName = new TextBox();
	private TextBox password = new PasswordTextBox();

	public void onModuleLoad() {
		RootPanel.get("userList").add(new Button("Who am I?", new ClickHandler() {
			public void onClick(ClickEvent event) {
				userService.whoAmI(new AsyncCallback<User>() {
					public void onSuccess(User user) {
						if (user == null) {
							Window.alert("You are not anyone.");
						} else {
							Window.alert(user.getName());
						}
					}
					public void onFailure(Throwable caught) { }
				});
			}
		}));
		RootPanel.get("userList").add(userList);
		RootPanel.get("loginName").add(loginName);
		RootPanel.get("password").add(password);
		RootPanel.get("loginOptions").add(new Button("Login", new ClickHandler() {
			public void onClick(ClickEvent event) {
				loginService.login(loginName.getValue(), password.getValue(), new AsyncCallback<User>() {
					public void onSuccess(User result) {
						if (result == null) {
							Window.alert("Login failed.");
						} else {
							Window.alert("Welcome " + result.getName());
						}
					}
					public void onFailure(Throwable caught) {}
				});
			}
		}));
		RootPanel.get("loginOptions").add(new Button("Register", new ClickHandler() {
			public void onClick(ClickEvent event) {
				User user = new User();
				user.setLoginName(loginName.getText());
				user.setPassword(password.getText());
				user.setName(user.getLoginName());
				
				loginService.register(user, new AsyncCallback<User>() {
					public void onSuccess(User result) {
						if (result == null) {
							Window.alert("Registration failed.");
						} else {
							refreshUserList();
						}
					}
					public void onFailure(Throwable caught) {}
				});
			}
		}));
		
	}
	
	private void refreshUserList() {
		userService.fetchUsers(new AsyncCallback<List<User>>() {
			public void onSuccess(List<User> result) {
				userList.clear();
				for (User user : result) {
					userList.addItem(user.getName() + " : " + user.getPhrase());
				}
			};
			
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR);
			};
		});
	}
}
