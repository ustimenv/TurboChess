//'use strict';

var script = document.createElement('script');            //TODO import jquery locally
script.src = 'https://code.jquery.com/jquery-3.4.1.min.js';
script.type = 'text/javascript';
document.getElementsByTagName('head')[0].appendChild(script);


// Room creation & joining
var usernamePage = document.querySelector('#username-page');
var roomPage = document.querySelector('#room-page');
var rootContainer = document.querySelector('#root-container')
var connecting = document.querySelector('.connecting-to-room');
var createRoomForm = document.querySelector('#createRoomForm');
var roomCodeForm = document.querySelector('#roomCodeForm');
if(createRoomForm!=null)createRoomForm.addEventListener('submit', handleCreateRoom, true);
if(roomCodeForm!=null)roomCodeForm.addEventListener('submit', handleJoinRoom, true);
var roomCode = null;            // set via an ajax call by creating or joining a room
//var username           ---->> is defined and set in the inline script in room.html

var stompClient = null;

// In-room messaging
var messageForm = document.querySelector('#messageForm');
var messagesBox = document.querySelector('#messages-box');      // has two children: event box (join/leave) & chat box; put msgs in correct boxes based on TYPE enum
var messageInputBox = document.querySelector('#message-input-box');
messageForm.addEventListener('submit', sendMessage, true);
var colours = ['#e30e1f', '#0e0ee3', '#cde01d', '#cde01d', '#070806', '#e010b7', '#467a7a'];

// Betting
var betForm = document.querySelector('#betForm');
var betInputBox = document.querySelector('#bet-input-box');
var bettedOnWhitesRadio = document.querySelector('#whites-win');
var bettedOnDrawRadio = document.querySelector('#draw');
var bettedOnBlacksRadio = document.querySelector('#blacks-win');
betForm.addEventListener('submit', sendBetRaise, true);
var betPlacedOn = null;
var totalBet = 0;

// Chess
var actionsDiv = document.querySelector('#actions');
var boardDiv = document.querySelector('.board');
var isRoomOwner = false;
var numPeopleInRoom = 0;
const game = new Chess();
var myColour = null;
var isDraw = false;
var gameOverSent = false;
/**
*         CHESS
*/

function onDragStart (source, piece, position, orientation) {

    if(!game.game_over()){
        if(numPeopleInRoom < 2)  return false; // need at least two players to play
        if(!piece.includes(myColour)){  // can't move other colour's pieces
            return false;
        }
    } else{
        var gameVictory = 0;    // -1 = loss, 0=draw, 1 = victory
        //timer.stop()
        if(!game.in_draw()){    // if not a draw, then the colour whose turn it's currently NOT wins
            if(game.tun() === myColour){
                gameVictory=-1;
            } else{
                gameVictory=1;
            }
        } else{
            gameVictory=0;
        }
        if(!gameOverSent){
            gameOverSent = true;
            declareGameOver(gameVictory);
        }
        return false;
    }
}

function onDrop (source, target) {
    var move = game.move({
        from: source,
        to: target,
        promotion: 'q'                              // NOTE: always promote to a queen for example simplicity
    });
    if (move === null)    return 'snapback'         // illegal move
    sendMove(move);
}

function onSnapEnd () {
    board.position(game.fen());    // update the board position after the piece snap for castling, en passant, pawn promotion
}

var asd = {
    draggable:      true,
    position:       'start',
    onDragStart:    onDragStart,
    onDrop:         onDrop,
    onSnapEnd:      onSnapEnd
}
board = Chessboard('myBoard', asd)

/**
*           STOMP
*/

function connect() {
    usernamePage.classList.add('hidden');   // username input page goes away
    roomPage.classList.remove('hidden');    // the main chatroom page becomes visible, while we attempt to establish connection

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, {});
}

