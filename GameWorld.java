import java.util.Random;

/**
 * KIT101 Assignment 2
 *
 * WereWolfenstein 2D -- Library class
 * 
 * See also the Result enumerated type (in Result.java).
 *
 * @author James Montgomery
 * @version April 2016
 *
 * Notes:
 * 1. Students should not change this program
 * 2. Students need not submit this program
 */
public class GameWorld {
    
    // Final instance variables
    public final int NIGHT_LENGTH = 3;      // three actions until dawn arrives (day is instantaneous)
    private final int MAX_SHOTS_NEEDED = 3; // successful hits required to subdue werewolf

    //This map is _deliberately_ confusing, although it actually a regular layout
    private int[] east =  {1,2,0,4,5,3,7,8,6}; // areas to east of current location (index)
    private int[] west =  {2,0,1,5,3,4,8,6,7}; // areas to west of current location (index)
    private int[] north = {6,7,8,0,1,2,3,4,5}; // areas to north of current location (index)
    private int[] south = {3,4,5,6,7,8,0,1,2}; // areas to south of current location (index)
    private int numAreas = south.length;     // number of areas in the "world"

    // Non-final instance variables
    private int currentArea;    // current location of player
    private int villagePos;     // area where the village is located
    private int wolfPos;        // area where the werewolf can be found

    private Difficulty level;   // difficulty level of the game
    private int villagerCount;  // number of villagers remaining
    private int stepsUntilDawn; // number of actions until night falls
  
    private boolean isBitten;   // is the player currently bitten and in need of treatment? 
    private int hitsRemaining;  // number of shots still needed to subdue the werewolf

    private Random generator;   // to use for random placement in areas
    private boolean tracing;    // switch for tracing messages


    /**
     * Creates a game world for the WereWolfenstein 2D game.
     * @param traceOnOff whether or not tracing output should be shown
     */
    public GameWorld(boolean traceOnOff) {
        trace("GameWorld() starts...");
        generator = new Random();
        generator.setSeed(101); //this default setting makes the game more predictable, for testing
        setTracing(traceOnOff); //this may replace random number generator        
        trace("...GameWorld() ends");
    }


    /**
     * Returns the number of the current area.
     * @return which area number player is within
     */
    public int getCurrentArea() {
        trace("getCurrentArea() starts... ...and ends with value " + currentArea);
        return currentArea;
    }

    
    /**
     * Returns the number of shot attempts, formatted as "total hits/total required"
     * @return the fraction of shots that have hit the werewolf (out of the required number of hits)
     */
    public String getShotCount() {
        String count; // formatted total

        trace("getShotCount() starts...");
        count = (MAX_SHOTS_NEEDED - hitsRemaining) + "/" + MAX_SHOTS_NEEDED;
        trace("getShotCount() ...ends with value " + count);
        return count;
    }
    

    /**
     * Returns the current number of villagers.
     * @return the villager count, >= 0
     */
    public int getVillagePopulation() {
        return villagerCount;
    }
    
    
    /**
     * Returns the number of actions remaining until dawn arrives. 
     * @return actions remaining until dawn event
     */
    public int getActionsUntilNight() {
        return stepsUntilDawn;
    }

    
    /**
     * Randomly determines a unique starting location (currentArea), village
     * position (villagePos) and werewolf position (wolfPos).
     * @param difficulty - the difficulty level of the game
     * @return the starting location (area)
     */
    public int newGame(Difficulty difficulty) {
        trace("newGame() starts...");

        level = difficulty;

        //determine village location and initialise villagers and night length
        villagePos = generator.nextInt(numAreas);
        stepsUntilDawn = NIGHT_LENGTH;
        villagerCount = level.getVillagerCount();
        
        // determine player's position
        if (level.getPlayerStartsInVillage()) {
            //place player in village
            currentArea = villagePos;
        } else {
            //pick a random location for player away from the village
            do {
                currentArea = generator.nextInt(numAreas);
            } while (currentArea == villagePos);
        }
        trace("player starts at " + currentArea);
        
        // determine werewolf's position
        trace("calling resetTargetPosition()");
        resetWolfPosition();

        // define player's status
        trace("player is not bitten");
        isBitten = false;
        trace("werewolf is not hit");
        hitsRemaining = MAX_SHOTS_NEEDED;

        trace("...newGame() ends with value " + currentArea);
        return currentArea;
    }
    

