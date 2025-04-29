package chatgptDemo;

import javax.microedition.rms.*;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import org.json.me.*;

public class AppConfig {
    private static final String STORE_NAME = "AppConfigStoreJSON";
    public static String baseUrl = "https://api.openai-proxy.org/v1/chat/completions";
    public static String apiKey = "";
    public static String model = "gpt-3.5-turbo";
    public static double temperature = 0.7;
    public static String instruction = "";

    public static void saveAll(String burl, String key, String mdl, double temp, String it) {
        baseUrl = burl;
    	apiKey = key;
        model = mdl;
        temperature = temp;
        instruction = it;
        
        try {
            RecordStore.deleteRecordStore(STORE_NAME); // Clear old
        } catch (Exception ignored) {}

        try {
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME, true);
            JSONObject json = new JSONObject();
            json.put("api_key", apiKey);
            json.put("base_url", baseUrl);
            json.put("model", model);
            json.put("temperature", temperature);
            json.put("instruction", instruction);
            String data = json.toString();
            rs.addRecord(data.getBytes("UTF-8"), 0, data.length());
            rs.closeRecordStore();
        } catch (final Exception e) {
        	UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.inform("Save failed: " + e.toString());
                }
            });
        }
    }

    public static void load() {
        try {
            RecordStore rs = RecordStore.openRecordStore(STORE_NAME, false);
            if (rs.getNumRecords() > 0) {
                byte[] data = rs.getRecord(1);
                String s = new String(data, "UTF-8");
                JSONObject json = new JSONObject(s);
                apiKey = json.getString("api_key");
                baseUrl = json.getString("base_url");
                model = json.getString("model");
                temperature = json.getDouble("temperature");
                instruction = json.getString("instruction");
            }
            rs.closeRecordStore();
        } catch (final Exception e) {
//        	UiApplication.getUiApplication().invokeLater(new Runnable() {
//                public void run() {
//                    Dialog.inform("Load failed: " + e.toString());
//                }
//            });
        	
        }
    }
}