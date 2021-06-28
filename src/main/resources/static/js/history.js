//'use strict';

var script = document.createElement('script');            //TODO import jquery locally
script.src = 'https://code.jquery.com/jquery-3.4.1.min.js';
script.type = 'text/javascript';
document.getElementsByTagName('head')[0].appendChild(script);


//var username           ---->> is defined and set in the inline script in history.html

// Chess
var boardDiv;
var currentlyWhiteTurn;

var currentlyInSimulation;
var simulatedMoveIndex;
var simulatedMovesTotal;
var autoPlay;

var moves = [];
var game;
var asd;

var playPauseButton;
var backButton;
var forwardButton;


function showBoard(movesString){      // {src-dst|src-dst|...}
    const tmp = movesString.split("|");
    for(var i=0; i<tmp.length; i++){
        const move = tmp[i].split("-");
        const moveObj = {src : move[0],
                         dst : move[1]};
        moves.push(moveObj);
    }
    asd = {
        draggable:      true,
        position:       'start',
        onDragStart:    onDragStart,
        onDrop:         onDrop,
        onSnapEnd:      onSnapEnd
    }
    currentlyWhiteTurn=true;
    currentlyInSimulation=true;
    simulatedMoveIndex=0;
    simulatedMovesTotal=moves.length;
    autoPlay = false;

    game = new Chess();
    boardDiv = document.querySelector('.board');

    playPauseButton = document.querySelector('#play-pause');
    backButton = document.querySelector('#previous-turn');
    forwardButton = document.querySelector('#next-turn');

    board = Chessboard('myBoard', asd)
    boardDiv.classList.remove('hidden');                                        // and show the board

}

function loop(){
    if(autoPlay){
        if(simulatedMoveIndex >= simulatedMovesTotal) return;
        move(true);
        setTimeout(loop, 500);
    }
}

function handlePlayPauseButton(){
    console.log("Play/pause");
    autoPlay = !autoPlay;
    currentlyInSimulation=true;

    if(autoPlay){
        setTimeout(loop, 1000);
    }
}

function handleBackButton(){
    console.log("Backward");
    autoPlay = false;
    if(!currentlyInSimulation){
        resetSimulation();
    } else{
        move(false);
    }
}

function handleForwardButton(){
    console.log("Forward");
    autoPlay = false;
    if(!currentlyInSimulation){
        resetSimulation();
    } else{
        move(true);
    }
}

function move(forward){                 // simulator
    if(currentlyInSimulation){
        if(forward){
            if(simulatedMoveIndex >= simulatedMovesTotal){
                alert("All moves have been shown");
                return;
            } else{
                currentlyWhiteTurn =! currentlyWhiteTurn;
                game.move({from: moves[simulatedMoveIndex].src, to: moves[simulatedMoveIndex].dst});
                simulatedMoveIndex++;
            }

        } else{
            if(simulatedMoveIndex <=0){
                alert("This is turn 1");
                return;
            } else{
                currentlyWhiteTurn =! currentlyWhiteTurn;
                simulatedMoveIndex--;
                game.undo();
            }
        }

        board.position(game.fen());

    } else alert("Simulation unavailable, press the reset button to restart");
}

function onSnapEnd () {
    board.position(game.fen());    // update the board position after the piece snap for castling, en passant, pawn promotion
}


function onDrop(source, target) {
    var move = game.move({
        from: source,
        to: target,
        promotion: 'q'                              // NOTE: always promote to a queen for example simplicity
    });
    if (move === null)    return 'snapback'         // illegal move
    currentlyWhiteTurn = !currentlyInSimulation;
}


function onDragStart (source, piece, position, orientation) {
    currentlyInSimulation = false;
    autoPlay = false;
}

function resetSimulation(){
    currentlyInSimulation=true;
    autoPlay=false;
    game.reset();
    board.position(game.fen());
}

function setCSRFtoken(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
      });
}