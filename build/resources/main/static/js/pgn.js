function PGN() {

    var renderMoveNumber = function (movenumber) {
        var td = document.createElement("td");
        td.innerHTML = movenumber + ".";
        return td;
    };

    var getAnnotation = function(nag){
        switch (nag) {
            case 1: return "!";
            case 2: return "?";
            case 3: return "‼";
            case 4: return "⁇";
            case 5: return "⁉";
            case 6: return "⁈";
            //forced move
            case 7: return "□";
            //singular move (no alts)
            case 8: return " $8";
            //worst move
            case 9: return " $9";
            default: return "";
        }

    }

    var renderMoveText = function (move) {
        var td = document.createElement("td");
        if (move == currentMove) {
            td.innerHTML = "<strong>" + move.move.san + getAnnotation(move.nag) + "</strong>";
        } else {
            td.innerHTML = move.move.san + getAnnotation(move.nag);
        }
        return td;
    };

    var renderNull = function () {
        var td = document.createElement("td");
        td.innerHTML = "...";
        return td;
    };

    var renderComment = function (move) {
        var commentRow = document.createElement("tr");
        var td = document.createElement("td");
        td.setAttribute("class", "active small");
        td.setAttribute("colspan", "3");
        if (move == currentMove) {
            td.innerHTML = "<strong>" + move.comment + "</strong>";
        } else {
            td.innerHTML = move.comment;
        }
        commentRow.appendChild(td);
        return commentRow;
    };

    var renderVariantHeader = function (number) {
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        td.setAttribute("class", "active");
        td.setAttribute("colspan", "3");
        td.innerHTML = "<strong>" + number + "</strong>";
        tr.appendChild(td);
        return tr;
    };

    var renderMove = function (fragment, depth, move) {
        var tr = document.createElement("tr");
        var commentRow;

        if (move.move.color == 'w') {
            tr.appendChild(renderMoveNumber(move.move_number));
            tr.appendChild(renderMoveText(move));
            if (move.comment) {
                if (move.next != null) {
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

            if (move.next != null && move.next.variations.length == 0) {
                move = move.next;

                tr.appendChild(renderMoveText(move));

                if (move.comment) {
                    commentRow = renderComment(move);
                }
            } else if (move.next != null) {
                tr.appendChild(renderNull());
            } else {
                var td = document.createElement("td");
                tr.appendChild(td);
            }
            fragment.appendChild(tr);

            if (commentRow) {
                fragment.appendChild(commentRow);
            }
        } else {
            tr.appendChild(renderMoveNumber(move.move_number));
            tr.appendChild(renderNull());
            tr.appendChild(renderMoveText(move));

            if (move.comment) {
                commentRow = renderComment(move);
            }
            fragment.appendChild(tr);

            if (commentRow) {
                fragment.appendChild(commentRow);
            }
        }
        ;
        return move;
    };

    var renderMoves = function (fragment, depth, move, variantNumbers) {
        if (move != null) {
            console.log(move, depth);
            //white move

            //variants in white move
            if (move.variations.length > 0 && depth < move.depth()) {
                var number = 1;
                variantNumbers.push(number);
                var variations = move.variations;
                fragment.appendChild(renderVariantHeader(variantNumbers));
                move = renderMoves(fragment, depth + 1, move, variantNumbers.slice(0));
                for (index = 0; index < variations.length; index++) {
                    variantNumbers[depth]++;
                    fragment.appendChild(renderVariantHeader(variantNumbers));
                    renderMoves(fragment, depth + 1, variations[index], variantNumbers.slice(0));
                }
            } else {
                move = renderMove(fragment, depth + 1, move)
            }


            if (move != null) {
                //TODO should be?
                renderMoves(fragment, depth, move.next, variantNumbers.slice(0));
            }

        }
    };

    var updateMoveTable = function () {

        var fragment = document.createDocumentFragment();

        var variantNumbers = [];
        renderMoves(fragment, 0, gameLog.next, variantNumbers);


        $("#moves").empty().append(fragment);

    };
}