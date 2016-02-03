if (!Array.prototype.last){
    Array.prototype.last = function(){
        return this[this.length - 1];
    };
}

var HtmlRenderer = function(doc,fragment){

    var currentMove;

    var renderMoveNumber= function (movenumber) {
            var td = doc.createElement("td");
            td.innerHTML = movenumber + ".";
            return td;
        };


    var renderMoveText = function(move){
            var td = doc.createElement("td");

            if (move==currentMove){
                td.innerHTML = "<strong>"+ move.move.san+move.getAnnotation()+  "</strong>";
            } else {
                td.innerHTML = move.move.san+move.getAnnotation();
            }
            return td;
        };
    var renderNull= function(){
            var td = doc.createElement("td");
            td.innerHTML = "...";
            return td;
        };
    var renderComment = function (move) {
            var commentRow = doc.createElement("tr");
            var td = doc.createElement("td");
            td.setAttribute("class","active small");
            td.setAttribute("colspan","3");
            if (move==currentMove) {
                td.innerHTML = "<strong>"+ move.comment+  "</strong>";
            } else {
                td.innerHTML = move.comment;
            }
            commentRow.appendChild(td);
            return commentRow;
        };
    var renderVariantHeader = function (number) {
            var tr = doc.createElement("tr");
            var td = doc.createElement("td");
            td.setAttribute("class","active");
            td.setAttribute("colspan","3");
            td.innerHTML = "<strong>"+ number +  "</strong>";
            tr.appendChild(td);
            return tr;
        };


    var renderMove = function(depth,move){
        var tr = document.createElement("tr");
        var commentRow;

        if (move.move.color=='w') {
            tr.appendChild(renderMoveNumber(move.move_number));
            tr.appendChild(renderMoveText(move));
            if (move.comment){
                if (move.next!=null) {
                    tr.appendChild(renderNull());
                    fragment.appendChild(tr);
                    fragment.appendChild(renderComment(move));
                    tr = document.createElement("tr");
                    tr.appendChild(renderMoveNumber(move.move_number));
                    tr.appendChild(renderNull());
                } else {
                    commentRow = renderComment(move);
                }
            }

            if (move.next!=null && move.next.variations.length == 0){
                move=move.next;

                tr.appendChild(renderMoveText(move));

                if (move.comment){
                    commentRow = renderComment(move);
                }
            } else if (move.next!=null) {
                tr.appendChild(renderNull());
            } else {
                var td = document.createElement("td");
                tr.appendChild(td);
            }
            fragment.appendChild(tr);

            if (commentRow){
                fragment.appendChild(commentRow);
            }
        } else {
            tr.appendChild(renderMoveNumber(move.move_number));
            tr.appendChild(renderNull());
            tr.appendChild(renderMoveText(move));

            if (move.comment){
                commentRow = renderComment(move);
            }
            fragment.appendChild(tr);

            if (commentRow){
                fragment.appendChild(commentRow);
            }
        }
        return move;
    };

    this.renderMoves = function(depth,move,variantNumbers) {
        if (move != null) {
            //console.log(move, depth);
            //white move

            //variants in white move
            if (move.variations.length > 0 && depth < move.depth()) {
                var number = 1;
                variantNumbers.push(number);
                var variations = move.variations;
                fragment.appendChild(renderVariantHeader(variantNumbers));

                //why move???
                move = this.renderMoves(depth + 1, move, variantNumbers.slice(0));
                for (index = 0; index < variations.length; index++) {
                    variantNumbers[depth]++;
                    fragment.appendChild(renderVariantHeader(variantNumbers));
                    this.renderMoves(depth + 1, variations[index], variantNumbers.slice(0));
                }
            } else {
                move = renderMove(depth + 1, move)
            }


            if (move != null) {
                //TODO should be?
                this.renderMoves(depth, move.next, variantNumbers.slice(0));
            }
        }
    };

    this.setCurrentMove = function(move){
        currentMove=move;
    };


};

