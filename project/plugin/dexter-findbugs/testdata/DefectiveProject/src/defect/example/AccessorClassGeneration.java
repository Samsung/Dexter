package defect.example;

public class AccessorClassGeneration {
	public static void main(String[] args) {
	    Outer o = new Outer();
	    o.method();
	    System.out.println("successfully");
    }
}

class Outer {
	void method() {
		Inner ic = new Inner();// Causes generation of accessor class
	}

	public class Inner {
		private Inner() {
		}
	}
}
