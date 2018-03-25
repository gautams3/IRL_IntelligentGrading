package Tutorial;

import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learnfromdemo.RewardValueProjection;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRL;
import burlap.behavior.singleagent.learnfromdemo.mlirl.MLIRLRequest;
import burlap.behavior.singleagent.learnfromdemo.mlirl.commonrfs.LinearStateDifferentiableRF;
import burlap.behavior.singleagent.learnfromdemo.mlirl.differentiableplanners.DifferentiableSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.GroundedProp;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

import static Tutorial.GridWorldDomain.*;


public class HelloGridWorld {

    public Visualizer visualizer;
    public Tutorial.GridWorldDomain gridWorld;
    public OOSADomain domain;
    public GridLocation goal;
    public static int GRID_MAX_X = 5;
    public static int GRID_MAX_Y = 5;

    public HelloGridWorld()
    {
//        gridWorld = new Tutorial.GridWorldDomain(11,11); //11x11 grid world
//        gridWorld.setMapToFourRooms(); //four rooms layout
//        gridWorld.setDeterministicTransitionDynamics();

        gridWorld = new Tutorial.GridWorldDomain(GRID_MAX_X,GRID_MAX_Y);

        domain = gridWorld.generateDomain(); //generate the grid world domain

        //create visualizer and explorer
        visualizer = GridWorldVisualizer.getVisualizer(gridWorld.getMap());

    }

    public static void main(String[] args) {

        HelloGridWorld myGridWorld = new HelloGridWorld();

        myGridWorld.launchExplorer();
//        myGridWorld.launchSavedEpisodeSequenceVis("user_tries");
//        myGridWorld.runIRL("irl_demos", "user_tries");

    }

    /**
     * Creates a visual explorer that you can use to to record trajectories. Use the "`" key to reset to a random initial state
     * Use the W,S,A,D keys to move north, south, east, and west, respectively. To enable recording,
     * first open up the shell and type: "rec -b" (you only need to type this once). Then you can move in the explorer as normal.
     * Each demonstration begins after an environment reset.
     * After each demonstration that you want to keep, go back to the shell and type "rec -r"
     * If you reset the environment before you type that,
     * the episode will be discarded. To temporarily view the episodes you've created, in the shell type "episode -v". To actually record your
     * episodes to file, type "rec -w path/to/save/directory base_file_name" For example "rec -w irl_demos demo"
     */
    public void launchExplorer(){
        State init_state = basicState();

        VisualExplorer exp = new VisualExplorer(domain, visualizer, init_state);

//        //Set control keys to use w-s-a-d
//        exp.addKeyAction("w", Tutorial.GridWorldDomain.ACTION_NORTH, "");
//        exp.addKeyAction("s", Tutorial.GridWorldDomain.ACTION_SOUTH, "");
//        exp.addKeyAction("a", Tutorial.GridWorldDomain.ACTION_WEST, "");
//        exp.addKeyAction("d", Tutorial.GridWorldDomain.ACTION_EAST, "");

        //Set controls to actions (w,s,q,e,z,x). a,d not allowed
        exp.addKeyAction("w", Tutorial.GridWorldDomain.ACTION_F, "");
        exp.addKeyAction("s", Tutorial.GridWorldDomain.ACTION_R, "");
        exp.addKeyAction("q", Tutorial.GridWorldDomain.ACTION_FL, "");
        exp.addKeyAction("e", Tutorial.GridWorldDomain.ACTION_FR, "");
        exp.addKeyAction("z", Tutorial.GridWorldDomain.ACTION_RL, "");
        exp.addKeyAction("x", Tutorial.GridWorldDomain.ACTION_RR, "");

        exp.initGUI();
    }

    /** Launch a episode sequence visualizer to display the saved trajectories in the folder "irlDemo" */
    public void launchSavedEpisodeSequenceVis(String pathToEpisodes)
    {
        new EpisodeSequenceVisualizer(this.visualizer, this.domain, pathToEpisodes);
    }

    /**
     * A state feature vector generator that create a binary feature vector where each element
     * indicates whether the agent is in a cell of of a different type. All zeros indicates
     * that the agent is in an empty cell.
     */
    public static class LocationFeatures implements DenseStateFeatures {

        protected int numLocations;
        PropositionalFunction inLocationPF;

        public LocationFeatures () { }

        public LocationFeatures(OODomain domain, int numLocations){
            this.numLocations = numLocations;
            this.inLocationPF = domain.propFunction(GridWorldDomain.PF_AT_LOCATION);
        }

        public LocationFeatures(int numLocations, PropositionalFunction inLocationPF) {
            this.numLocations = numLocations;
            this.inLocationPF = inLocationPF;
        }

        @Override
        public double[] features(State s) {

            double [] fv = new double[this.numLocations];

            int aL = this.getActiveLocationVal((OOState)s);
            if(aL != -1){
                fv[aL] = 1.;
            }

            return fv;
        }


        protected int getActiveLocationVal(OOState s){

            List<GroundedProp> gps = this.inLocationPF.allGroundings(s);
            for(GroundedProp gp : gps){
                if(gp.isTrue(s)){
                    GridLocation l = (GridLocation)s.object(gp.params[1]);
                    return l.type;
                }
            }

            return -1;
        }

        @Override
        public DenseStateFeatures copy() {
            return new LocationFeatures(numLocations, inLocationPF);
        }
    }

