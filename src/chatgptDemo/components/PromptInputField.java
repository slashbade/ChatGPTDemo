package chatgptDemo.components;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;

public class PromptInputField extends EditField {
    private SendListener listener;
    private String placeholder = "Type a message...";

    public PromptInputField(String initialText, String placeholder, int maxChars, long style) {
        super("", initialText, maxChars, style);
        this.placeholder = placeholder;
        
        super.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));
        Font baseFont = Font.getDefault();
		Font inputFont = baseFont.derive(Font.PLAIN, baseFont.getHeight());
		super.setPadding(0, 2, 0, 2);
        super.setFont(inputFont);
    }

    public boolean keyChar(char key, int status, int time) {
        if (key == Keypad.KEY_ENTER) {
            boolean altDown = (status & Keypad.status(Keypad.KEY_SHIFT_LEFT)) != 0;
            if (altDown) {
                // Shift + Enter → insert newline
                this.insert("\n");
            } else {
                // Enter → send
                if (listener != null) {
                    final String text = getText().trim();
                    if (text.length() > 0) {
                        listener.onSend(text);
                        setText(""); // clear input
                    }
                }
            }
            return true; // consume key
        }
        return super.keyChar(key, status, time);
    }

    
    public void setSendListener(SendListener l) {
        this.listener = l;
    }

    public interface SendListener {
        void onSend(String message);
    }
    
    protected void paint(Graphics g) {
        // Draw placeholder if empty
        int padding = 0;
    	if (getTextLength() == 0) {
            g.setColor(0x999999); // light gray
            g.drawText(placeholder, padding, padding);
        }
    	g.setColor(0x000000);
    	super.paint(g);
    }
    
}
