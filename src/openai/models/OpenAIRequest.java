package openai.models;

import java.util.Vector;


import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONString;

public class OpenAIRequest implements JSONString {
	String model;
	Vector messages;
	boolean stream;
	
	public OpenAIRequest(String model, Vector messages, boolean stream) {
		this.model = model;
		this.messages = messages;
		this.stream = stream;
	}
	
	public OpenAIRequest(JSONObject json) throws JSONException {
		model = json.getString("model");
		JSONArray messagesJson = json.getJSONArray("messages");
		messages = new Vector();
		for (int i = 0; i < messagesJson.length(); i++) {
			messages.addElement(new OpenAIMessage(messagesJson.getJSONObject(i)));
		}
	}
	
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("model", model);
			json.put("messages", new JSONArray(messages));
			json.put("stream", stream);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json.toString();
	}
}
