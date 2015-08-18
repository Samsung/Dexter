package defect.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Overloading {
	public static void main(String[] args) {
		BadCollectionClassifier.run();
		
		GoodCollectionClassifier.run();
	}
}


class BadCollectionClassifier {
	public static String classify(Set<?> s){
		return "Set";
	}
	
	public static String classify(List<?> list){
		return "List";
	}
	
	public static String classify(Collection<?> c){
		return "Unknown Collection";
	}
	
	public static void run() {
		Collection<?>[] collections = {
			new HashSet<String>(),
			new ArrayList<BigInteger>(),
			new HashMap<String, String>().values()
		};
		
		for(Collection<?> c : collections){
			System.out.println(classify(c));
		}
	}
}

class GoodCollectionClassifier {
	public static String classify(Collection<?> c){
		return c instanceof Set ? "Set" :
			c instanceof List ? "List" : "Unknown Collection";
	}
	
	public static void run() {
		Collection<?>[] collections = {
			new HashSet<String>(),
			new ArrayList<BigInteger>(),
			new HashMap<String, String>().values()
		};
		
		for(Collection<?> c : collections){
			System.out.println(classify(c));
		}
	}
}