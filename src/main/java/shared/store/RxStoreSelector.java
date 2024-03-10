package shared.store;

@FunctionalInterface
public interface RxStoreSelector<T, R> {
    R select(T state);
}
