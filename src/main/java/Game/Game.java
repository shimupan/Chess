package Game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import Controls.*;
import Main.Main;
import Piece.*;
import Player.*;
import Util.*;
import Util.Enums.GameState;
import Util.Enums.MoveType;
import Util.Enums.PlayerType;;

public class Game extends JPanel implements Runnable {

    private static Game instance = null;

    private String initialFen;
    private PlayerType initialPlayerTypeWhite;
    private PlayerType initialPlayerTypeBlack;

    private Thread gameThread;
    private Board board;
    private Board AIBoard;
    
    private int halfmoveClock = 0;
    private int moves = 1;
    private boolean checkGameState = false;
    
    private Player white;
    private Player black;
    private GameState winner = GameState.Playing;
    private boolean processing = false;
    
    public Mouse mouse;
    
    public Piece promotionPC = null;
    public Piece activePC = null;
    public Piece checkingPC = null;
    public ArrayList<Piece> promoPCLst = new ArrayList<>(4);
    public ArrayList<Move> gameMoves = new ArrayList<>();

    public int currColor = CONSTANTS.WHITE;

    public Square hoveredSquare = null;
    public Square previousMoveLocation = null;
    public Square currentMoveLocation = null;
    public Square activeSQ = null;

    public MoveGen mg;
    
    public boolean clearEnPassantNextTurn = false;
    
    public boolean canMove = false;
    public boolean validSquare = false;

    /*
     * CONSTRUCTOR
     */

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /*
     * INIT AND RESET
     */

    private Game() {
        setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        setBackground(Color.BLACK);
    }

    public void init(String fen, PlayerType PlayerTypeWhite, PlayerType PlayerTypeBlack) {

        this.initialFen = fen;
        this.initialPlayerTypeWhite = PlayerTypeWhite;
        this.initialPlayerTypeBlack = PlayerTypeBlack;

        this.board = new Board();
        this.AIBoard = new Board(this.board);
        this.mouse = new Mouse();
        addMouseListener(this.mouse);
        addMouseMotionListener(this.mouse);

        // default fen
        if(fen == "") fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        String[] gameState = board.loadFEN(fen);
        this.currColor = gameState[1].equals("w") ? CONSTANTS.WHITE : CONSTANTS.BLACK;
        
        this.halfmoveClock = Integer.parseInt(gameState[4]);
        this.moves = Integer.parseInt(gameState[5]);

        // Set up Players
        if(PlayerTypeWhite == PlayerType.Human) {
            this.white = new Human(this.board, CONSTANTS.WHITE);
        } else if (PlayerTypeWhite == PlayerType.AI) {
            this.white = new AI(this.board, this.AIBoard, CONSTANTS.WHITE);
        }

        if(PlayerTypeBlack == PlayerType.Human) {
            this.black = new Human(this.board, CONSTANTS.BLACK);
        } else if (PlayerTypeBlack == PlayerType.AI) {
            this.black = new AI(this.board, this.AIBoard, CONSTANTS.BLACK);
        }

        this.mg = new MoveGen(this.board, this.currColor, this.checkingPC);
        repaint();
    }

