package openai;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import openai.models.OpenAICompletionChunk;

public class ResponseStream extends InputStreamReader {

	public ResponseStream(InputStream is) throws UnsupportedEncodingException {
		super(is, "UTF-8");
	}
	
	public OpenAICompletionChunk readChunk() throws IOException, JSONException {
		int ch;
		StringBuffer lineBuffer = new StringBuffer();
		while ((ch = this.read()) != -1) {
			char c = (char) ch;
            if (c == '\n') {
                String line = lineBuffer.toString().trim();
                lineBuffer.setLength(0);

                if (line.startsWith("data: ")) {
                    String jsonPart = line.substring(6).trim();
                    if ("[DONE]".equals(jsonPart)) {
                        break;
                    }
                    return new OpenAICompletionChunk(new JSONObject(jsonPart));
                }
            }
            else if (c != '\r') {
                if (lineBuffer.length() < 4096) {
                    lineBuffer.append(c);
                } else {
                    lineBuffer.setLength(0);
                }
            }
		}
		return null;
	}
}
