package chatgptDemo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.json.me.JSONObject;

import net.rim.device.api.ui.UiApplication;

import openai.*;

public class StreamReaderThread extends Thread {

    private InputStream is;
    private StreamUpdateCallback callback;

    public StreamReaderThread(InputStream is, StreamUpdateCallback callback) {
        this.is = is;
        this.callback = callback;
    }

    public void run() {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(is, "UTF-8");
            StringBuffer lineBuffer = new StringBuffer();
            int ch;

            while ((ch = reader.read()) != -1) {
                char c = (char) ch;
                if (c == '\n') {
                    String line = lineBuffer.toString().trim();
                    lineBuffer.setLength(0);

                    if (line.startsWith("data: ")) {
                        String jsonPart = line.substring(6).trim();
                        if ("[DONE]".equals(jsonPart)) {
                            break;
                        }
                        OpenAICompletionChunk chunk = new OpenAICompletionChunk(new JSONObject(jsonPart));
                        if (chunk.choices.isEmpty()) {
                        	continue;
                        }
                        OpenAIChoice message = (OpenAIChoice) chunk.choices.firstElement();
                        final String updateText = message.delta.content;
                        UiApplication.getUiApplication().invokeLater(new Runnable() {
                            public void run() {
                                callback.onNewContent(updateText);
                            }
                        });
                        
                    }
                } else if (c != '\r') {
                    if (lineBuffer.length() < 4096) { // �?制最大行长
                        lineBuffer.append(c);
                    } else {
                        lineBuffer.setLength(0); // 超长直接丢弃这行
                    }
                }
            }
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    callback.onComplete(); // ✅ 统一提交完整内容
                }
            });

        } catch (final Exception e) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    callback.onError("Stream read error: " + e.toString());
                }
            });
        } finally {
            try {
                if (reader != null) reader.close();
                if (is != null) is.close();
            } catch (IOException ignored) {}
        }
    }

    public interface StreamUpdateCallback {
        void onNewContent(String content);
        void onComplete();
        void onError(String error);
    }
}
