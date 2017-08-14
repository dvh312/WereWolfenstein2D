
/**
 * WereWolfenstein 2D: possible outcomes from an action by the player.
 * 
 * To declare a variable of this type, do so the same way as for other classes:
 * <pre><code>Result outcome;</code></pre>
 *  
 * To refer to a value of this enumerated type (such as SUCCESS), write:
 * <pre><code>Result.SUCCESS</code></pre>
 * as in this assignment statement
 * <pre><code>outcome = Result.SUCCESS;</code></pre>
 * or this if condition
 * <pre><code>if (outcome == Result.SUCCESS)</code></pre>
 * 
 * You will <em>never</em> use the <code>new</code> keyword with this type.
 *     
 */
public enum Result {
    CAPTURED,   //werewolf has been subdued and captured
    SUCCESS,    //operation (walking or shooting) was successful
    IMPOSSIBLE, //operation (walking or shooting) unsuccessful because it was not allowed
    BITTEN,     //player is bitten
    VILLAGE,    //player moves or shoots into the village area
    FAILURE,    //player shoots into empty area, etc.
    NEAR,       //werewolf or village is in an adjacent area
    FAR         //werewolf or village is not in an adjacent area
}
