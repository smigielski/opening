if (typeof require != "undefined") {
    var chai = require('chai');
    var jsdom = require("jsdom");

    var Chess = require('../../../main/web/bower_components/chess.js/chess.js').Chess;
    var Opening = require('../../../main/web/js/opening.js').Opening;
    var PgnRenderer = require('../../../main/web/js/opening.js').PgnRenderer;
    var PgnLoader = require('../../../main/web/js/opening.js').PgnLoader;
    var HtmlRenderer = require('../../../main/web/js/opening.js').HtmlRenderer;
}

var assert = chai.assert;

describe("Moves", function() {
    var game,opening;

    beforeEach(function(){
        game = new Chess();
        opening = new Opening();
    });

    it('create move',function() {

        var move = game.move('e4');
        opening.createMove(move);
    });

    it('takeback',function() {

        var move = game.move('e4');

        opening.createMove(move);
        var isPossible = opening.takeback();
        assert.equal(isPossible,move);
        //only one move to take back
        var notPossible = opening.takeback();
        assert.isNull(notPossible);
        //Check move count??
        //assert(opening.)
    });

    it('navigation',function() {


        opening.createMove(game.move('e4'));
        opening.createMove(game.move('e5'));


        assert.equal(opening.prevMove().san,'e5');
        assert.equal(opening.prevMove().san,'e4');
        assert.isNull(opening.prevMove());

        assert.equal(opening.nextMove().san,'e4');
        assert.equal(opening.nextMove().san,'e5');
        assert.isNull(opening.nextMove());
    });

    it('variants',function() {


        opening.createMove(game.move('e4'));
        opening.createMove(game.move('e5'));
        opening.prevMove();
        game.undo();
        //create variant
        opening.createMove(game.move('c5'));

        assert.equal(opening.prevVariant().san,'e5');
        assert.isNull(opening.prevVariant());

        assert.equal(opening.nextVariant().san,'c5');
        assert.isNull(opening.nextVariant());
    });


});

describe("PGP Renderer", function() {
    var game, opening;

    beforeEach(function () {
        game = new Chess();
        opening = new Opening();
    });

    it('simple pgn', function () {
        var move = game.move('e4');
        opening.createMove(move);
        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4');
    });

    it('two move pgn', function () {
        opening.createMove(game.move('c4'));
        opening.createMove(game.move('e6'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('d5'));
        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. c4 e6 2. Nf3 d5');
    });

    it('nag in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.updateNag(3);
        opening.createMove(game.move('e5'));
        opening.updateNag(6);
        opening.createMove(game.move('d4'));
        opening.updateNag(4);
        opening.createMove(game.move('d5'));
        opening.updateNag(5);
        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4!! e5?! 2. d4?? d5!?');
    });

    it('test commetn in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.updateComment("E4 opening");
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('e6'));
        opening.updateComment("Sicilian Defence, French Variation");
        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 { E4 opening } c5 2. Nf3 e6 { Sicilian Defence, French Variation }');
    });


    it('test variations with black in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('e6'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d6'));

        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 c5 2. Nf3 e6 { Sicilian Defence, French Variation } ( 2. ... d6 )');
    });

    it('test two variations with black in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d5'));
        opening.prevMove();
        game.undo();
        game.move(opening.nextMove());
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('e6'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d6'));

        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 c5 ( 1. ... d5 ) 2. Nf3 e6 { Sicilian Defence, French Variation } ( 2. ... d6 )');
    });

    it('test variations with black in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.createMove(game.move('d6'));
        opening.prevMove();
        game.undo();
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d4'));
        opening.createMove(game.move('cxd4'));

        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ) 2. ... d6');
    });

    it('test variations with black in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d4'));
        opening.createMove(game.move('cxd4'));
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('Nc6'));

        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ( 2. ... Nc6 ) )');
    });

    it('test double variations with black in pgn', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d4'));
        opening.createMove(game.move('cxd4'));
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('Nc6'));
        opening.prevMove();
        game.undo();
        opening.prevMove();
        game.undo();
        opening.prevMove();
        game.undo();
        game.move(opening.nextMove());
        opening.createMove(game.move('c4'));
        opening.createMove(game.move('d5'));
        var pgn = opening.render(new PgnRenderer());
        assert.equal(pgn,'1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ( 2. ... Nc6 ) ) ( 2. c4 d5 )');
    });

});

describe("PGP Loader", function() {

    var tests = [
        { name: "pgn", pgn: "1. e4" },
        { name: "simple pgn", pgn: "1. e4 e5 2. d4" },
        { name: "nag in pgn", pgn: "1. e4!! e5?! 2. d4?? d5!?" },
        { name: "nag numneric in pgn", pgn: "1. e4!! e5?! 2. d4?? d5 $8" },
        { name: "comments pgn", pgn: "1. e4 { E4 opening } c5 2. Nf3 e6 { Sicilian Defence, French Variation }" },
        { name: "variation with black", pgn: "1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ) 2. ... d6" },
        { name: "variation with black", pgn: "1. e4 c5 2. Nf3 e6 { Sicilian Defence, French Variation } ( 2. ... d6 )" },
        { name: "two variation with black", pgn: "1. e4 c5 ( 1. ... d5 ) 2. Nf3 e6 { Sicilian Defence, French Variation } ( 2. ... d6 )" },
        { name: "nested variation", pgn: "1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ( 2. ... Nc6 ) )" },
        { name: "double variation", pgn: "1. e4 c5 2. Nf3 { Sicilian Defence, French Variation } ( 2. d4 cxd4 ( 2. ... Nc6 ) ) ( 2. c4 d5 )" },
        { name: "knigth capture", pgn: "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 a6" },
        { name: "queen", pgn: "1. e4 e5 ( 1. ... d5 2. e5 f5 3. exf6 Kd7 4. fxg7 d4 5. gxh8=Q )"}
    ];

    var game, opening;

    beforeEach(function () {
        game = new Chess();
        opening = new Opening();
    });


    tests.forEach(function(test, i) {
        var passed = true;

        it(test.name, function() {
            var success = new PgnLoader(game,opening).load(test.pgn);
            assert.ok(success);
            assert.equal(opening.render(new PgnRenderer()),test.pgn);
        });
    });

    //it ('matchBracket', function () {
    //    var loader = new PgnLoader();
    //    assert.equal(-1, loader.matchBracket('',0));
    //    assert.equal(2, loader.matchBracket('()',0));
    //    assert.equal(-1, loader.matchBracket('( ( )',0));
    //    assert.equal(18, loader.matchBracket('( ( ( ( ) ) as ) )',0));
    //
    //});



});

