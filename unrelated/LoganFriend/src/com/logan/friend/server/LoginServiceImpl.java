package com.logan.friend.server;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJsonRestClient;
import com.google.code.facebookapi.ProfileField;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logan.friend.client.Constants;
import com.logan.friend.client.LoginService;
import com.logan.friend.client.User;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	String DefaultToken = "ALhR-_teJC7WnmOUeWmRtiaQoYwyS99sGdddK1eXRv6zkCJEeYVHguUve6uZ7N0lgwIgg8TLuH_niFf-chy19YIFAcYNUtqzHQ";

	public User login(String loginName, String password) {
//		return loginWithFacebook();
		User user = UserManager.get().getByLogin(loginName, password);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}

	public void logout() {
		getThreadLocalRequest().getSession().setAttribute("currentUser", null);
		return;
	}

	public User loginWithFriendConnect() {
		User u = null;
		//Get authentication token from cookie
		Cookie cookie = getCookie("fcauth" + Constants.FRIEND_CONNECT_ID);
		String securityToken = cookie != null ? cookie.getValue() : null;
		if (securityToken == null) {
			//If no auth token, then no GFC user signed in
			return null;
		}

		try {
			//Make REST call to GFC servers 
			String uri = "http://www.google.com/friendconnect/api/people/@viewer/@self?fcauth=" + securityToken;
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
				InputStreamReader reader = new InputStreamReader(connection.getInputStream());
				JSONObject object = new JSONObject(new JSONTokener(reader));
				JSONObject entry = (JSONObject) object.get("entry");
				u = UserManager.get().getFC(entry.getString("id"));
				if (u != null) {
					// User fetched properly
					getThreadLocalRequest().getSession().setAttribute("currentUser", u);
					return u;
				} else {
					// User does not exist in our DB add him to it.
					u = new User();
					u.setName(entry.get("displayName").toString());
					u.setFriendConnectId(entry.get("id").toString());
					u.setLoginName(entry.get("displayName").toString());
					u.setPassword(entry.get("displayName").toString().substring(0, 1));
					u.setPhrase("Friend Connect User");
					UserManager.get().add(u);
					getThreadLocalRequest().getSession().setAttribute("currentUser", u);
					return u;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return u;
	}

	public User register(User user) {
		UserManager.get().add(user);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}

	public User loginWithFacebook() {
		Cookie cookie = getCookie(Constants.FACEBOOK_API + "_session_key");
		String sessionKey = cookie != null ? cookie.getValue() : null;
		//sessionKey = "3.3z9EPNSg5v_vvlDPF2Luzw__.86400.1244660400-687625460";
		
		if (sessionKey != null) {
			User u2 = new User();
			u2.setName(sessionKey);
		} else {
			return null;
		}
		
		FacebookJsonRestClient client = new FacebookJsonRestClient(Constants.FACEBOOK_API, Constants.FACEBOOK_SECRET,
				sessionKey);
		try {
			long uid = client.users_getLoggedInUser();
			List<Long> uids = new ArrayList<Long>(1);
			uids.add(uid);
			List<ProfileField> fields = new ArrayList<ProfileField>(1);
			fields.add(ProfileField.NAME);
			JSONArray result = (JSONArray) client.users_getInfo(uids, fields);
			if (result.length() == 1) {
				try {
					JSONObject obj = result.getJSONObject(0);
					String name = (String) obj.get("name");
					
					User user = UserManager.get().getFB(uid);
					if (user == null) {
						user = new User();
						user.setFacebookId(uid);
						user.setName(name);
						user.setPhrase("Facebook User");
						UserManager.get().add(user);
					}
					getThreadLocalRequest().getSession().setAttribute("currentUser", user);
					return user;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (FacebookException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	private Cookie getCookie(String name) {
		if (getThreadLocalRequest() != null && getThreadLocalRequest().getCookies() != null) {
			for (Cookie cookie : getThreadLocalRequest().getCookies()) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
}
