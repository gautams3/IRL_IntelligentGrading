package Tutorial;

import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.mdp.core.Domain;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import java.awt.*;

/**
 * Created by gsalhotra on 3/22/2018.
 */
public class XYThetaWorldVisualizer extends GridWorldVisualizer {

    public XYThetaWorldVisualizer () {
        super();
    }

    /**
     * Returns state render layer for a gird world domain with the provided wall map.
     * @param map the wall map matrix where 0s indicate it is clear of walls, 1s indicate a full cell wall in that cell, 2s indicate a 1D north wall, 3s indicate a 1D east wall, and 4s indicate a 1D north and east wall.
     * @return a grid world domain state render layer
     */
    public static StateRenderLayer getRenderLayer(int [][] map){

        StateRenderLayer r = new StateRenderLayer();

        r.addStatePainter(new MapPainter(map));
        OOStatePainter oopainter = new OOStatePainter();
        oopainter.addObjectClassPainter(XYThetaDomain.CLASS_LOCATION, new LocationPainter(map));
        oopainter.addObjectClassPainter(XYThetaDomain.CLASS_XYTAGENT, new CellPainter(2, Color.gray, map));
        r.addStatePainter(oopainter);

        return r;

    }

    /**
     * Returns visualizer for a grid world domain with the provided wall map.
     * @param map the wall map matrix where 0s indicate it is clear of walls, 1s indicate a full cell wall in that cell, 2s indicate a 1D north wall, 3s indicate a 1D east wall, and 4s indicate a 1D north and east wall.
     * @return a grid world domain visualizer
     */
    public static Visualizer getVisualizer(int [][] map){

        StateRenderLayer r = getRenderLayer(map);
        Visualizer v = new Visualizer(r);

        return v;
    }

}
