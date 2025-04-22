package markdown;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MarkdownContainer extends VerticalFieldManager {
    private Vector renderedFields = new Vector();
    private StringBuffer processQueue = new StringBuffer();
    private RichTextField previewField;
    private Font baseFont;

    public MarkdownContainer(Font baseFont) {
    	super(Manager.USE_ALL_WIDTH | Manager.FIELD_LEFT);
    	this.baseFont = baseFont;
    	previewField = new RichTextField("");
    	previewField.setFont(baseFont);
        add(previewField);
    }
    
    public int getPreferredWidth() {
    	int maxWidth = Display.getWidth() * 7/8;
    	int preferredWidth = 200;
    	for (int i = 0; i < this.getFieldCount(); i++) {
    		Field f = getField(i);
    		if (f instanceof RichTextField) {
    			preferredWidth = Math.max(preferredWidth, ((RichTextField) f).getFont().getAdvance(((RichTextField) f).getText()));
    		}
    		
    	}
    	return Math.min(maxWidth, preferredWidth);
    }

    public void addContent(String newContent) {
        processQueue.append(newContent);
    }
    
    public RichTextField getPreviewField() {
    	return previewField;
    }

    public void update(boolean isComplete) {
    	String previewText = "";
        final String markdown = processQueue.toString();
        processQueue.setLength(0);
        if (markdown.equals("")) {
        	return;
        }
        final Vector lines = StringUtils.split(markdown, '\n');
        
        if (!isComplete) {
        	previewText = (String) lines.elementAt(lines.size() - 1);
        	lines.removeElementAt(lines.size() - 1);
        	processQueue.append(previewText);
        }
        previewField.setText(previewText);
        boolean inCodeBlock = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = ((String) lines.elementAt(i));

            if (line.equals("```")) {
                inCodeBlock = !inCodeBlock;
                continue;
            }

                Font boldFont = baseFont.derive(Font.BOLD);
                Font monoFont = baseFont;

                if (inCodeBlock) {
                    RichTextField codeField = new RichTextField(line, RichTextField.NON_FOCUSABLE);
                    codeField.setFont(monoFont);
                    insertBeforePreview(codeField);
                } else if (line.startsWith("#### ")) {
                    RichTextField h4 = MarkdownInlineFormatter.parseFormattedRichText(line.substring(5), baseFont);
                    h4.setFont(boldFont.derive(Font.BOLD, baseFont.getHeight()));
                    insertBeforePreview(h4);
                } else if (line.startsWith("### ")) {
                    RichTextField h3 = MarkdownInlineFormatter.parseFormattedRichText(line.substring(4), baseFont);
                    h3.setFont(boldFont.derive(Font.BOLD, baseFont.getHeight()+2));
                    insertBeforePreview(h3);
                } else if (line.startsWith("## ")) {
                    RichTextField h2 = MarkdownInlineFormatter.parseFormattedRichText(line.substring(3), baseFont);
                    h2.setFont(boldFont.derive(Font.BOLD, baseFont.getHeight()+4));
                    insertBeforePreview(h2);
                } else if (line.startsWith("# ")) {
                    RichTextField h1 = MarkdownInlineFormatter.parseFormattedRichText(line.substring(2), baseFont);
                    h1.setFont(boldFont.derive(Font.BOLD, baseFont.getHeight()+8));
                    insertBeforePreview(h1);
                } else if (line.equals("---")) {
                	SeparatorField sp = new SeparatorField(); 
                	sp.setMargin(4, 0, 4, 0);
                    insertBeforePreview(sp);
                } else if (!line.equals("")) {
                	RichTextField plain = MarkdownInlineFormatter.parseFormattedRichText(line, baseFont);
                	plain.setFont(baseFont);
                    insertBeforePreview(plain);
                }
        }
        if (isComplete) {
        	delete(previewField);
        }
    }

    private void insertBeforePreview(Field field) {
        int index = previewField.getIndex();
        this.insert(field, index);
        renderedFields.addElement(field);
    }

}