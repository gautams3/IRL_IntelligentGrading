package Tutorial;

import burlap.domain.singleagent.gridworld.state.GridAgent;

import java.util.Arrays;
import java.util.List;

import static Tutorial.XYThetaDomain.VAR_THETA;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_X;
import static burlap.domain.singleagent.gridworld.GridWorldDomain.VAR_Y;

/**
 * Created by gautam on 12/03/2018.
 */
public class XYThetaAgent extends GridAgent {
    public int theta; //0=+x, 1=+y, 2=-x, 3=-y
    private final static List<Object> keys = Arrays.<Object>asList(VAR_X, VAR_Y, VAR_THETA);


    public XYThetaAgent(int x, int y, int theta) {
        super(x,y);
        this.theta = theta;
    }

    public XYThetaAgent(int x, int y, int theta, String name) {
        super(x,y,name);
        this.theta = theta;
    }

    @Override
    public XYThetaAgent copy() {
        return new XYThetaAgent(x, y, theta, name);
    }

    @Override
    public List<Object> variableKeys() {
        return keys;
    }

    public String toString() {
        String out = "(X,Y,Theta) = (" +x+ ", " +y+ ", " +theta*90+ " deg)";
        return out;
    }

    @Override
    public Object get(Object variableKey) {
        if(!(variableKey instanceof String)){
            throw new RuntimeException("GridAgent variable key must be a string");
        }

        String key = (String)variableKey;
        if(key.equals(VAR_X)){
            return x;
        }
        else if(key.equals(VAR_Y)){
            return y;
        }
        else if(key.equals(VAR_THETA)){
            return theta;
        }

        throw new RuntimeException("Unknown key " + key);
    }

    @Override
    public String className() {
        return XYThetaDomain.CLASS_XYTAGENT;
    }

}