var PgnRenderer = function(){


    var renderMoveNumber = function (movenumber) {
            return movenumber + ". ";
        };
        var renderMoveText = function(move){
            return move.move.san+(move.getAnnotation())+" ";
        };
        var renderNull= function(){
            return "... ";
        };
        var renderComment = function (move) {
            return"{ " + move.comment + " } ";
        };
        var renderVariantHeader = function (number) {
            return"( ";
        };
        var renderVariantFooter = function (number) {
            return " ) ";
        };

        var renderMove = function(str, depth,move){


            if (move.move.color=='w') {
                str.push(renderMoveNumber(move.move_number));
                str.push(renderMoveText(move));
                if (move.comment){
                    str.push(renderComment(move));
                }

                if (move.next!=null && move.next.variations.length == 0){
                    move=move.next;

                    str.push(renderMoveText(move));

                    if (move.comment){
                        str.push(renderComment(move));
                    }
                } else if (move.next!=null) {

                }


            } else {
                str.push(renderMoveNumber(move.move_number));
                str.push(renderNull());
                str.push(renderMoveText(move));

                if (move.comment){
                    str.push(renderComment(move));
                }
            }
            return move;
        };

    var renderSingleMove = function(str, depth,move){
        if (move.move.color=='w') {
            str.push(renderMoveNumber(move.move_number));
        }
        str.push(renderMoveText(move));
        if (move.comment){
            str.push(renderComment(move));
        }
        return move.next;
    };

    this.setCurrentMove = function(move){

    };

    this.renderMoves = function(depth,move,variantNumbers){
        var str = [];

        if (move!=null) {

            //white move

            //variants in white move
            if (move.variations.length > 0 && depth<move.depth()){
                var number = 1;
                variantNumbers.push(number);
                var variations= move.variations;
                //str.push(this.renderMoves(depth+1,move,variantNumbers.slice(0)));
                move = renderSingleMove(str,depth+1,move)
                for	(index = 0; index < variations.length; index++) {
                    variantNumbers[depth]++;
                    str.push(renderVariantHeader(variantNumbers));
                    str.push(this.renderMoves(depth+1,variations[index],variantNumbers.slice(0)));
                    str.push(renderVariantFooter());
                }
                str.push(this.renderMoves(depth+1,move,variantNumbers.slice(0)));
            } else {
                move = renderMove(str,depth+1,move)
            }


            if (move!=null){
                //TODO should be?
                str.push(this.renderMoves(depth,move.next,variantNumbers.slice(0)));
            }

        }

        return str.join("").trim();

    };
};

