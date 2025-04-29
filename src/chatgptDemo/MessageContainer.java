package chatgptDemo;

import markdown.MarkdownContainer;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class MessageContainer extends VerticalFieldManager {
	public String role;
	public String content;
	public RichTextField roleField;
	private MarkdownContainer markdownContainer;
	public int padding = 14;

	public MessageContainer(final String role, String content) {
		super(Manager.FIELD_VCENTER);
		this.role = role;
		this.content = content;
		Border shadowBorder = BorderFactory.createSimpleBorder(new XYEdges(1,
				1, 1, 1), // top, right, bottom, left thickness
				new XYEdges(0xCCCCCC, // top (lighter for light source)
						0x999999, // right
						0x666666, // bottom (darker shadow)
						0xAAAAAA // left
				), // top, right, bottom, left color
				Border.STYLE_SOLID);
		super.setBorder(shadowBorder);

		Font baseFont = Font.getDefault();
		Font roleFont = baseFont.derive(Font.BOLD, baseFont.getHeight() - 8);
		RichTextField roleField = new RichTextField(
				(role.equals("user") ? "You" : "Assistant"),
				(role.equals("user") ? RichTextField.TEXT_JUSTIFY_RIGHT
						: RichTextField.TEXT_JUSTIFY_LEFT)) {
			public int getPreferredWidth() {
				return getFont().getAdvance(getText());
			}
		};

		roleField.setFont(roleFont);
		roleField.setMargin(padding / 2, padding, 0, padding);
		markdownContainer = new MarkdownContainer(baseFont);
		markdownContainer.addContent(content);
		markdownContainer.setMargin(0, padding, padding / 2, padding);
		SeparatorField separatorField = new SeparatorField();
		separatorField.setMargin(0, padding, 0, padding);
		add(roleField);
		add(separatorField);
		add(markdownContainer);
	}

	protected void paint(Graphics g) {
		g.setColor(role.equals("user") ? 0xE5EBF3 : 0xDDDDDD);
		g.fillRoundRect(getContentLeft(), getContentTop(), getWidth(),
				getHeight(), 8, 8);
		g.setColor(0x000000);
		super.paint(g);
	}

	protected void sublayout(int width, int height) {
		int preferredWidth = 200;
		for (int i = 0; i < getFieldCount(); i++) {
			Field field = getField(i);
			if (field instanceof MarkdownContainer)
				preferredWidth = Math.max(preferredWidth,
						field.getPreferredWidth());
		}
		for (int i = 0; i < getFieldCount(); i++) {
			Field field = getField(i);
			layoutChild(field, preferredWidth, height);
		}
		setExtent(preferredWidth, getHeight());
		super.sublayout(preferredWidth + 2 * padding, height);
	}
	
	public void addContent(String newContent) {
		MarkdownContainer mc = (MarkdownContainer) super.getField(2);
		mc.addContent(newContent);
		RichTextField previewField = mc.getPreviewField();
		previewField.setCursorPosition(previewField.getText().length());
		previewField.setFocus();
	}
	public void parseContent(boolean isComplete) {
		MarkdownContainer mc = (MarkdownContainer) super.getField(2);
		mc.update(isComplete);
	}
}
