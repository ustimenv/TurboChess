'use strict';

var usernamePage = document.querySelector('#username-page');    // root container
var chatPage = document.querySelector('#chat-page');            //
var usernameInput = document.querySelector('#usernameInput');
var messageForm = document.querySelector('#messageForm');
var messageInputBox = document.querySelector('#message-input-box');
var messagesBox = document.querySelector('#messages-box');      // has two children: event box (join/leave) & chat box; put msgs in correct boxes based on TYPE enum
var connecting = document.querySelector('.connecting-to-room');

messageForm.addEventListener('submit', sendMessage, true)
usernameInput.addEventListener('submit', connect, true)

var peopleInRoom=0;
var stompClient = null;
var username = null;

var colours = ['#e30e1f', '#0e0ee3', '#cde01d', '#cde01d', '#070806', '#e010b7', '#467a7a',
];

function connect(e) {
    username = document.querySelector('#name').value.trim();

    if(username) {                              // once a valid username was entered
        usernamePage.classList.add('hidden');   // username input page goes away
        chatPage.classList.remove('hidden');    // the main chatroom page becomes visible, while we attempt to establish connection

        var socket = new SockJS('/ws');         // TODO websockets vs sockjs
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, {});
    }
    e.preventDefault();
}


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

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');
    switch(message.type){
    case 'TEXT':
        messageElement.classList.add('chat-message');
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.from[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = colours[Math.abs(hashString(message.from) % colours.length)];
        peopleInRoom++;
        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.from);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    break;
    case 'JOIN_ROOM':
        messageElement.classList.add('event-message');
        message.text = message.from + ' joined!';
    break;
    case 'LEAVE_ROOM':
        messageElement.classList.add('event-message');
        message.text = message.from + ' left!';
    break;
    case 'VOTE_KICK':
        alert("VOTE KICK TO-DO!");
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