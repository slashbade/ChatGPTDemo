package chatgptDemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.json.me.JSONException;

import openai.OpenAIClient;
import openai.OpenAICompletion;
import openai.OpenAICompletionChoice;
import openai.OpenAIMessage;
import openai.OpenAIStreamClient;

import net.rim.device.api.ui.UiApplication;

public class ChatDispatcher extends Thread {
	ChatScreen screen;
	OpenAIStreamClient client;
	OpenAIClient titleClient;
	UiApplication uiApplication = UiApplication.getUiApplication();
	
	
	public ChatDispatcher(ChatScreen screen) {
		this.screen = screen;
	}
	
	public void run() {
		try {
			client = new OpenAIStreamClient(AppConfig.baseUrl, AppConfig.apiKey);
			if (!(AppConfig.instruction.trim().equals(""))) {
				screen.appendMessage("system", AppConfig.instruction);
			}
			final InputStream is = client.connectStream(screen.messages, AppConfig.model);
			final StringBuffer currentBuffer = new StringBuffer();
			final MessageContainerWrapper messageWrapper = screen.addMessageWrapper("assistant", "");
			
			StreamReaderThread.StreamUpdateCallback streamReaderCallback = 
					new StreamReaderThread.StreamUpdateCallback() {
				public void onNewContent(final String content) {
					currentBuffer.append(content);
					screen.addContentAtMessageWrapper(messageWrapper, content);
				}

				public void onComplete() {
					screen.appendMessage("assistant", currentBuffer.toString());
					if (screen.messages.size() == 2) {
						screen.chatTitle = generateChatTitle();
					}
					uiApplication.invokeLater(new Runnable() {
						public void run() {
							messageWrapper.parseContent(true);
							screen.inputContainer.getField(0).setFocus();
							
						}
					});
				}
				
				public void onError(final String error) {
					Util.dialogAlert("Stream Error: " + error);
				}
			};
			
			StreamReaderThread streamReaderThread = new StreamReaderThread(
					is, streamReaderCallback);
			streamReaderThread.start();

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
			OpenAICompletion response = titleClient.generateCompletion(titleMessages, "gpt-3.5-turbo");
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
