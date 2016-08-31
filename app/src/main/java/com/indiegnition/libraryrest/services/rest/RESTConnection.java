package com.indiegnition.libraryrest.services.rest;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.indiegnition.libraryrest.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Javier Galvan Martinez on 03/11/2015.
 * Clase encargada de la comunicacion HTTP con REST
 */
public class RESTConnection extends AsyncTask<Void, Void, RESTResponse> {

	private static final String TAG = "RESTConnection";

	// For encoding
	private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	// URL configured in build.gradle (Module: app)
	private final String BASE_URL_REST = BuildConfig.BASE_URL_REST;

    private RESTRequest request;
    private RESTHelper restHelper;

    public RESTConnection(RESTRequest request, RESTHelper restHelper){
        super();
        this.request = request;
        this.restHelper = restHelper;
    }

    /**
     * LLamar para comprobar la conexion antes de hacer la peticion
     * @param activity la activity desde la que hay que hacer la peticion
     */
    public static boolean checkConnectivity(Activity activity){
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected RESTResponse doInBackground(Void... none)
	{
		Log.d(TAG, this.request.getMethod().toString() + " " + BASE_URL_REST + this.request.getURL());


        //TODO comprobar que solo haya una request

		HttpURLConnection connection = null;

        int statusCode = -1;
		String responseString = null;
        Exception error = null;

        try
		{
//			String encodedURL = URLEncoder.encode(this.request.getURL(), "UTF-8");
			String encodedURL = Uri.encode(request.getURL(), ALLOWED_URI_CHARS);

			Log.d(TAG, "encodedURL = " + encodedURL);

			// Configure common connection
//			URL url = new URL(BASE_URL_REST + this.request.getURL());
			URL url = new URL(BASE_URL_REST + encodedURL);
            connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(this.request.getMethod().toString());
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoInput(true);	// Always has response
			connection.setDoOutput(false);	// By default, no body

            // Expirar a los 10 segundos si la conexión no se establece
            connection.setConnectTimeout(10000);
            // Esperar solo 15 segundos para que finalice la lectura
			connection.setReadTimeout(15000);

			// Set body
			if (request.getJsonObject() != null)
			{
				Log.d(TAG, "Sending BODY : " + request.getJsonObject().toString(4));

				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");

				OutputStreamWriter output = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
				output.write(request.getJsonObject().toString());
				output.flush();
				output.close();
			}

			// Status code
			statusCode = connection.getResponseCode();
			Log.d(TAG, "statusCode : " + statusCode);

			// Read response
            if (statusCode/100 == 2) // 2xx code
			{
				InputStream is = connection.getInputStream();
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferReader.readLine()) != null)
				{
                    stringBuilder.append(line);
                }
				bufferReader.close();
				responseString = stringBuilder.toString();

				Log.d(TAG, "responseString : " + responseString);
            }
        }
		catch (IOException e)
		{
            error = e;
            e.printStackTrace();
        }
		catch (JSONException e)
		{
			// DEBUG Delete this catch
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}

        RESTResponse response = new RESTResponse(statusCode, error, responseString);
        return response;
    }
    @Override
    protected void onPostExecute(RESTResponse restResponse) {
        // Cuando acaba llama el restHelper
        this.restHelper.OnResult(restResponse);
    }

    /**
     * Clase que se encarga de tratar la respuesta
     * Construir el item y mandarlo al callback asociado
     */
    public static class RESTHelper {
        RESTCallback callback;
        Class classDTOA;

        public RESTHelper(RESTCallback callback, Class responseClass){
            this.callback = callback;
            this.classDTOA = responseClass;
        }
        public void OnResult (RESTResponse response)
        {
            callback.onResult(null, response.getStatusCode(), response.getError());
        }
    }

    public static class RESTHelperItem extends RESTHelper {
        public RESTHelperItem(RESTCallback callback, Class responseClass) {
            super(callback, responseClass);
        }
        @Override
        public void OnResult(RESTResponse response){
            Exception error = response.getError();
            DTO concreteDTO = null;
            String jsonString = response.getJsonString();

            if (response.getStatusCode()/100 == 2 && jsonString != null) {
                try {
                    JSONObject json = new JSONObject(jsonString);

                    // Creamos el objeto DTO concreto
                    concreteDTO = (DTO) classDTOA.newInstance();
                    concreteDTO.setFromJSON(json);
                }
                catch (JSONException e) {
                    error = e;
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            callback.onResult(concreteDTO, response.getStatusCode(), error);
        }
    }
    public static class RESTHelperList extends RESTHelper {
        public RESTHelperList(RESTCallback callback, Class responseClass) {
            super(callback, responseClass);
        }

        @Override
        public void OnResult (RESTResponse response) {
            Exception error = response.getError();
            List<DTO> listDTO = new ArrayList<>();
            String jsonString = response.getJsonString();

            if (response.getStatusCode()/100 == 2 && jsonString != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        DTO concreteDTO = (DTO)classDTOA.newInstance();
                        concreteDTO.setFromJSON(json);
                        listDTO.add(concreteDTO);
                    }
                }
                catch (JSONException e){
                    error = e;
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            callback.onResult(listDTO, response.getStatusCode(), error);
        }
    }
}
