package lecMod2;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit {

    int numDesignSchools = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
    Direction scoutDirection = Direction.NORTH;

    public Miner(RobotController r) {
        super(r);
        System.out.println("Miner constructor!");

        //Find all the distance 1 away units and loop thru to find the HQ
        RobotInfo[] adjacentRobots = rc.senseNearbyRobots(-1);
        System.out.println("Sensed Nearby Robots!");
        System.out.println(adjacentRobots);
        for (RobotInfo nearbyThing: adjacentRobots){
            if (nearbyThing.type == RobotType.HQ){
                //Check if the direction to the HQ is one of each:
                if (rc.getLocation().directionTo(nearbyThing.location) == Direction.WEST){
                    System.out.println("We are a EAST SCOUT");
                    scoutDirection = Direction.EAST;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.EAST){
                    System.out.println("We are a WEST SCOUT");
                    scoutDirection = Direction.WEST;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.NORTH){
                    System.out.println("We are a SOUTH SCOUT");
                    scoutDirection = Direction.SOUTH;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.SOUTH){
                    System.out.println("We are a NORTH SCOUT");
                    scoutDirection = Direction.NORTH;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.NORTHWEST){
                    System.out.println("We are a SOUTHEAST SCOUT");
                    scoutDirection = Direction.SOUTHEAST;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.NORTHEAST){
                    System.out.println("We are a SOUTHWEST SCOUT");
                    scoutDirection = Direction.SOUTHWEST;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.SOUTHWEST){
                    System.out.println("We are a NORTHEAST SCOUT");
                    scoutDirection = Direction.NORTHEAST;
                } else if (rc.getLocation().directionTo(nearbyThing.location) == Direction.SOUTHEAST){
                    System.out.println("We are a NORTHWEST SCOUT");
                    scoutDirection = Direction.NORTHWEST;
                } else {
                    System.out.println("DID NOT GET A SCOUT DIRECTION :(");
                }
            }
        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        System.out.println(waterHeight);
        numDesignSchools += comms.getNewDesignSchoolCount();
        comms.updateSoupLocations(soupLocations);
        checkIfSoupGone();

        for (Direction dir : Util.directions)
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if (!soupLocations.contains(soupLoc)) {
                    comms.broadcastSoupLocation(soupLoc);
                }
            }
        // mine first, then when full, deposit
        for (Direction dir : Util.directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());

        if (numDesignSchools < 3){
            if(tryBuild(RobotType.DESIGN_SCHOOL, Util.randomDirection()))
                System.out.println("created a design school");
        }

        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // time to go back to the HQ
            if(nav.goTo(hqLoc))
                System.out.println("moved towards HQ");
        } else if (soupLocations.size() > 0) {
            nav.goTo(soupLocations.get(0));
            rc.setIndicatorLine(rc.getLocation(), soupLocations.get(0), 255, 255, 0);
        } else if (nav.tryMove(scoutDirection)) {
            // otherwise, move in the created scout direction!
            System.out.println("I moved in my scout direction!");
        } else {
            nav.tryMove(Util.randomDirection());
            System.out.println("Moved in Random Direction");
        }
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    void checkIfSoupGone() throws GameActionException {
        if (soupLocations.size() > 0) {
            MapLocation targetSoupLoc = soupLocations.get(0);
            if (rc.canSenseLocation(targetSoupLoc)
                    && rc.senseSoup(targetSoupLoc) == 0) {
                soupLocations.remove(0);
            }
        }
    }
}
