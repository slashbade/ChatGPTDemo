package chatgptDemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MessageWrapper extends VerticalFieldManager {
	
	public MessageWrapper(String role, String content) {
		super((role.equals("user") ? Field.FIELD_RIGHT : Field.FIELD_LEFT));
		super.setPadding(5, 10, 5, 10);
		super.add(new MessageContainer(role, content));
	}
//	public void updateContent(String currentContent) {
//		((MessageContainer) super.getField(0)).updateContent(currentContent);
//	}
	public void addContent(String newContent) {
		((MessageContainer) super.getField(0)).addContent(newContent);
	}
	public void parseContent(boolean isComplete) {
		((MessageContainer) super.getField(0)).parseContent(isComplete);
	}
}
