package chatgptDemo.screens;

import java.util.Vector;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import chatgptDemo.AppConfig;
import chatgptDemo.ChatDispatcher;
import chatgptDemo.Util;
import chatgptDemo.components.MessageContainerWrapper;
import chatgptDemo.components.PromptInputContainer;
import chatgptDemo.components.PromptInputField;
import chatgptDemo.components.TitleContainer;
import openai.models.OpenAIMessage;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

public class ChatScreen extends MainScreen {

	private UiApplication uiApplication = UiApplication.getUiApplication();
	private TitleContainer titleContainer;
	private LabelField modelField;
	private VerticalFieldManager chatContainer;
	public PromptInputContainer inputContainer;

	public Vector messages = new Vector(); // stores Message objects
	public String chatTitle = "No name";
	private int recordId;

	private static String chatStoreName = "ChatStore";
	private static String chatTitleStoreName = "ChatTitleStore";

	public ChatScreen(String chatTitle, int recordId) {
		super(Manager.NO_VERTICAL_SCROLL);
		// Util.dialogAlert(recordId);
		this.recordId = recordId;
		if (recordId > 0) {
			load(chatTitle, recordId);
		}
		AppConfig.load();
		Font baseFont = Font.getDefault();
		Font titleFont = baseFont.derive(Font.BOLD, baseFont.getHeight() + 4);
		Font modelFont = baseFont.derive(Font.PLAIN, baseFont.getHeight() - 4);
		LabelField titleField = new LabelField("ChatGPT Client", LabelField.FIELD_LEFT);
		titleField.setFont(titleFont);
		modelField = new LabelField("Model: " + AppConfig.model, LabelField.FIELD_LEFT);
		modelField.setFont(modelFont);
		titleContainer = new TitleContainer();
		titleContainer.setPadding(12, 8, 12, 8);
		titleContainer.add(titleField);
		titleContainer.add(modelField);
		setTitle(titleContainer);

		chatContainer = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR | Manager.USE_ALL_WIDTH);
		Background background = BackgroundFactory
				.createSolidBackground(0xb2b2b2);
		chatContainer.setBackground(background);
		add(chatContainer);

		inputContainer = new PromptInputContainer();
		inputContainer.setSendListener(new PromptInputField.SendListener() {
			public void onSend(String userContent) {
				sendMessages(userContent);
			}
		});
		setStatus(inputContainer);
		getMainManager().setBackground(background);

