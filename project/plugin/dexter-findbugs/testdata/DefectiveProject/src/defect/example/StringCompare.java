package defect.example;

import util.Timer;

public final class StringCompare {
	final static String h = "abc";
	
	public static void main(String[] args) {
		//compareString1();
		compareString2();
	}

	private final static void compareString1() {
	    String a = "abc";
		String b = "abc";
		String c = new String("abc");	// <<== new String하는 경우에만 다름
		String d = a.intern();
		String e = new String("abc").intern();
		String f = "abc".intern();
		final String g = "abc";
		
		System.out.println("a:" + a.hashCode());
		System.out.println("a.equals(b) ? " + a.equals(b));
		System.out.println("a == b ? " + (a == b)  + "  b:" + b.hashCode()); 
		System.out.println("a == c ? " + (a == c)  + "  c:" + c.hashCode());
		System.out.println("a == d ? " + (a == d)  + "  d:" + d.hashCode());
		System.out.println("a == e ? " + (a == e)  + "  e:" + e.hashCode());
		System.out.println("a == f ? " + (a == f)  + "  f:" + f.hashCode());
		System.out.println("a == g ? " + (a == g)  + "  g:" + g.hashCode());
		System.out.println("a == h ? " + (a == h)  + "  h:" + h.hashCode());
		System.out.println("a.equals(c) ? " + a.equals(c));
    }
	
	private static final void compareString2() {
		final String s = "abcdefghijklmlopqrstuvwxyz";
		final int count = Integer.MAX_VALUE;
		
		Timer t1 = new Timer();
		t1.start();
		for(int i = 0; i<count; i++){
			int var = s.startsWith("a") ? 1 : -1; 
		}
		t1.end();
		t1.print();
		
		Timer t2 = new Timer();
		t2.start();
		for(int i = 0; i<count; i++){
			int var = s.charAt(0) == 'a' ? 1 : -1; 
		}
		t2.end();
		t2.print();
	}
}
