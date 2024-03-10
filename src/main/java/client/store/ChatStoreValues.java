package client.store;

import java.util.ArrayList;
import java.util.List;

public class ChatStoreValues {
    public final ArrayList<String> chatList = new ArrayList<>();
    public String selectedChat = "";

    public void setChatList(List<String> chatList) {
        this.chatList.clear();
        this.chatList.addAll(chatList);
    }
}
