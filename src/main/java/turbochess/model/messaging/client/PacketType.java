package turbochess.model.messaging.client;

public enum PacketType{
    TEXT,           // text message sent from a user into the room
    MOVE,           //
    CREATE_ROOM,    // user sends a request to create a room, receives the room code and is placed in the room.
                    // Server's job is to create an endpoint at {PREFIX}/${roomcode}.
    JOIN_ROOM,      // user sends a request to join a room with a code specified in the payload,
                    // is placed in the room if successful with the assigned role
    GAME_OVER,
    LEAVE_ROOM,     // on websocket termination, client automatically sends a goodbye message
    BET_RAISE,      // user informs the server they wish to increase their bet on the current match BY the amount
                    // specified in the payload, server checks the user's coin balance
    CHEER,          // cheer for your friend!
    SAVE_ROOM,            // if the users wish to resume the game at a later game, only then do we save the game state the DB

    EMPTY           // for packets without any data (we differentiate between them based on destination end-points)

}
