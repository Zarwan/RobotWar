package robot_war_summative;

import java.awt.Color;
import becker.robots.City;
import becker.robots.Direction;

/**
 * Creates an artificially intelligent robot that will fight others in an arena
 * @author Zarwan Hashem
 * @version June 14, 2014
 */
public class AttackFighterRobot extends FighterRobot {

	//Constants
	private final int NUMPLAYERS = BattleManager.NUM_PLAYERS;
	private static final int ATTACK = 6;
	private static final int DEFENCE = 1;
	private final static int NUMMOVES = 3;
	private final int MINMOVINGENERGY = 20;
	private final int ENERGYPERSTEP = 5;

	private HashemOppData [] playerData = new HashemOppData [this.NUMPLAYERS];
	private int health, id, energy;
	private int movesTaken = 0;
	private int idToAttack = 0;
	private boolean attacker = false;

	/**
	 * Constructs and initializes a robot and the variables that it uses
	 * @param c - The city the robot will be placed in
	 * @param a - The robot's initial avenue (y)
	 * @param s - The robot's initial street (x)
	 * @param d - The robot's initial direction
	 * @param id - The ID number of the robot
	 * @param MAX_HEALTH - The initial health of the robot
	 */
	public AttackFighterRobot(City c, int a, int s, Direction d, int id, int MAX_HEALTH) {
		super(c, a, s, d, id, ATTACK, DEFENCE, NUMMOVES);
		this.health = MAX_HEALTH;
		this.id = id;
		this.setLabel();

		//Initializes the OppData that the robot will use to store information
		for (int i = 0; i < this.NUMPLAYERS; i++) {
			this.playerData[i] = new HashemOppData (i, 0, 0, 0, 0);
		}
	}

	/**
	 * Labels (puts text on) the robot and controls its colour
	 */
	public void setLabel () {

		String label = String.valueOf(this.id + ": " + this.health);

		//Sets the robot's colour based on its health
		if (this.health <= 0) {
			super.setColor(Color.BLACK);
		}
		else {
			super.setColor(Color.RED);
		}

		super.setLabel(label); //Updates the robot's label
	}



	/**
	 * Moves the robot to the location given
	 * @param x - The robot's new x position
	 * @param y - the Robot's new y position
	 */
	public void goToLocation (int x, int y) {

		//Turns the robot to face the x direction that it has to move in
		if (x < super.getAvenue()) {
			this.turnWest();
		}

		else if (x > super.getAvenue()) {
			this.turnEast();
		}

		//Moves the robot to the specified avenue (x position)
		while (super.getAvenue() != x) {
			super.move();
		}

		//Turns the robot to face the y direction that it has to move in
		if (y < super.getStreet()) {
			this.turnNorth();
		}

		else if (y > super.getStreet()) {
			this.turnSouth();
		}

		//Moves the robot to the specified street (y position)
		while (super.getStreet() != y) {
			super.move();
		}
	}

	/**
	 * Turns the robot south
	 */
	private void turnSouth() {

		if (super.isFacingEast()) {
			super.turnRight();
		}
		else if (super.isFacingWest()) {
			super.turnLeft();
		}
		else if (super.isFacingNorth()) {
			super.turnAround();
		}
	}

	/**
	 * Turns the robot north
	 */
	private void turnNorth() {

		if (super.isFacingEast()) {
			super.turnLeft();
		}
		else if (super.isFacingWest()) {
			super.turnRight();
		}
		else if (super.isFacingSouth()) {
			super.turnAround();
		}
	}

	/**
	 * Turns robot east
	 */
	private void turnEast() {

		if (super.isFacingWest()) {
			super.turnAround();
		}
		else if (super.isFacingSouth()) {
			super.turnLeft();
		}
		else if (super.isFacingNorth()) {
			super.turnRight();
		}
	}

	/**
	 * Turns the robot west
	 */
	private void turnWest() {

		if (super.isFacingEast()) {
			super.turnAround();
		}
		else if (super.isFacingSouth()) {
			super.turnRight();
		}
		else if (super.isFacingNorth()) {
			super.turnLeft();
		}
	}



	/**
	 * Tells the battle manager what the robot wants to do
	 * @param energy - The robot's current energy
	 * @param playerData - The array of OppData that contains information about all of the robots
	 */
	public TurnRequest takeTurn (int energy, OppData [] playerData) {

		int avenueRequest;
		int streetRequest;
		TurnRequest turnRequest;

		//Updates the robot's copy of OppData
		for (int i = 0; i < this.playerData.length; i++) {
			this.playerData[i].setAvenue(playerData[i].getAvenue());
			this.playerData[i].setStreet(playerData[i].getStreet());
			this.playerData[i].setHealth(playerData[i].getHealth());

			//Updates the robot's own information
			if (i == this.id) {
				this.playerData[i].setEnergy(energy);
				this.health = this.playerData[i].getHealth();
				this.energy = this.playerData[i].getEnergy();
			}
		}

		setLabel(); //Updates the label on the robot

		this.idToAttack = calcIDToAttackByDistance(); //Calculate which robot to target

		//Calculate which avenue and street to move to based on the target
		avenueRequest = avenueRequestCalc(this.idToAttack);
		streetRequest = streetRequestCalc(this.idToAttack);
		this.movesTaken = 0; //Reset moves counter

		//Create turn request based on if the robot should start a battle or not
		if (avenueRequest == playerData[this.idToAttack].getAvenue() && streetRequest == playerData[idToAttack].getStreet()) {
			turnRequest = new TurnRequest (avenueRequest, streetRequest, this.idToAttack, this.ATTACK);
			this.attacker = true;
		}
		else {
			turnRequest = new TurnRequest (avenueRequest, streetRequest, -1, 0);
		}

		return turnRequest;
	}



