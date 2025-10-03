package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.util.Direction;                           // Directions in Sepia


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue; // heap in java
import java.util.Set;
import java.util.ArrayList;

// JAVA PROJECT IMPORTS


public class DijkstraMazeAgent
    extends MazeAgent
{

    public DijkstraMazeAgent(int playerNum)
    {
        super(playerNum);
    }
    public boolean isValidMove(Vertex from, Vertex to, StateView state){
        if(state.inBounds(to.getXCoordinate(), to.getYCoordinate()) && state.isResourceAt(to.getXCoordinate(), to.getYCoordinate()) == false && state.isUnitAt(to.getXCoordinate(), to.getYCoordinate()) == false){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        Map<Direction, Float> directionCostMap = new HashMap<>();
        directionCostMap.put(Direction.NORTH, 10f);
        directionCostMap.put(Direction.SOUTH, 1f);
        directionCostMap.put(Direction.EAST, 5f);
        directionCostMap.put(Direction.WEST, 5f);
        directionCostMap.put(Direction.NORTHEAST, (float) Math.sqrt(Math.pow(directionCostMap.get(Direction.NORTH), 2) + Math.pow(directionCostMap.get(Direction.EAST), 2)));
        directionCostMap.put(Direction.NORTHWEST, (float) Math.sqrt(Math.pow(directionCostMap.get(Direction.NORTH), 2) + Math.pow(directionCostMap.get(Direction.WEST), 2)));
        directionCostMap.put(Direction.SOUTHEAST, (float) Math.sqrt(Math.pow(directionCostMap.get(Direction.SOUTH), 2) + Math.pow(directionCostMap.get(Direction.EAST), 2)));
        directionCostMap.put(Direction.SOUTHWEST, (float) Math.sqrt(Math.pow(directionCostMap.get(Direction.SOUTH), 2) + Math.pow(directionCostMap.get(Direction.WEST), 2)));
        PriorityQueue<Path> pq = new PriorityQueue<>(Comparator.comparing(Path::getTrueCost));
        Set<Vertex> visited = new HashSet<>();
        Map<Vertex, Vertex> parentMap = new HashMap<>();
        Map<Vertex, Float> costMap = new HashMap<>();
        pq.add(new Path(src));
        costMap.put(src, 0f);
        parentMap.put(src, null);
        ArrayList<Vertex> pathList = new ArrayList<>();
        while (!pq.isEmpty()) {
            Path currentPath = pq.poll();
            Vertex current = currentPath.getDestination();
            if (Math.abs(current.getXCoordinate() - goal.getXCoordinate()) <= 1 && Math.abs(current.getYCoordinate() - goal.getYCoordinate()) <= 1) {
                float finalMoveCost = 0f;
                // determine cost of final move
                if(current.getXCoordinate() == goal.getXCoordinate() && current.getYCoordinate() == goal.getYCoordinate() + 1){
                    finalMoveCost = 10f;
                }else if(current.getXCoordinate() == goal.getXCoordinate() && current.getYCoordinate() == goal.getYCoordinate() - 1){
                    finalMoveCost = 1f;
                }else if(current.getXCoordinate() == goal.getXCoordinate() + 1 && current.getYCoordinate() == goal.getYCoordinate()){
                    finalMoveCost = 5f;
                }else if(current.getXCoordinate() == goal.getXCoordinate() - 1 && current.getYCoordinate() == goal.getYCoordinate()){
                    finalMoveCost = 5f;
                } else if(current.getXCoordinate() == goal.getXCoordinate() + 1 && current.getYCoordinate() == goal.getYCoordinate() + 1){
                    finalMoveCost = (float) Math.sqrt(Math.pow(10f, 2) + Math.pow(5f, 2));
                } else if(current.getXCoordinate() == goal.getXCoordinate() - 1 && current.getYCoordinate() == goal.getYCoordinate() + 1){
                    finalMoveCost = (float) Math.sqrt(Math.pow(10f, 2) + Math.pow(5f, 2));
                } else if(current.getXCoordinate() == goal.getXCoordinate() + 1 && current.getYCoordinate() == goal.getYCoordinate() - 1){
                    finalMoveCost = (float) Math.sqrt(Math.pow(1f, 2) + Math.pow(5f, 2));
                } else if(current.getXCoordinate() == goal.getXCoordinate() - 1 && current.getYCoordinate() == goal.getYCoordinate() - 1){
                    finalMoveCost = (float) Math.sqrt(Math.pow(1f, 2) + Math.pow(5f, 2));
                }
                currentPath = new Path(goal, finalMoveCost, currentPath);
                System.out.println("Dijkstra found a path from " + src + " to " + goal + " with cost " + currentPath.getTrueCost());
                return currentPath;
            }
            visited.add(current);
            for(Direction dir : Direction.values()){
                Vertex neighbor = new Vertex(current.getXCoordinate() + dir.xComponent(), current.getYCoordinate() + dir.yComponent());
                if(isValidMove(current, neighbor, state) && !visited.contains(neighbor)){
                    float tentativeCost = costMap.get(current) + directionCostMap.get(dir);
                    if(!costMap.containsKey(neighbor) || tentativeCost < costMap.get(neighbor)){
                        costMap.put(neighbor, tentativeCost);
                        parentMap.put(neighbor, current);
                        pq.add(new Path(neighbor, directionCostMap.get(dir), currentPath));
                    }
                }
            }
        }
        return null;
    }

}