    /**
     * setup initial state
     * @return init grid world state
     */
    protected State basicState()
    {
        GridLocation loc0 = new GridLocation(1, 3, 1, "loc0");
        GridLocation loc1 = new GridLocation(2, 3, 1, "loc1");
        goal = new GridLocation(3, 3, 2, "GOAL");
        GridLocation loc3 = new GridLocation(1, 2, 0, "loc3");
        GridLocation loc4 = new GridLocation(2, 2, 0, "loc4");
        GridLocation loc5 = new GridLocation(3, 2, 0, "loc5");
        GridLocation loc6 = new GridLocation(1, 0, 0, "loc6");
        GridLocation loc7 = new GridLocation(2, 0, 0, "loc7");
        GridLocation loc8 = new GridLocation(3, 0, 0, "loc8");

        GridWorldState parking_init_state = new GridWorldState(
                new GridAgent(0, 2, 1), loc0, loc1, goal, loc3, loc4, loc5, loc6, loc7, loc8);

        //Use the GridWorldState below to use the standard grid world representation
//        GridWorldState parking_init_state = new GridWorldState(
//                new GridAgent(0, 2), loc0, loc1, goal, loc3, loc4, loc5, loc6, loc7, loc8);

        return parking_init_state;
    }

    /**
     * Runs MLIRL on the trajectories stored in the pathToEpisodes directory and then visualizes the learned reward function.
     */
    public void runIRL(String pathToEpisodes, String pathToUserTries)
    {
        /** SETUP AND RUN IRL */
        //create reward function features to use
        LocationFeatures features = new LocationFeatures(this.domain, 5);

        //create a reward function that is linear with respect to those features and has small random
        //parameter values to start
        LinearStateDifferentiableRF rf = new LinearStateDifferentiableRF(features, 5);
        for(int i = 0; i < rf.numParameters(); i++){
            rf.setParameter(i, RandomFactory.getMapped(0).nextDouble()*0.2 - 0.1);
        }

        //load our saved demonstrations from disk
        List<Episode> episodes = Episode.readEpisodes(pathToEpisodes);

        //use either DifferentiableVI or DifferentiableSparseSampling for planning. The latter enables receding horizon IRL,
        //but you will probably want to use a fairly large horizon for this kind of reward function.
        double beta = 10;
        //DifferentiableVI dplanner = new DifferentiableVI(this.domain, rf, 0.99, beta, new SimpleHashableStateFactory(), 0.01, 100);
        DifferentiableSparseSampling dplanner = new DifferentiableSparseSampling(this.domain, rf, 0.99, new SimpleHashableStateFactory(), 10, -1, beta);

        dplanner.toggleDebugPrinting(false);

        //define the IRL problem
        MLIRLRequest request = new MLIRLRequest(domain, dplanner, episodes, rf);
        request.setBoltzmannBeta(beta);

        //run MLIRL on it
        MLIRL irl = new MLIRL(request, 0.1, 0.1, 10); /// TODO: 25/03/2018 set small negative reward for road here somewhere
        irl.performIRL();

        //get all states in the domain so we can visualize the learned reward function for them
        List<State> allStates = StateReachability.getReachableStates(basicState(), this.domain, new SimpleHashableStateFactory());

        /**
         * SCORING CODE
         * */
        /// TODO: 25/03/2018 Store the reward function to a file (using YAML?)
        /// TODO: 25/03/2018 Move to other function that reads reward function from file, user tries from file, and gives you a score
        System.out.println("\n\nNow applying learned policy by agent to test paths tried by users from folder " + pathToUserTries);

        double reward, totalReward;
        DecimalFormat df = new DecimalFormat("#0.000");
        if (pathToUserTries != null) {
            List<Episode> userTries = Episode.readEpisodes(pathToUserTries);
            for (Episode ep : userTries) {
                totalReward = 0; //reset
                for (State s : ep.stateSequence) {
                    reward = rf.reward(null, null, s);
                    totalReward += reward; /// TODO: 25/03/2018 Use gamma here
                    System.out.println(s.get("agent:x") +", "+ s.get("agent:y")+", "+ GridWorldState.getCardinalDirection((GridWorldState)s) +": "+ "\t" + df.format(reward));
                }
                System.out.println("Score for this try: " + df.format(totalReward));
            }
        }

        /** VISUALIZE */
        //get a standard grid world value function visualizer, but give it StateRewardFunctionValue which returns the
        //reward value received upon reaching each state which will thereby let us render the reward function that is
        //learned rather than the value function for it.

        StateValuePainter2D svp = new StateValuePainter2D();
        OOVariableKey xVar = new OOVariableKey(CLASS_AGENT, VAR_X);
        OOVariableKey yVar = new OOVariableKey(CLASS_AGENT, VAR_Y);
        VariableDomain xRange = new VariableDomain(0, GRID_MAX_X);
        VariableDomain yRange = new VariableDomain(0, GRID_MAX_Y);
        svp.setXYKeys(xVar, yVar, xRange, yRange, 1, 1);
        svp.setValueStringRenderingFormat(18, Color.RED, 2, 0.4f , 0.5f);
        LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
        rb.addNextLandMark(0., Color.BLACK);
        rb.addNextLandMark(1., Color.WHITE);
        svp.setColorBlend(rb);

        ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp, new RewardValueProjection(rf));

        PolicyGlyphPainter2D spp = ArrowActionGlyph.getNSEWPolicyGlyphPainter(xVar, yVar, xRange, yRange, 1, 1,
                ACTION_F, ACTION_R, ACTION_FR, ACTION_FR);

        gui.setSpp(spp);
        gui.setPolicy(new GreedyQPolicy((QProvider) request.getPlanner()));
        gui.setBgColor(Color.GRAY);

        gui.initGUI();
    }

}