function onConnected() {
    stompClient.subscribe(`/queue/${roomCode}`, onMessageReceived);             // subscribe to the room's channe;
                                                                                // inform the room that you've subbed
    stompClient.send(`/app/${roomCode}.chat.addUser`, {},
                     JSON.stringify({from: username, type: 'JOIN_ROOM', roomToJoin: roomCode}));

    connecting.classList.add('hidden');                                         // remove the 'Connecting...'
    boardDiv.classList.remove('hidden');                                        // and show the board
    if(isRoomOwner){
        actionsDiv.insertAdjacentHTML('afterbegin', '<img src="img/save.jpg" style="width: 50px;" title="Save game" onclick="handleSaveRoom()"/>');
    }
}

function sendMessage(e) {
    var messageContent = messageInputBox.value.trim();
    if(messageContent && stompClient) {
        var packet = {
            from: username,
            text: messageInputBox.value,
            type: 'TEXT',
            context: roomCode
        };
        stompClient.send(`/app/${roomCode}.chat.sendMessage`, {}, JSON.stringify(packet));
        messageInputBox.value = '';
    }
    e.preventDefault();
}

function sendMove(movementJSON){
    var packet = JSON.stringify({from: username, context: roomCode, type: 'MOVE', fen: game.fen(),
                                origin: movementJSON["from"], destination: movementJSON["to"],
                                colour: movementJSON["color"]
                                });
    console.log("SENDING MOVE " + packet);
    stompClient.send(`/app/${roomCode}.sys.sendMove`, {}, packet);
}

function sendBetRaise(e){
     if(betInputBox && stompClient) {
         var colourBettedOn = null;
         if(bettedOnWhitesRadio.checked){
            colourBettedOn = 'whites';
         } else if(bettedOnDrawRadio.checked){
            colourBettedOn = 'draw';
         } else if(bettedOnBlacksRadio.checked){
            colourBettedOn = 'blacks';
         } else{
            console.log("To bet, gotta pick a side to bet on");
            return;
         }
         var packet = {
            from: username,
            betAmount: betInputBox.value,
            bettingOn: colourBettedOn,
            type: 'BET_RAISE',
            context: roomCode
         };

         stompClient.send(`/app/${roomCode}.sys.placeBet`, {}, JSON.stringify(packet));
         totalBet += parseInt(betInputBox.value);
         betInputBox.value = 0;
     }
     e.preventDefault();
}

function sendCheer(e){
     var packet = {
         from: username,
         payload: null,
         type: 'CHEER',
         context: roomCode
     };
     stompClient.send(`/app/${roomCode}.chat.cheer`, {}, JSON.stringify(packet));
}

