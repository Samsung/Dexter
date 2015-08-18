package defect.example;

/**
 * @author min.ho.kim
 *
 * NullPointerException이 발생해야 하지만 실제로 발생하지 않음.
 */
public class ConstructorCallsOverridableMethod extends ParentClass{
	public ConstructorCallsOverridableMethod() {
		testMethod();
		System.out.println("Called Constructor");
	}
	
	
	@Override
	protected void testMethod(   ) {
	    super.testMethod();
	    System.out.println("Called Override testMethod");
	}
	
	public static void main(String[] args) {
	    ConstructorCallsOverridableMethod c = new ConstructorCallsOverridableMethod();
    }
}

class ChildClass extends ConstructorCallsOverridableMethod {
	public ChildClass(                ) {
		super();
    }
	
	public static void main(String[] args) {
	    ChildClass j = new ChildClass();
    }
	
	@Override
	protected void testMethod() {
	    super.testMethod();
	}
}

class ParentClass {
	protected void testMethod(){
		System.out.println("Called Parent testMethod");
	}
}
