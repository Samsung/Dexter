package defect.example;

public class ConstructorCallsOverridableMethod2 {
	public static void main(String[] args) {
	    JuniorClass j = new JuniorClass();
    }
}

class SeniorClass {
	public SeniorClass() {
		toString(); // may throw NullPointerException if overridden
	}

	public String toString() {
		return "IAmSeniorClass";
	}
}

class JuniorClass extends ConstructorCallsOverridableMethod2 {
	private String name;

	public JuniorClass() {
		super(); // Automatic call leads to NullPointerException
		name = "JuniorClass";
	}

	public String toString() {
		return name.toUpperCase();
	}
}
