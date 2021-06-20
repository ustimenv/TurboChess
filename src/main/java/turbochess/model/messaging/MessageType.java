package turbochess.model.messaging;

public enum MessageType{
//STOMP---->        (we don't save text messages)
    TEXT,           // text message sent from a user into the room

//AJAX POST---->    (since the state 0f the server changes)
    CREATE_ROOM,    // user sends a request to create a room, receives the room code and is placed in the room.
                    // Server's job is to create an endpoint at {PREFIX}/${roomcode}.
    JOIN_ROOM,      // user sends a request to join a room with a code specified in the payload,
                    // is placed in the room if successful

    LEAVE_ROOM,     // on websocket session termination, client automatically sends a goodbye message

    BET_RAISE,      // user informs the server they wish to increase their bet on the current match to the amount
                    // specified in the payload, server checks the user's coin balance


    MOVEMENT,        // TODO if the user's role in the room is that of a player,
                    // parse the payload to extract the desired movement and broadcast

    SAVE            // if the users wish to resume the game at a later game, only then do we save the game state the DB

}
