package chatgptDemo;

import java.util.Vector;

import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

public class ChatHomeScreen extends MainScreen {
	
	private String chatTitleStoreName = "ChatTitleStore";
	private String chatStoreName = "ChatStore";
    private ListField chatList;
    private Vector chatSessions;
//    private EditField searchField;
    private SearchInputContainer searchInputContainer;
    private Vector fullChatSessions; // full unfiltered list

    public ChatHomeScreen() {
        setTitle("Chat Sessions");
        
//        chatSessions = loadSessions();
        
        fullChatSessions = loadSessions(); // Keep the full list
        chatSessions = new Vector();
        chatSessions = copy(fullChatSessions); // Initially show all
//        Util.dialogAlert(fullChatSessions.size());
//        Util.dialogAlert(chatSessions.size());

        // Create Search Field
//        searchField = new EditField("Search: ", "", 50, EditField.EDITABLE) {
//            protected boolean keyChar(char key, int status, int time) {
//                boolean handled = super.keyChar(key, status, time);
//                filterSessions();
//                return handled;
//            }
//        };
//        add(searchField);
        searchInputContainer = new SearchInputContainer();
        searchInputContainer.setSendListener(new SearchInputField.SendListener() {
			
			public void onSend(String query) {
				filterSessions(query);
			}
		});
        add(searchInputContainer);
        
        
        chatList = new ListField() {
            protected boolean navigationClick(int status, int time) {
                int index = getSelectedIndex();
                if (index >= 0) {
                    openSession(index);
                    return true;
                }
                return false;
            }
        };
        chatList.setCallback(new ChatListCallback());

        chatList.setSize(chatSessions.size());
        add(chatList);
        
//        Background background = BackgroundFactory
//				.createSolidBackground(0xb2b2b2);
//        getMainManager().setBackground(background);
    }
    
    private void filterSessions(String query) {
        if (query.trim() == "") {
        	chatSessions = copy(fullChatSessions);
        	chatList.setSize(chatSessions.size());
            chatList.invalidate();
        	return;
        }
        chatSessions.removeAllElements();
//        Util.dialogAlert(fullChatSessions.size());

        for (int i = 0; i < fullChatSessions.size(); i++) {
            JSONObject session = (JSONObject) fullChatSessions.elementAt(i);
            String title = session.optString("chatTitle").toLowerCase();
//            Util.dialogAlert(session.optInt("chatTitle"));
            if (title.indexOf(query) >= 0) {
                chatSessions.addElement(session);
            }
        }
        chatList.setSize(chatSessions.size());
        chatList.invalidate();
    }

    protected void onExposed() {
    	fullChatSessions = loadSessions();
        filterSessions("");
	}
    
    private Vector loadSessions() {
    	Vector chatSessions = new Vector();
    	try {
			RecordStore rsi = RecordStore.openRecordStore(chatTitleStoreName, true);
			// for first init
			if (rsi.getNumRecords() == 0) {
				JSONArray emptyArray = new JSONArray();
				rsi.addRecord(emptyArray.toString().getBytes("UTF-8"), 0, emptyArray.toString().length());
			}
			String data = new String(rsi.getRecord(1), "UTF-8");
			JSONArray mj = new JSONArray(data);
//			Util.dialogAlert(data);
			for (int i = 0; i < mj.length(); i++) {
				JSONObject chatSession = (JSONObject) mj.get(i);
				chatSessions.addElement(chatSession);
			}
		} catch (Exception e) {
			Util.dialogAlert("Load chat sessions failed: " + e.toString());
			
		}
        return chatSessions;
    }

    private void openSession(int index) {
        JSONObject chatSession = (JSONObject) chatSessions.elementAt(index);
        UiApplication.getUiApplication().pushScreen(
            new ChatScreen(chatSession.optString("chatTitle"),
            		chatSession.optInt("recordId")) // Pass the session title if needed
        );
    }

    private void createNewSession() {
    	UiApplication.getUiApplication().pushScreen(
                new ChatScreen("No name", -1) // Pass the session title if needed
            );
    }

    private void deleteSession(int index) {
    	JSONObject chatSession = (JSONObject) chatSessions.elementAt(index);
    	String chatTitle = chatSession.optString("chatTitle");
    	int recordId = chatSession.optInt("recordId");
    	
    	int response = Dialog.ask(Dialog.D_YES_NO, 
            "Are you sure you want to delete \"" + chatTitle + "\"?");
        if (response == Dialog.YES) {
            chatSessions.removeElementAt(index);
            chatList.setSize(chatSessions.size());
            chatList.invalidate();
        }
    	
    	try {
    		// Delete chatTitle
    		RecordStore rsi = RecordStore.openRecordStore(chatTitleStoreName, true);
    		String s = new String(rsi.getRecord(1), "UTF-8");
    		JSONArray mj = new JSONArray(s);
    		JSONArray nmj = new JSONArray();
    		for (int i = 0; i < mj.length(); i++) {
    			JSONObject cs = (JSONObject) mj.get(i);
    			if (cs.equals(chatSession)) {
    				nmj = removeElementAt(mj, i);
    				break;
    			}
    		};
    		byte[] data = nmj.toString().getBytes("UTF-8");
    		rsi.setRecord(1, data, 0, data.length);
    		rsi.closeRecordStore();
    		// Delete chat
    		RecordStore rsc = RecordStore.openRecordStore(chatStoreName, true);
    		rsc.deleteRecord(recordId);
    		rsc.closeRecordStore();
    	} catch (Exception e) {
    		Util.dialogAlert("Delete failed: " + e.toString());
    	}
    	
    	
        
    }
    
    private JSONArray removeElementAt(JSONArray arr, int i) {
    	JSONArray newArr = new JSONArray();
    	for (int j = 0; j < arr.length(); j++) {
    		if (j != i) {
    			try { newArr.put(arr.get(j)); } catch (Exception e) {};
    		}
    	}
    	return newArr;
    }
    
    private Vector copy(Vector arr) {
    	Vector newArr = new Vector();
    	for (int i = 0; i < arr.size(); i++) {
    		newArr.addElement(arr.elementAt(i));
    	}
    	return newArr;
    }

    private class ChatListCallback implements ListFieldCallback {
        public void drawListRow(ListField list, Graphics g, int index, int y, int width) {
        	JSONObject chatSession = (JSONObject) chatSessions.elementAt(index);
        	String title = chatSession.optString("chatTitle");
            g.drawText(title, 10, y);
        }

        public Object get(ListField list, int index) {
            return chatSessions.elementAt(index);
        }

        public int indexOfList(ListField list, String prefix, int start) {
            return chatSessions.indexOf(prefix, start);
        }

        public int getPreferredWidth(ListField list) {
            return Display.getWidth();
        }
    }
    
    protected void makeMenu(Menu menu, int instance) {
        super.makeMenu(menu, instance);

        menu.add(new MenuItem(new StringProvider("New Chat"), 100, 10) {
            public void run() {
                createNewSession();
            }
        });

        menu.add(new MenuItem(new StringProvider("Delete Chat"), 110, 10) {
            public void run() {
                int index = chatList.getSelectedIndex();
//                Util.dialogAlert(index);
//                Util.dialogAlert(chatSessions.size());
                if (index >= 0) {
                    deleteSession(index);
                }
            }
        });
        menu.add(new MenuItem(new StringProvider("Settings"), 100, 10) {
			public void run() {
				SettingsScreen settingsScreen = new SettingsScreen();
				UiApplication.getUiApplication().pushScreen(settingsScreen);
			}
		});
    }
}