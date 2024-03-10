package shared.users;

import shared.connection.Connection;

public record User(Connection connection, String nickName) {}
