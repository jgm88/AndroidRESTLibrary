package com.indiegnition.libraryrest.services;

import com.indiegnition.libraryrest.models.UserDTO;
import com.indiegnition.libraryrest.services.rest.RESTCallback;
import com.indiegnition.libraryrest.services.rest.RESTConnection;
import com.indiegnition.libraryrest.services.rest.RESTRequest;

/**
 * Created by Javier Galvan Martinez on 28/10/15.
 */
public final class UserService {

	// region - Current loged user

	private static UserDTO currentLogedUser = null;

	public static UserDTO getCurrentLogedUser ()
	{
		return currentLogedUser;
	}

	public static void setCurrentLogedUser (UserDTO logingUser)
	{
		currentLogedUser = logingUser;
	}

	// endregion

	public static void createUser(int userId, String userEmail, final RESTCallback callback){
		RESTRequest.Method method = RESTRequest.Method.POST;
		String url = "User/CreateUser?id="+userId+"&email="+userEmail;
		RESTRequest request = new RESTRequest(method, url, null);
		// Create connection and execute
		RESTConnection.RESTHelper rh = new RESTConnection.RESTHelper(callback, null);
		new RESTConnection(request, rh).execute();
	}

	public static void getUser(int userId, final RESTCallback callback){
		RESTRequest.Method method = RESTRequest.Method.GET;
		String url = "User/"+userId;
		RESTRequest request = new RESTRequest(method, url, null);
		// Create connection and execute
		Class classDTOA = UserDTO.class;
		RESTConnection.RESTHelper rh = new RESTConnection.RESTHelperItem(callback, classDTOA);
		new RESTConnection(request, rh).execute();
	}


}
