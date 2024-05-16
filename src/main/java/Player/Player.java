package Player;

import Game.Board;
import Game.Game;

public abstract class Player {

    public Board board;
    public Game game;
    public int color;

    public Player(Board board, int color) {
        this.board = board;
        this.color = color;
        this.game = Game.getInstance();
    }
    
    public abstract void makeMove();

    public void update() { }
    
}