var Opening = function() {


    var BLACK = 'b';
    var WHITE = 'w';


    function Start(){
        this.next = null;
        this.move_number = 0;
        this.add = function(move){
            this.next=move;
            move.prev=this;
            move.move_number=1;
            return move;
        };
        this.clean = function (move) {
            this.next = null;
        };

        this.isLast = function(){
            return false;
        };
    }

    function Move(move,moveNumber) {
        this.move = move;
        this.next = null;
        this.prev = null;
        this.variations = [];
        this.move_number = moveNumber;
        this.nag;
        this.comment;

        this.getAnnotation = function(){
            switch (this.nag) {
                case 1: return "!";
                case 2: return "?";
                case 3: return "!!";
                case 4: return "??";
                case 5: return "!?";
                case 6: return "?!";
                //forced move
                case 7: return "□";
                //singular move (no alts)
                case 8: return " $8";
                //worst move
                case 9: return " $9";
                default: return "";
            }

        };

        this.isLast = function(){
            return this.next===null;
        };

        this.remove = function(){

            while (this.next!=null){
                this.next.remove();
            }

            if (this.prev.next==this){
                if (this.variations.length>0){
                    var variant = this.variations[0];
                    for (var i = 1;i<this.variations.length;++i){
                        variant.newVariation(this.variations[i]);
                    }
                    this.prev.next=variant;
                } else {
                    this.prev.next=null;
                }
            } else {
                this.prev.next.removeVariation(this);
            }



            return this.prev;
        };

        this.add = function(move){
            this.next=move;
            move.prev=this;
            if (move.move.color==WHITE){
                move.move_number=this.move_number+1;
            } else {
                move.move_number=this.move_number;
            }
            return move;
        };

        this.newVariation = function (move) {
            this.variations.push(move);
            move.move_number=this.move_number;
            move.prev=this.prev;
            return move;
        };

        this.removeVariation = function (move) {

            var index = this.variations.indexOf(move);
            if (index > -1) {
                this.variations.splice(index, 1);
            }
        };

        this.depth = function () {
            var depthNumber = 0;
            move = this;
            while(move.prev!=null){
                if (move.prev.next.variations.length>0){
                    depthNumber++;
                }
                move=move.prev;
            }
            return depthNumber;
        };

        this.contains = function (san) {
            if (this.move.san == san){
                return this;
            }
            for (var i = 0;i<this.variations.length;++i){
                if (this.variations[i].move.san==san){
                    return this.variations[i];
                }
            }
            return null;
        };

    }

    Start.prototype.toString = function() {
        return this.next!=null? " "+ this.next.toString() : "";
    };
    Move.prototype.toString = function() {
        return this.move.san + (this.next!=null? " "+ this.next.toString() : "");
    };

    var gameLog = new Start();

    var currentMove = gameLog;





    return {
        currentMove: function (){
          return currentMove;
        },
        createMove: function(move) {
            if (currentMove.next!=null && currentMove.next.contains(move.san)!=null){
                //just move currentMove
                currentMove = currentMove.next.contains(move.san);
            } else if (currentMove.next==null){
                currentMove = currentMove.add(new Move(move));
            } else {
                //start variation
                currentMove = currentMove.next;
                currentMove = currentMove.newVariation(new Move(move));
            }
        },
        takeback: function(){
            if (currentMove.isLast()) {
                var move = currentMove.move;
                console.log("removing: " + move.san);
                currentMove = currentMove.remove();
                return move;
            }
            return null;
        },
        prevMove: function () {
            if (currentMove.prev!=null) {
                var move = currentMove.move;
                currentMove = currentMove.prev;
                return move;
            } else {
                return null;
            }
        },
        nextMove: function () {
            if (currentMove.next!=null) {
                currentMove=currentMove.next;
                return currentMove.move;
            }
            return null;
        },
        prevVariant: function (){
            if (currentMove==currentMove.prev.next){
                //no more variants
            } else {
                var mainMove = currentMove.prev.next;
                var index = mainMove.variations.indexOf(currentMove);
                if (index<=0){
                    currentMove=mainMove;
                } else {
                    currentMove=mainMove.variations[--index];
                }

                return currentMove.move;
            }
            return null;
        },
        nextVariant: function(){
            var mainMove = currentMove.prev.next;
            if (mainMove.variations.length>0){
                var index = mainMove.variations.indexOf(currentMove);
                if (index+1<mainMove.variations.length){
                    currentMove=mainMove.variations[index+1];
                    return currentMove.move;
                }
            }
            return null;
        },
        updateNag: function(nag){
            currentMove.nag=nag;
        },
        updateComment: function(comment){
            currentMove.comment=comment;
        },
        render: function (renderer){
            var variantNumbers = [];
            renderer.setCurrentMove(currentMove);
            return renderer.renderMoves(0,gameLog.next,variantNumbers);
        }

    }
};

/* export Chess object if using node or any other CommonJS compatible
 * environment */
if (typeof exports !== 'undefined') exports.Opening = Opening;
if (typeof exports !== 'undefined') exports.HtmlRenderer = HtmlRenderer;
if (typeof exports !== 'undefined') exports.PgnRenderer = PgnRenderer;

/* export Chess object for any RequireJS compatible environment */
if (typeof define !== 'undefined') define( function () { return Opening;  });
if (typeof define !== 'undefined') define( function () { return HtmlRenderer; });
if (typeof define !== 'undefined') define( function () { return PgnRenderer; });
