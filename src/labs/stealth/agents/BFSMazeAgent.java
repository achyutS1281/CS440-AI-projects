package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


import java.util.HashSet;       // will need for bfs
import java.util.Queue;         // will need for bfs
import java.util.LinkedList;    // will need for bfs
import java.util.Set;           // will need for bfs
import java.util.List;          // will need for path reconstruction
import java.util.ArrayList;    // will need for path reconstruction
import java.util.Collections;   // will need for path reconstruction
import java.util.Map;           // will need for parent map
// JAVA PROJECT IMPORTS


public class BFSMazeAgent
    extends MazeAgent
{

    public BFSMazeAgent(int playerNum)
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
        // Implement BFS to find the shortest path from src to goal
        System.out.println("Starting BFS from " + src + " to " + goal);
        Queue<Vertex> queue = new LinkedList<>();
        Set<Vertex> visited = new HashSet<>();
        Map<Vertex, Vertex> parentMap = new java.util.HashMap<>();
        queue.add(src);
        visited.add(src);
        parentMap.put(src, null);
        ArrayList<Vertex> pathList = new ArrayList<>();
        Path path = new Path(src);
        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            if (Math.abs(current.getXCoordinate() - goal.getXCoordinate()) <= 1 && Math.abs(current.getYCoordinate() - goal.getYCoordinate()) <= 1) {
                while (current != null) {
                    Vertex parent = parentMap.get(current);
                    pathList.add(0, current);
                    current = parent;
                }
                for (int i = 1; i < pathList.size(); i++) {
                    Vertex to = (Vertex) pathList.get(i);
                    path = new Path(to, 1, path);
                }
                path = new Path(goal, 1, path);
                return path;
            }
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    Vertex neighbor = new Vertex(current.getXCoordinate() + i, current.getYCoordinate() + j);
                    if (!visited.contains(neighbor) && isValidMove(current, neighbor, state)) {
                        visited.add(neighbor);
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }

                }
            }
        }
        System.out.println("No path found from " + src + " to " + goal);
        return null;
    }

}
