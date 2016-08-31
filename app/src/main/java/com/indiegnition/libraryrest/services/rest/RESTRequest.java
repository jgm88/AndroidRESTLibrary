package com.indiegnition.libraryrest.services.rest;

import org.json.JSONObject;

/**
 * Created by Javier Galvan Martinez on 04/11/2015.
 * Se responsabiliza de los parametros de configuracion de la peticion http.
 * Esta contiene el tipo de peticion GET/POST/PUT/DELETE, la url de acceso al recurso
 * y el objeto JSON que queramos enviar en caso de que exista
 */
public class RESTRequest {

    public enum Method {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");
        private final String sMethod;

        private Method(String s){
            sMethod = s;
        }
        public boolean equalsName(String otherMethod) {
            return (otherMethod == null) ? false : sMethod.equals(otherMethod);
        }

        public String toString() {
            return this.sMethod;
        }
    }
    private Method method;
    private String URL;
    private JSONObject jsonObject;

    public RESTRequest(Method m, String URL, JSONObject jsonObject){

        this.method = m;
        this.URL = URL;
        this.jsonObject = jsonObject;
    }


    // region Getters

    public Method getMethod() {
        return method;
    }
    public String getURL() {
        return URL;
    }
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    // endregion

}
