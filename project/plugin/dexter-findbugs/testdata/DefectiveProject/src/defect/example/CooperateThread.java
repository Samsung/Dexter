package defect.example;

import java.util.concurrent.TimeUnit;

public class CooperateThread {
	public static void main(String[] args) throws InterruptedException {
		GoodStopThread.run();
		
		TimeUnit.SECONDS.sleep(5);
		System.out.println("==========================================");
		BadStopThread.run();
	}
}

class BadStopThread {
	private static boolean stopRequested;
	
	public static void run() throws InterruptedException {
		Thread backgroundThread = new Thread(new Runnable(){
			@Override
			public void run() {
				int i = 0;
				// Java Effective에서는 아래 코드가 계속 수행된다고 하지만 실제 중지됨
				while (!stopRequested){
					System.out.println("i:" + i++);
				}
			}
		});
		
		backgroundThread.start();
		
		TimeUnit.SECONDS.sleep(1);
		stopRequested = true;
		System.out.println("Stopped  stopRequested:" + stopRequested);
	}
}

class GoodStopThread {
	private static boolean stopRequested;
	private static synchronized void requestStop() {
		stopRequested = true;
	}
	
	private static synchronized boolean stopRequest(){
		return stopRequested;
	}
	
	public static void run() throws InterruptedException{
		Thread backgroundThread = new Thread(new Runnable(){
			@Override
			public void run() {
				int i =0;
				while (! stopRequested){
					System.out.println("i:" + i++);
				}
			}
		});
		
		backgroundThread.start();
		
		TimeUnit.SECONDS.sleep(1);
		stopRequested = true;
		System.out.println("Stopped  stopRequested:" + stopRequested);
	}
}