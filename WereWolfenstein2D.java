import kit101.QuickGUI;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * KIT101 Assignment 2
 *
 * WereWolfenstein 2D -- Organiser Class
 *
 * A class of WereWolfenstein2D game contains all the methods 
 * to play the game.
 *
 * @author Hoang Vinh Dinh
 * @version 7 May 2016
 * @studentID 421682
 * @org University of Tasmania
 *
 * Stage Reached: 4
 */
public class WereWolfenstein2D extends QuickGUI implements ActionListener{
    //only stage 4
    /**  
    *   An enum that indicate different states of the game
    *   
    *   PLAY Ask for play
    *   DIFF ask for difficulty
    *   ACTION ask for action
    *   POS ask for position (only for walk, shoot action)
    */
    private enum State {PLAY, DIFF, ACTION, POS}; //different state of the game

    // Final instance variables
    private final boolean TRACE = false;     //default trace is off
    private final char WANTPLAYCHAR = 'y';   //player will type 'y' if they want to play
    private final int MAXSAVEDMOVE = 10; //number of moves that are saved

    //only stage 3
    private final int UNKNOWNVILLAGEPOS = -1; //represent unknown position of village

    // Non-final instance variables
    private GameWorld gameWorld; //gameWorld saved properties for one game
    private boolean tracing; //turn on or off the debug message

    //additional variables to save properties for gameWorld in one game
    private int remainingBullets; //saved the number of bullets left for each game
    private boolean gameEnded; //indicates whether the game is ended or not
    private Difficulty difficulty; //contains information about the difficulty that player choose
    private int[] savedMoves; //an array to remembered the moves that player make during one game
    private int countMoves; //count the number of moves that are already saved in the array

    //variables only use for GUI, paint (Stage 3)
    private JTextField txtAction; //a text field receive string input from user
    private JTextArea txtGameDetails; //big text area, show game details
    private JButton btnWalk, btnShoot, btnReset, btnQuit; // 4 buttons to control the game: quit, walk , shoot, reset
    private JLabel lblQuestion; //label the question or instruction for player to do the next step
    private int villagePos; //only used in painted map, show the village position as 'known' (green box) after the player walk/shoot into

    //variables only use for event drive program (Stage 4)
    private State currState; //enum variable to save current state
    private boolean chosenWalk; //identify walk or shoot when asking for position

    /**
     * A constructor to show the game name, explain how to play
     * as well as create a new GameWorld prepare for each game.
     */
    public WereWolfenstein2D() {

        //createGUI
        super("WereWolfenstein 2D"); //create a GUI with that title
        createGUI();

        // constructor body
        setTracing(TRACE);

        //print title and explain
        printGameTitle();
        explain();

        //setup for 'ask for play' state
        currState = State.PLAY;
        updateQuestion();

        //always ready for type in the text box
        txtAction.requestFocus();

        //create new game world to play
        gameWorld = new GameWorld(TRACE);
    }

    /**
     * Create buttons, labels, text fields to show information
     */
    private void createGUI(){
        //final variable for number of line, char of the text area, default width and height for the GUI panel
        final int NUMLINE = 10, NUMCHAR = 42, WIDTH = 520, HEIGHT = 520;
        txtGameDetails = new JTextArea(NUMLINE, NUMCHAR);
        txtGameDetails.setOpaque(false); //do not show white background 
        txtGameDetails.setEditable(false); //cannot edit the text field
        txtGameDetails.setLineWrap(true); //will wrap text...
        txtGameDetails.setWrapStyleWord(true); //...and will break text at word boundaries

        //create new button, label, textfield object
        btnWalk = new JButton("Walk");
        btnShoot = new JButton("Shoot");
        btnReset = new JButton("Reset");
        btnQuit = new JButton("Quit");
        lblQuestion = new JLabel();
        txtAction = new JTextField(NUMCHAR);

        //add listener
        txtAction.addActionListener(this);
        btnShoot.addActionListener(this);
        btnQuit.addActionListener(this);
        btnWalk.addActionListener(this);
        btnReset.addActionListener(this);

        //add GUI components to the panel
        add(txtGameDetails);
        add(btnWalk);
        add(btnShoot);
        add(btnReset);
        add(btnQuit);
        add(lblQuestion);
        add(txtAction);
        
        //set size of the panel
        setWidth(WIDTH);
        setHeight(HEIGHT);
    }

