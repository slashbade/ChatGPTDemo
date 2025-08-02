package chatgptDemo.components;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class TitleContainer extends VerticalFieldManager {
	public TitleContainer() {
		super(Manager.FIELD_LEFT | Manager.USE_ALL_WIDTH);
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
        g.setColor(Color.WHITE);
    }
}
