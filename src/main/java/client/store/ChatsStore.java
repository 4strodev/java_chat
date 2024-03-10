package client.store;

import shared.store.RxStore;

public class ChatsStore extends RxStore<ChatStoreValues> {
    private static ChatsStore instance;
    public static ChatsStore getInstance() {
        if (instance == null) {
            instance = new ChatsStore();
        }

        return instance;
    }

    public ChatsStore() {
        super();
        var defaultValues = new ChatStoreValues();
        this.setDefaultState(defaultValues);
    }

    public void selectNick(String nick) {
        var values = this.snapshot();
        values.selectedChat = nick;
        this.setState(values);
    }
}
