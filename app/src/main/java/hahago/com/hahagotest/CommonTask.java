package hahago.com.hahagotest;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class CommonTask extends AsyncTask<String, Integer, String>{
    private final static String TAG = "CommonTask";
    private String url, outStr;
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];

        String jsonIn;
        JsonObject jsonObject = new JsonObject();

                String atrNameKey = params[1];
                String atrNameValue = params[2];
                jsonObject.addProperty(atrNameKey, Double.parseDouble(atrNameValue));
        String atrNameKey2 = params[3];
        String atrNameValue2 = params[4];
        jsonObject.addProperty(atrNameKey2, Double.parseDouble(atrNameValue2));

        try{
            jsonIn = getRemoteData(url, jsonObject.toString());
        }catch (IOException e){
            Log.e(TAG, e.toString());
            return null;
        }

        return jsonIn;
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder inStr = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL("https://hahago-api-tesla.appspot.com/fortest/api/").openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");


        // 是Https請求
        if (connection instanceof HttpsURLConnection) {
            SSLContext sslContext = SSLContextUtil.getSSLContext();
            if (sslContext != null) {
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                ((HttpsURLConnection) connection).setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
            }
        }


        connection.setConnectTimeout(8 * 1000);
        connection.setReadTimeout(8 * 1000);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut : " + jsonOut);
        bw.close();
        connection.setInstanceFollowRedirects(true);


        int responseCode = connection.getResponseCode();


        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                inStr.append(line);
            }
        } else {
            Log.d(TAG, "response code : " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "inStr : " + inStr);
        return inStr.toString();
    }

}
