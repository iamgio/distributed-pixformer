package pixformer.server

/**
 * URL endpoints for WebSocket messages.
 */
object Endpoints {
    const val WEBSOCKETS = "ws"

    const val REALIGN = "realign"
}

/**
 * Values for the `type` query parameter of WebSocket messages.
 */
object EventType {
    const val PLAYER_CONNECT = "connect"
    const val PLAYER_JUMP = "jump"
}
