package CalculatorofInt_Rational_Complex;


/**
 * CST8284 Object-Oriented Programming (Java) Project 1 section: 310 Lecture
 * Professor: Eric Torunski Lab Professor: Dave Houtman
 * 
 * @author Jean
 * 
 *         Objective: The goal of this project is to create a sub-class in Java, and
 *         use inheritance, super, and override.
 * 
 *         Due on: February 12, 2016
 * 
 */
public class Integer {
	/**
	 * This is an int variable
	 * 
	 */
	protected int intX;

	/**
	 * This is the empty constructor
	 * 
	 */
	public Integer() {
		this(0);
	}

	/**
	 * This is the initial constructor
	 * 
	 * @param intX
	 *            This is the value of intX
	 */
	public Integer(int intX) {
		this.intX = intX;
	}

	/**
	 * This is the copy constructor
	 * 
	 * @param other
	 *            This is the value of Integer object
	 */
	public Integer(Integer other) {
		this.intX = other.intX;
	}

	/**
	 * This is the method to get intX value
	 * 
	 * @return intX
	 */
	public int getInt() {
		return intX;
	}

	/**
	 * This is the method to assign intX value
	 * 
	 * @param intX
	 *            This is the intX value to assign
	 */
	public void setInt(int intX) {
		this.intX = intX;
	}

	/**
	 * This is the override of the equals method
	 * 
	 * @param obj
	 *            This is the obj to compare
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Integer) {
			Integer other = (Integer) obj;
			return this.intX == other.intX;
		}
		return false;
	}

	@Override
	/**
	 * This is the override of the clone method
	 * 
	 * @return Integer
	 */
	public Integer clone() {
		return new Integer(this);
	}

	/**
	 * This is the override of the toString method
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return "" + intX;
	}

	/**
	 * This is add method to add a given int value
	 * 
	 * @param y
	 *            This is the int value to add
	 * 
	 * @return Integer
	 */
	public Integer add(int y) {

		return new Integer(this.getInt() + y);
	}

	/**
	 * This is subtract method to subtract a given int value
	 * 
	 * @param y
	 *            This is the int value to subtract
	 * @return Integer
	 */
	public Integer subtract(int y) {

		return new Integer(this.getInt() - y);
	}

	/**
	 * This is multiply method to multiple a given int value
	 * 
	 * @param y
	 *            This is the int value to multiple
	 * @return Integer
	 */
	public Integer multiply(int y) {

		return new Integer(this.getInt() * y);
	}

	/**
	 * This is divide method to divide a given int value and return the result
	 * as an int
	 * 
	 * @param y
	 *            This is the int value to divide
	 * @return Integer
	 */
	public Integer divide(int y) {
		if (y == 0)
			return null;
		else
			return new Integer(this.getInt() / y);
	}

}


