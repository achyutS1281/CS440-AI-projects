package src.labs.scripted.agents;


// SYSTEM IMPORTS
import edu.cwru.sepia.action.Action;                                        // how we tell sepia what each unit will do
import edu.cwru.sepia.agent.Agent;                                          // base class for an Agent in sepia
import edu.cwru.sepia.environment.model.history.History.HistoryView;        // history of the game so far
import edu.cwru.sepia.environment.model.state.ResourceNode;                 // tree or gold
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;    // the "state" of that resource
import edu.cwru.sepia.environment.model.state.ResourceType;                 // what kind of resource units are carrying
import edu.cwru.sepia.environment.model.state.State.StateView;              // current state of the game
import edu.cwru.sepia.environment.model.state.Unit.UnitView;                // current state of a unit
import edu.cwru.sepia.util.Direction;                                       // directions for moving in the map


import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

// JAVA PROJECT IMPORTS



public class ZigZagAgent
    extends Agent
{

    // put your fields here! You will probably want to remember the following information:
    //      - your friendly unit id
    //      - the enemy unit id
    //      - the id of the gold
	private Integer myUnitId;
	private Integer enemyId;
	private Integer goldId;
	private String lastMove = "EAST";
    /**
     * The constructor for this type. The arguments (including the player number: id of the team we are controlling)
     * are contained within the game's xml file that we are running. We can also add extra arguments to the game's xml
     * config for this agent and those will be included in args.
     */
	public ZigZagAgent(int playerNum, String[] args)
	{
		super(playerNum); // make sure to call parent type (Agent)'s constructor!

        // initialize your fields here!
		this.myUnitId = null;
		this.enemyId = null;
		this.goldId = null;
        // helpful printout just to help debug
		System.out.println("Constructed ZigZagAgent");
	}

    /////////////////////////////// GETTERS AND SETTERS (this is Java after all) ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public Integer getEnemyId() {
		return enemyId;
	}
	public void setEnemyId(Integer enemyId) {
		this.enemyId = enemyId;
	}
	public Integer getGoldId() {
		return goldId;
	}
	public void setGoldId(Integer goldId) {
		this.goldId = goldId;
	}
	public Integer getMyUnitId() {
		return myUnitId;
	}
	public void setMyUnitId(Integer myUnitId) {
		this.myUnitId = myUnitId;
	}
	public int getNextMove() {
		if (this.lastMove.equals("EAST")) {
			this.lastMove = "NORTH";
			return 0;
		} else {
			this.lastMove = "EAST";
			return 1;
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Map<Integer, Action> initialStep(StateView state,
                                            HistoryView history)
	{
		List<Integer> myUnitIds = state.getUnitIds(this.getPlayerNumber());
        if(myUnitIds.size() != 1)
        {
            System.err.println("Should Only be 1 unit on my team");
			System.exit(-1);
        }
		this.setMyUnitId(myUnitIds.get(0));
		Integer[] playerNumbers = state.getPlayerNumbers();
		if(playerNumbers.length != 2)
		{
			System.err.println("Only 2 players allowed");
			System.exit(-1);
		}
		Integer enemyPlayerNumber = playerNumbers[0] == this.getPlayerNumber() ? playerNumbers[1] : playerNumbers[0];
		List<Integer> enemyUnitIds = state.getUnitIds(enemyPlayerNumber);
		if(enemyUnitIds.size() != 1)
		{
			System.err.println("Enemy should only have 1 unit");
			System.exit(-1);
		}

		for(Integer unitID : enemyUnitIds)
        {
		    if(!state.getUnit(unitID).getTemplateView().getName().toLowerCase().equals("footman"))
		    {
			    System.err.println("Enemy can only have footmen units");
			    System.exit(-1);
		    }
        }
		this.setEnemyId(enemyUnitIds.get(0));
		List<Integer> resources = state.getResourceNodeIds(ResourceNode.Type.GOLD_MINE);
		setGoldId(resources.get(0));
		return middleStep(state, history);
	}

	@Override
	public Map<Integer, Action> middleStep(StateView state,
                                           HistoryView history)
    {

        Map<Integer, Action> actions = new HashMap<Integer, Action>();
		UnitView myUnit = state.getUnit(this.getMyUnitId());
		UnitView enemyUnit = state.getUnit(this.getEnemyId());
		if(Math.abs(myUnit.getXPosition() - enemyUnit.getXPosition()) <= 1 && Math.abs(myUnit.getYPosition() - enemyUnit.getYPosition()) <= 1) {
			actions.put(myUnitId, Action.createPrimitiveAttack(myUnitId, enemyId));
			return actions;
		}else {
			if (state.getTurnNumber() == 0) {
				actions.put(myUnitId, Action.createPrimitiveMove(myUnitId, Direction.EAST));
			} else {
				int nextMove = this.getNextMove();
				if (nextMove == 0) {
					actions.put(myUnitId, Action.createPrimitiveMove(myUnitId, Direction.NORTH));
				} else {
					actions.put(myUnitId, Action.createPrimitiveMove(myUnitId, Direction.EAST));
				}
			}
		}
        // TODO: your code to give your unit actions for this turn goes here!

        return actions;
	}

    @Override
	public void terminalStep(StateView state,
                             HistoryView history)
    {
        // don't need to do anything
    }

    /**
     * The following two methods aren't really used by us much in this class. These methods are used to load/save
     * the Agent (for instance if our Agent "learned" during the game we might want to save the model, etc.). Until the
     * very end of this class we will ignore these two methods.
     */
    @Override
	public void loadPlayerData(InputStream is) {}

	@Override
	public void savePlayerData(OutputStream os) {}

}

