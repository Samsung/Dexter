package defect.example;

public class LockInversion {
     
    
	public static void main(String[] args) throws InterruptedException {
		goodTest();
		
		System.out.println("===============================================");
		Thread.sleep(5000);
		badTest();
	}
  
	private static void badTest(       ) {
		final BadDeadlock bad = new BadDeadlock();
		System.out.println("Start Bad Dead Lock Test");

		Thread r1 = new Thread() {
			public void run() {
				for(int i=0; i<50; i++){
					try {
						BadDeadlock.lock1();
						bad.print("r1:" + i + " >> ");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread r2 = new Thread() {
			public void run() {
				for(int i=0; i<50; i++){
					try {
						BadDeadlock.lock2();
						bad.print("\t\t\t\t\tr2:" + i + " >> ");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		r1.start();
		r2.start();
	}
	
	private static void goodTest() {
		final GoodDeadlock good = new GoodDeadlock();
		System.out.println("Start Good Dead Lock Test");

		Thread r1 = new Thread() {
			public void run() {
				for(int i=0; i<50; i++){
					try {
						GoodDeadlock.lock1();
						good.print("r1:" + i + " >> ");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread r2 = new Thread() { 
			public void run() {
				for(int i=0; i<50; i++){
					try {
						GoodDeadlock.lock2();
						good.print("\t\t\t\t\tr2:" + i + " >> ");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		r1.start();
		r2.start();
	}
}

class BadDeadlock { 
	static Integer o1 = 1;
	static Integer o2 = 1;

	public static void lock1() throws InterruptedException {
		synchronized (o1) {
			o1++;
			Thread.sleep(10);
			synchronized (o2) {
				o2++;
				Thread.sleep(10);
			}
		}
	}

	public static void lock2(     ) throws InterruptedException {
		synchronized (o2) {
			o2--;
			Thread.sleep(5);
			synchronized (o1) {
				o1--;
				Thread.sleep(5);
			}
		}
	}

	public void print(String head) {
		System.out.println(head + " O1:" + o1 + " 02:" + o2);
	}
}

class GoodDeadlock {                                                                                                                 
	static Integer o1 = 1;
	static Integer o2 = 1;

	public synchronized static void lock1() throws InterruptedException {
		o1++;
		Thread.sleep(10);
		o2++;
		Thread.sleep(10);  
	}

	public synchronized static void lock2() throws InterruptedException {  
		o2--;
		Thread.sleep(5);
		o1--;
		Thread.sleep(5);
	}

	public void print(String head) {          
		System.out.println(head + "O1:" + o1 + " 02:" + o2);
	}
}