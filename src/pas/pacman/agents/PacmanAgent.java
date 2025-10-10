package src.pas.pacman.agents;


// SYSTEM IMPORTS
import java.util.*;


// JAVA PROJECT IMPORTS
import edu.bu.pas.pacman.agents.SearchAgent;
import edu.bu.pas.pacman.game.DefaultBoard;
import edu.bu.pas.pacman.interfaces.ThriftyPelletEater;
import edu.bu.pas.pacman.game.Action;
import edu.bu.pas.pacman.game.Game.GameView;
import edu.bu.pas.pacman.graph.Path;
import edu.bu.pas.pacman.graph.PelletGraph.PelletVertex;
import edu.bu.pas.pacman.utils.Coordinate;
import edu.bu.pas.pacman.utils.Pair;


public class PacmanAgent
    extends SearchAgent
    implements ThriftyPelletEater
{

    private final Random random;

    public PacmanAgent(int myUnitId,
                       int pacmanId,
                       int ghostChaseRadius)
    {
        super(myUnitId, pacmanId, ghostChaseRadius);
        this.random = new Random();
    }

    public final Random getRandom() { return this.random; }
    @Override
    public Set<PelletVertex> getOutoingNeighbors(final PelletVertex vertex,
                                                 final GameView game)
    {
        // Run BFS
        Coordinate start = vertex.getPacmanCoordinate();
        //System.out.println(start);
        Set<PelletVertex> neighbors = new HashSet<>();
        //Run BFS until we find all reachable pellets
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while(!queue.isEmpty()){
            Coordinate current = queue.poll();
            //System.out.println(getOutgoingNeighbors(current, game));
            for(Coordinate neighbor : getOutgoingNeighbors(current, game)){
                GameView gameView = game;
                if(!visited.contains(neighbor)){
                    if (vertex.getRemainingPelletCoordinates().contains(neighbor)) {
                        PelletVertex neighborVertex = vertex.removePellet(neighbor);
                        neighbors.add(neighborVertex);
                        continue;
                    }
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    @Override
    public float getEdgeWeight(final PelletVertex src,
                               final PelletVertex dst)
    {
        return 1f;
    }
    public Coordinate findRoot(Coordinate c, Map<Coordinate, Coordinate> parentMap){
        //System.out.println("Finding root of " + c + " with parent map " + parentMap);
        if(parentMap.get(c).equals(c)){
            return c;
        }else{
            Coordinate root = findRoot(parentMap.get(c), parentMap);
            parentMap.put(c, root);
            return root;
        }
    }
    @Override
    public float getHeuristic(final PelletVertex src,
                              final GameView game)
    {
        Set<Coordinate> pellets = new HashSet<>(src.getRemainingPelletCoordinates());
        //System.out.println(pellets);
        pellets.add(src.getPacmanCoordinate());
        //System.out.println(src.getRemainingPelletCoordinates());
        float mstCost = 0f;
        List<Pair<Pair<Coordinate, Coordinate>, Float>> edges = new LinkedList<>();
        for(Coordinate pellet1 : pellets) {
            PelletVertex pelletVertex = src.removePellet(pellet1);
            for (PelletVertex pellet2 : getOutoingNeighbors(pelletVertex, game)) {
                Coordinate pellet2Coord = pellet2.getPacmanCoordinate();
                if (!pellet1.equals(pellet2Coord)) {
                    //edges.add(new Pair<>(new Pair<>(pellet1, pellet2Coord), graphSearch(pellet1, pellet2Coord, game).getTrueCost()));
                    float manhattanDist = Math.abs(pellet1.getXCoordinate() - pellet2Coord.getXCoordinate()) +
                                  Math.abs(pellet1.getYCoordinate() - pellet2Coord.getYCoordinate());
                    edges.add(new Pair<>(new Pair<>(pellet1, pellet2Coord), manhattanDist));
                }
            }
        }
        edges.sort(Comparator.comparing(Pair::getSecond));
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();
        for(Coordinate pellet : pellets) {
            //System.out.println(pellet);
            parentMap.put(pellet, pellet);
        }
        for(Pair<Pair<Coordinate, Coordinate>, Float> edge : edges) {
            Coordinate c1 = edge.getFirst().getFirst();
            Coordinate c2 = edge.getFirst().getSecond();
            //System.out.println(c1 + " " + c2 + " " + game.getCell(c1).getCellState() + " " + game.getCell(c2).getCellState());
            Coordinate root1 = findRoot(c1, parentMap);
            Coordinate root2 = findRoot(c2, parentMap);
            if(!root1.equals(root2)) {
                mstCost += edge.getSecond();
                parentMap.put(root1, root2);
            }
        }
        return mstCost;
    }

    @Override
    public Path<PelletVertex> findPathToEatAllPelletsTheFastest(final GameView game)
    {
        //Implement A* to find the fastest path to eat all pellets
        PelletVertex src = new PelletVertex(game);
        System.out.println("Finding path to fastest of " + src.getPacmanCoordinate());
        PriorityQueue<Path<PelletVertex>> pq = new PriorityQueue<>(Comparator.comparing(path -> path.getTrueCost() + path.getEstimatedPathCostToGoal()));
        Set<String> visited = new HashSet<>();
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();
        Map<String, Float> costMap = new HashMap<>();
        pq.add(new Path<>(src, 0f, getHeuristic(src, game), null));
        costMap.put(src.getPacmanCoordinate()+" "+src.getRemainingPelletCoordinates(), 0f);
        //System.out.println(src.getPacmanCoordinate()+ "" + src.getRemainingPelletCoordinates().size());
        parentMap.put(src.getPacmanCoordinate(), null);
        while (!pq.isEmpty()) {
            Path<PelletVertex> currentPath = pq.poll();
            PelletVertex current = currentPath.getDestination();
            //System.out.println(current.getRemainingPelletCoordinates().size());
            if (current.getRemainingPelletCoordinates().isEmpty()) {
                System.out.println("A* found a path to eat all pellets with cost " + currentPath.getTrueCost());
                System.out.println(currentPath);
                return currentPath;
            }
            String keyCurrent = current.getPacmanCoordinate() + " " + current.getRemainingPelletCoordinates();
            visited.add(keyCurrent);
            for (PelletVertex neighbor : getOutoingNeighbors(current, game)) {
                //System.out.println(neighbor + " " + current.getPacmanCoordinate() + " " + currentPath.getTrueCost());
                //System.out.println(current.getPacmanCoordinate()+ "" + current.getRemainingPelletCoordinates().size());
                //System.out.println(costMap.get(keyCurrent));
                float tentativeCost = costMap.get(keyCurrent) + graphSearch(current.getPacmanCoordinate(), neighbor.getPacmanCoordinate(), game).getTrueCost();
                String keyNeighbor = neighbor.getPacmanCoordinate() + " " + neighbor.getRemainingPelletCoordinates();
                //System.out.println(visited + " " + neighbor.getPacmanCoordinate());
                if (!visited.contains(keyNeighbor)) {
                    costMap.put(keyNeighbor, tentativeCost);
                    parentMap.put(neighbor.getPacmanCoordinate(), current.getPacmanCoordinate());
                    pq.add(new Path<>(neighbor, graphSearch(current.getPacmanCoordinate(), neighbor.getPacmanCoordinate(), game).getTrueCost(), getHeuristic(neighbor, game), currentPath));
                    visited.add(keyNeighbor);

                }else if(tentativeCost < costMap.get(keyNeighbor)){
                    costMap.put(keyNeighbor, tentativeCost);
                    parentMap.put(neighbor.getPacmanCoordinate(), current.getPacmanCoordinate());
                    pq.add(new Path<>(neighbor, graphSearch(current.getPacmanCoordinate(), neighbor.getPacmanCoordinate(), game).getTrueCost(), getHeuristic(neighbor, game), currentPath));
                }else{
                    System.out.println("Skipping neighbor " + neighbor + " with cost " + tentativeCost + " and heuristic " + getHeuristic(neighbor, game) + " and cost map " + costMap.get(keyNeighbor));
                }
            }
        }

        System.out.println("No path found to eat all pellets ");
        return null;


    }

    @Override
    public Set<Coordinate> getOutgoingNeighbors(final Coordinate src,
                                                final GameView game)
    {
        Set<Coordinate> coords = new HashSet<Coordinate>();
        int currentX = src.getXCoordinate();
        int currentY = src.getYCoordinate();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) != Math.abs(dy)) {
                    int newX = (int) currentX + dx;
                    int newY = (int) currentY + dy;
                    Action currAction = null;
                    try {
                        currAction = Action.inferFromCoordinates(src, new Coordinate(newX, newY));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (game.isLegalPacmanMove(src, currAction)) {
                        coords.add(new Coordinate(newX, newY));
                    }
                }
            }
        }
        return coords;
    }

    @Override
    public Path<Coordinate> graphSearch(final Coordinate src,
                                        final Coordinate tgt,
                                        final GameView game)
    {
        PriorityQueue<Path<Coordinate>> pq = new PriorityQueue<>(Comparator.comparing(Path<Coordinate>::getTrueCost));
        Set<Coordinate> visited = new HashSet<>();
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();
        Map<Coordinate, Float> costMap = new HashMap<>();
        pq.add(new Path<Coordinate>(src));
        costMap.put(src, 0f);
        parentMap.put(src, null);
        while (!pq.isEmpty()) {
            Path<Coordinate> currentPath = pq.poll();
            Coordinate current = currentPath.getDestination();
            if (Math.abs(current.getXCoordinate() - tgt.getXCoordinate()) <= 1 && Math.abs(current.getYCoordinate() - tgt.getYCoordinate()) <= 1 && Math.abs(current.getXCoordinate() - tgt.getXCoordinate()) != Math.abs(current.getYCoordinate() - tgt.getYCoordinate())) {
                float finalMoveCost = 1f;
                currentPath = new Path<>(tgt, finalMoveCost, currentPath);
                return currentPath;
            }
            visited.add(current);
            for(Coordinate neighbor : getOutgoingNeighbors(current, game)){
                float tentativeCost = costMap.get(current) + 1f;
                if(!visited.contains(neighbor)){
                    if(!costMap.containsKey(neighbor) || tentativeCost < costMap.get(neighbor)){
                        costMap.put(neighbor, tentativeCost);
                        parentMap.put(neighbor, current);
                        pq.add(new Path<Coordinate>(neighbor, 1f, currentPath));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void makePlan(final GameView game)
    {
        Path<PelletVertex> path = findPathToEatAllPelletsTheFastest(game);
        Stack<Coordinate> stack = new Stack<>();
        Path<PelletVertex> currentPath = path;
        int i = 0;
        while(currentPath != null){
            PelletVertex currentVertex = currentPath.getDestination();
            Coordinate currentCoord = currentVertex.getPacmanCoordinate();
            Path<Coordinate> movePath = null;
            if(i == 0) {
                stack.push(currentCoord);
                i++;
            }
            if(currentPath.getParentPath() != null && currentPath.getParentPath().getDestination() != null){
                PelletVertex parentVertex = currentPath.getParentPath().getDestination();
                Coordinate parentCoord = parentVertex.getPacmanCoordinate();
                System.out.println("Finding move path from " + currentCoord + " to " + parentCoord);
                movePath = graphSearch(parentCoord, currentCoord, game);
                movePath = movePath.getParentPath();
            }
            while(movePath != null){
                System.out.println(movePath);
                Coordinate moveCoord = movePath.getDestination();
                stack.push(moveCoord);
                System.out.println(stack);
                movePath = movePath.getParentPath();
            }
            currentPath = currentPath.getParentPath();
        }
        System.out.println(stack);
        this.setPlanToGetToTarget(stack);

    }

    @Override
    public Action makeMove(final GameView game)
    {
        if(getPlanToGetToTarget() == null){
            makePlan(game);
            //System.out.println("Made plan: " + getPlanToGetToTarget().size() + " " + getPlanToGetToTarget());
        }
        if(getPlanToGetToTarget().isEmpty()){
            for(Coordinate pellet : new PelletVertex(game).getRemainingPelletCoordinates()){
                System.out.println("Remaining pellet at: " + pellet);
            }
        }
        Coordinate nextCoord = this.getPlanToGetToTarget().pop();
        Action action = null;
        try {
            action = Action.inferFromCoordinates(game.getEntity(game.getPacmanId()).getCurrentCoordinate(), nextCoord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return action;
    }

    @Override
    public void afterGameEnds(final GameView game)
    {

    }
}
