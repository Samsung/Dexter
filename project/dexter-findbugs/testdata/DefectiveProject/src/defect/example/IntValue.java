package defect.example;

public class IntValue {
	public static void main(String[] args) {
		int x = 10;
		int y = 5;
		int max = Integer.MAX_VALUE;
		float f = 0.1f;
		long l = x * max;
		long lcast = (long) x * max;
		
		System.out.println("x * max = " + (x * max));
		System.out.println("l = " + l);									// -10
		System.out.println("lcast = " + lcast);						// 21474836470
		System.out.println("x / y = " + (x / y));						// 2
		System.out.println("(int) x / y = " + ((int) x / y));		// 2
		System.out.println("(int) x / f = " + ((int) x / f));		// 100.0
		System.out.println("(int) x * f = " + ((int) x * f));		// 1.0
		System.out.println("y / x = " + (y / x));						// 0
		System.out.println("(float) y / x = " + (float)(y / x));	// 0.0
		System.out.println("(float)y / (float)x = " + ((float)y / (float)x));	// 0.5
		System.out.println("Math.max(x, y) = " + Math.max(x, y));	// 10
		System.out.println("Math.max(x, f) = " + Math.max(x, f));	// 10.0
		System.out.println("(int)Math.max(x, f) = " + (int)Math.max(x, f));	// 10
		System.out.println("(max + max) / 2 = " + ((max + max) / 2)); 	// -1
	}
}
