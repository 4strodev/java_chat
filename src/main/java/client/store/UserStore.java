package client.store;

import shared.store.RxStore;

public class UserStore extends RxStore<UserStoreValues> {
    private static UserStore instance;
    protected UserStore() {
        super();
        this.setDefaultState(new UserStoreValues());
    }

    public static UserStore getInstance() {
        if (instance == null) {
            instance = new UserStore();
        }

        return instance;
    }

    public void setNickname(String nickname) {
        var values = this.snapshot();
        values.nickname = nickname;
        this.setState(values);
    }
}
