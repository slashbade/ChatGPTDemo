package openai;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;

import chatgptDemo.AppConfig;

/**
 * StreamChatGPTClient: 用于与OpenAI建立�?�?连接。
 */
public class OpenAIStreamClient {
	
	public String baseUrl;
    private String apiKey;

    public OpenAIStreamClient() {}
    
    public OpenAIStreamClient(String bul, String apk) {
    	baseUrl = bul;
    	apiKey = apk;
    }
    
    public void setApiKey(String key) {
        this.apiKey = key;
    }

    /**
     * 建立连接并返回 InputStream
     */
    public InputStream connectStream(Vector messages) throws IOException {
        if (apiKey == null || apiKey.length() == 0) {
            throw new IOException("API Key not set.");
        }

        String url = baseUrl + ";interface=wifi;EndToEndRequired"; // OpenAI Chat Completion End point
        HttpsConnection conn = (HttpsConnection) Connector.open(url);

        conn.setRequestMethod(HttpsConnection.POST);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Accept", "text/event-stream");
        
        OpenAIRequest openAIRequest = new OpenAIRequest(AppConfig.model, messages, true);
        byte[] bodyData = openAIRequest.toJSONString().getBytes("UTF-8");
        
        conn.setRequestProperty("Content-Length", String.valueOf(bodyData.length));

        OutputStream os = conn.openOutputStream();
        os.write(bodyData);
        os.flush();
        os.close();

        return conn.openInputStream();
    }
}

