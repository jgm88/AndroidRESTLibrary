package com.indiegnition.libraryrest.services.rest;

/**
 * Created by Javier Galvan Martinez on 17/12/2015.
 */
public abstract class RESTCallback {

    public abstract void onResult(Object object, int statusCode, Exception error);

}
