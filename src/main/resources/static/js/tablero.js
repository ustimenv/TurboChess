//var game = new Chess()
//
//function onDragStart (source, piece, position, orientation) {
//    if(game.game_over()){                               // do not pick up pieces if the game is over
//        var msg = document.getElementById('endGame');
//        if(msg != null){
//            msg.style.display = "flex";
//            if(game.turn() === 'b' ){                                           // blacks turn
//                msg.style.display = "flex";
//                document.getElementById('win').style.display = "flex";
//            } else  document.getElementById('loose').style.display = "flex";    // whites turn
//        } else  return false;
//  }
//  if (piece.search(/^b/) !== -1)    return false;             // only pick up pieces for White TODO why(????)
//}
//
//function makeRandomMove(){
//    var possibleMoves = game.moves()
//    if(possibleMoves.length === 0)  return;  // game over
//    var randomIdx = Math.floor(Math.random() * possibleMoves.length)
//    game.move(possibleMoves[randomIdx])
//    board.position(game.fen())
//}
//
//function onDrop (source, target) {
//    // see if the move is legal
//    var move = game.move({
//        from: source,
//        to: target,
//        promotion: 'q'                            // NOTE: always promote to a queen for example simplicity
//    });
//
//  if (move === null)    return 'snapback'         // illegal move
//  window.setTimeout(makeRandomMove, 250)          // make random legal move for black
//}
//
//function onSnapEnd () {
//    board.position(game.fen());    // update the board position after the piece snap for castling, en passant, pawn promotion
//}
//
//var asd = {
//    draggable:      true,
//    position:       'start',
//    onDragStart:    onDragStart,
//    onDrop:         onDrop,
//    onSnapEnd:      onSnapEnd
//}
//board = Chessboard('myBoard', asd)