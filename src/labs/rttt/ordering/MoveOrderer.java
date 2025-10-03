package src.labs.rttt.ordering;


// SYSTEM IMPORTS
import edu.bu.labs.rttt.game.CellType;
import edu.bu.labs.rttt.game.PlayerType;
import edu.bu.labs.rttt.game.RecursiveTicTacToeGame;
import edu.bu.labs.rttt.game.RecursiveTicTacToeGame.RecursiveTicTacToeGameView;
import edu.bu.labs.rttt.traversal.Node;
import edu.bu.labs.rttt.utils.Coordinate;
import edu.bu.labs.rttt.utils.Pair;

import java.util.List;
import java.util.Collections;


// JAVA PROJECT IMPORTS

class PriorityMoveSorter implements java.util.Comparator<Node> {
    private PlayerType agent;

    public PriorityMoveSorter(PlayerType agent) {
        this.agent = agent;
    }

    @Override
    public int compare(Node a, Node b) {
        RecursiveTicTacToeGameView viewA = a.getView();
        RecursiveTicTacToeGameView viewB = b.getView();
        Coordinate coordA = a.getLastMove();
        Coordinate coordB = b.getLastMove();
        boolean aWins = viewA.getGameView(viewA.getCurrentGameCoord()).getIsGameOver() && viewA.getGameView(viewA.getCurrentGameCoord()).didPlayerWin(agent);
        boolean bWins = viewB.getGameView(viewB.getCurrentGameCoord()).getIsGameOver() && viewB.getGameView(viewB.getCurrentGameCoord()).didPlayerWin(agent);
        boolean aBlocks = isBlockingMove(coordA, viewA, agent);
        boolean bBlocks = isBlockingMove(coordB, viewB, agent);
        if (aWins && !bWins) return -1;
        if (bWins && !aWins) return 1;
        if (aBlocks && !bBlocks) return -1;
        if (bBlocks && !aBlocks) return 1;
        if (isCenter(coordA) && !isCenter(coordB)) return -1;
        if (isCenter(coordB) && !isCenter(coordA)) return 1;
        if (isCorner(coordA) && !isCorner(coordB)) return -1;
        if (isCorner(coordB) && !isCorner(coordA)) return 1;
        if (isEdge(coordA) && !isEdge(coordB)) return -1;
        if (isEdge(coordB) && !isEdge(coordA)) return 1;

        return 0;
    }

    public static boolean isCenter(Coordinate coord) {
        return coord.getYCoordinate() == 1 && coord.getXCoordinate() == 1;
    }

    public static boolean isCorner(Coordinate coord) {
        return (coord.getYCoordinate() == 0 && coord.getXCoordinate() == 0) ||
                (coord.getYCoordinate() == 0 && coord.getXCoordinate() == 2) ||
                (coord.getYCoordinate() == 2 && coord.getXCoordinate() == 0) ||
                (coord.getYCoordinate() == 2 && coord.getXCoordinate() == 2);
    }

    public static boolean isEdge(Coordinate coord) {
        return (coord.getYCoordinate() == 0 && coord.getXCoordinate() == 1) ||
                (coord.getYCoordinate() == 1 && coord.getXCoordinate() == 0) ||
                (coord.getYCoordinate() == 1 && coord.getXCoordinate() == 2) ||
                (coord.getYCoordinate() == 2 && coord.getXCoordinate() == 1);
    }

    public static boolean isBlockingMove(Coordinate coord, RecursiveTicTacToeGameView view, PlayerType agent) {
        CellType opponent = agent == PlayerType.X ? CellType.O : CellType.X;
        int row = coord.getYCoordinate();
        int col = coord.getXCoordinate();
        if (isCenter(coord) &&
                ((view.getGameView(view.getCurrentGameCoord()).getCellType(row, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(row, 2) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(0, col) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, col) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(0, col - 1) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, col + 1) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(0, col + 1) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, col - 1) == opponent))) {
            return true;

        }
        if ((coord.getYCoordinate() == 0 && coord.getXCoordinate() == 1) &&
                ((view.getGameView(view.getCurrentGameCoord()).getCellType(0, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(0, 2) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(1, 1) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, 1) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(1, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, 2) == opponent))) {
            return true;
        }
        if ((coord.getYCoordinate() == 1 && coord.getXCoordinate() == 0) &&
                ((view.getGameView(view.getCurrentGameCoord()).getCellType(0, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, 0) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(1, 1) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(1, 2) == opponent))) {
            return true;
        }
        if ((coord.getYCoordinate() == 1 && coord.getXCoordinate() == 2) &&
                ((view.getGameView(view.getCurrentGameCoord()).getCellType(0, 2) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, 2) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(1, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(1, 1) == opponent))) {
            return true;
        }
        if ((coord.getYCoordinate() == 2 && coord.getXCoordinate() == 1) &&
                ((view.getGameView(view.getCurrentGameCoord()).getCellType(2, 0) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(2, 2) == opponent) ||
                        (view.getGameView(view.getCurrentGameCoord()).getCellType(0, 1) == opponent && view.getGameView(view.getCurrentGameCoord()).getCellType(1, 1) == opponent))) {
            return true;
        }
        return false;
    }
}
public class MoveOrderer
    extends Object {

    public static List<Node> orderChildren(List<Node> children, PlayerType agent) {
        // this default ordering does no ordering at all and just returns the children in whatever order they
        // were generated in
        Collections.sort(children, new PriorityMoveSorter(agent));
        return children;

    }

}