    /** Randomly determines a unique location for werewolf (wolfPos). */
    private void resetWolfPosition() {
        int pos;    // werewolf position

        trace("resetWolfPosition() starts...");

        pos = generator.nextInt(numAreas);
        while (pos == currentArea || pos == villagePos) {
            trace("clash detected");
            // avoid clash with current location
            pos = generator.nextInt(numAreas);
        }

        wolfPos = pos;
        trace("werewolf position is " + wolfPos);

        trace("...resetWolfPosition() ends");
    }

    
    /**
     * Returns the nearness of the werewolf.
     * @return Status of werewolf's location
     *    BITTEN: if player is currently bitten (and delirious)
     *    NEAR: if werewolf is in a connected area
     *    FAR: if werewolf is elsewhere
     */
    public Result werewolfNear() {
        trace("werewolfNear() starts");
        if (isBitten) {
            trace("werewolfNear(): player is still delirious from a bite so cannot sense nearness of werewolf");
            return Result.BITTEN;
        }
        trace("werewolfNear() returning result of nearnessTo(" + wolfPos + ")");
        return nearnessTo(wolfPos);
    }

    
    /**
     * Returns true if the village is near the player (in an adjacent area),
     * false otherwise.
     * @return true if the player is near (but not in) the village, false otherwise.
     */
    public boolean isVillageNear() {
        trace("villageNear() starts and returns result of nearnessTo(" + villagePos + ") == Result.NEAR");
        return nearnessTo(villagePos) == Result.NEAR;
    }
   
    
    /**
     * Returns the nearness of the player to the nominated area.
     * @param area  the area (werewolf or village) to assess
     * @return Nearness of player to nominated area
     *    NEAR: if player is adjacent to area
     *    FAR: if player is not adjacent to the area
     */
    private Result nearnessTo(int area) {
        Result closeness;    // closeness of player to area

        trace("nearnessTo() starts...");
        if ((east[currentArea] == area) ||
            (west[currentArea] == area) ||
            (north[currentArea] == area) ||
            (south[currentArea] == area))
        {
            // player is close to area
            closeness = Result.NEAR;
            trace("area is close");
        } else {
            // player is not adjacent to area
            closeness = Result.FAR;
            trace("area is far");
        }

        trace("...nearnessTo() ends with value " + closeness);

        return closeness;
    }
    
    
    /**
     * Try to move the player to another area. If the move is not IMPOSSIBLE
     * then the number of actions remaining before dawn arrives is decremented.
     * @param into  the area to try to move into
     * @return Result of the movement attempt
     *    SUCCESS: move was successful (current position changed)
     *    VILLAGE: move was successful and player has arrived in the village (and is not longer bitten) 
     *    BITTEN: move was successful but player encountered the werewolf
     *    FAILURE: move was successful but already bitten player encountered the werewolf again
     *    IMPOSSIBLE: move was impossible (current position not changed)
     */
    public Result tryWalk(int into) {
        Result result;    // outcome of walk attempt

        trace("tryWalk() starts...");
        
        if (areasConnected(currentArea, into)) {
            trace("move into area " + into );
            currentArea = into;
            if (currentArea != wolfPos) {
                // werewolf not found
                trace("werewolf not encountered");
                result = Result.SUCCESS;

                if (currentArea == villagePos) {
                    isBitten = false;
                    result = Result.VILLAGE;
                }
            } else {
                // werewolf encountered
                if (isBitten) {
                    trace("werewolf encountered again");
                    result = Result.FAILURE;
                } else {
                    // not bitten
                    trace("werewolf encountered");
                    result = Result.BITTEN;
                    isBitten = true;
                    resetWolfPosition();                    
                }
            }
            stepsUntilDawn--; //one more action taken
        } else { // area not connected
            trace("move not possible");
            result = Result.IMPOSSIBLE;
        }

        trace("...tryWalk() ends with value " + result);

        return result;
    }


