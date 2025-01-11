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
    const val PLAYER_DISCONNECT = "disconnect"
    const val PLAYER_MOVE_RIGHT = "right"
    const val PLAYER_MOVE_LEFT = "left"
    const val PLAYER_JUMP = "jump"
    const val PLAYER_SPRINT = "sprint"
    const val PLAYER_ABILITY = "ability"
}
