package com.indiegnition.libraryrest.services.rest;

/**
 * Created by Javier Galvan Martinez on 04/11/2015.
 * Se ocupa de almacenar la respuesta de la comunicacion con el servicio web
 * contiene el objeto devuelto y el codigo de estado
 */
public class RESTResponse {
    private int statusCode;
	private String jsonString;
    private Exception error;


    public RESTResponse(int statusCode, Exception error, String jsonString) {
        this.statusCode = statusCode;
        this.error = error;
        this.jsonString = jsonString;
    }

    // region Getters

    public int getStatusCode() {
        return statusCode;
    }

    public Exception getError() {
        return error;
    }

    public String getJsonString() {
        return jsonString;
    }

    // endregion
}
