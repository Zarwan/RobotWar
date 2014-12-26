package robot_war_summative;

/**
 * Stores additional information on robots in the battle (Extends OppData)
 * @author Zarwan Hashem
 * @version June 14, 2014
 */
public class HashemOppData extends OppData {
	
	private int energy;
	private int distanceFromPlayer;
	private int fightsInitiatedWin;
	private int fightsInitiatedLoss;
	private int fightsInitiatedTie;
	private int fightsDefendWin;
	private int fightsDefendLoss;
	private int fightsDefendTie;
	
	/**
	 * Constructs and initializes a HashemOppData object for a single robot
	 * @param id - The ID of the robot
	 * @param a - The current avenue of the robot
	 * @param s - The current street of the robot
	 * @param health - The current health of the robot
	 * @param energy - The current energy of the robot
	 */
	public HashemOppData(int id, int a, int s, int health, int energy) {
		super(id, a, s, health);
		this.energy = energy;
		this.fightsInitiatedWin = 0;
		this.fightsInitiatedLoss = 0;
		this.fightsInitiatedTie = 0;
		this.fightsDefendWin = 0;
		this.fightsDefendLoss = 0;
		this.fightsDefendTie = 0;
	}
	
	/**
	 * Updates the robot's energy
	 * @param energy - The energy of the robot
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
	/**
	 * Returns the robot's energy
	 * @return the robot's energy
	 */
	public int getEnergy() {
		return this.energy;
	}
	
	
	/**
	 * Calculates and saves the distance from the robot to the player
	 * @param a - The avenue of the player
	 * @param s - The street of the player
	 */
	public void setDistanceFromPlayer (int a, int s) {
		this.distanceFromPlayer = Math.abs(super.getAvenue() - a);
		this.distanceFromPlayer += Math.abs(super.getStreet() - s);
	}
	
	/**
	 * Returns the distance from the robot to the player
	 * @return - The distance from the robot to the player
	 */
	public int getDistanceFromPlayer () {
		return this.distanceFromPlayer;
	}
	
	/**
	 * Returns the number of fights the robot has won as an attacker
	 * @return the number of fights the robot has won as an attacker
	 */
	public int getFightsInitiatedWin() {
		return this.fightsInitiatedWin;
	}
	
	/**
	 * Returns the number of fights the robot has lost as an attacker
	 * @return the number of fights the robot has lost as an attacker
	 */
	public int getFightsInitiatedLoss() {
		return this.fightsInitiatedLoss;
	}
	
	/**
	 * Returns the number of fights the robot has tied as an attacker
	 * @return the number of fights the robot has tied as an attacker
	 */
	public int getFightsInitiatedTie() {
		return this.fightsInitiatedTie;
	}
	
	/**
	 * Returns the number of fights the robot has won as a defender
	 * @return the number of fights the robot has won as a defender
	 */
	public int getFightsDefendWin() {
		return this.fightsDefendWin;
	}
	
	/**
	 * Returns the number of fights the robot has lost as a defender
	 * @return the number of fights the robot has lost as a defender
	 */
	public int getFightsDefendLoss() {
		return this.fightsDefendLoss;
	}
	
	/**
	 * Returns the number of fights the robot has tied as a defender
	 * @return the number of fights the robot has tied as a defender
	 */
	public int getFightsDefendTie()	{
		return this.fightsDefendTie;
	}
	
	/**
	 * Updates the number of fights the robot has won as an attacker
	 */
	public void addFightsInitiatedWin() {
		this.fightsInitiatedWin ++;
	}

	/**
	 * Updates the number of fights the robot has lost as an attacker
	 */
	public void addFightsInitiatedLoss() {
		this.fightsInitiatedLoss ++;
	}

	/**
	 * Updates the number of fights the robot has tied as an attacker
	 */
	public void addFightsInitiatedTie()	{
		this.fightsInitiatedTie ++;
	}

	/**
	 * Updates the number of fights the robot has won as a defender
	 */
	public void addFightsDefendWin() {
		this.fightsDefendWin ++;
	}

	/**
	 * Updates the number of fights the robot has lost as a defender
	 */
	public void addFightsDefendLoss() {
		this.fightsDefendLoss ++;
	}

	/**
	 * Updates the number of fights the robot has tied as a defender
	 */
	public void addFightsDefendTie() {
		this.fightsDefendTie ++;
	}

}
