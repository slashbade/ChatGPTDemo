package chatgptDemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class Util {
	public static void dialogAlert(final String s) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert(s);
			}
		});
	}
	
	public static void dialogAlert(final int i) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert(String.valueOf(i));
			}
		});
	}
}
