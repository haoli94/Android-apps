package edu.stanford.cs108.tetris;


public class TetrisBrainLogic extends TetrisLogic{
    private DefaultBrain AIBrain;
    private boolean BrianMode = false;
    private Brain.Move nextBestMove;

    public TetrisBrainLogic(TetrisUIInterface uiInterface) {
        super(uiInterface);
        AIBrain = new DefaultBrain();
    }

    @Override
    public void tick(int verb) {
        // if BrainMode is not activated, use the default tick(verb)
        if (!BrianMode) {
            super.tick(verb);
            return;
        }
        // else use brain and nextBestMove
        super.board.undo();
        // Undo before use the nextBestMove
        nextBestMove = AIBrain.bestMove(super.board, super.currentPiece, super.board.getHeight(), nextBestMove);
        double threshold = 0.08;
        if(nextBestMove != null&&!nextBestMove.piece.equals(super.currentPiece)) {
            super.tick(ROTATE);
        }
        //If the nextBestMove.piece is different we rotate it
        if (nextBestMove == null) {
            double moveInt = Math.random();
            if (moveInt <= 0.25) {
                super.tick(DOWN);
            } else if (0.25 < moveInt && moveInt <= 0.5) {
                super.tick(RIGHT);
            } else if (0.5 < moveInt && moveInt <= 0.75) {
                super.tick(LEFT);
            }else{
                super.tick(ROTATE);
            }
        }
        //If there is no best move, choose a random action
        if (nextBestMove.x < currentX) {
            super.tick(LEFT);
        }
        //This means the piece goes to the left side
        else if (nextBestMove.x > currentX) {
            super.tick(RIGHT);
        }
        //This means the piece goes to the right side
        else if (nextBestMove.y < currentY){
            double rd = Math.random();
            if (rd <= threshold) {
                super.tick(DROP);
            }
        }
        //This means the next best move is down so we simply drop it with a certain probability (random noise)
        super.tick(DOWN);
    }


    public void setBrianMode(boolean Brian) {
        BrianMode = Brian;
    }
}
