package Tutorial;

import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Grid world where locations are GridWorldLocations (x,y), but the agent also has a theta. Agent is
 * XYThetaAgent (x,y,theta) instead of GridAgent (x,y)
 *
 * Created by gautam on 12/03/2018.
 */
public class XYThetaState extends GridWorldState {
    public XYThetaAgent agent;

    public XYThetaState(int x, int y, int theta, GridLocation...locations){
        this(new XYThetaAgent(x, y, theta), locations);
    }

    public XYThetaState(XYThetaAgent agent, GridLocation...locations){
        this.agent = agent;
        if(locations.length == 0){
            this.locations = new ArrayList<GridLocation>();
        }
        else {
            this.locations = Arrays.asList(locations);
        }
    }

    public XYThetaState(XYThetaAgent agent, List<GridLocation> locations){
        this.agent = agent;
        this.locations = locations;
    }

    @Override
    public String toString() {
        return ("Agent "+this.agent.toString() + "; Locations " + this.locations.toString());
    }

    @Override
    public State copy() {
        return new XYThetaState(agent, locations);
    }

    @Override
    public List<ObjectInstance> objects() {
        List<ObjectInstance> obs = new ArrayList<ObjectInstance>(1+locations.size());
        obs.add(agent);
        obs.addAll(locations);
        return obs;
    }

    @Override
    public Object get(Object variableKey) {
        OOVariableKey key = OOStateUtilities.generateKey(variableKey);
        if(key.obName.equals(agent.name())){
            return agent.get(key.obVarKey);
        }
        int ind = this.locationInd(key.obName);
        if(ind == -1){
            throw new RuntimeException("Cannot find object " + key.obName);
        }
        return locations.get(ind).get(key.obVarKey);
    }

    @Override
    public int numObjects() {
        return 1 + this.locations.size();
    }

    @Override
    public ObjectInstance object(String oname) {
        if(oname.equals(agent.name())){
            return agent;
        }
        int ind = this.locationInd(oname);
        if(ind != -1){
            return locations.get(ind);
        }
        return null;
    }




    public XYThetaAgent touchAgent(){
        this.agent = agent.copy();
        return agent;
    }

    @Override
    public List<ObjectInstance> objectsOfClass(String oclass) {
        if(oclass.equals("XYTagent") || oclass.equals("agent")){
            return Arrays.<ObjectInstance>asList(agent);
        }
        else if(oclass.equals("location")){
            return new ArrayList<ObjectInstance>(locations);
        }
        throw new RuntimeException("Unknown class type " + oclass);
    }

}