describe("HTML Renderer", function() {
    var game, opening, fragment;

    beforeEach(function () {
        global.document = jsdom.jsdom('<html><head></head><body></body>');
        //global.window = document.parentWindow;
        fragment = document.createDocumentFragment();

        game = new Chess();
        opening = new Opening();

    });


    it('simple html', function () {
        var move = game.move('e4');
        opening.createMove(move);

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);

        assert.equal(jsdom.serializeDocument(document), '<html><head></head><body><tr><td>1.</td><td><strong>e4</strong></td><td></td></tr></body></html>');
    });

    it('two move html', function () {
        opening.createMove(game.move('c4'));
        opening.createMove(game.move('e6'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('d5'));

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), '<html><head></head><body><tr><td>1.</td><td>c4</td><td>e6</td></tr><tr><td>2.</td><td>Nf3</td><td><strong>d5</strong></td></tr></body></html>');
    });

    it('nag in html', function () {
        opening.createMove(game.move('e4'));
        opening.updateNag(3);
        opening.createMove(game.move('e5'));
        opening.updateNag(6);
        opening.createMove(game.move('d4'));
        opening.updateNag(4);
        opening.createMove(game.move('d5'));
        opening.updateNag(5);

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), '<html><head></head><body><tr><td>1.</td><td>e4!!</td><td>e5?!</td></tr><tr><td>2.</td><td>d4??</td><td><strong>d5!?</strong></td></tr></body></html>');
    });

    it('test commetn in html', function () {
        opening.createMove(game.move('e4'));
        opening.updateComment("E4 opening");
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('e6'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), '<html><head></head><body><tr><td>1.</td><td>e4</td><td>...</td></tr><tr><td class=\"active small\" colspan=\"3\">E4 opening</td></tr><tr><td>1.</td><td>...</td><td>c5</td></tr><tr><td>2.</td><td>Nf3</td><td><strong>e6</strong></td></tr><tr><td class=\"active small\" colspan=\"3\"><strong>Sicilian Defence, French Variation</strong></td></tr></body></html>');

    });


    it('test variations with black in html', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.createMove(game.move('e6'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d6'));

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), "<html><head></head><body><tr><td>1.</td><td>e4</td><td>c5</td></tr><tr><td>2.</td><td>Nf3</td><td>...</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>1</strong></td></tr><tr><td>2.</td><td>...</td><td>e6</td></tr><tr><td class=\"active small\" colspan=\"3\">Sicilian Defence, French Variation</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>2</strong></td></tr><tr><td>2.</td><td>...</td><td><strong>d6</strong></td></tr></body></html>");
    });

    it('test variations with black in html', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d4'));
        opening.createMove(game.move('cxd4'));

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), "<html><head></head><body><tr><td>1.</td><td>e4</td><td>c5</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>1</strong></td></tr><tr><td>2.</td><td>Nf3</td><td></td></tr><tr><td class=\"active small\" colspan=\"3\">Sicilian Defence, French Variation</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>2</strong></td></tr><tr><td>2.</td><td>d4</td><td><strong>cxd4</strong></td></tr></body></html>");
    });

    it('test variations with black in html', function () {
        opening.createMove(game.move('e4'));
        opening.createMove(game.move('c5'));
        opening.createMove(game.move('Nf3'));
        opening.updateComment("Sicilian Defence, French Variation");
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('d4'));
        opening.createMove(game.move('cxd4'));
        opening.prevMove();
        game.undo();
        opening.createMove(game.move('Nc6'));

        opening.render(new HtmlRenderer(document,fragment));
        var bodyEl = document.body;
        bodyEl.appendChild(fragment);
        assert.equal(jsdom.serializeDocument(document), "<html><head></head><body><tr><td>1.</td><td>e4</td><td>c5</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>1</strong></td></tr><tr><td>2.</td><td>Nf3</td><td></td></tr><tr><td class=\"active small\" colspan=\"3\">Sicilian Defence, French Variation</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>2</strong></td></tr><tr><td>2.</td><td>d4</td><td>...</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>2,1</strong></td></tr><tr><td>2.</td><td>...</td><td>cxd4</td></tr><tr><td class=\"active\" colspan=\"3\"><strong>2,2</strong></td></tr><tr><td>2.</td><td>...</td><td><strong>Nc6</strong></td></tr></body></html>");
    });


});

describe("PGN", function() {
    var positions = [
        {
            moves: ['e4' ,'e5']
        }
        ];
    positions.forEach(function(position, i) {

        it(i, function() {
            var game = new Chess();
            var opening = new Opening();
            for (var j = 0; j < position.moves.length; j++) {
                var move = game.move(position.moves[j]);
                if (move === null) {
                    error_message = "move() did not accept " + position.moves[j] + " : ";
                    break;
                } else {
                    opening.createMove(move);
                }
            }
            //test here
        });
    });
});