    /**
     * Listen for button clicked, enter pressed in text field
     * Including the whole game process
     * 
     * @param e an event that happen in GUI (button click, enter press)
     */
    public void actionPerformed(ActionEvent e){
        final Object source = e.getSource(); //get the source(which GUI component) of the event for later process
        int chosenArea; //chosen area for shoot or walk

        if (currState == State.PLAY){
            if (source == txtAction){
                if (txtAction.getText().charAt(0) == WANTPLAYCHAR){
                    currState = State.DIFF; //move to next state
                } else {
                    System.exit(0); //halt the program
                }
            }
        } else if (currState == State.DIFF){
            if (source == txtAction){
                txtGameDetails.setText("");
                assignChosenDifficulty(txtAction.getText()); //parse the data from string to save to difficulty variable
                
                //create new game with chosen difficulty and reset all properties of the game, show difficulty settings and first game details
                createNewGame();
                showDifficultySettings();
                showCurrentGameDetails();

                currState = State.ACTION;
            }
        } else if (currState == State.ACTION){
            if (source == btnReset){
                performReset();
            } else if (source == btnQuit){
                performQuit();
                currState = State.PLAY;
            } else if (source == btnWalk){
                //walk button pressed - change to ask for position state
                chosenWalk = true;
                currState = State.POS;
            } else if (source == btnShoot) {
                //shoot button pressed - change to ask for position state
                chosenWalk = false; //user choose to shoot so it's not walk
                currState = State.POS;
            }
        } else if (currState == State.POS){
            if (source == txtAction){
                try{  //throw if not valid value in the text field
                    chosenArea = Integer.valueOf(txtAction.getText());

                    //perform walk or shoot into that area
                    if (chosenWalk){
                        performWalk(chosenArea);
                    } else {
                        performShoot(chosenArea);
                    }
                    //check for dawn after the action
                    if (!gameEnded){
                        if (gameWorld.checkForDawn()){
                            performDawn();    
                        }
                    }
                    //game not end, continue to ask for the next action
                    if (!gameEnded){
                        showCurrentGameDetails();
                        currState = State.ACTION;
                    }
                    //if game ended, show saved moves, reset to ask for play state
                    if (gameEnded){
                        showSavedMoves();
                        currState = State.PLAY;
                    }
                } catch(NumberFormatException ex){
                    trace("Not valid position number");
                }
            }
            
        } else {
            trace("Invalid state! go and check");
        }

        //always reset input field after each action
        txtAction.setText("");

        //always ready for type in the text box
        txtAction.requestFocus();

        //update the question in the label to match the current state
        updateQuestion();

        //repaint after each action
        repaint();
    }

    /**
     * Update the question in the label to match the current state
     */
    private void updateQuestion(){
        switch( currState ){
            case PLAY:
                lblQuestion.setText("Would you like to play WereWolfenstein 2D (y/n)? ");
                break;
            case DIFF:
                lblQuestion.setText("Enter your preferred difficulty (easy, normal or hard):");
                break;
            case ACTION:
                lblQuestion.setText("Please select an action by clicking buttons above.");
                break;
            case POS:
                if (chosenWalk){
                    lblQuestion.setText("Which area would you like to walk into?");
                } else {
                    lblQuestion.setText("Which area would you like to shoot into?");
                }
                break;
        }
    }


