package Player;

import Game.Board;
import Game.Game;
import Util.Coordinate;

public abstract class Player {

    public Board board;
    public Game game;

    public Player(Board board) {
        this.board = board;
        this.game = Game.getInstance();
    }
    
    public abstract Coordinate makeMove();

    public void update() { }
    
}
