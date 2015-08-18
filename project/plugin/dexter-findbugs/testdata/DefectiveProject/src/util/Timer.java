package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Timer {
	private long start = -1;
	private long end = -1;
	
	private Calendar startCal = Calendar.getInstance();
	private Calendar endCal = Calendar.getInstance();
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Timer(){
		start();
	}
	
	public void start(){
		start = System.currentTimeMillis();
		startCal.setTimeInMillis(start);
	}
	
	public void end(){
		end = System.currentTimeMillis();
		endCal.setTimeInMillis(end);
	}
	
	public void print(){
		String startTime = format.format(startCal.getTime());
		String endTime = format.format(endCal.getTime()); 
		
		//Logger logger = Logger.getLogger(this.getClass());
		StringBuilder msg = new StringBuilder();
		long result = end - start;
		
		msg.append("START:").append(startTime).append(" ~ END:").append(endTime)
		    .append(", TOTAL:").append(result/(1000*60*60)).append("h ").append(result/(1000*60)).append("m ")
		    .append(result/1000 > 60 ? (result/1000) % 60 : result/1000).append("s (").append(result).append(")");
		System.out.println(msg.toString());
		//logger.info(msg.toString());
	}
}
