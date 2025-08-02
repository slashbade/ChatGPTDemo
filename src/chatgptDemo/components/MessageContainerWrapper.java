package chatgptDemo.components;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MessageContainerWrapper extends VerticalFieldManager {
	
	public MessageContainerWrapper(String role, String content) {
		super((role.equals("user") ? Field.FIELD_RIGHT : Field.FIELD_LEFT));
		super.setPadding(5, 10, 5, 10);
		super.add(new MessageContainer(role, content));
	}

	public void addContent(String newContent) {
		((MessageContainer) super.getField(0)).addContent(newContent);
	}
	public void parseContent(boolean isComplete) {
		((MessageContainer) super.getField(0)).parseContent(isComplete);
	}
}
