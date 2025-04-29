package chatgptDemo;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

public class SettingsScreen extends MainScreen {
	EditField baseUrlField;
	PasswordEditField apiKeyField;
	EditField modelField;
	EditField tempField;
	EditField instructionField;
	ButtonField saveButton;

    public SettingsScreen() {
        setTitle("Settings");
        
        VerticalFieldManager instanceSettingsGroup = createSettingsGroup("Instance");
        
        addTitle("Instance URL:", instanceSettingsGroup);
        baseUrlField = new EditField("", AppConfig.baseUrl, 200, EditField.EDITABLE | Field.FOCUSABLE | EditField.FILTER_URL);
        instanceSettingsGroup.add(padded(baseUrlField));

        addTitle("Instance API Key:", instanceSettingsGroup);
        apiKeyField = new PasswordEditField("", AppConfig.apiKey, 200, EditField.EDITABLE | Field.FOCUSABLE | EditField.FILTER_URL);
        instanceSettingsGroup.add(padded(apiKeyField));

        VerticalFieldManager modelSettingsGroup = createSettingsGroup("Model");
        addTitle("Model: (e.g. gpt-3.5-turbo)", modelSettingsGroup);
        modelField = new EditField("", AppConfig.model, 50, EditField.EDITABLE | Field.FOCUSABLE);
        modelSettingsGroup.add(padded(modelField));


        addTitle("Temperature: (0.0 ~ 1.0)", modelSettingsGroup);
        tempField = new EditField("", String.valueOf(AppConfig.temperature), 10, EditField.EDITABLE | Field.FOCUSABLE);
        modelSettingsGroup.add(padded(tempField));

        addTitle("Instruction:", modelSettingsGroup);
        instructionField = new EditField("", AppConfig.instruction, 200, EditField.EDITABLE | Field.FOCUSABLE);
        modelSettingsGroup.add(padded(instructionField));
        
        // Save button
        saveButton = new ButtonField("Save", ButtonField.CONSUME_CLICK | Field.FIELD_RIGHT);
        saveButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                save();
                Dialog.alert("Settings saved!");
                UiApplication.getUiApplication().popScreen(SettingsScreen.this);
            }
        });
        
        add(instanceSettingsGroup);
        add(modelSettingsGroup);
        add(padded(saveButton));
        Background background = BackgroundFactory.createSolidBackground(0xb2b2b2);
        getMainManager().setBackground(background);
        this.setTransition();
    }
    
    public void save() {
    	String baseUrl = baseUrlField.getText().trim();
    	String apiKey = apiKeyField.getText().trim();
        String model = modelField.getText().trim();
        String tempStr = tempField.getText().trim();
        String instruction = instructionField.getText().trim();
        
        if (apiKey.length() == 0 || model.length() == 0 || tempStr.length() == 0) {
            Dialog.alert("All fields are required.");
            return;
        }

        double temp;
        try {
            temp = Double.parseDouble(tempStr);
            if (temp < 0.0 || temp > 1.0) {
                Dialog.alert("Temperature must be between 0.0 and 1.0");
                return;
            }
        } catch (NumberFormatException e) {
            Dialog.alert("Temperature must be a number.");
            return;
        }

        AppConfig.saveAll(baseUrl, apiKey, model, temp, instruction);
    }
    
    private VerticalFieldManager createSettingsGroup(String groupName) {
    	VerticalFieldManager vfm = new VerticalFieldManager(Manager.USE_ALL_WIDTH);
    	vfm.setMargin(4, 4, 4, 4);
    	vfm.setPadding(8, 8, 8, 8);
    	RichTextField groupNameField = new RichTextField(groupName, Field.NON_FOCUSABLE) {
    		protected void paint(Graphics g) {
    			g.setColor(Color.GRAY);
    			super.paint(g);
    		}
    	};
    	Font groupNameFont = getFont().derive(Font.BOLD, getFont().getHeight() - 4);
    	groupNameField.setFont(groupNameFont);
    	SeparatorField sp = new SeparatorField();
    	vfm.add(groupNameField);
    	vfm.add(sp);
    	Background background = BackgroundFactory.createSolidBackground(Color.WHITE);
    	vfm.setBackground(background);
    	return vfm;
    }
    
    
    private void addTitle(String title, VerticalFieldManager vfm) {
        LabelField label = new LabelField(title, LabelField.FIELD_LEFT | DrawStyle.LEADING);
        label.setMargin(8, 0, 2, 0);
        vfm.add(label);
    }

    private void setTransition() {
		SettingsScreen settingsScreen = SettingsScreen.this;
		TransitionContext transition = new TransitionContext(TransitionContext.TRANSITION_ZOOM);
		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 100);
		transition.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_IN);
		transition.setIntAttribute(TransitionContext.ATTR_SCALE, 75);
		UiEngineInstance engine = Ui.getUiEngineInstance();
		engine.setTransition(null, settingsScreen, UiEngineInstance.TRIGGER_PUSH, transition);
		TransitionContext transitionPop = new TransitionContext(TransitionContext.TRANSITION_ZOOM);
		transitionPop.setIntAttribute(TransitionContext.ATTR_DURATION, 100);
		transitionPop.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);
		transitionPop.setIntAttribute(TransitionContext.ATTR_SCALE, 125);
		engine.setTransition(settingsScreen, null, UiEngineInstance.TRIGGER_POP, transitionPop);
    }

    private Field padded(Field f) {
        f.setMargin(2, 0, 6, 0);
        return f;
    }

    protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(new StringProvider("Save"), 100, 10) {
			public void run() {
				save();
			}
		});
	}
}
