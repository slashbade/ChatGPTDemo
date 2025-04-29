package chatgptDemo;

import chatgptDemo.PromptInputField.SendListener;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class PromptInputContainer extends HorizontalFieldManager {
	HorizontalFieldManager inputFieldWrapper;
	PromptInputField inputField;
	SendListener listener;

	public PromptInputContainer() {
		// TODO Auto-generated constructor stub
		super(Manager.USE_ALL_WIDTH);
		setPadding(8, 8, 8, 8);
		inputFieldWrapper = new HorizontalFieldManager(Manager.FIELD_VCENTER) {
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
		inputField = new PromptInputField("", " Type a message", 500, EditField.EDITABLE
				| Field.FOCUSABLE | Field.USE_ALL_WIDTH | Field.FIELD_VCENTER);
		inputField.setMargin(8, 2, 8, 2);
		inputFieldWrapper.add(inputField);
		add(inputFieldWrapper);
	}
	
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
	
    public void setSendListener(PromptInputField.SendListener l) {
        inputField.setSendListener(l);
    }

}
