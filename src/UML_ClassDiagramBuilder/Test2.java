package UML_ClassDiagramBuilder;

abstract public class Test2 {

	public int a;
	public String name;
	abstract protected String isThisInItalics();
	
	public Test2( String n   ){
		super(); //Call Object constructor();
		name = n;
	}
}
