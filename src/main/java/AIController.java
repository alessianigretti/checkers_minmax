import java.util.ArrayList;
import java.util.List;

/**
 * The AIController class controls the generation of new AI moves.
 * AIController uses a minimax algorithm with alpha-beta pruning.
 * The depth of the minimax algorithm is dictated by the player's choice,
 * which is transmitted via the GUI to the getAIMove() function.
 */
public class AIController
{
    private Board board;
    // the possible successors for a given state
    public List<MovesAndScores> successorEvaluations;
    // count of static evaluations, dynamic evaluations and pruning
    public int seCount, deCount, pCount;
    // maximum depth of the minimax algorithm
    private int maxDepth;

    /**
     * Instantiates a new AIController object.
     *
     * @param board the board linked to the AI controller.
     */
    public AIController(Board board)
    {
        this.board = board;
    }

    /**
     * Gets a move from the AI.
     * The move is obtained via a minimax algorithm which populates the successorEvaluations list.
     *
     * @param difficulty the difficulty chosen by the player.
     * @return the AI move generated by the minimax algorithm.
     */
    public Move getAIMove(int difficulty)
    {
        // if no difficulty level was selected, the moves are selected randomly
        if (difficulty == 0)
        {
            return getRandomMove();
        }
        // if a difficulty level is selected, minimax is run
        else
        {
            successorEvaluations = new ArrayList<>();
            maxDepth = difficulty;
            deCount = 0;
            seCount = 0;
            pCount = 0;
            // minimax evaluation
            minimax(0, CheckersGame.Player.AI, Integer.MIN_VALUE, Integer.MAX_VALUE);

            int best = -1;
            int MAX = -100;
            // successors are evaluated and first best successor is picked
            for (int i = 0; i < successorEvaluations.size(); i++)
            {
                if (MAX < successorEvaluations.get(i).getScore())
                {
                    MAX = successorEvaluations.get(i).getScore();
                    best = i;
                }
            }

            return successorEvaluations.get(best).getMove();
        }
    }

    /**
     * Produces a random move.
     *
     * @return a random move.
     */
    private Move getRandomMove()
    {
        // list all possible AI moves
        List<Move> movesAvailable = board.getAvailableStates(CheckersGame.Player.AI);
        // pick a random move
        int random = (int) Math.random() * movesAvailable.size();
        // return a random move
        return movesAvailable.get(random);
    }

    /**
     * Implements the minimax algorithm with alpha-beta pruning.
     *
     * @param depth  the current depth of the tree.
     * @param player the current player.
     * @param alpha  the alpha value.
     * @param beta   the beta value.
     * @return the value of the static evaluation.
     */
    public int minimax(int depth, CheckersGame.Player player, int alpha, int beta)
    {
        int bestScore = 0;

        // if the tree has reached its maximum depth, the evaluation is calculated
        // depending on the current number of checkers on the baord
        if (depth > maxDepth)
        {
            seCount++;
            return board.getHeuristics();
            //return board.getCheckers(Checker.Colour.BLACK).size() - board.getCheckers(Checker.Colour.RED).size();
        }

        // all possible moves are generated for current player
        List<Move> movesAvailable = board.getAvailableStates(player);
        if (movesAvailable.isEmpty())
        {
            seCount++;
            return 0;
        }

        // iterate through all moves available
        for (int i = 0; i < movesAvailable.size(); i++)
        {
            // clone a list of all checkers to be restored after the move has been attempted
            List<Checker> allCheckers = board.cloneList(board.getCheckers());

            // get the move available
            Move move = movesAvailable.get(i);
            deCount++;

            // player maximising (the AI)
            if (player == CheckersGame.Player.AI)
            {
                bestScore = Integer.MIN_VALUE;

                // move is attempted as a test
                board.makeMove(move, true);

                // current score of the static evaluation is stored, depth is increased
                int currentScore = minimax(depth + 1, CheckersGame.Player.HUMAN, alpha, beta);

                // best score is stored
                bestScore = Math.max(bestScore, currentScore);

                // alpha is stored
                alpha = Math.max(currentScore, bestScore);

                // if root node has been reached, the evaluation is stored in the list of successor evaluations
                if (depth == 0)
                    successorEvaluations.add(new MovesAndScores(move, currentScore));
            }
            // player minimising (the human player)
            else if (player == CheckersGame.Player.HUMAN)
            {
                bestScore = Integer.MAX_VALUE;

                // move is attempted as a test
                board.makeMove(move, true);

                // current score of the static evaluation is stored, depth is increased
                int currentScore = minimax(depth + 1, CheckersGame.Player.AI, alpha, beta);

                // best score is stored
                bestScore = Math.min(bestScore, currentScore);

                // beta is stored
                beta = Math.min(currentScore, bestScore);
            }

            // board is cleared of all attempts and refilled with checkers
            board.clearBoard();
            board.fillWithExistingCheckers(allCheckers);
            if (move.getCapturedChecker() != null) move.getCapturedChecker().getTile().emptyTile();

            // pruning is executed to optimise algorithm
            if (alpha >= beta)
            {
                pCount++;
                break;
            }
        }

        return bestScore;
    }
}
