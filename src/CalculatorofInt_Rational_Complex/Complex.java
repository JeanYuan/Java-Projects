package CalculatorofInt_Rational_Complex;


/**
 *  
 * @author Jean
 * 
 *         Objective: The goal of this project is to create a sub-class in Java, and
 *         use inheritance, super, and override.
 * 
 *         Due on: February 12, 2016
 * 
 */
public class Complex extends Rational {
	/**
	 * There is a Rational object for imaginary part
	 * 
	 */
	protected Rational imaginary;

	/**
	 * This is the empty constructor
	 * 
	 */
	public Complex() {
		this(0, 0);
	}

	/**
	 * This is the initial constructor with int variables as both real and
	 * imaginary
	 * 
	 * @param real
	 *            This is the int value of real part
	 * @param imaginary
	 *            This is the int value of imaginary part
	 */
	public Complex(int real, int imaginary) {
		super(real);
		this.imaginary = new Rational(imaginary);
	}

	/**
	 * This is the initial constructor
	 * 
	 * @param real
	 *            This is the Rational object for real part
	 * @param imaginary
	 *            This is the Rational object for imaginary part
	 */
	public Complex(Rational real, Rational imaginary) {
		super(real);
		this.imaginary = imaginary;
	}

	/**
	 * This is the copy constructor
	 * 
	 * @param other
	 *            This is the Complex object to be copied
	 */
	public Complex(Complex other) {
		this(new Rational(other.getInt(), other.getDenominator()), other.imaginary);
	}

	/**
	 * This is get method to access real part
	 * 
	 * @return Rational
	 */
	public Rational getReal() {
		return this;
	}

	/**
	 * This is set method to assign real part
	 * 
	 * @param real
	 *            This is the Rational object for real part
	 */
	public void setReal(Rational real) {
		this.setInt(real.getInt());
		this.setDenominator(real.getDenominator());
	}

	/**
	 * This is get method to access imaginary part
	 * 
	 * @return Rational
	 */
	public Rational getImaginary() {
		return imaginary;
	}

	/**
	 * This is set method to assign imaginary part
	 * 
	 * @param imaginary
	 *            This is the Rational object for imaginary part
	 */
	public void setImaginary(Rational imaginary) {
		this.imaginary = imaginary;
	}

	/**
	 * This is override equals method
	 * 
	 * @param obj
	 *            This is the object to compare
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			Complex other = (Complex) obj;
			return (other.getInt() == this.getInt() && other.imaginary.getInt() == this.imaginary.getInt()
					&& other.getDenominator() == this.getDenominator()
					&& other.imaginary.getDenominator() == this.imaginary.getDenominator());
		}
		return false;
	}

	/**
	 * This is clone method
	 * 
	 * @return Complex
	 */
	public Complex clone() {
		return new Complex(this);
	}

	/**
	 * This is override toString method
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		if (this.imaginary.getInt() / this.imaginary.getDenominator() >= 0) {
			return super.toString() + " + " + "i" + this.imaginary.toString();
		} else {
			Complex convert = new Complex(this);
			if (this.imaginary.getInt() < 0) {
				convert.imaginary.setInt(-1 * this.imaginary.getInt());
			} else {
				convert.imaginary.setDenominator(-1 * this.imaginary.getDenominator());
			}
			return super.toString() + " - " + "i" + this.imaginary.toString();
		}

	}

	/**
	 * This is add method to add a given complex object
	 * 
	 * @param y
	 *            This is the complex object to add
	 * @return Complex
	 */
	public Complex add(Complex y) {
		Complex result = new Complex(this.getReal().add(y.getReal()), this.imaginary.add(y.getImaginary()));

		return result;
	}

	/**
	 * This is subtract method to subtract a given complex object
	 * 
	 * @param y
	 *            This is the complex object to subtract
	 * @return Complex
	 */
	public Complex subtract(Complex y) {
		Complex result = new Complex(this.getReal().subtract(y.getReal()), this.imaginary.subtract(y.getImaginary()));

		return result;
	}

	/**
	 * This is multiply method to multiple a given complex object
	 * 
	 * @param y
	 *            This is the complex object to multiple
	 * @return Complex
	 */
	public Complex multiply(Complex y) {
		Complex result = new Complex(
				this.getReal().multiply(y.getReal()).subtract(this.imaginary.multiply(y.getImaginary())),
				this.getReal().multiply(y.getImaginary()).add(this.imaginary.multiply(y.getReal())));

		return result;
	}

	/**
	 * This is divide method to divide a given Complex object
	 * 
	 * @param y
	 *            This is the Complex object to divide
	 * @return Complex
	 */
	public Complex divide(Complex y) {
		Complex result = new Complex(
				this.getReal().multiply(y.getReal()).add(this.imaginary.multiply(y.getImaginary()))
						.divide(y.getReal().multiply(y.getReal()).add(y.imaginary.multiply(y.getImaginary()))),
				this.imaginary.multiply(y.getReal()).subtract(this.getReal().multiply(y.getImaginary()))
						.divide(y.getReal().multiply(y.getReal()).add(y.imaginary.multiply(y.getImaginary()))));

		return result;
	}

}

