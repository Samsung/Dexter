package defect.example;

import util.Timer;

public class WrapperInFor {
	public static void main(String[] args) {
		Timer t1 = new Timer();
		t1.start();
		badWrapper();
		t1.end();
		t1.print();
		
		Timer t2 = new Timer();
		t2.start();
		goodWrapper();
		t2.end();
		t2.print();
	}
	
	public static void badWrapper(){
		Long sum = 0L;
		for(long i=0; i<Integer.MAX_VALUE; i++){
			// create Long object every time << Bad Performance
			sum += i;
		}
		
		System.out.println("Long sum: " + sum); 
	}
	
	public static void goodWrapper(){
		long sum = 0L;
		for(long i=0; i<Integer.MAX_VALUE; i++){
			sum += i;
		}
		
		System.out.println("Long sum: " + sum);
	}
}

