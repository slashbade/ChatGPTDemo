package openai;

import java.util.Hashtable;

import org.json.me.JSONException;
import org.json.me.JSONString;
import org.json.me.JSONObject;

public class OpenAICompletionChoice implements JSONString {
	public OpenAIMessage message;
	public Integer index;
	
	public OpenAICompletionChoice(JSONObject json) throws JSONException {
		message = new OpenAIMessage(json.getJSONObject("message"));
		index = new Integer(json.getInt("index"));
	}
	
	public String toJSONString() {
		Hashtable ht = new Hashtable();
		ht.put("message", message.toString());
		ht.put("index", index.toString());
		return new JSONObject(ht).toString();
	}

}
