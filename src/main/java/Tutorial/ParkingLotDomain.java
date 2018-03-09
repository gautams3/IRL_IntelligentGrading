package Tutorial;

import burlap.domain.singleagent.gridworld.GridWorldDomain;

/**
 * Created by gautam on 08/03/2018.
 */
public class ParkingLotDomain extends GridWorldDomain {

    public ParkingLotDomain(int width, int height){
        super(width, height);
    }

    public void setMapToParkingLot()
    {
        this.width = 5;
        this.height = 5;
        this.numLocationTypes = 4;
        this.makeEmptyMap();
    }
}