	/**
	 * Calculates which robot is closest to the player
	 * @return the ID of the closest robot
	 */
	private int calcIDToAttackByDistance() {
		int closestID = this.id; 

		//Calculates the distance from every robot to the player
		for (int i = 0; i < this.playerData.length; i++) {

			//Checks if the current robot is a valid target
			if (i != this.id && this.playerData[i].getHealth() > 0) {
				this.playerData[i].setDistanceFromPlayer(super.getAvenue(), super.getStreet());
			}

			//Makes sure than robots that aren't valid are not considered
			else {
				this.playerData[i].setDistanceFromPlayer(10000, 10000);
			}
		}

		//Finds the closest robot ID
		for (int i = 0; i < this.playerData.length; i++) {
			if (this.playerData[i].getDistanceFromPlayer() < this.playerData[closestID].getDistanceFromPlayer()) {
				closestID = i;
			}
		}

		return closestID; 
	}



	/** Calculates which avenue to go to to get closer to the target robot
	 * @param idToAttack - The robot ID to get close to
	 * @return - The avenue to move to
	 */
	private int avenueRequestCalc(int idToAttack) {
		int avenue = super.getAvenue(); //Keeps track of the robot's new avenue request


		//Keeps moving unless the robot has reached its move limit, is low on energy, or not met conditions
		//in the while loop
		while (this.movesTaken < this.NUMMOVES && this.energy > this.MINMOVINGENERGY) {

			//Checks which way the robot has to move to get closer to the target's avenue
			if (avenue < this.playerData[idToAttack].getAvenue() && avenue != BattleManager.WIDTH-1) {
				avenue++;
				this.movesTaken++;
				this.energy -= this.ENERGYPERSTEP;
			}
			else if (avenue > this.playerData[idToAttack].getAvenue() && avenue != 0){
				avenue--;
				this.movesTaken++;
				this.energy -= this.ENERGYPERSTEP;
			}
			else {
				break;
			}
		}
		return avenue;
	}

	/**
	 * Calculates which street to go to to get closer to the target robot
	 * @param idToAttack - The robot ID to get close to
	 * @return - The street to move to
	 */
	private int streetRequestCalc(int idToAttack) {
		int street = super.getStreet(); //Keeps track of the robot's new street request

		//Keeps moving unless the robot has reached its move limit, is low on energy, or not met conditions
		//in the while loop
		while (this.movesTaken < this.NUMMOVES && this.energy > this.MINMOVINGENERGY) {

			//Checks which way the robot has to move to get closer to the target's street
			if (street < this.playerData[idToAttack].getStreet() && street != BattleManager.HEIGHT-1) {
				street++;
				this.movesTaken++;
				this.energy -= this.ENERGYPERSTEP;
			}
			else if (street > this.playerData[idToAttack].getStreet() && street != 0){
				street--;
				this.movesTaken++;
				this.energy -= this.ENERGYPERSTEP;
			}
			else {
				break;
			}
		}
		return street;
	}



	/**
	 * Saves the results of the player's battles
	 * @param myHealthLost - The health that the player has lost in the battle
	 * @param opponentID - The ID of the robot fought
	 * @param opponentHealthLost - The health that the player's opponent lost in the battle
	 */
	public void battleResult (int myHealthLost, int opponentID, int opponentHealthLost) {


		//Checks if the player is the attacker or defender
		if (this.attacker = true && this.id >= 0) {
			this.attacker = false;

			//Checks who won the battle and updates the appropriate stats
			if (myHealthLost < opponentHealthLost) {
				this.playerData[this.id].addFightsInitiatedWin();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsDefendLoss();
				}
			}
			else if (myHealthLost > opponentHealthLost) {
				this.playerData[this.id].addFightsInitiatedLoss();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsDefendWin();
				}
			}
			else {
				this.playerData[this.id].addFightsInitiatedTie();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsDefendTie();
				}
			}
		}

		//Updates defender stats
		else {

			//Checks who won the battle and updates the appropriate stats
			if (myHealthLost < opponentHealthLost) {
				this.playerData[this.id].addFightsDefendWin();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsInitiatedLoss();
				}
			}
			else if (myHealthLost > opponentHealthLost) {
				this.playerData[this.id].addFightsDefendLoss();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsInitiatedWin();
				}
			}
			else {
				this.playerData[this.id].addFightsDefendTie();

				//Checks if the opponent is dead (Thus has an invalid ID and can't be updated)
				if (opponentID >= 0) {
					this.playerData[opponentID].addFightsInitiatedTie();
				}
			}
		}
		this.health -= myHealthLost; //Updates the player's health
	}

}


