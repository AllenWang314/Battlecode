package lecMod2;
import battlecode.common.*;

public class HQ extends Shooter {
    static int numMiners = 0;

    public HQ(RobotController r) throws GameActionException {
        super(r);

        comms.sendHqLoc(rc.getLocation());
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        double currentWaterHeight = getWaterHeight();
        System.out.println(currentWaterHeight);

        Direction[] minerCreateDirections = {Direction.NORTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.SOUTHEAST};

        //Try to make a miner in each direction
        if(numMiners < 5) {
            if (tryBuild(RobotType.MINER, minerCreateDirections[numMiners])) {
                System.out.println("Created a new directional Miner");
                numMiners++;
            }

/*             for (Direction dir : Util.directions)
                if(tryBuild(RobotType.MINER, dir)){
                    numMiners++;
                } */
        }
    }
}