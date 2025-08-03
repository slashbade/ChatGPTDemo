package chatgptDemo;

import java.io.IOException;
import java.util.Vector;

import org.json.me.JSONException;

import chatgptDemo.screens.ChatScreen;

import openai.OpenAIClient;
import openai.ResponseStream;
import openai.models.OpenAICompletion;
import openai.models.OpenAICompletionChoice;
import openai.models.OpenAICompletionChunk;
import openai.models.OpenAICompletionChunkChoice;
import openai.models.OpenAIMessage;

import net.rim.device.api.ui.UiApplication;

public class ChatDispatcher extends Thread {
	ChatScreen screen;
	String userContent;
	OpenAIClient client;
	OpenAIClient titleClient;
	UiApplication uiApplication = UiApplication.getUiApplication();
	
	
	public ChatDispatcher(ChatScreen screen, String userContent) {
		this.screen = screen;
		this.userContent = userContent;
	}
	
	public void run() {
		try {
			client = new OpenAIClient(AppConfig.baseUrl, AppConfig.apiKey);
			if (!(AppConfig.instruction.trim().equals("")) && screen.messages.isEmpty()) {
				screen.appendMessage("system", AppConfig.instruction);
			}
			screen.appendMessage("user", userContent);
			screen.addMessageWrapper("user", userContent);
			final ResponseStream rs = client.createChatCompletionStream(screen.messages, AppConfig.model);
			final StringBuffer currentBuffer = new StringBuffer();
			screen.addMessageWrapper("assistant", "");
			OpenAICompletionChunk chunk;
			
			while ((chunk = rs.readChunk()) != null) {
//				Util.dialogAlert(111);
				if (chunk.choices.isEmpty()) continue;
				final OpenAICompletionChunkChoice choice = (OpenAICompletionChunkChoice) chunk.choices.firstElement();
				currentBuffer.append(choice.delta.content);
                screen.addContentAtLastWrapper(choice.delta.content);
			}
			try {
                if (rs != null) rs.close();
            } catch (IOException ignored) {}
			screen.appendMessage("assistant", currentBuffer.toString());
			if (screen.messages.size() == 2) screen.setChatTitle(generateChatTitle());
			screen.parseContentAtLastWrapper(true);
			screen.setFocusOnInput();

		} catch (final Exception e) {
			Util.dialogAlert("Send failed: " + e.toString());
		}
	}
	
	private static String ppMessages(Vector messages) {
		String p = "";
		for (int i = 0; i < messages.size(); i++) {
			OpenAIMessage m = (OpenAIMessage) messages.elementAt(i);
			p += "["+ m.role + "] " + m.content + "\n";
		}
		return p;
	}
	
	private String generateChatTitle() {
		titleClient = new OpenAIClient(AppConfig.baseUrl, AppConfig.apiKey);
		String titleInstructionString = "Summarize the following conversation in 5 words or fewer:\n";
		titleInstructionString += ppMessages(screen.messages);
		Vector titleMessages = new Vector();
		titleMessages.addElement(new OpenAIMessage("user", titleInstructionString));
		try {
			OpenAICompletion response = titleClient.createChatCompletion(titleMessages, "gpt-3.5-turbo");
			String titleString = ((OpenAICompletionChoice) response.choices.elementAt(0)).message.content;
//			Util.dialogAlert(titleString);
			return titleString;
		} catch (JSONException e) {
			Util.dialogAlert("JSON failed while generating title: " + e.toString());
			return "No name";
		} catch (IOException e) {
			Util.dialogAlert("Generate failed while generating title: " + e.toString());
			return "No name";
		}
	}
	
	
	
}
