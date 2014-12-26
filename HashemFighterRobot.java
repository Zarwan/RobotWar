package robot_war_summative;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import becker.robots.City;
import becker.robots.Direction;

/**
 * Creates an artificially intelligent robot that will fight others in an arena
 * @author Zarwan Hashem
 * @version December 26, 2014
 */
public class HashemFighterRobot extends FighterRobot {
	
	//Constants
	private final int NUMPLAYERS = BattleManager.NUM_PLAYERS;
	private static final int ATTACK = 5;
	private static final int DEFENCE = 3;
	private static final int NUMMOVES = 2;
	private final int ENERGYPERSTEP = 5;
	private final int HIGHPRIORITY = 10;
	private final int MINATTACKINGHEALTH = 50;
	private final int MINATTACKINGENERGY = 30;
	private final int MINRUNNINGENERGY = 10;
	private final int ATTACKRANGE = 3;
	private final int MINPLAYERS = 2;
	private final int MAXDISTANCE = 1000;

	private HashemOppData [] playerData = new HashemOppData [this.NUMPLAYERS];
	private int [] playerPriority = new int [this.NUMPLAYERS];
	private int health, id, energy;
	private int movesTaken = 0;
	private int idPriority = 0;
	private int minMovingEnergy = 50;
	private boolean attacker = false;
	private boolean defence = true;
	private int playersLeft = BattleManager.NUM_PLAYERS;
	private int deadPlayers = 0;

	/**
	 * Constructs and initializes a robot and the variables that it uses
	 * @param c - The city the robot will be placed in
	 * @param a - The robot's initial avenue (y)
	 * @param s - The robot's initial street (x)
	 * @param d - The robot's initial direction
	 * @param id - The ID number of the robot
	 * @param MAX_HEALTH - The initial health of the robot
	 */
	public HashemFighterRobot(City c, int a, int s, Direction d, int id, int MAX_HEALTH) {
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
			super.setColor(Color.CYAN);
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

		this.deadPlayers = 0; //Resets number of dead robots

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
			
			//Keeps track of how many players are dead
			if (this.playerData[i].getHealth() <= 0) {
				this.deadPlayers++;
			}
			
			//Calculates the distance from each robot to the player
			if (i != this.id && this.playerData[i].getHealth() > 0) {
				this.playerData[i].setDistanceFromPlayer(super.getAvenue(), super.getStreet());
			}
			//Prevents the player itself and dead players from being involved in calculations
			else {
				this.playerData[i].setDistanceFromPlayer(this.MAXDISTANCE, this.MAXDISTANCE);
			}
			
			this.playerPriority[i] = 0; //Resets player priority
		}
		
		this.playersLeft = this.NUMPLAYERS - this.deadPlayers;

		this.setLabel(); //Updates the label on the robot
		
		//Calculate which robot to target
		this.calcIDByDistance();
		this.calcIDByHistory();
		this.calcIDByHealth();
		this.idPriority = this.calcIDByPriority();

		//Calculate if the robot should be defensive or offensive and update energy accordingly
		if ((this.playerData[this.idPriority].getDistanceFromPlayer() <= this.ATTACKRANGE && this.health >= this.MINATTACKINGHEALTH) || this.playersLeft <= this.MINPLAYERS) {
			this.defence = false;
			this.minMovingEnergy = this.MINATTACKINGENERGY;
		}
		else {
			this.defence = true;
			this.minMovingEnergy = this.MINRUNNINGENERGY;
		}

		//Calculate which avenue and street to move to based on the target and the robot's defensive/offensive state
		avenueRequest = this.avenueRequestCalc(this.idPriority, this.defence);
		streetRequest = this.streetRequestCalc(this.idPriority, this.defence);
		this.movesTaken = 0; //Reset moves counter

		//Create turn request based on if the robot should start a battle or not
		if (avenueRequest == this.playerData[this.idPriority].getAvenue() && streetRequest == this.playerData[this.idPriority].getStreet() && this.defence == false) {
			turnRequest = new TurnRequest (avenueRequest, streetRequest, this.idPriority, this.ATTACK);
			this.attacker = true;
		}
		else {
			turnRequest = new TurnRequest (avenueRequest, streetRequest, -1, 0);
		}

