package com.logan.friend.server;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.json.JSONTokener;

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
		User user = UserManager.get().getByLogin(loginName, password);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}
	
	public void logout() {
		getThreadLocalRequest().getSession().setAttribute("currentUser", null);
		return;
	}

	public User loginWithFriendConnect(String friendConnectId) {
		User u = null;
		if (friendConnectId == null)
			return null;
		String securityToken = null;
		if(getThreadLocalRequest() != null && getThreadLocalRequest().getCookies() != null){
			for (Cookie cookie : getThreadLocalRequest().getCookies()) {
				if (("fcauth" + Constants.FRIEND_CONNECT_ID).equals(cookie.getName())) {
					securityToken = cookie.getValue();
					User u2 = new User();
					u2.setName(securityToken);
					//return u2;
					break;
				}
			}
		}else{
			//Default bad this is now blank
			securityToken = DefaultToken;
		}
		if (securityToken == null)
			return null;
		
		//TODO
		try{
			String uri = "http://www.google.com/friendconnect/api/people/@viewer/@self?fcauth="+securityToken;	
			URL url = new URL(uri);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	        connection.setRequestMethod("GET");	    
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            // OK
	        	InputStreamReader reader = new InputStreamReader(connection.getInputStream());
	        	JSONObject object = new JSONObject(new JSONTokener(reader));
				JSONObject entry = (JSONObject)object.get("entry");
				u = UserManager.get().getFC(entry.getString("id"));
				if(u != null){
					//User fetched properly
					getThreadLocalRequest().getSession().setAttribute("currentUser", u);
					return u;
				}else{
					//User does not exist in our DB add him to it.
					u = new User();
					u.setName(entry.get("displayName").toString());
					u.setFriendConnectId(entry.get("id").toString());
					u.setLoginName(entry.get("displayName").toString());
					u.setPassword(entry.get("displayName").toString().substring(0,1));
					UserManager.get().add(u);
					getThreadLocalRequest().getSession().setAttribute("currentUser", u);
					return u;
				}
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return u;
	}

	public User register(User user) {
		UserManager.get().add(user);
		getThreadLocalRequest().getSession().setAttribute("currentUser", user);
		return user;
	}
}
