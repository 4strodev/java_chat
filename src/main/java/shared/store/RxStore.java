package shared.store;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class RxStore<T> {
    private T defaultState;
    private final BehaviorSubject<T> state;
    protected RxStore() {
        this.defaultState = null;
        this.state = BehaviorSubject.create();
    }

    public void setDefaultState(T values) {
        this.defaultState = values;
        state.onNext(values);
    }

    public RxStore(T initialState) {
        this.defaultState = initialState;
        this.state = BehaviorSubject.createDefault(initialState);
    }
    public void setState(T state) {
        this.state.onNext(state);
    }

    public Observable<T> select() {
        return this.state.hide();
    }

    public <R> Observable<R> selectOnly(RxStoreSelector<T, R> selector) {
        return this.state.compose((upstream) -> upstream.map(selector::select)).hide();
    }

    public T snapshot() {
        return this.state.getValue();
    }

    public <R> R snapshotOnly(RxStoreSelector<T, R> selector) {
        return selector.select(this.state.getValue());
    }

    public void reset() {
        this.state.onNext(defaultState);
    }
}
