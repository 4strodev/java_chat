package client.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatStoreValues {
    public final HashMap<String, ArrayList<String>> chatMessages = new HashMap<>();
    public final ArrayList<String> chatList = new ArrayList<>();
    public String selectedChat = "";

    public void setChatList(List<String> chatList) {
        this.chatList.clear();
        this.chatList.addAll(chatList);
    }

    public void addMessage(String user, String message) {
        if (!this.chatMessages.containsKey(user)) {
            return;
        }
        var messages = this.chatMessages.get(user);
        messages.add(message);
    }
}
