package openai;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.HttpsConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;

import openai.models.OpenAICompletion;
import openai.models.OpenAIRequest;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class OpenAIClient {
	
	public String baseUrl;
    private String apiKey;
    
    public OpenAIClient(String baseUrl, String apiKey) {
    	this.baseUrl = baseUrl;
    	this.apiKey = apiKey;
    }
    
    public OpenAICompletion createChatCompletion(Vector messages, String model) throws IOException, JSONException {
        OpenAIRequest requestBody = new OpenAIRequest(model, messages, false);
        byte[] bodyData = requestBody.toJSONString().getBytes("UTF-8");
   
        HttpsConnection conn = createHttpsConnection();
        OutputStream os = conn.openOutputStream();
        os.write(bodyData);
        os.flush();
        
        InputStream is = conn.openInputStream();
        OpenAICompletion response = new OpenAICompletion(new JSONObject(readStreamToString(is)));
        if (os != null) os.close();
        if (is != null) is.close();
        if (conn != null) conn.close();
        return response;
    }
    
//    /**
//     * Stream mode completion, return an InputStream
//     */
//    public InputStream generateChatCompletionStream(Vector messages, String model) throws IOException {
//        OpenAIRequest openAIRequest = new OpenAIRequest(model, messages, true);
//        byte[] bodyData = openAIRequest.toJSONString().getBytes("UTF-8");
//        
//        HttpsConnection conn = createHttpsConnection();
//        OutputStream os = conn.openOutputStream();
//        os.write(bodyData);
//        os.flush();
//        os.close();
//
//        return conn.openInputStream();
//    }
    /**
     * Stream mode completion, return an InputStream
     */
    public ResponseStream generateChatCompletionStream(Vector messages, String model) throws IOException {
        OpenAIRequest openAIRequest = new OpenAIRequest(model, messages, true);
        byte[] bodyData = openAIRequest.toJSONString().getBytes("UTF-8");
        
        HttpsConnection conn = createHttpsConnection();
        OutputStream os = conn.openOutputStream();
        os.write(bodyData);
        os.flush();
        os.close();

        return new ResponseStream(conn.openInputStream());
    }
    /**
     * Main function to provide connection object
     * @return
     * @throws IOException
     */
    private HttpsConnection createHttpsConnection() throws IOException {
        if (apiKey == null || apiKey.length() == 0) {
            throw new IOException("API Key not set.");
        }
    	ConnectionFactory connFactory = new ConnectionFactory();
    	connFactory.setEndToEndRequired(true);
    	int[] preferredTransportTypes = {
    		TransportInfo.TRANSPORT_TCP_WIFI, 
    		TransportInfo.TRANSPORT_TCP_CELLULAR,
    		TransportInfo.TRANSPORT_WAP2,
    		TransportInfo.TRANSPORT_WAP
    	};
    	connFactory.setPreferredTransportTypes(preferredTransportTypes);
    	ConnectionDescriptor connDescr = connFactory.getConnection(baseUrl);
    	if (connDescr==null) {
    		throw new IOException("Network not available.");
    	}
    	HttpsConnection conn = (HttpsConnection) connDescr.getConnection();
        conn.setRequestMethod(HttpsConnection.POST);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        return conn;
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