    /**
     * Print the title of the game
     */
    private void printGameTitle(){
        String output; //save output message
        output = "";
        output += "WereWolfenstein 2D\n";
        output += "==================\n";
        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * Print the explaination of the game
     */
    public void explain() {
        // very brief explain about the rules of the game
        String output; //save output message
        output = "";
        output += "\n";
        output += "You have to track down and destroy a supernatural wolf that is terrorizing your village.\n";
        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * assign the difficulty to the 'difficulty' variables
     * @param chosenDifficulty the string that user type to chose difficulty of the game
     */
    private void assignChosenDifficulty(String chosenDifficulty){
        try {
            difficulty = Difficulty.valueOf(chosenDifficulty.toUpperCase()); //assign the chosen difficulty to the 'difficulty' variable
        } catch (IllegalArgumentException ex){
            txtGameDetails.append("Invalid difficulty. Set to default: Normal\n");
            difficulty = Difficulty.NORMAL;
        }
    }

    /**
     * Create a new game, reset all the property variables of the game,
     * reset the saved moves, reset known village position to unknown
     */
    private void createNewGame(){
        //create new game with chosen difficulty
        gameWorld.newGame(difficulty);
        remainingBullets = difficulty.getSilverBulletCount();
        gameEnded = false;

        //reset saved moves
        savedMoves = new int[MAXSAVEDMOVE];
        countMoves = 0;

        //only for GUI, reset village pos to unknown
        if (difficulty == Difficulty.EASY){
            villagePos = gameWorld.getCurrentArea();
        } else {
            villagePos = UNKNOWNVILLAGEPOS;
        }
    }

    /**
     * Show difficulty settings
     */
    private void showDifficultySettings(){
        String output; //save output message
        output = "";
        output += "Your selected difficulty settings:\n";
        output += difficulty.toString() + '\n';
        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * Show all possible current game details on the screen
     * for player to make the next action
     */
    private void showCurrentGameDetails(){
        //single constant character for each direction
        final char N = 'n', E = 'e', S = 's', W = 'w';
        String output; //save the output to print
        output = "";
        //always show these information
        output += '\n';
        //show number of action left until dawn
        output += "There are " + gameWorld.getActionsUntilNight() + 
                            " actions until dawn arrives.\n";
        //show current village population
        output += "At last count there were " + 
                            gameWorld.getVillagePopulation() + 
                            " villagers remaining.\n";
        //show if near village
        if (gameWorld.isVillageNear()) {
            output += "You can hear sounds of village life nearby.\n";
        }


        //additional information show according to bitten or not
        if (gameWorld.werewolfNear() == Result.BITTEN){
            output += '\n';
            output += "Your bite has not been healed yet so you are not able to pay attention to your surroundings.\n";
        } else {  //show if not bitten
            output += '\n';
            //show current area
            output += "You are in area " + gameWorld.getCurrentArea() + ".\n";
            //show number of four connecting area
            output += "To your north is " + gameWorld.nextArea(N) + 
                                ", to your east is " + gameWorld.nextArea(E) + 
                                ", to your south is " + gameWorld.nextArea(S) + 
                                " and to your west is " + gameWorld.nextArea(W) + ".\n";
            //show remaining bullets and number of hitted shot
            output += "You have hit your target " + gameWorld.getShotCount() + 
                                " times and have " + remainingBullets + " bullets remaining.\n";

            //show if near the wolf
            if (gameWorld.werewolfNear() == Result.NEAR){
                output += '\n';
                output += "You can smell a wolf nearby.\n";
            }
        }

        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * Contains all the steps for the walk action:
     * Walk and show the results
     * Save the moves if player walk successfully 
     * @param chosenArea the area that player want to walk into
     */
    private void performWalk(int chosenArea){
        Result actionResult; // the result after walk to that area
        String output; //save the output to print
        output = "";

        txtGameDetails.setText(""); // reset textarea

        actionResult = gameWorld.tryWalk(chosenArea);
        //show the answer based on the result after trying to walk
        if (actionResult == Result.SUCCESS){
            output += "Walk successful but your target is still on the loose.\n";
        } else if (actionResult == Result.IMPOSSIBLE){
            output += "Sorry, that's not possible from your current location.\n";
        } else if (actionResult == Result.VILLAGE){
            output += "You walk into the village. They tend to any wounds you have suffered while away.\n";
            villagePos = chosenArea;
        } else if (actionResult == Result.BITTEN){
            output += "You got too close to the target, were bitten and are now delirious.\n";
        } else if (actionResult == Result.FAILURE) {
            output += "You got too close to the target again and were mortally wounded.\n";
            gameEnded = true;
        }

        trace(output);
        txtGameDetails.append(output);

        //save the move when successully moved
        if (actionResult != Result.IMPOSSIBLE) {
            addToSavedMoves(chosenArea);
        }
    }

    /**
     * Contains all steps for the shoot action:
     * Shoot to that area
     * reduce the number of bullets
     * Print the answer if the shoot success or not
     *
     * Check if out of bullets after the shoot
     * @param chosenArea the area that player want to shoot into
     */
    private void performShoot(int chosenArea){
        Result actionResult; // the result after shoot to that area
        String output; //save the output to print
        output = "";
       
        txtGameDetails.setText(""); // reset textarea

        actionResult = gameWorld.shoot(chosenArea);
        remainingBullets--; //reduce the number of bullets after the shot
        
        if (actionResult == Result.CAPTURED){
            output += "You hit the target! And you're able to capture it. Well done!\n";
            gameEnded = true;
        } else if (actionResult == Result.SUCCESS){
            output += "You hit the target!\n";
        } else if (actionResult == Result.VILLAGE){
            output += "Oops, you just shot wildly into the village! One more villager decides to leave...\n";
            villagePos = chosenArea;
            //check if there is any villager left
            if (gameWorld.getVillagePopulation() == 0){
                output += "...and it was the last one.\n";
                gameEnded = true;
            }
        } else if (actionResult == Result.IMPOSSIBLE){
            output += "Sorry, that's not possible from your current location.\n";
            remainingBullets++; //cannot shoot - do not reduce the number of bullet - gain 1 again
        } else if (actionResult == Result.FAILURE){
            output += "You fire a silver bullet but hit nothing but thin air.\n";
        }

        //check if out of bullets or not
        if (!gameEnded && remainingBullets == 0){
            output += "You're all out of bullets. With nothing to protect it, the fate of the village is now sealed.\n";
            gameEnded = true;
        }

        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * Reset the whole game with the same difficulty, 
     * reset the variables save properties of the game,
     * reset the saved moves,
     * reset known village position to unknown.
     */
    private void performReset(){
        txtGameDetails.setText(""); // reset textarea
        trace("Whole game reset!");
        txtGameDetails.append("Whole game reset!\n");
        gameWorld.reset();
        remainingBullets = difficulty.getSilverBulletCount();
        gameEnded = false;

        // reset the saved moves
        savedMoves = new int[MAXSAVEDMOVE];
        countMoves = 0;

        //only for GUI, reset village pos to unknown
        if (difficulty == Difficulty.EASY){
            villagePos = gameWorld.getCurrentArea();
        } else {
            villagePos = UNKNOWNVILLAGEPOS;
        }

        //show new game details, repaint new map
        showCurrentGameDetails();
        repaint();
    }

    /**
     * Quit the current game.
     */
    private void performQuit(){
        txtGameDetails.setText(""); // reset textarea
        trace("Quit selected.");
        txtGameDetails.append("Quit selected.\n");
        gameEnded = true;

        //game ended - show at most MAXSAVEDMOVE recent moves
        showSavedMoves();
        repaint();
    }

    /**
     * Show message if dawn happen,
     * check for any survival villager.
     */
    private void performDawn(){
        trace("");
        trace("Dawn arrives, another villager has left during the night.");
        txtGameDetails.append('\n' + "Dawn arrives, another villager has left during the night." + '\n');

        //check if there is any villager left
        if (gameWorld.getVillagePopulation() == 0){
            trace("");
            trace("Alas, the village is now empty. You have failed to protect them.");
            txtGameDetails.append('\n' + "Alas, the village is now empty. You have failed to protect them." + '\n');
            gameEnded = true;
        }
    }

    /**
     * Print at most last MAXSAVEDMOVE moves.
     */
    private void showSavedMoves(){
        String output; //save the output to print
        output = "";
        output += "\n" + countMoves + " move";

        //plural 'moves' if zero or more than one saved moves.
        if (countMoves != 1) {
            output += "s";
        }
        //print out the saved moves
        for (int i = 0; i < countMoves; i++) { //local variable i for iterator of the loop, point to each position in the array
            if (i == 0) {
                output += ": ";
            } else {
                output += ", ";
            }
            output += savedMoves[i];
        }
        output += ".\n";

        trace(output);
        txtGameDetails.append(output);
    }

    /**
     * Add one move to saved array,
     * call every time the player successfully walk
     * @param area the area that player walks into
     */
    private void addToSavedMoves(int area){
        if (countMoves < MAXSAVEDMOVE){
            savedMoves[countMoves] = area;
            countMoves++;
        } else { //already MAXSAVEDMOVE moves are saved
            //remove the oldest and shift all to the left.
            for (int i = 0; i < countMoves - 1; i++){ //local variable i for iterator of the loop, point to each position in the array
                savedMoves[i] = savedMoves[i + 1];
            }
            savedMoves[countMoves - 1] = area;
        }
    }
    
    /**
     * Paints the currently game details, including 3 bars, a map, and text when near village or wolf
     * @param g current Graphics variable
     */
    public void paintComponent(Graphics g) {
        final int LEFT = 75, TOP = 400; //default left and top position for drawing
        final int NIGHTLENGTH = 3; //total number action in night - just for drawing a 'night' bar
        final Color darkGreen = new Color(51, 204, 51); //darker green
        final int MAXHP = 2; //total of 3 states: good, bitten, dead
        final int HPWIDTH = 100, HPHEIGHT = 20, HPBORDER = 2; //width, height and thickness of the border when drawing a bar
        final int FONTSIZE = 30; //font size for drawing big string

        super.paintComponent(g);
        if (currState == State.ACTION || currState == State.POS){

            //draw health point
            if (gameWorld.werewolfNear() != Result.BITTEN){
                //good state
                drawHPBar(g, "Health", LEFT, TOP, HPWIDTH, HPHEIGHT, HPBORDER, 2, MAXHP);
            } else if (gameEnded && remainingBullets > 0 && gameWorld.getVillagePopulation() > 0){
                //dead state
                drawHPBar(g, "Health", LEFT, TOP, HPWIDTH, HPHEIGHT, HPBORDER, 0, MAXHP);
            } else {
                //bitten state
                drawHPBar(g, "Health", LEFT, TOP, HPWIDTH, HPHEIGHT, HPBORDER, 1, MAXHP);
            }
            
            //draw villager left
            drawHPBar(g, "Villagers", LEFT, TOP + 30, HPWIDTH, HPHEIGHT, HPBORDER, gameWorld.getVillagePopulation(), difficulty.getVillagerCount());

            //draw action until dawn - night length
            drawHPBar(g, "Night", LEFT, TOP + 60, HPWIDTH, HPHEIGHT, HPBORDER, gameWorld.getActionsUntilNight(), NIGHTLENGTH);

            //draw bullets count, only draw if not bitten
            if (gameWorld.werewolfNear() != Result.BITTEN){
                drawHPBar(g, "Bullets", LEFT, TOP + 90, HPWIDTH, HPHEIGHT, HPBORDER, remainingBullets, difficulty.getSilverBulletCount());
            }
            
            drawMap(g, Color.ORANGE); //draw a map
            //show if near village
            if (gameWorld.isVillageNear()){
                drawBigString(g, "  Village >", FONTSIZE, darkGreen, LEFT + 250, TOP + 20);
            }
            //show if near wolf
            if (gameWorld.werewolfNear() == Result.NEAR){
                drawBigString(g, "Wolf Nearby", FONTSIZE, Color.RED, LEFT + 250, TOP + 70);            
            }
        }
    }

    /**
     * Draw a map with 3*3 small square, show village position if found,
     * current play position.
     *
     * Automatically save and update the position of the village 
     * if player walk / shoot into (show as green box)
     * 
     * Current position show as blue box
     * 
     * @param g current graphics component
     * @param c default color for each small square (position)
     */
    private void drawMap(Graphics g, Color c){
        final int SIZE = 30, SPACE = 2, LEFT = 200, TOP = 400; //final variable for size of each square, space, left and top position
        final int NUMCELL = 9; //number of cell in the map
        final int CELLEACHROW = 3; //number of cell each row
        //change left and top for each square in mapthe 
        int left = LEFT, top = TOP;
        for (int i = 0; i < NUMCELL; i++){ //local variable i is the iterator of the loop
            if (i > 0 && i % CELLEACHROW == 0){ //if already 3 cells in a row, draw in next row
                left = LEFT;
                top += (SIZE + SPACE);
            }

            //set the background color for each box 
            if (i == villagePos){
                g.setColor(Color.GREEN);
            } else if (i == gameWorld.getCurrentArea()) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(c);
            }

            //if the player is bitten, do not show current area (change blue box to default color)
            if (gameWorld.werewolfNear() == Result.BITTEN){
                if (g.getColor() == Color.BLUE){
                    g.setColor(c);
                }
            }

            g.fillRect(left, top, SIZE, SIZE); //draw one cell
            
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), left + 5, top + 15); //write a label for each cell

            left += SPACE + SIZE; //going to next cell in same row
        }
    }

    /**
     * Draw a HP bar, change color when low HP, with black border, white background
     * @param g        current graphics
     * @param label    Name of the bar
     * @param hpLeft   Left position
     * @param hpTop    Top position
     * @param hpW      Width
     * @param hpH      Height
     * @param hpBorder thickness of the border
     * @param curr     current point
     * @param total    total point
     */
    private void drawHPBar(Graphics g, String label, int hpLeft, int hpTop, int hpW, int hpH, int hpBorder, int curr, int total){
        final double GREENLVL = 75, YELLOWLVL = 50, ORANGELVL = 25; //percentage level for each color change in the bar
        double percentage; //percentage of hp left
        Color c; //color for the bar

        
        g.setColor(Color.BLACK); 
        g.drawString(label, hpLeft - 65, hpTop + 15); //write a black label
        g.fillRect(hpLeft - hpBorder, hpTop - hpBorder, hpW + hpBorder * 2, hpH + hpBorder * 2); //draw the border
        
        g.setColor(Color.WHITE);
        g.fillRect(hpLeft, hpTop, hpW, hpH); //draw the white background
        
        //get color based on percentage
        percentage = (double) curr / total * 100.0;
        if (percentage > GREENLVL) {
            c = Color.GREEN;
        } else if (percentage > YELLOWLVL) {
            c = Color.YELLOW;
        } else if (percentage > ORANGELVL) {
            c = Color.ORANGE;
        } else {
            c = Color.RED;
        }

        //draw the bar with chosen color
        g.setColor(c);
        g.fillRect(hpLeft, hpTop, (int)(percentage / 100.0 * hpW), hpH);
    }

    /**
     * Draw a big string to notify village or wolf is near
     * @param g     current graphics use to draw
     * @param text  the text we want to draw
     * @param fsize font size of the text
     * @param c     color of the text
     * @param left  left position
     * @param top   top position
     */
    private void drawBigString(Graphics g, String text, int fsize, Color c, int left, int top){
        Font f; //create new bigger font

        f = new Font("Calibri", Font.BOLD, fsize); 
        g.setFont(f);
        g.setColor(c);
        g.drawString(text, left, top);
    }

    /**
     * Turn on or off debug messages
     * @param onOff set to on or off
     */
    public void setTracing(boolean onOff) {
        tracing = onOff;
    }

    /**
     * Print debug message when debugging is on
     * @param message the message that developer want to show
     */
    public void trace(String message) {
        if (tracing) {
            System.out.println("WereWolfenstein2D: " + message);
        }
    }
    
}
