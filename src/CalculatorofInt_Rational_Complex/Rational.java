package CalculatorofInt_Rational_Complex;


/**
 * CST8284 Object-Oriented Programming (Java) Project 1 section: 310 Lecture
 * Professor: Eric Torunski Lab Professor: Dave Houtman
 * 
 * @author Jean
 * 
 *         Objective: The goal of this Project is to create a sub-class in Java, and
 *         use inheritance, super, and override.
 * 
 *         Due on: February 12, 2016
 * 
 * 
 */

public class Rational extends Integer {
	/**
	 * This is an int variable as denominator
	 * 
	 */
	protected int denominator;

	/**
	 * This is an empty constructor
	 * 
	 */
	public Rational() {
		this(0, 1);
	}

	/**
	 * This is initial constructor: for integer: with 1 as the denominator
	 * 
	 * @param numerator
	 *            This is the int value for numerator
	 */
	public Rational(int numerator) {
		super(numerator);
		this.denominator = 1;
	}

	/**
	 * This is initial constructor for fraction: denominator is an int variable
	 * rather than 1 or 0
	 * 
	 * @param numerator
	 *            This is the int value for numerator
	 * @param denominator
	 *            This is the int value for denominator
	 */
	public Rational(int numerator, int denominator) {
		super(numerator);// use the method from parent class to assign the
							// numerator
		this.denominator = denominator;
	}

	/**
	 * This is copy constructor
	 * 
	 * @param other
	 *            This is the value of Rational object
	 */
	public Rational(Rational other) {
		this(other.intX, other.denominator);
	}

	/**
	 * This is get method for denominator
	 * 
	 * @return int
	 */
	public int getDenominator() {
		return denominator;
	}

	/**
	 * This is set method for denominator
	 * 
	 * @param denominator
	 *            This is the int value of denominator
	 */
	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	/**
	 * This is override equals method
	 * 
	 * @param obj
	 *            This is the Rational object to compare
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rational) {
			Rational other = (Rational) obj;
			return (this.intX == other.intX && this.denominator == other.denominator);
		}
		return false;
	}

	/**
	 * This is override clone method
	 * 
	 * @return Rational
	 */
	@Override
	public Rational clone() {
		return new Rational(this);
	}

	/**
	 * This is override toString method
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return this.intX + "/" + this.denominator;
	}

	/**
	 * This is add method to add a given Rational object and return the result
	 * as a Rational object
	 * 
	 * @param y
	 *            This is the Rational object to add
	 * @return Rational
	 */
	public Rational add(Rational y) {

		return new Rational(this.getInt() * y.getDenominator() + y.getInt() * this.getDenominator(),
				this.getDenominator() * y.getDenominator());
	}

	/**
	 * This is subtract method to subtract a given Rational object and return
	 * the result as a Rational object
	 * 
	 * @param y
	 *            This is the Rational object to subtract
	 * @return Rational
	 */
	public Rational subtract(Rational y) {

		return new Rational(this.getInt() * y.getDenominator() - y.getInt() * this.getDenominator(),
				this.getDenominator() * y.getDenominator());
	}

	/**
	 * This is multiply method to multiple a given Rational object and return
	 * the result as a Rational object
	 * 
	 * @param y
	 *            This is the Rational object to multiply
	 * @return Rational
	 */
	public Rational multiply(Rational y) {

		return new Rational(this.getInt() * y.getInt(), this.getDenominator() * y.getDenominator());
	}

	/**
	 * This is divide method to divide a given Rational object and return the
	 * result as a Rational object
	 * 
	 * @param y
	 *            This is the Rational object to divide
	 * @return Rational
	 */
	public Rational divide(Rational y) {

		return new Rational(this.getInt() * y.getDenominator(), this.getDenominator() * y.getInt());

	}

}
