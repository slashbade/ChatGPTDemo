package openai;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
//import net.rim.device.api.ui.component.Dialog;

/**
 * A minimal ChatGPT client for BlackBerry 7.
 */
public class OpenAIClient {
    private String apiKey; // 这里存储你的 OpenAI API Key
    
    // ChatGPT API endpoint (chat/completions)
    private static final String CHAT_GPT_URL = "https://api.openai-proxy.org/v1/chat/completions;interface=wifi;EndToEndRequired";
    
    /**
     * 设置 API Key (Bearer Token)
     */
    public void setApiKey(String key) {
        this.apiKey = key;
    }
    
    
    /**
     * 给 ChatGPT �?��?消�?�并得到回�?(仅演示最基本逻辑).
     * 
     * @param userMessage 用户输入的内容
     * @return ChatGPT 返回的回�?字符串(若解�?失败返回 JSON 或 null)
     */
     public String sendMessage(String userMessage) {
        if (apiKey == null || apiKey.trim().length() == 0) {
            // API Key 为空时直接返回错误�??示
            return "Error: API Key not set.";
        }
        
        HttpsConnection connection = null;
        OutputStream os = null;
        InputStream is = null;
        
        try {
//        	Dialog.alert("connection opening");
            connection = (HttpsConnection) Connector.open(CHAT_GPT_URL);
//            Dialog.alert("connection opened");
            connection.setRequestMethod(HttpsConnection.POST);
//            Dialog.alert("connection opened");
            
            // 2. 设置请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            
            String requestBody =
                "{"
                    + "\"model\":\"gpt-4o\","
                    + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}]"
                + "}";
//            Dialog.alert(requestBody);
            // 4. 写出请求体
            byte[] data = requestBody.getBytes("UTF-8");
//            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            
            os = connection.openOutputStream();
            os.write(data);
            os.flush();
//            Dialog.alert("outputed");
            
            // 5. 获�?��?务器返回的状�?�?
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsConnection.HTTP_OK
                || responseCode == 200) {
                // 6. 读�?��?应数�?�
                is = connection.openInputStream();
                String responseJson = readStreamToString(is);
                
                // 7. 解�? JSON(这里�?��?�简�?�的示例处�?�)
                //    ChatGPT 的返回格�?中，最常�?需�?的内容在
                //    "choices"[0]."message"."content"
                //    这里用简易方法获�?�对应字段。
                String parsedResult = parseChatGPTResponse(responseJson);
                return parsedResult != null ? parsedResult : responseJson;
            } else {
                // 出错时读�?�错误�?（如有），或直接返回状�?�?
                return "Error: HTTP response code " + responseCode;
            }
        } catch (Exception e) {
            return "Exception: " + e.toString();
        } finally {
            // 关闭�?与连接
            try {
                if (os != null) os.close();
                if (is != null) is.close();
                if (connection != null) connection.close();
            } catch (IOException ioe) {
                // 忽略
            }
        }
    }
    
    /**
     * 读 InputStream 到字符串
     */
    private String readStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
    
    /**
     * 简�?�替�?� JSON 中的引�?�和�??斜�?�，防止拼接出错
     */
    private String escapeJson(String text) {
        // 如果�?更完善的转义，需�?处�?�更多字符
        String s = replace(text, "\\", "\\\\");
        s = replace(s, "\"", "\\\"");
        return s;
    }
    
    /**
     * 简�?�字符串替�?�，BlackBerry 没有内置 replaceAll。
     */
    private String replace(String source, String target, String replacement) {
        if (source == null || target == null || target.length() == 0) {
            return source;
        }
        StringBuffer sb = new StringBuffer();
        int start = 0;
        int index = 0;
        while ((index = source.indexOf(target, start)) != -1) {
            sb.append(source.substring(start, index));
            sb.append(replacement);
            start = index + target.length();
        }
        sb.append(source.substring(start));
        return sb.toString();
    }
    
    /**
     * 解�? ChatGPT JSON �?应中最常�?的 content
     * 
     * 返回 "choices" 数组中第一项 "message" 的 "content"。
     * 若找�?到则返回 null。
     */
    private String parseChatGPTResponse(String json) {
        // 
        // 对于 ChatGPT chat/completions 接�?�返回的大致结构:
        // {
        //   "id": "...",
        //   "object": "...",
        //   "created": 123456789,
        //   "choices": [
        //     {
        //       "index": 0,
        //       "message": {
        //         "role": "assistant",
        //         "content": "...真正的回�?文本..."
        //       },
        //       "finish_reason": "stop"
        //     }
        //   ],
        //   "usage": {...}
        // }
        // 
        // 若无�?�适的 JSON 库，这里演示�?简字符串�?�索:
        // (正�?项目建议使用�?��?�的解�?库)
        
        // 1. 找到 "choices"
        int choicesIndex = json.indexOf("\"choices\"");
        if (choicesIndex < 0) {
            return null;
        }
        
        // 2. 找到第一项 "message"
        int messageIndex = json.indexOf("\"message\"", choicesIndex);
        if (messageIndex < 0) {
            return null;
        }
        
        // 3. 找到 "content"
        int contentIndex = json.indexOf("\"content\"", messageIndex);
        if (contentIndex < 0) {
            return null;
        }
        
        // 4. 找到冒�?�与�?�续引�?�
        int colonIndex = json.indexOf(":", contentIndex);
        if (colonIndex < 0) {
            return null;
        }
        int firstQuote = json.indexOf("\"", colonIndex);
        if (firstQuote < 0) {
            return null;
        }
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote < 0) {
            return null;
        }
        
        String content = json.substring(firstQuote + 1, secondQuote);
        // �?�能还需把其中的转义符还原，如 \\n -> \n，这里就演示到此
        return content;
    }
}