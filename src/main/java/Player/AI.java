package Player;

import Game.Board;
import Util.Coordinate;

public class AI extends Player {

    private Board AIBoard;

    public AI(Board board, Board AIBoard) {
        super(board);
        this.AIBoard = AIBoard;
    }

    @Override
    public Coordinate makeMove() { return new Coordinate(0, 0); }

}
