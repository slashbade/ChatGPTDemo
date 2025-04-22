package chatgptDemo;

import java.io.InputStream;
import java.util.Vector;

import openai.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

public class StreamMultiChatScreen extends MainScreen {

	private UiApplication uiApplication = UiApplication.getUiApplication();
	private VerticalFieldManager titleContainer;
	private LabelField modelField;
	private VerticalFieldManager chatContainer;
	private HorizontalFieldManager inputContainer;
	private PromptInputField inputField;

	private OpenAIStreamClient client;
	private Vector messages = new Vector(); // stores Message objects

	public StreamMultiChatScreen() {
		super(Manager.NO_VERTICAL_SCROLL);
		AppConfig.load();
		Font baseFont = Font.getDefault();
		Font titleFont = baseFont.derive(Font.BOLD, baseFont.getHeight() + 4);
		Font modelFont = baseFont.derive(Font.PLAIN, baseFont.getHeight() - 4);
		LabelField titleField = new LabelField("ChatGPT Demo",
				LabelField.FIELD_LEFT);
		titleField.setFont(titleFont);
		modelField = new LabelField("Model: " + AppConfig.model,
				LabelField.FIELD_LEFT);
		modelField.setFont(modelFont);
		titleContainer = new VerticalFieldManager(Manager.FIELD_LEFT);
		titleContainer.setPadding(10, 14, 10, 14);
		titleContainer.add(titleField);
		titleContainer.add(modelField);
		setTitle(titleContainer);

		chatContainer = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR | Manager.USE_ALL_WIDTH);
		Background background = BackgroundFactory
				.createSolidBackground(0xb2b2b2);
		this.setBackground(background);
		chatContainer.setBackground(background);
		add(chatContainer);

		inputContainer = new HorizontalFieldManager(Manager.USE_ALL_WIDTH) {
			protected void paintBackground(Graphics g) {
				int w = getWidth();
				int h = getHeight();

				// Simulate vertical glass shine
				g.setColor(0x222222); // deep gray
				g.fillRect(0, 0, w, h);

				// Light gloss at top
				g.setColor(0x444444);
				g.drawLine(0, 0, w, 0);

				g.setColor(0x666666);
				g.drawLine(0, 1, w, 1);

				// Subtle highlight bottom edge
				g.setColor(0x111111);
				g.drawLine(0, h - 1, w, h - 1);
			}
		};
		inputContainer.setPadding(10, 14, 10, 14);
		inputContainer.setBackground(background);
		inputField = new PromptInputField("", 500, EditField.EDITABLE
				| Field.FOCUSABLE | Field.USE_ALL_WIDTH);
		inputField.setSendListener(new PromptInputField.SendListener() {
			public void onSend(String message) {
				addMessage("user", message);
				sendMessages();
			}
		});
		getMainManager().setBackground(background);

		inputContainer.add(inputField);
		setStatus(inputContainer);
		client = new OpenAIStreamClient(AppConfig.baseUrl, AppConfig.apiKey);
	}

	protected void onExposed() {
		super.onExposed();
		modelField.setText("Model: " + AppConfig.model);
	}

	private void addMessage(final String role, final String content) {
		messages.addElement(new OpenAIMessage(role, content));
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				MessageWrapper messageWrapper = new MessageWrapper(role, content);
				chatContainer.add(messageWrapper);
				messageWrapper.parseContent(true);
				
			}
		});
	}

	private void addMessageContent(final MessageWrapper messageWrapper,
			final String newContent) {
		uiApplication.invokeLater(new Runnable() {
			public void run() {
				messageWrapper.addContent(newContent);
			}
		});
	}

	private void sendMessages() {
		Thread t = new Thread() {
			public void run() {
				try {
					if (!(AppConfig.instruction.trim().equals(""))) {
						messages.addElement(new OpenAIMessage("system",
								AppConfig.instruction));
					}
					final InputStream is = client.connectStream(messages);
					final StringBuffer currentBuffer = new StringBuffer();
					final MessageWrapper messageWrapper = new MessageWrapper(
							"assistant", "");
					uiApplication.invokeLater(new Runnable() {
						public void run() {
							chatContainer.add(messageWrapper);
						}
					});

					StreamReaderThread streamReaderThread = new StreamReaderThread(
							is, new StreamReaderThread.StreamUpdateCallback() {
								public void onNewContent(final String content) {
									currentBuffer.append(content);
									addMessageContent(messageWrapper,
											content);
									uiApplication.invokeLater(new Runnable() {
										public void run() {
									messageWrapper.parseContent(false);}});
								}

								public void onComplete() {

									messages.addElement(new OpenAIMessage(
											"assistant", currentBuffer
													.toString()));
									uiApplication.invokeLater(new Runnable() {
										public void run() {
											inputContainer.getField(0)
													.setFocus();
											messageWrapper.parseContent(true);
										}
									});
								}

								public void onError(final String error) {
									UiApplication.getUiApplication()
											.invokeLater(new Runnable() {
												public void run() {
													Dialog.alert("Stream Error: "
															+ error);
												}
											});
								}
							});
					streamReaderThread.start();

				} catch (final Exception e) {
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {
								public void run() {
									Dialog.alert("Send failed: " + e.toString());
								}
							});
				}
			}
		};
		t.start();
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(new StringProvider("Settings"), 100, 10) {
			public void run() {
				UiApplication.getUiApplication().pushScreen(
						new SettingsScreen());
			}
		});
		menu.add(new MenuItem(new StringProvider("Clear Chat"), 110, 10) {
			public void run() {
				messages.removeAllElements();
				chatContainer.deleteAll();
			}
		});
		menu.add(new MenuItem(new StringProvider("Send"), 90, 10) {
			public void run() {
				String message = inputField.getText().trim();
				if (message.length() > 0) {
					addMessage("user", message);
					sendMessages();
					inputField.setText(""); // 清空输入框
				} else {
					Dialog.alert("Please enter a message.");
				}
			}
		});
	}
}