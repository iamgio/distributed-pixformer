package pixformer.server

/**
 * URL endpoints for WebSocket messages.
 */
object Endpoints {
    const val WEBSOCKETS = "ws"
    const val CLIENT_SUBSCRIBE = "subscribe"

    const val REALIGN = "realign"
}

/**
 * Values for the `type` query parameter of WebSocket messages.
 */
object EventType {
    const val PLAYER_CONNECT = "connect"
    const val PLAYER_MOVE_RIGHT = "right"
    const val PLAYER_MOVE_LEFT = "left"
    const val PLAYER_JUMP = "jump"
}
