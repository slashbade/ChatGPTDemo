package chatgptDemo.components;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class SearchInputField extends EditField {
    private SendListener listener;
    private String placeholder = "Search";

    public SearchInputField(String initialText, String placeholder, int maxChars, long style) {
        super("", initialText, maxChars, style);
        this.placeholder = placeholder;
        
        super.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));
        Font baseFont = Font.getDefault();
		Font inputFont = baseFont.derive(Font.PLAIN, baseFont.getHeight());
		super.setPadding(0, 2, 0, 2);
        super.setFont(inputFont);
    }

    protected boolean keyChar(char key, int status, int time) {
        boolean handled = super.keyChar(key, status, time);
        final String query = getText().trim().toLowerCase();
        listener.onSend(query);
        return handled;
    }

    
    public void setSendListener(SendListener l) {
        this.listener = l;
    }

    public interface SendListener {
        void onSend(String query);
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
