package defect.example;

public final class FinalTest {
	public static void main(String[] args) {
	    
    }
	
}

final class FinalVariableTest {
	private final int m_finalInt;
	
	public FinalVariableTest(final int finalIntVar){
		m_finalInt = finalIntVar;
	}
	
	public void localFinalValue(final int finalIntVar){
		final int localFinal;
		
		if(finalIntVar > 0){
			localFinal = finalIntVar;
		} else {
			localFinal = -1;
		}
		
		System.out.println("localFinal : " + localFinal);
		
		// error: localFinal = 2;
	}
}
