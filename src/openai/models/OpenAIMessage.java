package openai.models;

import java.util.Hashtable;

import org.json.me.JSONObject;
import org.json.me.JSONString;

public class OpenAIMessage implements JSONString {
	public String role;
	public String content;
	
	public OpenAIMessage(String r, String c) {
		role = r;
		content = c;
	}
	
	public OpenAIMessage(JSONObject json) {
		role = json.optString("role");
		content = json.optString("content");
	}
	
	public String toJSONString() {
		// TODO Auto-generated method stub
		Hashtable ht = new Hashtable();
		ht.put("role", role);
		ht.put("content", content);
		return new JSONObject(ht).toString();
	}

}