    public void initGUI() {
        Main.resetButton = ButtonFactory.createButton(
            "Reset Game",
            new Rectangle(850, 700, 200, 50),
            new Font("Arial", Font.BOLD, 20),
            new Color(119,154,88),
            new Color(234,235,200)
        );
        Main.resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processing = true;
                resetGame();
                processing = false;
            }
        });

        Main.undoButton = ButtonFactory.createButton(
            "Undo Move",
            new Rectangle(850, 600, 200, 50),
            new Font("Arial", Font.BOLD, 20),
            new Color(119,154,88),
            new Color(234,235,200)
        );
        Main.undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processing = true;
                undoMove();
                processing = false;
            }
        });
    }
    
    protected void undoMove() {
        if(this.gameMoves.size() == 0) return;

        Move lastMove = this.gameMoves.get(this.gameMoves.size() - 1);
        this.gameMoves.removeLast();
        this.halfmoveClock--;
        this.moves--;
        this.getCurrPlayer().undoMove(lastMove);
        this.swapTurn();
        this.repaint();
        
    }

    private void resetGame() {
        init(initialFen, initialPlayerTypeWhite, initialPlayerTypeBlack);
        mg.board = this.board;
        this.winner = GameState.Playing;
        promotionPC = null;
        activePC = null;
        checkingPC = null;
        promoPCLst = new ArrayList<>(4);
        hoveredSquare = null;
        previousMoveLocation = null;
        currentMoveLocation = null;
        activeSQ = null;
        clearEnPassantNextTurn = false;
        canMove = false;
        validSquare = false;
    }

    public void start() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    /*
     * MAIN LOOP
     */
    @Override
    public void run() {
        double drawInterval = 1000000000 / CONSTANTS.FPS;
        double delta = 0;
        long prevTime = System.nanoTime();
        long currTime;
        Sound.play("game-start");
        while(gameThread != null) {
            currTime = System.nanoTime();
            delta += (currTime - prevTime)/drawInterval;
            prevTime = currTime;
            
            if(delta >= 1 && !processing) {
                
                if(!checkGameState && !processing) {
                    if(gameMoves.size() > 0) System.out.println(gameMoves.get(gameMoves.size()-1).type);
                    checkGameState = true;
                    mg.currColor = this.currColor;
                    mg.checkingPC = this.checkingPC;
                    GameState gs = mg.checkGameState();
                    if(gs == GameState.Checkmate) {
                        this.winner = (currColor == CONSTANTS.WHITE) ? GameState.BlackWin : GameState.WhiteWin;
                    } else if(gs == GameState.Stalemate) {
                        this.winner = GameState.Stalemate;
                    }
                    repaint();
                    System.out.println(gs);
                }

                if(this.winner == GameState.Playing && !processing) {
                    Player p = getCurrPlayer();
                    p.makeMove();
                }

                delta--;
            }
            sleep(15);
        }
    }

    /* 
     * RENDERING
    */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent( g );

        Graphics2D g2 = (Graphics2D) g;

        board.draw( g2 );

        // Highlight the current move set
        if(this.previousMoveLocation != null && this.currentMoveLocation != null) {
            g2.setColor(Color.YELLOW.brighter());
            g2.fillRect(this.previousMoveLocation.getCol() * CONSTANTS.SQSIZE, 
                        this.previousMoveLocation.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            g2.fillRect(this.currentMoveLocation.getCol() * CONSTANTS.SQSIZE, 
                        this.currentMoveLocation.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            if(this.currentMoveLocation.containsPiece()) {
                this.currentMoveLocation.getPiece().draw(g2);
            }
        }

        // Color hovered square
        if(this.hoveredSquare != null) {
            g2.setColor(Color.YELLOW.brighter());
            g2.fillRect(this.hoveredSquare.getCol() * CONSTANTS.SQSIZE, 
                        this.hoveredSquare.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            this.hoveredSquare.getPiece().draw(g2);

            if(this.activePC != null) {
                this.activePC.getValidMoves(this.board, true);
                for(Move c: this.activePC.validMoves) {
                    // Set color to semi-transparent gray
                    g2.setColor(new Color(128, 128, 128, 128)); // RGBA values

                    // Calculate the center of the square
                    int centerX = c.destCoords.col * CONSTANTS.SQSIZE + CONSTANTS.SQSIZE / 2;
                    int centerY = c.destCoords.row * CONSTANTS.SQSIZE + CONSTANTS.SQSIZE / 2;

                    // Draw a circle in the center of the square
                    g2.fillOval(centerX - CONSTANTS.SQSIZE / 8, 
                                centerY - CONSTANTS.SQSIZE / 8, 
                                CONSTANTS.SQSIZE / 4, 
                                CONSTANTS.SQSIZE / 4);

                    if(board.getPiece(c.destCoords.row, c.destCoords.col) != null) {
                        g2.setColor(Color.RED.brighter());
                        g2.fillRect(c.destCoords.col * CONSTANTS.SQSIZE, 
                                    c.destCoords.row * CONSTANTS.SQSIZE, 
                                    CONSTANTS.SQSIZE, 
                                    CONSTANTS.SQSIZE);
                        board.getPiece(c.destCoords.row, c.destCoords.col).draw(g2);
                    }
                }
            }
        }

        // Draw the piece moving
        if(this.activePC != null) {
            if(this.canMove) {
                // Draw a border around the square
                g2.setColor(Color.WHITE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                int borderThickness = 5;
                g2.setStroke(new BasicStroke(borderThickness)); // Set the thickness of the border
                g2.drawRect(this.activePC.col * CONSTANTS.SQSIZE + borderThickness / 2, 
                            this.activePC.row * CONSTANTS.SQSIZE + borderThickness / 2, 
                            CONSTANTS.SQSIZE - borderThickness, 
                            CONSTANTS.SQSIZE - borderThickness);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
            }
            this.activePC.draw(g2);
        }

        // handle promotion
        if(this.promotionPC != null && currPlayerIsHuman() ) {
            for (int i = 0; i <= 3; i++) {
                int offset = (this.promotionPC.color == CONSTANTS.WHITE) ? i : -i;
                Piece tempPiece;
                switch (i) {
                    case 0:
                        tempPiece = new Queen(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 1:
                        tempPiece = new Rook(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 2:
                        tempPiece = new Bishop(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 3:
                        tempPiece = new Knight(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    default:
                        continue;
                }
                g2.setColor(Color.WHITE);
                g2.fillRect(tempPiece.getX(tempPiece.col), tempPiece.getY(tempPiece.row), CONSTANTS.SQSIZE, CONSTANTS.SQSIZE);
                g2.drawImage(tempPiece.img, tempPiece.getX(tempPiece.col), tempPiece.getY(tempPiece.row), CONSTANTS.SQSIZE, CONSTANTS.SQSIZE, null);
                promoPCLst.add(i, tempPiece);
            }
        }

        // Handle Winner
        if(this.winner != GameState.Playing) {
            g2.setFont(new Font("TimesRoman", Font.BOLD, 50)); // Set the font
            g2.setColor(Color.BLACK); // Set the color

            String message = "";
            if(this.winner == GameState.WhiteWin) {
                message = "White wins by Check Mate!";
            } else if(this.winner == GameState.BlackWin) {
                message = "Black wins Check Mate!";
            } else {
                message = "Stalemate!";
            }

            // Get the FontMetrics
            FontMetrics metrics = g2.getFontMetrics(g2.getFont());
            // Determine the X coordinate for the text
            int x = 400 - metrics.stringWidth(message) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = 400 - metrics.getHeight() / 2 + metrics.getAscent();

            // Draw the message
            g2.drawString(message, x, y);
        }
    }

    /*
     * GAME LOGIC
     */

    public void swapTurn() {
        this.currColor = (this.currColor == CONSTANTS.WHITE) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
        checkGameState = false;
    }

    public void handlePieceSelection(int pieceRow, int pieceCol) {
        this.activeSQ = board.getSquare(pieceRow, pieceCol);
        // Square has a piece on it and the piece is the curr turn
        if(this.activeSQ.containsPiece() && 
            this.activeSQ.getPiece().color == this.currColor) {
            this.activePC = activeSQ.getPiece();
            this.activePC.prevCol = this.activePC.col;
            this.activePC.prevRow = this.activePC.row;
            this.hoveredSquare = this.activeSQ;
        }
    }

    public void handlePiecePlacement(Move move) {
        if(this.validSquare) {
            // Update moved piece
            this.activePC.updatePos(this.board, move, true);
            
            // square highlighting
            int currRow = this.activePC.getRow(this.activePC.y);
            int currCol = this.activePC.getCol(this.activePC.x);
            this.currentMoveLocation = board.rep[currRow][currCol];
            
            int prevRow = this.activePC.prevRow;
            int prevCol = this.activePC.prevCol;
            this.previousMoveLocation = board.rep[prevRow][prevCol];
            this.hoveredSquare = null;

            // castle
            if(Piece.castlePC != null) {
                this.handleCastling();
                Piece.castlePC.updatePos(this.board, move, false);
                Piece.castlePC = null;
            }

            // Check the pseudo legal moves
            if(Piece.putKingInCheck(this.activePC, this.board)) {
                Sound.play("move-check");
                this.checkingPC = this.activePC;
            }

            // en-passant
            if (this.clearEnPassantNextTurn) {
                Piece.prevEnpassantPiece = Piece.enpassantPiece;
                Piece.enpassantPiece = null;
            }
            this.clearEnPassantNextTurn = !(Piece.enpassantPiece == null);

            // Check if the move moved the pawn 
            if(Type.isPawn(this.activePC)) {
                int promotionRank = this.activePC.color == CONSTANTS.WHITE ? 0 : 7;
                if(this.activePC.row == promotionRank) {
                    this.promotionPC = this.activePC;
                }
            }

            // Promotion
            if (this.promotionPC == null) {
                this.gameMoves.add(move);
                this.halfmoveClock++;
                this.moves++;
                this.swapTurn(); 
            }

        } else {
            Sound.play("illegal");
            this.activePC.resetPos();
        }
        this.activePC = null;
        this.activeSQ = null;
        this.repaint();
    }

    public void handlePiecePromotion(int clickedRow, int clickedCol, Move move) {

        Set<Piece> pcs = (this.promotionPC.color == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;

        if(this.promoPCLst.isEmpty()) this.promoPCLst.add(new Queen(this.promotionPC.color, clickedRow, clickedCol));
        
        for (Piece pc : this.promoPCLst) {
            if (pc.row == clickedRow && pc.col == clickedCol) {
                // Promotion piece was clicked, handle accordingly
                this.board.handlePromotion(this.promotionPC.row, this.promotionPC.col, pc);
                pcs.add(pc);
                move.type = MoveType.Promotion;
                move.setPCBeforePromotion(pc);
                break;
            }
        }
        pcs.remove(this.promotionPC);
        this.promotionPC = null;
        this.promoPCLst.clear();
        this.gameMoves.add(move);
        this.swapTurn();
        this.repaint();
        return;
    }
      
    /*
     * UTILITY
     */

    public void handleCastling() {
        if(Piece.castlePC != null) {
            // Check which side rook is being castled
            Piece.castlePC.prevCol = Piece.castlePC.col;
            Piece.castlePC.prevRow = Piece.castlePC.row;
            if(Piece.castlePC.col == 0) { // left rook
                Piece.castlePC.col += 3;
            } else {
                Piece.castlePC.col -= 2;
            }
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean currPlayerIsHuman() {
        return ( (this.currColor == CONSTANTS.WHITE && this.white instanceof Human) || 
                 (this.currColor == CONSTANTS.BLACK && this.black instanceof Human) );
    }

    private Player getCurrPlayer() {
        if(this.currColor == CONSTANTS.WHITE) {
            return this.white;
        } else {
            return this.black;
        }
    }

    public int perft(int depth) {
        if(depth == 0) return 1;
        int nodes = 0;
        mg.currColor = this.currColor;
        mg.generateMoves();
        Set<Move> move = new HashSet<>(mg.moves);
        for(Move m: move) {
            this.validSquare = true;
            this.handlePieceSelection(m.p.row, m.p.col);
            this.activePC.row = m.destCoords.row;
            this.activePC.col = m.destCoords.col;
            if( Type.isPawn(this.activePC) && 
                ((this.currColor == CONSTANTS.WHITE && m.destCoords.row == 0) || 
                (this.currColor == CONSTANTS.BLACK && m.destCoords.row == 7)) ) {
                this.promotionPC = this.activePC;
            }
            this.handlePiecePlacement(m);
            nodes += perft(depth - 1);
            this.undoMove();
        }
        return nodes;
    }

}
