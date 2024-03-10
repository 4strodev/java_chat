package shared.users;

import shared.connection.Connection;

public record User(Connection messaggingConnection, Connection notificationsConnection, String nickName) {}
