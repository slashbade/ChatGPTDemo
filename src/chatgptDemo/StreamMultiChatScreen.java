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
		titleContainer = new VerticalFieldManager(Manager.FIELD_LEFT | Manager.USE_ALL_WIDTH){
			protected void paintBackground(Graphics g) {
		        int w = getWidth();
		        int h = getHeight();

		        /* ------------ 1. base vertical gradient (soft blue‑grey) ------------ */
		        // top: 0x3B4754  ≈ #3b4754, bottom: 0x1C2128  ≈ #1c2128
		        g.drawGradientFilledRect(new XYRect(0, 0, w, h),
		                                 0x3b4754,      // lighter tint at the top
		                                 0x1c2128);     // deeper shade at the bottom

		        /* ------------ 2. glass “shine” band (covers upper ~35 %) ------------ */
		        int oldAlpha = g.getGlobalAlpha();        // remember current alpha
		        g.setGlobalAlpha(70);                     // 0‑255, 70 ≈ 27 % opaque
		        g.drawGradientFilledRect(new XYRect(0, 0, w, h * 35 / 100),
		                                 0xffffff,        // white fades into…
		                                 0x3b4754);       // …the base top colour
		        g.setGlobalAlpha(oldAlpha);               // restore

		        /* ------------ 3. soft top edge highlight --------------------------- */
		        g.setColor(0xffffff);                     // pure white
		        g.drawLine(0, 0, w, 0);                   // 1‑pixel highlight

		        /* ------------ 4. subtle inner shadow at the bottom ----------------- */
		        g.setGlobalAlpha(50);                     // very light touch
		        g.setColor(0x000000);                     // black, slightly transparent
		        g.fillRect(0, h - 3, w, 3);               // 3‑pixel fade
		        g.setGlobalAlpha(oldAlpha);               // always restore
		        g.setColor(Color.WHITE);
		    }
		};
		titleContainer.setPadding(12, 8, 12, 8);
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

		        /* ------------ 1. base vertical gradient (soft blue‑grey) ------------ */
		        // top: 0x3B4754  ≈ #3b4754, bottom: 0x1C2128  ≈ #1c2128
		        g.drawGradientFilledRect(new XYRect(0, 0, w, h),
		                                 0x3b4754,      // lighter tint at the top
		                                 0x1c2128);     // deeper shade at the bottom

		        /* ------------ 2. glass “shine” band (covers upper ~35 %) ------------ */
		        int oldAlpha = g.getGlobalAlpha();        // remember current alpha
		        g.setGlobalAlpha(70);                     // 0‑255, 70 ≈ 27 % opaque
		        g.drawGradientFilledRect(new XYRect(0, 0, w, h * 35 / 100),
		                                 0xffffff,        // white fades into…
		                                 0x3b4754);       // …the base top colour
		        g.setGlobalAlpha(oldAlpha);               // restore

		        /* ------------ 3. soft top edge highlight --------------------------- */
		        g.setColor(0xffffff);                     // pure white
		        g.drawLine(0, 0, w, 0);                   // 1‑pixel highlight

		        /* ------------ 4. subtle inner shadow at the bottom ----------------- */
		        g.setGlobalAlpha(50);                     // very light touch
		        g.setColor(0x000000);                     // black, slightly transparent
		        g.fillRect(0, h - 3, w, 3);               // 3‑pixel fade
		        g.setGlobalAlpha(oldAlpha);               // always restore
		    }
		};
		inputContainer.setPadding(8, 8, 8, 8);
		inputContainer.setBackground(background);
		inputField = new PromptInputField("", 500, EditField.EDITABLE
				| Field.FOCUSABLE | Field.USE_ALL_WIDTH | Field.FIELD_VCENTER);
		inputField.setMargin(8, 2, 8, 2);
		inputField.setSendListener(new PromptInputField.SendListener() {
			public void onSend(String message) {
				addMessage("user", message);
				sendMessages();
			}
		});
		HorizontalFieldManager inputFieldWrapper = new HorizontalFieldManager(Manager.FIELD_VCENTER) {
			protected void paint(Graphics g) {
				int round = 8;
				int left = getContentLeft();
				int top = getContentTop();
				int width = getContentWidth();
				int height = getContentHeight();
				g.setColor(Color.WHITE);
				g.fillRoundRect(left, top, width, height, round, round);
				
				g.setColor(0x000000);
				super.paint(g);
				g.setGlobalAlpha(150);
				g.drawGradientFilledRoundedRect(left, top, width, round,
						Color.GRAY, Color.WHITE, false,
						Graphics.TOP_LEFT_ROUNDED_RECT_CORNER | Graphics.TOP_RIGHT_ROUNDED_RECT_CORNER, round, round);
				g.setColor(Color.BLACK);
				g.setGlobalAlpha(255);
				g.drawRoundRect(left, top, width, height, round, round);
			}
		};
		
		inputFieldWrapper.add(inputField);
		
		getMainManager().setBackground(background);

		inputContainer.add(inputFieldWrapper);
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