/**
 * Created by gautam on 10/03/2018.
 */

package Tutorial;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.Arrays;
import java.util.List;

public class XYThetaDomain extends GridWorldDomain {
    public static final String VAR_THETA = "theta"; //TODO: use this in move function, and in movementDirectionFromIndex() function
    public static final String ACTION_F = "Forward";
    public static final String ACTION_R = "Rear";
    public static final String ACTION_FR = "fwdAndRight";
    public static final String ACTION_FL = "fwdAndLeft";
    public static final String ACTION_RR = "revAndRight";
    public static final String ACTION_RL = "revAndLeft";
    public static final String CLASS_XYTAGENT = "XYTagent";

    public XYThetaDomain(int width, int height){
        super(width, height);
    }

    public void setMapToParkingLot()
    {
        this.width = 5;
        this.height = 5;
        this.numLocationTypes = 4;
        this.makeEmptyMap();
    }

    public void setDeterministicTransitionDynamics(){
        int na = 6; //6 actions (F, B, FR, FL, RR, RL)
        transitionDynamics = new double[na][na];
        for(int i = 0; i < na; i++){
            Arrays.fill(transitionDynamics[i], 0.);
            transitionDynamics[i][i] = 1.;
        }
    }

    //TODO: What is a propositional function?
    // TODO: Override generatePfs to stop using WallToPF function (it calls GridWorld functions)

    @Override
    public OOSADomain generateDomain() {

        OOSADomain domain = new OOSADomain();

        int [][] cmap = this.getMap();

        domain.addStateClass(CLASS_AGENT, XYThetaAgent.class).addStateClass(CLASS_LOCATION, GridLocation.class);

        XYThetaModel smodel = new XYThetaModel(cmap, getTransitionDynamics());
        RewardFunction rf = this.rf;
        TerminalFunction tf = this.tf;

        if(rf == null){
            rf = new UniformCostRF();
        }
        if(tf == null){
            tf = new NullTermination();
        }


        FactoredModel model = new FactoredModel(smodel, rf, tf);
        domain.setModel(model);

        domain.addActionTypes(
                new UniversalActionType(ACTION_F),
                new UniversalActionType(ACTION_R),
                new UniversalActionType(ACTION_FL),
                new UniversalActionType(ACTION_FR),
                new UniversalActionType(ACTION_RL),
                new UniversalActionType(ACTION_RR));


        OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

        return domain;
    }

    public static ValueFunctionVisualizerGUI getGridWorldValueFunctionVisualization(List<State> states, int maxX, int maxY, ValueFunction valueFunction, Policy p){
        return ValueFunctionVisualizerGUI.createGridWorldBasedValueFunctionVisualizerGUI(states, valueFunction, p,
                new OOVariableKey(CLASS_AGENT, VAR_X), new OOVariableKey(CLASS_AGENT, VAR_Y), new VariableDomain(0, maxX), new VariableDomain(0, maxY), 1, 1,
                ACTION_F, ACTION_R, ACTION_FL, ACTION_FR); //TODO: createXYThetaBasedValueFunctionVisualizerGUI, for 6 actions instead of 4
    }

    protected static int [] movementDirectionFromIndex(int i){

        int [] result = null;

        switch (i) {
            case 0:
                result = new int[]{0,1,0}; //F
                break;

            case 1:
                result = new int[]{0,-1,0}; //R
                break;

            case 2:
                result = new int[]{-1,1,1}; //FL
                break;

            case 3:
                result = new int[]{1,1,-1}; //FR
                break;

            case 4:
                result = new int[]{-1,-1,-1}; //RL
                break;

            case 5:
                result = new int[]{1,-1,1}; //RR
                break;

            default:
                break;
        }

        return result;
    }

