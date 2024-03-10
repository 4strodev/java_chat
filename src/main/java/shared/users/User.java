package shared.users;

import server.connection.Connection;

public record User(Connection connection, String nickName) {}
