package defect.example;

import java.util.ArrayList;
import java.util.List;

public class ResourceLeakTest {
	public final static int count = 10;
	
	public static void main(String[] args) {
	    //new BadResourceLeakHandle().run();
//	    BadResourceLeakHandle bad = new BadResourceLeakHandle();
//	    bad.run2();
//	    bad.cleanList();
	    
	    GoodResourceLeakHandle good = new GoodResourceLeakHandle();
	    good.run();
    }
}

class BadResourceLeakHandle{
	
	public void run(){
		List<Thread> list = new ArrayList<Thread>(ResourceLeakTest.count);

		for(int i=0; i<ResourceLeakTest.count; i++){
			Thread t = new Thread(Integer.toString(i)){
				@Override
				public void run() {
				    super.run();
				    while(true){
				    	System.out.println("Thread " + getName() + " running.");
				    	try {
				    		Thread.sleep(1000);
				    	} catch (InterruptedException e) {
				    		e.printStackTrace();
				    	}
				    }
				}
			};
			t.start();
			list.add(t);
		}
	}
	
	private List<Thread> list = new ArrayList<Thread>(ResourceLeakTest.count);
	public void run2(){

		for(int i=0; i<ResourceLeakTest.count; i++){
			Thread t = new Thread(Integer.toString(i)){
				@Override
				public void run() {
				    super.run();
				    while(true){
				    	System.out.println("Thread " + getName() + " running.");
				    	try {
				    		Thread.sleep(1000);
				    	} catch (InterruptedException e) {
				    		e.printStackTrace();
				    	}
				    }
				}
			};
			t.start();
			list.add(t);
		}
	}
	
	public void cleanList(){
		list.clear();
		list = new ArrayList<Thread>(ResourceLeakTest.count);
	}
}

class GoodResourceLeakHandle{
	protected static boolean isOk = true;
	
	public void run(){
		List<Thread> list = new ArrayList<Thread>(ResourceLeakTest.count);

		for(int i=0; i<ResourceLeakTest.count; i++){
			Thread t = new Thread(Integer.toString(i)){
				@Override
				public void run() {
				    super.run();
				    while(GoodResourceLeakHandle.isOk){
				    	System.out.println("Thread " + getName() + " running.");
				    	try {
				    		Thread.sleep(1000);
				    	} catch (InterruptedException e) {
				    		e.printStackTrace();
				    	}
				    }
				}
			};
			t.start();
			list.add(t);
		}
		
		GoodResourceLeakHandle.isOk = false;
		list.clear();
	}
	
	private List<Thread> list = new ArrayList<Thread>(ResourceLeakTest.count);
	public void run2(){

		for(int i=0; i<ResourceLeakTest.count; i++){
			Thread t = new Thread(Integer.toString(i)){
				@Override
				public void run() {
				    super.run();
				    while(true){
				    	System.out.println("Thread " + getName() + " running.");
				    	try {
				    		Thread.sleep(1000);
				    	} catch (InterruptedException e) {
				    		e.printStackTrace();
				    	}
				    }
				}
			};
			t.start();
			list.add(t);
		}
	}
	
	public void cleanList(){
		list.clear();
		list = new ArrayList<Thread>(ResourceLeakTest.count);
	}
}