function onMessageReceived(messageReceived) {
    var message = JSON.parse(messageReceived.body);

    var messageElement = document.createElement('li');
    // by the end of the switch, we will have initialised either an event msg or a chat msg
    var messageToShow="";

    switch(message.type){
    // CHAT
    case 'TEXT':
        messageElement.classList.add('chat-message');                       // make a pretty avatar &  display the msg
        var avatarElement = document.createElement('i');
        avatarElement.appendChild(document.createTextNode(message.from[0]));        // user's initials
        avatarElement.style['background-color'] = colours[Math.abs(hashString(message.from) % colours.length)]; //user's avatar colour
        messageElement.appendChild(avatarElement);     // msgElem contains avatar & 1st letter

        var usernameElement = document.createElement('span');           // now display username
        var usernameText = document.createTextNode(message.from);       // the actual text will be appended afterwards
        usernameElement.appendChild(usernameText);                  // the initial setup is done here to place the text
        messageElement.appendChild(usernameElement);    // in the correct div (we want to keep events & chats separate)
    break;
    case 'BET_RAISE':
        messageElement.classList.add('event-message');
        messageToShow = message.from + ' has increased their bet to ' + message.betAmount + '!';
    break;
    case 'CHEER':
        messageElement.classList.add('event-message');
        messageToShow = message.text;
    break;

    case 'CREATE_ROOM':
//        alert('creating room?');
    break;

    case 'JOIN_ROOM':
        numPeopleInRoom=parseInt(message.numParticipants);
        messageElement.classList.add('event-message');
        messageToShow = message.from + ' joined!';
    break;
    case 'LEAVE_ROOM':
        numPeopleInRoom--;
        messageElement.classList.add('event-message');
        messageToShow = message.from + ' left!';
    break;
    case 'MOVE':
        console.log(message);
        if(message.from !== username){
            game.move({from: message.origin, to: message.destination});
            board.position(game.fen());
        }
        return;
    break;
    default:
        alert('Unexpected arg ' + message.type);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(messageToShow);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messagesBox.appendChild(messageElement);
    messagesBox.scrollTop = messagesBox.scrollHeight;
}

/**
*           AJAX
*/
function handleJoinRoom(e){
    e.preventDefault();
    var code = document.getElementById("roomcode").value;

    if(code){
        setCSRFtoken();
        var data = {}
        data['from'] = username;
        data['type']='JOIN_ROOM';
        data['roomToJoin']=code;
        data['context']='';   // at this point the context hasn't been set or 'approved' by the server

        $.ajax({
                type : 'POST',
                contentType : 'application/json',
                dataType : 'json',
                data : JSON.stringify(data),
                url : '/api/join_room',
                success : function(response) {
                    myColour = response.colourAssigned;
                    fen = response.fen;
                    totalBet = response.accumulatedBet;
                    game.load(fen);
                    board.position(game.fen());
                    roomCode = code;                        // set the room 'context'
                    connect();

                },
                error : function(e) {
                    alert("Room " + code + " doesn't exist!");
                    console.log('ERROR: ', e);
                },
                done : function(e) {
                    console.log('done...');
                }
            });
    }
}

function handleCreateRoom(e){
    e.preventDefault();
    var capacity = document.getElementById('createRoomForm')[0].value;      // todo validate capacity on-the-fly
    if(capacity < 2){       // todo display the error msg in a div
        alert("Room capacity must be at least 2!");
        return;
    }

    setCSRFtoken();
    var data = {}
    data['from'] = username;
    data['type']='CREATE_ROOM';
    data['capacity']=capacity;
    data['context']='';

    $.ajax({
        type : 'POST',
        contentType : 'application/json',
        dataType : 'json',
        data : JSON.stringify(data),
        url : '/api/create_room',
        success : function(response) {
            isRoomOwner=true;
            myColour = response.colourAssigned;
            roomCode = response.roomCodeAssigned;
            connect();
        },
        error : function(e) {
            console.log('ERROR: ', e);
        },
        done : function(e) {
            console.log('done...');
        }
    });
}

function declareGameOver(gameResult){
    setCSRFtoken();
    var data = {}
    data['from'] = username;
    data['type'] = 'GAME_OVER';
    data['context'] = roomCode;
    switch(gameResult){
    case -1:
        data['result']='LOSS';
        break;
    case 1:
        data['result']='WIN';
        break;
    case 0:
    default:
        data['result']='DRAW';
        break;
    }

    $.ajax({
        type : 'POST',
        contentType : 'application/json',
        dataType : 'json',
        data : JSON.stringify(data),
        url : '/api/game_over',
        success : function(response) {
            alert("Game over!");
        },
        error : function(e) {
            console.log('ERROR: ', e);
        },
        done : function(e) {
            console.log('done...');
        }
    });
}

function handleIncreaseBet(){
    var value = parseInt(betInputBox.value, 10);
    value = isNaN(value) ? 0 : value;
    value+=5;
    betInputBox.value = value;
}


/**
*           UTILITY METHODS
*/

function setCSRFtoken(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
      });
}

function hashString(str) {
    var hash = 0, i, chr;
    if (str.length === 0) return hash;
    for (i = 0; i < str.length; i++) {
        chr   = str.charCodeAt(i);
        hash  = ((hash << 5) - hash) + chr;
        hash |= 0;
    }
    return hash;
}

