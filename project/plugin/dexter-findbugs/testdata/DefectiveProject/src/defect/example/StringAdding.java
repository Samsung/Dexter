package defect.example;

import util.Timer;

public class StringAdding {
	private static int MAX = 50000;
	
	public static void main(String[] args) {
		badAddingString01();
		goodAddingString01();
		
		Timer t1 = new Timer();
		t1.start();
		badAddingString05();
		t1.end();
		t1.print();
		
		Timer t2 = new Timer();
		t2.start();
		badAddingString05();
		t2.end();
		t2.print();
	}
	
	public static void badAddingString01(){
		// compiler optimization occurred : String s = "abcde123"; 
		String s = "a" + "b" + "c" + "d" + "e" + "1" + "2" + "3";
	}
	
	public static void goodAddingString01(){
		StringBuffer sb = new StringBuffer(20);
		sb.append("a").append("b").append("c").append("d").append("e")
			.append("1").append("2").append("3");
	}
	
	public static void badAddingString02(){
		// compiler optimization occurred : StringBuilder
		String v = "v";
		String s = "a" + "b" + "c" + "d" + v;
	}
	
	public static void badAddingString03(){
		// compiler optimization occurred : String s = "abcd1";
		String s = "a" + "b" + "c" + "d" + 1;
	}
	
	public static void badAddingString04(String v){
		// compiler optimization occurred : StringBuilder
		String s = "a" + "b" + "c" + "d" + v;
	}
	
	public static void badAddingString05(){
		// compiler optimization occurred : StringBuilder
		// same result with goodAddingString05() method
		String s = "";
		for(int i=0; i<MAX; i++){
			s += "" + i;
		}
	}
	
	public static void goodAddingString05(){
		StringBuilder sb = new StringBuilder(1000);
		for(int i=0; i<MAX; i++){
			sb.append(i);
		}
	}
}
