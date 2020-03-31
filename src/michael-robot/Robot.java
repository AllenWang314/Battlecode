package lecMod2;
import battlecode.common.*;

public class Robot {
    RobotController rc;
    Communications comms;

    int turnCount = 0;
    double waterHeight = 0;

    public Robot(RobotController r) {
        this.rc = r;
        comms = new Communications(rc);
    }

    public void takeTurn() throws GameActionException {
        turnCount += 1;
        waterHeight = getWaterHeight();
    }

    public double getWaterHeight() throws GameActionException {
        int currentRound = rc.getRoundNum();
        double newWaterHeight = Math.exp(0.0028*currentRound - 1.38* Math.sin(0.00157*currentRound - 1.73) + 1.38*Math.sin(-1.73)) - 1;
        return newWaterHeight;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        }
        return false;
    }
}