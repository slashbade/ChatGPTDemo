package openai;

import java.util.Vector;
//import java.util.Hashtable;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONString;
import org.json.me.JSONObject;

public class OpenAICompletionChunk implements JSONString {
	public Vector choices;
	public String id;
	public String created;
	public String model;
	
	public OpenAICompletionChunk(JSONObject json) throws JSONException {
		model = json.getString("model");
		id = json.getString("id");
		created = json.getString("created");
		JSONArray messagesJson = json.getJSONArray("choices");
		choices = new Vector();
		for (int i = 0; i < messagesJson.length(); i++) {
			choices.addElement(new OpenAIChoice(messagesJson.getJSONObject(i)));
		}
	}
	
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

}
