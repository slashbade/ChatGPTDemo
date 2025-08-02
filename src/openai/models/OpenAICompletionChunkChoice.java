package openai.models;

import java.util.Hashtable;


import org.json.me.JSONException;
import org.json.me.JSONString;
import org.json.me.JSONObject;

public class OpenAICompletionChunkChoice implements JSONString {
	public OpenAIMessage delta;
	public String finishReason;
	public Integer index;
	
	public OpenAICompletionChunkChoice(JSONObject json) throws JSONException {
		delta = new OpenAIMessage(json.getJSONObject("delta"));
		finishReason = json.optString("finish_reason");
		index = new Integer(json.getInt("index"));
	}
	
	public String toJSONString() {
		Hashtable ht = new Hashtable();
		ht.put("delta", delta.toString());
		ht.put("finish_reason", finishReason);
		ht.put("index", index.toString());
		return new JSONObject(ht).toString();
	}

}
