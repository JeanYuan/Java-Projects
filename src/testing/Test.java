package testing;

import java.util.Vector;
import CalculatorofInt_Rational_Complex.Integer;
import CalculatorofInt_Rational_Complex.Rational;
import CalculatorofInt_Rational_Complex.Complex;
/**
 * CST8284 Object-Oriented Programming (Java) Assignment 1 section: 310 Lecture
 * Professor: Eric Torunski Lab Professor: Dave Houtman
 * 
 * @author JIE YUAN
 * 
 *         Objective: The goal of this lab is to create a sub-class in Java, and
 *         use inheritance, super, and override.
 * 
 *         Due on: February 12, 2016
 *
 */
public class Test {

		static Vector<String> passed = new Vector<>();
		static Vector<String> failed = new Vector<>();

		/**This is the static main method for testing
		 * 
		 * @param args This is a String array
		 */
		public static void main(String args[])
		{
			Integer i1 = new Integer(5);
			Integer i2 = new Integer(6);

			Integer clone1 = i1.clone();

			Test.test(i1  == i1, 			"Testing Integer identity");
			Test.test( i1 != clone1,			"Testing Integer inequality");
			Test.test(i1.equals(clone1),  	"Testing Integer equality");

			Test.test( i1.add(5).getInt() == 10,  		"Testing Integer add");
			Test.test( i1.subtract(3).getInt() == 2,  	"Testing Integer subtract");
			Test.test(i1.multiply(4).getInt() == 20, 	"Testing Integer multiply");
			Test.test(i1.divide(2).getInt() == 2, 		"Testing Integer divide");
			Test.test(i1.toString().equals("5"), 		"Testing Integer toString");
			i1.setInt(33);
			Test.test(i1.getInt() == 33, 	"Testing Integer Set");

			Rational r1 = new Rational();
			Rational r2 = new Rational(3, 4);	//r2 = 3 / 4
			Rational r3 = new Rational(6);		// r3 =  6 / 1

			Test.test(r1 == r1, 				"Testing Rational identity");
			Test.test( r1!= r1.clone(),			"Testing Rational inequality2");

			r1.setInt(3);
			r1.setDenominator(4);
			Test.test(r1.equals(r2), 				"Testing Rational equality");
			Test.test( !r1.equals(i1), 				"Testing Rational equality2");
			Test.test(r1.add(r2).getInt() == 24, 		"Testing Rational add");
			Test.test(r1.add(r2).getDenominator() == 16,"Testing Rational add2");

			r1.setDenominator(2);	//R1 is now 3/2
			r2.setInt(2);		//R2 is now 2/4

			Test.test(r1.subtract(r2).getInt() == 8, 			"Testing Rational add3"); //(3*4) - (2*2)
			Test.test(r1.subtract(r2).getDenominator() == 8,	"Testing Rational add4");
			Test.test(r1.multiply(r2).getInt() == 6, 			"Testing Rational multiply");
			Test.test(r1.multiply(r2).getDenominator() == 8, 	"Testing Rational multiply2");
			Test.test(r1.divide(r2).getInt() == 12,				"Testing Rational divide");
			Test.test(r1.divide(r2).getDenominator()== 4, 		"Testing Rational divide2");
			Test.test(r1.toString().equals("3/2"), 				"Testing Rational toString");
			Test.test(r2.toString().equals("2/4"),				"Testing Rational toString2");

			Complex c1 = new Complex();
			Complex c2 = new Complex(2, 4);		//Real part is 2, Imaginary is 4
			Complex result = null;

			c1.setReal(      new Rational(2) );
			c1.setImaginary( new Rational(4) );

			Test.test(  c1.clone() != c1, 		"Testing Complex identity");
			Test.test(  c1 == c1,				"Testing Complex identity2");
			Test.test(  c1.equals(c2),			"Testing Complex equality");

			result = c1.add(c2);
			Test.test( result.getReal().getInt() == 4, 			"Testing Complex add");
			Test.test( result.getReal().getDenominator() == 1, 	"Testing Complex add2");

			Test.test( result.getImaginary().getInt() == 8, 	"Testing Complex add3");
			Test.test( result.getReal().getDenominator() == 1, 	"Testing Complex add4");

			result = c1.subtract(new Complex( 3, 3));
			Test.test(result.getReal().getInt() == -1, 			"Testing Complex subtract");
			Test.test(result.getImaginary().getInt() == 1,		"Testing Complex subtract2");

			c2.setReal( new Rational(3) );
			c2.setImaginary( new Rational(3) );		//c2 is now (3 + i3)
		
			result = c1.multiply(c2);
			Test.test(result.getReal().getInt() == -6, 			"Testing Complex multiply");
			Test.test(result.getImaginary().getInt() == 18, 	"Testing Complex multiply2");

			result = c1.divide(c2);
			Test.test(result.getReal().getInt() == 18,				"Testing Complex divide");
			Test.test(result.getReal().getDenominator() == 18,		"Testing Complex divide2");
			Test.test(result.getImaginary().getInt() == 6,			"Testing Complex divide3");
			Test.test(result.getImaginary().getDenominator() == 18,	"Testing Complex divide4");

			Test.test(c1.toString().equals("2/1 + i4/1"),	"Testing Complex toString");
			Test.test(c2.toString().equals("3/1 + i3/1"),	"Testing Complex toString2");

			c1.setImaginary( new Rational(-4)); // c1 is now "2/1 - i4/1"
			String str = c1.toString();
			Test.test(str.equals("2/1 - i4/1"),	"Testing Complex toString3");

			Test.printTestResults();
		}

		public static void test(boolean result, String message)
		{
			if(passed.isEmpty() && failed.isEmpty())
			{
				System.out.println("Beginning tests:");
			}

			System.out.println(message + ":" + result);
			if(result)
				passed.add(message);
			else
				failed.add(message);
		}

		public static void printTestResults()
		{
			System.out.println("TEST RESULTS - Passed:");
			for(String test: passed)
			{
				System.out.println("\t" + test);
			}
			if(passed.isEmpty())	System.out.println("None");


			System.out.println("TEST RESULTS - Failed:");
			for(String test: failed)
			{
				System.out.println("\t" + test);
			}
			if(failed.isEmpty())	System.out.println("None");

			System.out.println("Summary:" + passed.size() + " passed, " + failed.size() + " failed, " + (passed.size() + failed.size()) + " total");
		}
	}
