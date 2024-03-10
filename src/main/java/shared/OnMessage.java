package shared;

@FunctionalInterface
public interface OnMessage<T> {
    void handleMessage(T message);
}
