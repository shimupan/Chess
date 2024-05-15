package Player;

import Game.Board;
import Game.Game;

public abstract class Player {

    public Board board;
    public Game game;

    public Player(Board board) {
        this.board = board;
        this.game = Game.getInstance();
    }
    
    public abstract void makeMove();

    public void update() { }
    
}