    /**
     * Try to shoot a silver bullet at the werewolf from the current area.
     * If the shot is not IMPOSSIBLE then the number of actions remaining
     * before dawn arrives is decremented.
     * @param into  the area to attempt to shoot into
     * @return status of attempt
     *    CAPTURED: werewolf has been subdued and captured
     *    SUCCESS: werewolf has been hit but is not yet captured
     *    VILLAGE: the shot went into the village and a villager has died
     *    FAILURE: werewolf not present
     *    IMPOSSIBLE: area not connected
     */
    public Result shoot(int into) {
        Result result;    // outcome of darting attempt

        trace("shootDart() starts...");
        
        if (areasConnected(currentArea, into)) {
            // area connected
            trace("shoot into area " + into );
            if (into == villagePos) {
                result = Result.VILLAGE;
                villagerCount--;
                trace("shot into village");
            } else if (into != wolfPos) {
                // not at werewolf location (but at least didn't shoot into the village!)
                result = Result.FAILURE;
                trace("werewolf not present");
            } else {
                // at werewolf location
                hitsRemaining--;
                if (hitsRemaining == 0) {
                    // last hit required to subdue the werewolf
                    trace("werewolf subdued and captured");
                    result = Result.CAPTURED;
                } else {
                    // not the last shot
                    result = Result.SUCCESS;
                    if (level.getWolfMovesWhenShot()) {
                        resetWolfPosition();
                    }
                    trace("werewolf found but not yet captured");
                }
            }
            stepsUntilDawn--; //one more action taken
        } else {
            // not at valid location
            result = Result.IMPOSSIBLE;
            trace("area not present");
        }
        
        trace("...shootDart() ends with value " + result);

        return result;
    }
    
    
    /**
     * Checks if there are no more actions left until dawn arrives. If dawn is
     * here then decrements the number of villagers, repositions the werewolf
     * and resets the number of actions until dawn arrives again. Returns true
     * if dawn occurred, false if it did not.
     * @return true if dawn just happened, false if has not yet arrived
     */
    public boolean checkForDawn() {
        if (stepsUntilDawn == 0) {
            if (villagerCount > 0) { //dawn may arrive after shooting the last villager
                villagerCount--;
            }
            stepsUntilDawn = NIGHT_LENGTH;
            resetWolfPosition();
            return true;
        }
        return false;
    }
    
    
    /**
     * Returns true if areas s1 and s2 are connected, false otherwise.
     * Also returns false if either area is an invalid area identifier.
     * @param s1 the first area
     * @param s2 the second area
     * @return true if areas are connected, false otherwise
     */
    private boolean areasConnected(int s1, int s2) {
        if (Math.min(s1, s2) >= 0 && Math.max(s1, s2) < numAreas) { //valid areas...
            //...but are they connected? 
            return east[s1] == s2 || north[s1] == s2 || west[s1] == s2 || south[s1] == s2;
        }
        //Either s1 or s2 is not a valid area identifier
        return false;
    }


    /**
     * Determine ID number of an adjacent area given its direction from the
     * current area.
     * @param direction the direction to look (n for north, e for east, s for south, w for west)
     * @return number of the area in that direction
     * @throws IllegalArgumentException if direction is null
     */
    public int nextArea(char direction) {
        int nextIs;    // area number of area in indicated direction
        
        //Valid values
        final char N = 'n', E = 'e', S = 's', W = 'w';

        trace("nextArea() starts...");
        
        // examine adjacent areas
        switch (direction) {
            case N: trace("determining number of area to the north");
                    nextIs = north[currentArea];
                    break;
            case E: trace("determining number of area to the east");
                    nextIs = east[currentArea];
                    break;
            case S: trace("determining number of area to the south");
                    nextIs = south[currentArea];
                    break;
            case W: trace("determining number of area to the west");
                    nextIs = west[currentArea];
                    break;
            default: throw new IllegalArgumentException("Direction must be one of " + N + ", " + E + ", " + S + " or " + W);
        }

        trace("...nextArea() ends with value for '" + direction + "' of " + nextIs);

        return nextIs;
    }


    /** Resets all game values. */
    public void reset() {
        trace("reset() starts...");

        // reset all game values
        trace("resetting all game values");
        newGame(level); //start a new game with the same difficulty

        trace("...reset() ends");
    }


    /**
     * Turn tracing messages on or off. If off then it is assumed that
     * debugging is not occurring and so a new (unseeded) random number
     * generator is created so the game is unpredictable.
     * @param shouldTrace  indicates if tracing messages should be displayed or not
     */
    public void setTracing(boolean shouldTrace) {
        if (! shouldTrace) { // not tracing so get an unseeded RNG
            generator = new Random();
        }
        tracing = shouldTrace;
    }

    /**
     * Prints the given tracing message if tracing is enabled.
     * @param message  the message to be displayed
     */
    public void trace(String message) {
        if (tracing) {
            System.out.println("GameWorld: " + message);
        }
    }
    
}
