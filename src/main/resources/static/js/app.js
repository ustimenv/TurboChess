'use strict';

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

createRoomForm.addEventListener('submit', createRoomInit, true);
roomCodeForm.addEventListener('submit', joinRoomInit, true);
messageForm.addEventListener('submit', sendMessage, true);
betForm.addEventListener('submit', betRaise, true);


var stompClient = null;
//var username = null;

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


function onConnected() {
    stompClient.subscribe('/queue/public', onMessageReceived);  // subscribe to the channel
    // tell the others on the channel you've subbed
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({from: username, type: 'JOIN_ROOM'}));
    connecting.classList.add('hidden');      // remove the "Connecting..."
}

function sendMessage(e) {
    var messageContent = messageInputBox.value.trim();
    if(messageContent && stompClient) {
        var packet = {
            from: username,
            text: messageInputBox.value,
            type: 'TEXT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(packet));
        messageInputBox.value = "";
    }
    e.preventDefault();
}

function betRaise(e) {          // TODO add logic
     if(betInputBox && stompClient) {
         var packet = {
             from: username,
             text: betInputBox.value,
             type: 'BET_RAISE'
         };

         stompClient.send("/app/chat.betRaise", {}, JSON.stringify(packet));
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
        message.text = message.from + ' has increased their bet to ' + message.text + '!';
    break;

    case 'CREATE_ROOM':
        alert("creating room?");
    break;

    case 'JOIN_ROOM':
        messageElement.classList.add('event-message');
        message.text = message.from + ' joined!';
    break;
    case 'LEAVE_ROOM':
        messageElement.classList.add('event-message');
        message.text = message.from + ' left!';
    break;
    default:
        alert("Unexpected arg " + message.type);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.text);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messagesBox.appendChild(messageElement);
    messagesBox.scrollTop = messagesBox.scrollHeight;
}


function hashString(str) {
  var hash = 0, i, chr;
  if (str.length === 0) return hash;
  for (i = 0; i < str.length; i++) {
    chr   = str.charCodeAt(i);
    hash  = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
}

function incrementValue(){
    var value = parseInt(betInputBox.value, 10);
    value = isNaN(value) ? 0 : value;
    value+=5;
    betInputBox.value = value;
}

//
function createRoomInit(){

    alert("name = " + username);
    //TODO call AJAX
//    usernamePage.classList.add('hidden');           // username input page goes away
//    chatPage.classList.remove('hidden');

}

function joinRoomInit(e){
    var code = document.getElementsByClassName('roomCodeForm')[0].value;

    if(code) {                                  // once a valid username was entered
        usernamePage.classList.add('hidden');   // username input page goes away
        chatPage.classList.remove('hidden');    // the main chatroom page becomes visible, while we attempt to establish connection

        var socket = new SockJS('/ws');         // TODO websockets vs sockjs
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, {});
    }
    e.preventDefault();
}

function setUsername(name){
    username=name;
    alert("USER NAME IS " + username);
}