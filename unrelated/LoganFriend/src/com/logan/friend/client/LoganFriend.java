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
import com.logan.friend.client.facebook.Facebook;
import com.logan.friend.client.facebook.LoginButton;
import com.logan.friend.client.facebook.TemplateData;
import com.logan.friend.server.UserManager;

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
		registerJSFunctions(this);
		
		userList.setVisibleItemCount(10);
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
				user.setPhrase("Unsocial User");
				
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
		RootPanel.get("loginOptions").add(new Button("Who am I?", new ClickHandler() {
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
		
		RootPanel.get("loginOptions").add(new Button("Log out", new ClickHandler() {
			public void onClick(ClickEvent event) {
				loginService.logout(new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
					}
					public void onFailure(Throwable caught) {
					}
				});
				logoutFriend();
			};
		}));
		
		RootPanel.get("userList").add(new Button("Clear Users", new ClickHandler() {
			public void onClick(ClickEvent event) {
				userService.clearUsers(new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
					}
					public void onFailure(Throwable caught) {
					}
				});
				loginService.logout(new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
					}
					public void onFailure(Throwable caught) {
					}
				});
				userList.clear();
				logoutFriend();
			}
		}));
		
		renderFCSignInButton();
		
//		Facebook.create(Constants.FACEBOOK_API);
//		RootPanel.get("facebookLogin").add(new LoginButton(Facebook.get()));
		
		refreshUserList();
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
	
	public void loginFCUser() {
		loginService.loginWithFriendConnect(new AsyncCallback<User>() {
			public void onSuccess(User result) {
				if (result == null)
					Window.alert("No User.");
				else
					Window.alert("Welcome " + result.getName());
					refreshUserList();
			}
			public void onFailure(Throwable caught) {
				Window.alert("Error!");
			}
		});
	}
	
	public void loginFBUser() {
		loginService.loginWithFacebook(new AsyncCallback<User>() {
			public void onSuccess(User result) {
				if (result == null)
					Window.alert("No User.");
				else
					Window.alert("Welcome " + result.getName());
					refreshUserList();
			}
			public void onFailure(Throwable caught) {
				Window.alert("Error!");
			}
		});
	}
	
	private native void registerJSFunctions(LoganFriend app) /*-{
		$wnd.friendConnectUserLoaded = function() {
			app.@com.logan.friend.client.LoganFriend::loginFCUser()();
		};
		$wnd.facebookUserLoaded = function() {
			app.@com.logan.friend.client.LoganFriend::loginFBUser()();
		};
	}-*/;
	
	private native void renderFCSignInButton() /*-{
		$wnd.google.friendconnect.renderSignInButton({id: 'friendConnectLogin', style: 'long'});
	}-*/;
	
	private native void logoutFriend() /*-{
		$wnd.google.friendconnect.requestSignOut();
		$wnd.FB.Connect.logout();
	}-*/;
}
