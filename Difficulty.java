
/**
 * WereWolfenstein 2D: possible difficulty settings.
 * 
 * To declare a variable of this type, do so the same way as for other classes:
 * <pre><code>Difficulty difficulty;</code></pre>
 *  
 * To refer to a value of this enumerated type (such as EASY), write:
 * <pre><code>Difficulty.EASY</code></pre>
 * as in this assignment statement
 * <pre><code>difficulty = Difficulty.EASY;</code></pre>
 * 
 * And, importantly, to convert a String into a value of this type, use the
 * following, which assumes that the String <code>input</code> contains some
 * variant of "easy", "normal" or "hard"):
 * <pre><code>difficulty = Difficulty.valueOf( input.toUpperCase() );</code></pre>
 * 
 * To display details of a Difficulty level, such as <code>difficulty</code>
 * above, just use it as part of a String, as in:
 * <pre><code>String diffMsg = "Settings: " + difficulty;</code></pre>
 * 
 * You will <em>never</em> use the <code>new</code> keyword with this type.
 *     
 */
public enum Difficulty {
    EASY,   //easy game, lots of villages to spare
    NORMAL, //normal game, fewer villagers & bullets
    HARD;   //hard game, the wolf relocates when shot
    
    /**
     * Returns a multi-line String describing the characteristics of this
     * difficulty level.
     */
    public String toString() {
        return "Player starts in the village: " + getPlayerStartsInVillage() +
                "\nNumber of villagers: " + getVillagerCount() +
                "\nAvailable silver bullets: " + getSilverBulletCount() + 
                "\nWerewolf moves when shot: " + getWolfMovesWhenShot();
    }
    
    /**
     * Returns true if the player starts in the same area as the village for
     * this difficulty level, false otherwise.
     */
    public boolean getPlayerStartsInVillage() {
        return this == EASY;
    }

    /**
     * Returns the initial number of villagers for this difficulty level.
     */
    public int getVillagerCount() {
        switch (this) {
        case EASY: return 6;
        case NORMAL: return 4;
        default /*HARD*/: return 4;
        }
    }

    /**
     * Returns the number of silver bullets the player starts with in this
     * difficulty level.
     */
    public int getSilverBulletCount() {
        switch (this) {
        case EASY: return 8;
        case NORMAL: return 6;
        default /*HARD*/: return 6;
        }
    }
    
    /**
     * Returns true if the werewolf moves when hit, false otherwise.
     */
    public boolean getWolfMovesWhenShot() {
        return this == HARD;
    }
    
}
