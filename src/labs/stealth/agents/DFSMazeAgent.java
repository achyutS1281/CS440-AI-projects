package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


import java.util.HashSet;   // will need for dfs
import java.util.Stack;     // will need for dfs
import java.util.Set;       // will need for dfs
import java.util.ArrayList; // will need for path reconstruction

// JAVA PROJECT IMPORTS


public class DFSMazeAgent
    extends MazeAgent
{

    public DFSMazeAgent(int playerNum)
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
        Stack<Vertex> stack = new Stack<>();
        Set<Vertex> visited = new HashSet<>();
        java.util.Map<Vertex, Vertex> parentMap = new java.util.HashMap<>();
        stack.push(src);
        visited.add(src);
        parentMap.put(src, null);
        Path path = new Path(src);
        ArrayList<Vertex> pathList = new ArrayList<>();
        while (!stack.isEmpty()) {
            Vertex current = stack.pop();
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
                    if (isValidMove(current, neighbor, state) && !visited.contains(neighbor)) {
                        stack.push(neighbor);
                        visited.add(neighbor);
                        parentMap.put(neighbor, current);
                    }
                }
            }
        }
        System.out.println("No path found from " + src + " to " + goal);
        return null;
    }

}
