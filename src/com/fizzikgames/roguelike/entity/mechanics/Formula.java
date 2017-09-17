package com.fizzikgames.roguelike.entity.mechanics;

/**
 * Contains global useful formulas
 * @author Maxim Tiourin
 * @version 1.00
 */
public class Formula {
	/**
	 * output = 0;
	 * foreach i = 1, i < currentLevel
	 * {
	 * 		output += baseIncrease + E ^ (multiplier * (i / maxLevel));
	 * }
	 * return floor(output);
	 */
	public static int levelExponentialScalingValue(double multiplier, double baseIncrease, int currentLevel, int maxLevel) {
		double output = 0;
		for (int i = 1; i < currentLevel; i++) {
			double formula = baseIncrease + (Math.pow(Math.E, multiplier * ((double) i / (double) maxLevel)));
			output += formula;
		}
		
		return (int) Math.floor(output);
	}
	
	/**
	 * Returns the experience needed to advance to the next level given the current level.
	 * Will Scale experience from lower bound to upper bound exponentially, given a maximum level.
	 */
	public static int levelExperienceScalingValue(double xpBase, double xpMax, int currentLevel, int maxLevel) {
		final double B = Math.log(xpMax / xpBase) / (double) (maxLevel - 1);
	    final double A = xpBase / (Math.exp(B) - 1.0);
	    
	    final int oldxp = (int) Math.round(A * Math.exp(B * (currentLevel - 1)));
	    final int newxp = (int) Math.round(A * Math.exp(B * currentLevel));
	    
	    return newxp - oldxp;
	}
	
	/**
	 * Returns n (n == input) when scale == input.
	 * Returns an increasingly larger n (n > input) when scale > input as input goes to -infinity.
	 * Returns an increasingly smaller n (n < input) when scale < input as input goes to infinity.
	 * Sample Table:
	 * 		input	|	output with scale = 5
	 * 		---------------------------------
	 * 			5	|			5
	 *  		10	|			7.807
	 * 			15  |			10
	 * 			20	|			11.861
	 * 
	 * 		input	|	output with scale = 15
	 * 		---------------------------------
	 * 			5	|			6.861
	 *  		10	|			11.374
	 * 			15  |			15
	 * 			20	|			18.117
	 */
	public static double basicDiminishingReturns(double input, double scale) {
	    if (input < 0) {
	    	return -basicDiminishingReturns(-input, scale);
	    }
	    
	    double mult = input / scale;
	    double trinum = (Math.sqrt(8.0 * mult + 1.0) - 1.0) / 2.0;
	    
	    return trinum * scale;
	}
	
	/**
	 * Returns input if input < softCap.
	 * Returns Max(softCap, basicDiminishingReturns(input, softCapScale)) if diminishes below hardCap.
	 * Returns Max(hardCap, basicDiminishingReturns(input, hardCapScale)) if diminishes below or at hardCap.
	 * Will constrain lower bound to minValue.
	 * Will constrain upper bound to maxValue.
	 */
	public static double complexDiminishingReturns(double input, double softCap, double hardCap, 
			double softCapScale, double hardCapScale, double minValue, double maxValue) {
		double min = minValue;
		double max = maxValue;
		
		if (input >= softCap) {
			double newvalue = basicDiminishingReturns(input, softCapScale);
			
			if (newvalue >= hardCap) {
				newvalue = basicDiminishingReturns(input, hardCapScale);
				
				return Math.max(hardCap, Math.min(newvalue, max));
			}
			else {
				return Math.max(softCap, Math.min(newvalue, max));
			}
		}
		else {
			return Math.max(min, Math.min(input, max));
		}
	}
	
	/**
	 * Returns input if input < softCap.
	 * Returns Max(softCap, basicDiminishingReturns(input, softCapScale)) if diminishes below hardCap.
	 * Returns Max(hardCap, basicDiminishingReturns(input, hardCapScale)) if diminishes below or at hardCap.
	 */
	public static double complexDiminishingReturns(double input, double softCap, double hardCap, 
			double softCapScale, double hardCapScale) {
		return complexDiminishingReturns(input, softCap, hardCap, softCapScale, hardCapScale, Double.MIN_VALUE, Double.MAX_VALUE);
	}
	
	/**
	 * Returns base to the power of the natural log of "of"
	 */
	public static int baseToThePowerOfNaturalLogOf(int base, int of) {
	    return (int) Math.ceil(Math.pow(base, Math.log(of)));
	}
}
