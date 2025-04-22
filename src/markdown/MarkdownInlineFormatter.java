package markdown;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;
import java.util.Vector;

public class MarkdownInlineFormatter {
    public static RichTextField parseFormattedRichText(String line, Font baseFont) {
        try {
            Font fontPlain = baseFont;
            Font fontBold = baseFont.derive(Font.BOLD);
            Font fontItalic = baseFont.derive(Font.ITALIC);
            Font fontMono = baseFont.derive(Font.PLAIN);

            Vector fontList = new Vector();
            fontList.addElement(fontPlain);   // 0
            fontList.addElement(fontBold);    // 1
            fontList.addElement(fontItalic);  // 2
            fontList.addElement(fontMono);    // 3

            StringBuffer outputText = new StringBuffer();
            Vector offsetList = new Vector();
            Vector attrList = new Vector();

            int pos = 0;
            offsetList.addElement(new Integer(0)); // Start of field

            while (pos < line.length()) {
                int nextBold = line.indexOf("**", pos);
                int nextItalic = line.indexOf("_", pos);
                int nextCode = line.indexOf("`", pos);

                // Find earliest formatting marker
                int next = minPositive(nextBold, nextItalic, nextCode);
                if (next == -1) {
                    // No more formatting
                    String rest = line.substring(pos);
                    outputText.append(rest);
                    offsetList.addElement(new Integer(outputText.length()));
                    attrList.addElement(new Byte((byte) 0));
                    break;
                }

                // Add normal part before format marker
                if (next > pos) {
                    String plain = line.substring(pos, next);
                    outputText.append(plain);
                    offsetList.addElement(new Integer(outputText.length()));
                    attrList.addElement(new Byte((byte) 0));  // plain
                }

                // Handle bold
                if (next == nextBold) {
                    int end = line.indexOf("**", next + 2);
                    if (end == -1) break;
                    String bold = line.substring(next + 2, end);
                    outputText.append(bold);
                    offsetList.addElement(new Integer(outputText.length()));
                    attrList.addElement(new Byte((byte) 1)); // bold
                    pos = end + 2;
                }
                // Handle italic
                else if (next == nextItalic) {
                    int end = line.indexOf("*", next + 1);
                    if (end == -1) break;
                    String italic = line.substring(next + 1, end);
                    outputText.append(italic);
                    offsetList.addElement(new Integer(outputText.length()));
                    attrList.addElement(new Byte((byte) 2)); // italic
                    pos = end + 1;
                }
                // Handle code
                else if (next == nextCode) {
                    int end = line.indexOf("`", next + 1);
                    if (end == -1) break;
                    String code = line.substring(next + 1, end);
                    outputText.append(code);
                    offsetList.addElement(new Integer(outputText.length()));
                    attrList.addElement(new Byte((byte) 3)); // mono
                    pos = end + 1;
                }
            }

            // Convert to arrays
            int[] offsets = new int[offsetList.size()];
            byte[] attributes = new byte[offsetList.size() - 1];
            Font[] fonts = new Font[fontList.size()];

            for (int i = 0; i < offsetList.size(); i++) {
                offsets[i] = ((Integer) offsetList.elementAt(i)).intValue();
            }
            for (int i = 0; i < attrList.size(); i++) {
                attributes[i] = ((Byte) attrList.elementAt(i)).byteValue();
            }
            for (int i = 0; i < fonts.length; i++) {
                fonts[i] = (Font) fontList.elementAt(i);
            }

            return new RichTextField(outputText.toString(), offsets, attributes, fonts, RichTextField.NON_FOCUSABLE);

        } catch (Exception e) {
            e.printStackTrace();
            return new RichTextField(line); // fallback
        }
    }

    // Return minimum positive among given values; -1 if all negative
    private static int minPositive(int a, int b, int c) {
        int min = -1;
        if (a >= 0) min = a;
        if (b >= 0 && (min == -1 || b < min)) min = b;
        if (c >= 0 && (min == -1 || c < min)) min = c;
        return min;
    }
}
