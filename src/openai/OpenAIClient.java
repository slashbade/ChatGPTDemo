package openai;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class OpenAIClient {
	
	public String baseUrl;
    private String apiKey;
    
    public OpenAIClient(String baseUrl, String apiKey) {
    	this.baseUrl = baseUrl;
    	this.apiKey = apiKey;
    }
    
    public OpenAICompletion generateCompletion(Vector messages, String model) throws IOException, JSONException {
        if (apiKey == null || apiKey.trim().length() == 0) {
        	throw new IOException("API Key not set.");
        }
        
        HttpsConnection connection = null;
        OutputStream os = null;
        InputStream is = null;
        
        String url = baseUrl + ";interface=wifi;EndToEndRequired";
        connection = (HttpsConnection) Connector.open(url);
        connection.setRequestMethod(HttpsConnection.POST);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            
        OpenAIRequest requestBody = new OpenAIRequest(model, messages, false);
        byte[] bodyData = requestBody.toJSONString().getBytes("UTF-8");
        connection.setRequestProperty("Content-Length", String.valueOf(bodyData.length));
            
        os = connection.openOutputStream();
        os.write(bodyData);
        os.flush();
        is = connection.openInputStream();
        OpenAICompletion response = new OpenAICompletion(new JSONObject(readStreamToString(is)));
        if (os != null) os.close();
        if (is != null) is.close();
        if (connection != null) connection.close();
        return response;
    }
    
    private String readStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
    

}