		return turnRequest;
	}

	
	
	/**
	 * Sort the robot's HashemOppData from lowest to highest based on a specific property using insertion sort
	 * @param playerData - The array of information
	 * @param propertyMethod - What to sort the data by: getID, getDistanceFromPlayer, getHealth,
	 * getFightsDefendWin
	 * @return - The sorted array of information
	 */
	private HashemOppData [] sortData(HashemOppData[] playerData, String propertyMethod) {
		
		HashemOppData currData = new HashemOppData (0, 0, 0, 0, 0);
		int index = 1;
		boolean swap;


		//Moves the sorted marker through the entire array
		for (int i = 1; i < playerData.length; i++) {
			swap = false;
			currData = playerData[i];

			//Catches the program if propertyMethod does not exist or behave as expected
			try {
				
				//Moves elements down until the proper position for the current one is found
				//The loop condition compares the HashemOppData based on the property that the method
				//propertyMethod returns. This determines how the array will be sorted.
				for (int k = i; k >= 0 && 
				((Integer)(currData.getClass().getMethod(propertyMethod).invoke(currData))) <= 
				 ((Integer)(playerData[k].getClass().getMethod(propertyMethod).invoke(playerData[k]))); k--) {

					//Checks if the element belongs in the first slot or not
					if (k != 0) {
						playerData[k] = playerData[k - 1];
					}
					index = k;
					swap = true;	
				}
			} catch (Exception e) {
				System.out.println(e);
			}

			//Places the element at the new index if other elements where shifted down
			if (swap == true) {
				playerData[index] = currData;
			}
		}	

		return playerData;  //Returns the sorted array
	}
	
	
	/**
	 * Calculates which robot ID to target based on which one is closest, has the lowest health, and has the
	 * least number of defensive wins
	 */
	private int calcIDByPriority() {
		this.playerData = this.sortData(this.playerData, "getID"); //Sorts the OppData by ID number so that the index can be used as the ID
		
		int idToTarget = 0;
		
		//Local variable initialized
		if (this.id != 0) {
			idToTarget = 0;
		}
		else {
			idToTarget = 1;
		}
		
		//Further calculates the initial ID to attack so that it is a valid one (In case the calculations
		//below end up not affecting the ID to attack and the initial one is used as a result)
		for (int i = 0; i < this.NUMPLAYERS; i++) {
			
			//Checks if the current ID is a valid one to target
			if (i != this.id && this.playerData[i].getHealth() > 0) {
				idToTarget = i;
				break;
			}
		}

		//Calculates which ID to attack based on which one has the highest priority value
		for (int i = 0; i < this.playerPriority.length; i++) {
			
			//Keeps track of the highest priority value
			if ((this.playerPriority[i] >= this.playerPriority[idToTarget]) && i != this.id && this.playerData[i].getHealth() > 0) {
				idToTarget = i;
			}
		}

		return idToTarget; //Returns the ID to target
	}

	/**
	 * Calculates which robot has the lowest health
	 * @return the ID of the robot with the lowest health
	 */
	private void calcIDByHealth() {
		this.playerData = this.sortData(this.playerData, "getHealth"); //Sorts OppData based on health
		
		int lowestHealthID = 0; //Local variable

		//Calculates which valid robot has the lowest health
		for (int i = 0; i < this.playerData.length; i++) {
			
			//Checks if the robot is a valid target
			if (this.playerData[i].getID() != this.id && this.playerData[i].getHealth() > 0) {
				lowestHealthID = this.playerData[i].getID();
				break;
			}
		}

		this.playerPriority[lowestHealthID]++; //Increases the target priority of the robot with the lowest health
		
		//Further increases the priority if the robot has extremely low health and is within range
		if (this.playerData[lowestHealthID].getHealth() <= this.ATTACK && this.playerData[lowestHealthID].getDistanceFromPlayer() <= this.ATTACKRANGE) {
			this.playerPriority[lowestHealthID] += this.HIGHPRIORITY;
		}
	}

	/**
	 * Calculates which robot is closest to the player
	 */
	private void calcIDByDistance() {
		this.playerData = this.sortData(this.playerData, "getDistanceFromPlayer"); //Sorts OppData based on distance from the player
		
		int closestID = 0; //Local variable

		//Calculates which valid robot is closest to the player
		for (int i = 0; i < this.playerData.length; i++) {
			
			//Checks if the robot is a valid target
			if (this.playerData[i].getID() != this.id && this.playerData[i].getHealth() > 0) {
				closestID = this.playerData[i].getID();
				break;
			}
		}
		this.playerPriority[closestID] += 2; //Updates the closet robot's priority
	}

	/**
	 * Calculates which robot has won the least number of battles as a defender
	 */
	private void calcIDByHistory() {
		this.playerData = this.sortData(this.playerData, "getFightsDefendWin"); //Sorts OppData based on number of defensive battle wins

		int idWithWorstHistory = 0; //Local variable

		//Calculates which valid robot has won the least number of battles as a defender
		for (int i = 0; i < this.playerData.length; i++) {
			
			//Checks if the robot is a valid target
			if (this.playerData[i].getID() != this.id && this.playerData[i].getHealth() > 0) {
				idWithWorstHistory = this.playerData[i].getID();
				break;
			}
		}

		this.playerPriority[idWithWorstHistory]++; //Updates the target's priority
	}



	/**
	 * Calculates which avenue to go to to get closer to/farther from the target robot
	 * @param idPriority - The robot ID to get close to/far from
	 * @param defence - True if the robot is on the defensive, false otherwise
	 * @return - The avenue to move to
	 */
	private int avenueRequestCalc(int idPriority, boolean defence) {
		int avenue = super.getAvenue(); //Local variable keeps track of the robot's new avenue request

		//Keeps moving unless the robot has reached its move limit, is low on energy, or not met conditions
		//in the while loop
		while (this.movesTaken < this.NUMMOVES && this.energy > this.minMovingEnergy) {
			
			//Gets closer to the target if the robot is not on the defensive
			if (this.defence == false) {
				
				//Checks which way the robot has to move to get closer to the target's avenue
				if (avenue < this.playerData[this.idPriority].getAvenue() && avenue != BattleManager.WIDTH-1) {
					avenue++;
					this.movesTaken++;
					this.energy -= this.ENERGYPERSTEP;
				}
				else if (avenue > this.playerData[this.idPriority].getAvenue() && avenue != 0){
					avenue--;
					this.movesTaken++;
					this.energy -= this.ENERGYPERSTEP;
				}
				else {
					break;
				}
			}
			
			//Flees or stays where it is if the robot is on the defensive
			else {
				
				//Checks if the target is close enough that the robot must flee
				if (this.playerData[this.idPriority].getDistanceFromPlayer() <= this.ATTACKRANGE) {
					
					//Checks which way the robot has to move to get farther from the target's avenue
					if (avenue < this.playerData[this.idPriority].getAvenue() && avenue != 0) {
						avenue--;
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else if (avenue > this.playerData[this.idPriority].getAvenue() && avenue != BattleManager.WIDTH-1){
						avenue++;
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else if (avenue == this.playerData[this.idPriority].getAvenue()) {
						
						//Prevents the robot from getting stuck at the edge of the arena
						if (avenue != BattleManager.WIDTH-1) {
							avenue++;
						}
						else {
							avenue--;
						}
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
		}
		return avenue; //Returns the avenue that the robot must go to
	}

	/**
	 * Calculates which street to go to to get closer to/farther from the target robot
	 * @param idPriority - The robot ID to get close to/far from
	 * @param defence - True if the robot is on the defensive, false otherwise
	 * @return - The street to move to
	 */
	private int streetRequestCalc(int idPriority, boolean defence) {
		int street = super.getStreet(); //Local variable keeps track of the robot's new street request

		//Keeps moving unless the robot has reached its move limit, is low on energy, or not met conditions
		//in the while loop
		while (this.movesTaken < this.NUMMOVES && this.energy > this.minMovingEnergy) {
			
			//Gets closer to the target if the robot is not on the defensive
			if (this.defence == false) {
				
				//Checks which way the robot has to move to get closer to the target's street
				if (street < this.playerData[this.idPriority].getStreet() && street != BattleManager.HEIGHT-1) {
					street++;
					this.movesTaken++;
					this.energy -= this.ENERGYPERSTEP;
				}
				else if (street > this.playerData[this.idPriority].getStreet() && street != 0){
					street--;
					this.movesTaken++;
					this.energy -= this.ENERGYPERSTEP;
				}
				else {
					break;
				}
			}
			
			//Flees or stays where it is if the robot is on the defensive
			else {
				
				//Checks if the target is close enough that the player has to flee
				if (this.playerData[this.idPriority].getDistanceFromPlayer() <= this.ATTACKRANGE) {
					
					//Checks which way the robot has to move
					if (street < this.playerData[this.idPriority].getStreet() && street != 0) {
						street--;
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else if (street > this.playerData[this.idPriority].getStreet() && street != BattleManager.HEIGHT-1){
						street++;
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else if (street == this.playerData[this.idPriority].getStreet()) {
						
						//Prevents the robot from getting stuck at the edge of the arena
						if (street != BattleManager.HEIGHT-1) {
							street++;
						}
						else {
							street--;
						}
						this.movesTaken++;
						this.energy -= this.ENERGYPERSTEP;
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
		}
		return street; //Returns the street that the player will move to
	}



	/**
	 * Saves the results of the player's battles
	 * @param myHealthLost - The health that the player has lost in the battle
	 * @param opponentID - The ID of the robot fought
	 * @param opponentHealthLost - The health that the player's opponent lost in the battle
	 */
	public void battleResult (int myHealthLost, int opponentID, int opponentHealthLost) {
		this.playerData = sortData(this.playerData, "getID"); //Sorts OppData based on robot ID
		
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

		//Checks if the robot should be changed from defensive/offensive
		if (this.health > this.MINATTACKINGHEALTH || this.playersLeft <= this.MINPLAYERS) {
			this.defence = false;
		}
		else if (opponentID >= 0) {
			if (this.playerData[this.id].getHealth() > this.playerData[opponentID].getHealth() - this.ATTACK*2) {
				this.defence = false;
			}
		}
		else {
			this.defence = true;
		}

	}

}
