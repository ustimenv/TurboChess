'use strict';

var script = document.createElement('script');              // Surely there's a better way to import jquery...
script.src = 'https://code.jquery.com/jquery-3.4.1.min.js';
script.type = 'text/javascript';
document.getElementsByTagName('head')[0].appendChild(script);

var usernamePage = document.querySelector('#username-page');    // primary container
var chatPage = document.querySelector('#chat-page');            //

var connecting = document.querySelector('.connecting-to-room');

var createRoomForm = document.querySelector('#createRoomForm');
var roomCodeForm = document.querySelector('#roomCodeForm');

var messageForm = document.querySelector('#messageForm');
var messagesBox = document.querySelector('#messages-box');      // has two children: event box (join/leave) & chat box; put msgs in correct boxes based on TYPE enum
var messageInputBox = document.querySelector('#message-input-box');

var betForm = document.querySelector('#betForm');
var betInputBox = document.querySelector('#bet-input-box');

createRoomForm.addEventListener('submit', handleCreateRoom, true);
roomCodeForm.addEventListener('submit', handleJoinRoom, true);
messageForm.addEventListener('submit', sendMessage, true);
betForm.addEventListener('submit', betRaise, true);


var stompClient = null;
var roomCode = null;            // set via an ajax call by creating or joining a room
//var username ---->>              is defined and set in the inline script in room.html

var colours = ['#e30e1f', '#0e0ee3', '#cde01d', '#cde01d', '#070806', '#e010b7', '#467a7a'];

//function connect(e) {
//    username = document.querySelector('#name').value.trim();
//
//    if(username) {                              // once a valid username was entered
//        usernamePage.classList.add('hidden');   // username input page goes away
//        chatPage.classList.remove('hidden');    // the main chatroom page becomes visible, while we attempt to establish connection
//
//        var socket = new SockJS('/ws');         // TODO websockets vs sockjs
//        stompClient = Stomp.over(socket);
//        stompClient.connect({}, onConnected, {});
//    }
//    e.preventDefault();
//}


/**
*           STOMP
*/

function connect() {
    usernamePage.classList.add('hidden');   // username input page goes away
    chatPage.classList.remove('hidden');    // the main chatroom page becomes visible, while we attempt to establish connection

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, {});
}

function onConnected() {
    stompClient.subscribe(`/queue/${roomCode}`, onMessageReceived);             // subscribe to the room's channe;
                                                                                // inform the room that you've subbed
    stompClient.send(`/app/${roomCode}.chat.addUser`, {},
                     JSON.stringify({from: username, type: 'JOIN_ROOM'}));
    connecting.classList.add('hidden');                                         // remove the 'Connecting...'
}

function sendMessage(e) {
    var messageContent = messageInputBox.value.trim();
    if(messageContent && stompClient) {
        var packet = {
            from: username,
            payload: messageInputBox.value,
            type: 'TEXT'
        };
        stompClient.send(`/app/${roomCode}.chat.sendMessage`, {}, JSON.stringify(packet));
        messageInputBox.value = '';
    }
    e.preventDefault();
}

function betRaise(e) {                      // TODO add logic
     if(betInputBox && stompClient) {
         var packet = {
             from: username,
             payload: betInputBox.value,
             type: 'BET_RAISE'
         };

         stompClient.send(`/app/${roomCode}.chat.betRaise`, {}, JSON.stringify(packet));
     }
     e.preventDefault();
}

function onMessageReceived(messageReceived) {
    var message = JSON.parse(messageReceived.body);

    var messageElement = document.createElement('li');
    // by the end of the switch, we will have initialised either an event msg or a chat msg
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
        message.payload = message.from + ' has increased their bet to ' + message.payload + '!';
    break;

    case 'CREATE_ROOM':
        alert('creating room?');
    break;

    case 'JOIN_ROOM':
        messageElement.classList.add('event-message');
        message.payload = message.from + ' joined!';
    break;
    case 'LEAVE_ROOM':
        messageElement.classList.add('event-message');
        message.payload = message.from + ' left!';
    break;
    default:
        alert('Unexpected arg ' + message.type);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.payload);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messagesBox.appendChild(messageElement);
    messagesBox.scrollTop = messagesBox.scrollHeight;
}

/**
*           AJAX
*/
function handleJoinRoom(e){
    var code = document.getElementsByClassName('roomCodeForm')[0].value;

    if(code){                                  // once a valid username was entered
        connect();
    }
    e.preventDefault();
}

function handleCreateRoom(e){        //todo hide current view, show the room view
    e.preventDefault();
    setCSRFtoken();
    var data = {}
    data['from'] = username;
    data['type']='CREATE_ROOM';
    data['payload']='';

    $.ajax({
        type : 'POST',
        contentType : 'application/json',
        dataType : 'json',
        data : JSON.stringify(data),
        url : '/api/createroom',
        success : function(response) {
            roomCode = response.payload;
            alert('NAME=' + username + ', '+ 'ROOM '+ roomCode);
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
function handleIncreaseBet(){
    // TODO add AJAX call to ensure user's coin balance is sufficient for the raise
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