    /**
     * Rotate vector
     * @param dv the vector (difference vector that you want to rotate)
     * @param thetaInit Initial theta of agent. 0=E, 1=N, 2=W, 3=S
     * @return final vector
     */
    //TODO: Instead of gridlocation, find a vector2D class for input and output
    private static GridLocation rotateVector(GridLocation dv, int thetaInit)
    {
        int dxIn = dv.x;
        int dyIn = dv.y;
        double dTheta = (thetaInit - 1) * Math.PI/2;

        double dxOut = dxIn*Math.cos(dTheta) - dyIn*Math.sin(dTheta);
        double dyOut = dxIn*Math.sin(dTheta) + dyIn*Math.cos(dTheta);

        if ((Math.abs(Math.round(dxOut) - dxOut) > 0.1) || (Math.abs(Math.round(dyOut) - dyOut) > 0.1))
        {
            System.out.print("What?! dxOut " +dxOut+ "or dyOut " +dyOut+ " are not integers!");
        }

        dv.x = (int)Math.round(dxOut);
        dv.y = (int)Math.round(dyOut);
        return dv;
    }

    public static class XYThetaModel extends GridWorldModel {

        public XYThetaModel(int[][] map, double[][] transitionDynamics) {
            this.map = map;
            this.transitionDynamics = transitionDynamics;
        }

        @Override
        public State sample(State s, Action a) {
            s = s.copy();
            double [] directionProbs = transitionDynamics[actionInd(a.actionName())];
            double roll = Math.random();
            double cumulativeProb = 0.;
            int dir = 0;
            for(int i = 0; i < directionProbs.length; i++){
                cumulativeProb += directionProbs[i];
                if(roll < cumulativeProb){
                    dir = i;
                    break;
                }
            }

            int [] dcomps = movementDirectionFromIndex(dir);
            return move(s, dcomps[0], dcomps[1], dcomps[2]);
        }

        /**
         * Attempts to move the agent into the given position, taking into account walls and blocks
         * @param s the current state
         * @param xd the attempted X displacement in agent's frame (-1, 0, 1)
         * @param yd the attempted Y displacement in agent's frame (-1, 0, 1)
         * @param thetad the attempted theta displacement in agent's frame (-1 -90deg, 0 0deg, +1 +90deg)
         * @return input state s, after modification
         */
        protected State move(State s, int xd, int yd, int thetad) {

            XYThetaState xyThetaState = (XYThetaState) s;

            int ax = xyThetaState.agent.x;
            int ay = xyThetaState.agent.y;
            int at = xyThetaState.agent.theta;

//            System.out.println("Move: Old pose " + xyThetaState.agent.toString());

            GridLocation dvIn = new GridLocation(xd, yd, "differenceVector");
            GridLocation dvOut = rotateVector(dvIn, at);

            int nx = ax + dvOut.x;
            int ny = ay + dvOut.y;
            int nt = (at + thetad + 4) % 4;

            //hit wall, so do not change position
            if(nx < 0 || nx >= map.length || ny < 0 || ny >= map[0].length || map[nx][ny] == 1 ||
                    (xd > 0 && (map[ax][ay] == 3 || map[ax][ay] == 4)) || (xd < 0 && (map[nx][ny] == 3 || map[nx][ny] == 4)) ||
                    (yd > 0 && (map[ax][ay] == 2 || map[ax][ay] == 4)) || (yd < 0 && (map[nx][ny] == 2 || map[nx][ny] == 4)) ){
                nx = ax;
                ny = ay;
                nt = at;
            }

            XYThetaAgent nagent = xyThetaState.touchAgent();
            nagent.x = nx;
            nagent.y = ny;
            nagent.theta = nt;

//            System.out.println("Move: New pose " + nagent.toString());

            return s;
        }

        protected int actionInd(String name){
            if(name.equals(ACTION_F)){
                return 0;
            }
            else if(name.equals(ACTION_R)){
                return 1;
            }
            else if(name.equals(ACTION_FL)){
                return 2;
            }
            else if(name.equals(ACTION_FR)){
                return 3;
            }
            else if(name.equals(ACTION_RL)){
                return 4;
            }
            else if(name.equals(ACTION_RR)){
                return 5;
            }
            throw new RuntimeException("Unknown action " + name);
        }

    }

}
