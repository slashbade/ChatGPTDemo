package chatgptDemo;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

public class SettingsScreen extends MainScreen {

    public SettingsScreen() {
        setTitle("Settings");
        
        addTitle("Instance URL");
        final EditField baseUrlField = new EditField("", AppConfig.baseUrl, 200, EditField.EDITABLE | Field.FOCUSABLE);
        add(padded(baseUrlField));

        addSpacer();
        addTitle("Instance API Key");
        final EditField apiKeyField = new EditField("", AppConfig.apiKey, 200, EditField.EDITABLE | Field.FOCUSABLE);
        add(padded(apiKeyField));

        addSpacer();

        addTitle("Model (e.g. gpt-3.5-turbo)");
        final EditField modelField = new EditField("", AppConfig.model, 50, EditField.EDITABLE | Field.FOCUSABLE);
        add(padded(modelField));

        addSpacer();

        addTitle("Temperature (0.0 ~ 1.0)");
        final EditField tempField = new EditField("", String.valueOf(AppConfig.temperature), 10, EditField.EDITABLE | Field.FOCUSABLE);
        add(padded(tempField));

        addSpacer();

        addTitle("Model Instruction");
        final EditField instructionField = new EditField("", AppConfig.instruction, 200, EditField.EDITABLE | Field.FOCUSABLE);
        add(padded(instructionField));
        
        // Save button
        ButtonField saveButton = new ButtonField("Save Settings", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
        saveButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
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
                Dialog.alert("Settings saved!");
                UiApplication.getUiApplication().popScreen(SettingsScreen.this);
            }
        });

        add(padded(saveButton));
    }

    private void addTitle(String title) {
        LabelField label = new LabelField(title, LabelField.FIELD_LEFT | DrawStyle.LEADING);
        label.setMargin(8, 0, 2, 0);
        add(label);
    }

    private void addSpacer() {
        add(new SeparatorField());
    }

    private Field padded(Field f) {
        f.setMargin(2, 0, 6, 0);
        return f;
    }
}
