package chatgptDemo;

import java.io.InputStream;
import java.util.Vector;

import openai.*;


import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.StringProvider;

public class StreamMultiChatScreen extends MainScreen {

	private VerticalFieldManager titleContainer;
	private LabelField modelField;
    private VerticalFieldManager chatContainer;
    private HorizontalFieldManager inputContainer;

    private OpenAIStreamClient client;
    private Vector messages = new Vector(); // stores Message objects

    public StreamMultiChatScreen() {
        super(Manager.NO_VERTICAL_SCROLL);
        AppConfig.load();
        Font baseFont = Font.getDefault();
        Font titleFont = baseFont.derive(Font.BOLD, baseFont.getHeight() + 2);
        Font modelFont = baseFont.derive(Font.PLAIN, baseFont.getHeight() - 2);
        LabelField titleField = new LabelField("ChatGPT Demo", LabelField.FIELD_LEFT);
        titleField.setFont(titleFont);
        modelField = new LabelField("Model: " + AppConfig.model, LabelField.FIELD_LEFT);
        modelField.setFont(modelFont);
        titleContainer = new VerticalFieldManager(Manager.FIELD_LEFT);
        titleContainer.setPadding(10, 14, 10, 14);
        titleContainer.add(titleField);
        titleContainer.add(new SeparatorField());
        titleContainer.add(modelField);
        setTitle(titleContainer);

        chatContainer = new VerticalFieldManager(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR | Manager.USE_ALL_WIDTH);
        Background background = BackgroundFactory.createSolidBackground(0xb2b2b2);
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
        final PromptInputField inputField = new PromptInputField("", 500, EditField.EDITABLE | Field.FOCUSABLE | Field.USE_ALL_WIDTH);
        inputField.setSendListener(new PromptInputField.SendListener() {
            public void onSend(String message) {
                addMessage("user", message);
                sendMessage(message);
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
    private VerticalFieldManager createMessageBubble(final String role, final String content) {
        final int padding = 14;
        Font baseFont = Font.getDefault();
        Font roleFont = baseFont.derive(Font.BOLD, baseFont.getHeight() - 6);
        Font contentFont = baseFont.derive(Font.PLAIN, baseFont.getHeight() - 2);
    	RichTextField roleField = new RichTextField((role.equals("user") ? "You" : "Assistant"));
    	roleField.setFont(roleFont);
        RichTextField contentField = new RichTextField(content,
            RichTextField.USE_ALL_WIDTH | RichTextField.TEXT_JUSTIFY_LEFT
        ) {
        	public int getPreferredWidth() {
        		int maxWidth = Display.getWidth();
            	if (role.equals("user")) {
                	maxWidth = maxWidth * 3 / 4;
                } else {
                	maxWidth = maxWidth * 7 / 8;
                }
                int textWidth = getFont().getAdvance(getText());
                int actualWidth = Math.min(textWidth, maxWidth);
                return actualWidth + 4 * padding;
        	}
        	
        	protected void layout(int width, int height) {
            	int maxWidth = Display.getWidth();
            	if (role.equals("user")) {
                	maxWidth = maxWidth * 3 / 4;
                } else {
                	maxWidth = maxWidth * 7 / 8;
                }
                int textWidth = getFont().getAdvance(getText());
                int actualWidth = Math.min(textWidth, maxWidth);
                super.layout(actualWidth, height);
                setExtent(actualWidth + 4*padding, getHeight());
            }
        };
        contentField.setFont(contentFont);
        VerticalFieldManager messageContainer = new VerticalFieldManager(Manager.FIELD_VCENTER) {
        	protected void paint(Graphics g) {
            	g.setColor(role.equals("user") ? 0xE5EBF3 : 0xDDDDDD);
            	g.fillRoundRect(getContentLeft(), getContentTop(), getWidth(), getHeight(), 8, 8);
            	g.setColor(0x000000);
            	g.pushRegion(getContentLeft() + padding, getContentTop(), getWidth() - 2*padding, getHeight(), 0, 0);
            	super.paint(g);
                g.popContext();
            }
        	
        	protected void sublayout(int width, int height) {
        		int maxTextWidth = 0;
        		int maxWidth = Display.getWidth();
        		maxWidth = role.equals("user") ? maxWidth * 3 / 4 : maxWidth * 7 / 8;
        		for (int i = 0; i < getFieldCount(); i++) {
        			Field field = getField(i);
        			if (field instanceof RichTextField) {
        				maxTextWidth = Math.max(maxTextWidth, field.getFont().getAdvance(((RichTextField) field).getText()));
        			}
        		}
        		int actualWidth = Math.min(maxWidth, maxTextWidth);
        		super.sublayout(width, height);
        		setExtent(actualWidth + 2 * padding, getHeight() + padding / 2);
        		
        	}
        };
        
        Border shadowBorder = BorderFactory.createSimpleBorder(
        	    new XYEdges(1, 1, 1, 1), // top, right, bottom, left thickness
        	    new XYEdges(
        	            0xCCCCCC, // top (lighter for light source)
        	            0x999999, // right
        	            0x666666, // bottom (darker shadow)
        	            0xAAAAAA  // left
        	        ), // top, right, bottom, left color
        	    Border.STYLE_SOLID
        	);
        messageContainer.setBorder(shadowBorder);
//        messageContainer.setPadding(2, 2, 2, 2);
        

        // Wrap it in an HFM for alignment
        VerticalFieldManager hfm = new VerticalFieldManager(
            (role.equals("user") ? Field.FIELD_RIGHT : Field.FIELD_LEFT)
        );
        hfm.setPadding(5, 10, 5, 10);
        messageContainer.add(roleField);
        messageContainer.add(new SeparatorField());
        messageContainer.add(contentField);
        hfm.add(messageContainer);
//        hfm.add(contentField);
        
        return hfm;
    }
    
    private void addMessage(final String role, final String content) {
        // Save for context
        messages.addElement(new OpenAIMessage(role, content));

        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
            	chatContainer.add(createMessageBubble(role, content));
            }
        });
    }

    private void sendMessage(final String userMessage) {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (!(AppConfig.instruction.trim().equals(""))) {
                    	messages.addElement(new OpenAIMessage("system", AppConfig.instruction));
                    }
                	final InputStream is = client.connectStream(messages);

                    // 缓存当前 assistant 回复临时气泡
                    final StringBuffer currentBuffer = new StringBuffer();

                    UiApplication.getUiApplication().invokeLater(new Runnable() {
                        public void run() {
                        	final VerticalFieldManager hfm = createMessageBubble("assistant", "");
                            chatContainer.add(hfm);

                            new StreamReaderThread(is, new StreamReaderThread.StreamUpdateCallback() {
                                public void onNewContent(final String content) {
                                    currentBuffer.append(content);
                                    UiApplication.getUiApplication().invokeLater(new Runnable() {
                                        public void run() {
                                        	
                                            RichTextField tf = (RichTextField) ((VerticalFieldManager) hfm.getField(0)).getField(2);
                                        	tf.setText(currentBuffer.toString());
                                            tf.setCursorPosition(tf.getText().length());
                                        	tf.setFocus();
                                        	tf.getManager().invalidate();
                                        }
                                    });
                                }

                                public void onComplete() {
                                    UiApplication.getUiApplication().invokeLater(new Runnable() {
                                        public void run() {
                                        	inputContainer.getField(0).setFocus();
                                        }
                                    });
                                }

                                public void onError(final String error) {
                                    UiApplication.getUiApplication().invokeLater(new Runnable() {
                                        public void run() {
                                            Dialog.alert("Stream Error: " + error);
                                        }
                                    });
                                }
                            }).start();
                        }
                    });

                } catch (final Exception e) {
                    UiApplication.getUiApplication().invokeLater(new Runnable() {
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
                UiApplication.getUiApplication().pushScreen(new SettingsScreen());
            }
        });
        menu.add(new MenuItem(new StringProvider("Clear Chat"), 110, 10) {
            public void run() {
                messages.removeAllElements();
                chatContainer.deleteAll();
            }
        });
    }
}