		// Load previous messages
		for (int i = 0; i < messages.size(); i++) {
			OpenAIMessage m = (OpenAIMessage) messages.elementAt(i);
			addMessageWrapper(m.role, m.content);
		}
		if (chatContainer.getFieldCount() > 0) {
			uiApplication.invokeLater(new Runnable() {
				public void run() {
					chatContainer.getField(chatContainer.getFieldCount() - 1).setFocus();
					chatContainer.invalidate();
				}
			});
		}
		setTransition();

	}

	protected void onExposed() {
		super.onExposed();
		modelField.setText("Model: " + AppConfig.model);
	}

	public void appendMessage(final String role, final String content) {
		messages.addElement(new OpenAIMessage(role, content));
	}

	public void addMessageWrapper(final String role, final String content) {
		final MessageContainerWrapper messageWrapper = new MessageContainerWrapper(role, content);
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				chatContainer.add(messageWrapper);
				messageWrapper.parseContent(true);
			}
		});
	}

	public MessageContainerWrapper getLastMessageContainerWrapper() {
		int messagesCount = chatContainer.getFieldCount();
		return (MessageContainerWrapper) chatContainer.getField(messagesCount - 1);
	}

	public void addContentAtLastWrapper(final String newContent) {
		final MessageContainerWrapper messageWrapper = getLastMessageContainerWrapper();
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				messageWrapper.addContent(newContent);
				messageWrapper.parseContent(false);
			}
		});
		this.setDirty(true);
	}

	public void parseContentAtLastWrapper(final boolean isComplete) {
		final MessageContainerWrapper messageWrapper = getLastMessageContainerWrapper();
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				messageWrapper.parseContent(isComplete);
			}
		});
	}

	public void setFocusOnInput() {
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				inputContainer.getField(0).setFocus();
			}
		});

	}

	public void setChatTitle(final String title) {
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				chatTitle = title;
			}
		});
	}

	private void sendMessages(String userContent) {
		// chatContainer.setMuddy(true);
		ChatDispatcher dispatcher = new ChatDispatcher(this, userContent);
		dispatcher.start();
	}

	public void load(String chatTitle, int recordId) {
		try {
			RecordStore rsm = RecordStore.openRecordStore(chatStoreName, true);
			byte[] data = rsm.getRecord(recordId);
			String s = new String(data, "UTF-8");
			JSONArray mj = new JSONArray(s);
			for (int i = 0; i < mj.length(); i++) {
				messages.addElement(new OpenAIMessage((JSONObject) mj.get(i)));
			}
			this.chatTitle = chatTitle;

		} catch (Exception e) {
			Util.dialogAlert("Load chat session failed: " + e.toString());
		}
	}

	public void save() {
		try {
			RecordStore rsm = RecordStore.openRecordStore(chatStoreName, true);
			byte[] messageData = (new JSONArray(messages)).toString().getBytes("UTF-8");
			if (!(recordId < 0)) {
				rsm.setRecord(recordId, messageData, 0, messageData.length);
				rsm.closeRecordStore();

				return;
			}

			int newRecordId = rsm.addRecord(messageData, 0, messageData.length);
			rsm.closeRecordStore();

			JSONObject chatSession = new JSONObject();
			chatSession.put("chatTitle", chatTitle);
			chatSession.put("time", System.currentTimeMillis());
			chatSession.put("recordId", newRecordId);

			RecordStore rsi = RecordStore.openRecordStore(chatTitleStoreName, true);

			byte[] chatSessionData = rsi.getRecord(1);
			JSONArray chatSessions = new JSONArray(new String(chatSessionData, "UTF-8"));
			chatSessions.put(chatSession);
			chatSessionData = chatSessions.toString().getBytes("UTF-8");

			rsi.setRecord(1, chatSessionData, 0, chatSessionData.length);
			rsi.closeRecordStore();
			setDirty(false);

		} catch (Exception e) {
			Util.dialogAlert("Save chat session failed: " + e.toString());
		}
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(new StringProvider("Settings"), 100, 10) {
			public void run() {
				SettingsScreen settingsScreen = new SettingsScreen();
				UiApplication.getUiApplication().pushScreen(settingsScreen);
			}
		});
		menu.add(new MenuItem(new StringProvider("Clear Chat"), 110, 10) {
			public void run() {
				messages.removeAllElements();
				chatContainer.deleteAll();
			}
		});
		menu.add(new MenuItem(new StringProvider("Save Chat"), 110, 10) {
			public void run() {
				save();
				Util.dialogAlert("Save chat session successfully!");
			}
		});
		menu.add(new MenuItem(new StringProvider("Send"), 120, 10) {
			public void run() {
				String userContent = inputContainer.inputField.getText().trim();
				if (userContent.length() > 0) {
					sendMessages(userContent);
					inputContainer.inputField.setText("");
				} else {
					Dialog.alert("Please enter a message.");
				}
			}
		});
	}

	private void setTransition() {
		ChatScreen chatScreen = ChatScreen.this;
		TransitionContext transition = new TransitionContext(TransitionContext.TRANSITION_ZOOM);
		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 100);
		transition.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_IN);
		transition.setIntAttribute(TransitionContext.ATTR_SCALE, 75);
		UiEngineInstance engine = Ui.getUiEngineInstance();
		engine.setTransition(null, chatScreen, UiEngineInstance.TRIGGER_PUSH, transition);
		TransitionContext transitionPop = new TransitionContext(TransitionContext.TRANSITION_ZOOM);
		transitionPop.setIntAttribute(TransitionContext.ATTR_DURATION, 100);
		transitionPop.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);
		transitionPop.setIntAttribute(TransitionContext.ATTR_SCALE, 125);
		engine.setTransition(chatScreen, null, UiEngineInstance.TRIGGER_POP, transitionPop);
	}
}