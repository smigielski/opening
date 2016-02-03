var Main = function() {

    var game = new Chess();
    var opening = new Opening();

    // do not pick up pieces if the game is over
    // only pick up pieces for the side to move
    var onDragStart = function(source, piece, position, orientation) {
        if (game.game_over() === true ||
            (game.turn() === 'w' && piece.search(/^b/) !== -1) ||
            (game.turn() === 'b' && piece.search(/^w/) !== -1)) {
            return false;
        }
    };

    var onDrop = function(source, target) {
        // see if the move is legal
        var move = game.move({
            from: source,
            to: target,
            promotion: 'q' // NOTE: always promote to a queen for example simplicity
        });

        // illegal move
        if (move === null) {
            return 'snapback'
        } else {
            console.log(game);
            opening.createMove(move);
            updateMoveTable();
        }
    };

    // update the board position after the piece snap
    // for castling, en passant, pawn promotion
    var onSnapEnd = function() {
        board.position(game.fen());
    };

    var takeback = function() {
        if (opening.takeback()!=null) {
            var move = game.undo();
            board.position(game.fen());
            updateMoveTable();
        }
    };

    var prevMove = function() {
        if (opening.prevMove()!=null) {
            var move = game.undo();
            board.position(game.fen());
            updateMoveTable();
        } else {
            console.warn("no more moves");
        }
    };


    var nextMove = function(){
        var nextMove = opening.nextMove();
        if (nextMove!=null) {
            var move = game.move(nextMove);
            board.position(game.fen());
            updateMoveTable();
        } else {
            console.warn("no more moves");
        }
    };

    var prevVariant = function (){
        var prevVariant = opening.prevVariant();
        if (prevVariant!=null){
            game.undo();
            board.position(game.fen());
            var move = game.move(prevVariant);
            board.position(game.fen());
            updateMoveTable();
        }
    };

    var nextVariant = function (){
        var nextVariant = opening.nextVariant();
        if (nextVariant!=null){
            game.undo();
            board.position(game.fen());
            var move = game.move(nextVariant);
            board.position(game.fen());
            updateMoveTable();
        }
    };

    var updateNag = function(){
        opening.updateNag(parseInt($("#move-nag").val()));
        //console.log(currentMove.nag);
        updateMoveTable();
    };

    var updateComment = function(){
        opening.updateComment($("#move-comment").val());
        updateMoveTable();
    };

    //Renderer



    var updateMoveTable = function(){

        var fragment = document.createDocumentFragment();

        opening.render(new HtmlRenderer(document,fragment));


        $("#moves").empty().append(fragment);

        $("#pgn").val(opening.render(new PgnRenderer()));

        var currentMove = opening.currentMove();

        $("#current-move").html(currentMove.move_number + ". "+ (currentMove.move.color=='b'?" ... ":"")+ currentMove.move.san);
        //TODO implement
        $("#move-nag").val(currentMove.nag);
        $("#move-comment").val(currentMove.comment);


    };





    $(document).keydown(function(e) {
        switch(e.which) {
            case 8: // backspace
                takeback();
                break;

            case 37: // left
                prevMove();
                break;

            case 38: // up
                prevVariant();
                break;

            case 39: // right
                nextMove();
                break;

            case 40: // down
                nextVariant();
                break;

            default: return; // exit this handler for other keys
        }
        e.preventDefault(); // prevent the default action (scroll / move caret)
    });




    var cfg = {
        pieceTheme: '../bower_components/chessboardjs/img/chesspieces/wikipedia/{piece}.png',
        draggable: true,
        position: 'start',
        onDragStart: onDragStart,
        onDrop: onDrop,
        onSnapEnd: onSnapEnd
    };

    var board = ChessBoard('board', cfg);
    $(window).resize(board.resize);
    $("#move-nag").change(updateNag);
    $("#move-comment").change(updateComment);



};
$(document).ready(Main);

