package defect.example;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class DoPriviliged {
	public static void main(String[] args) {
		System.out.println("java.home(not in security) : " + System.getProperty("java.home"));
		
		System.out.println("Security Manager : " + System.getSecurityManager());
		
		AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			@Override
			public Boolean run() {
				System.out.println("java.home : " + System.getProperty("java.home"));
				
				return Boolean.TRUE;
			}
		});
	}
}
