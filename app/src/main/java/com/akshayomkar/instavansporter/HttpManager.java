package com.akshayomkar.instavansporter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/*
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.net.http.AndroidHttpClient;
import android.util.Log;
*/

public class HttpManager {
	
	public static String getData(String url_path) {
        Log.d("url", "hm 1");
        InputStream is = null;
        String result="";
        StringBuilder response_str = null;
        try {
            URL url = new URL(url_path);
            Log.d("url", "hm 2");
            URLConnection con = url.openConnection();
            Log.d("url", "hm 3");
            if(con instanceof HttpURLConnection){
                Log.d("url", "hm 4");
                HttpURLConnection httpURLConnection = (HttpURLConnection)con;
                httpURLConnection.setRequestMethod("GET");
                int response = -1;

                httpURLConnection.connect();
                response = httpURLConnection.getResponseCode();
                Log.d("url", "hm 5 code" +response);
                if(response == HttpURLConnection.HTTP_OK){

                    is = httpURLConnection.getInputStream();

                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    response_str = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response_str.append(line);
                    }
                    result=response_str.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

        return result;
        /*
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("RESPONSE", responseString);
        return responseString;
*/
        /*
		AndroidHttpClient client = AndroidHttpClient.newInstance("AndroidAgent");
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		Log.d("URL",url);
		try {
			response = client.execute(request);
            String jsonFeed = EntityUtils.toString(response.getEntity());
			Log.d("URL",jsonFeed);
			return jsonFeed;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally{
			client.close();
		}
		*/
    }
}
