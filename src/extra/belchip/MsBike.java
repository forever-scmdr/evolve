package extra.belchip;

public class MsBike {
	private static final long HOURS = 3600000;
	private static final long MINUTES = 60000;
	private static final long SECONDS = 1000;
	public static String showTime(long ms){
		long r = ms%HOURS;
		long h = ms/HOURS;
		String s = (h<10)? "0"+h+":": h+":";
		long m = r/MINUTES;
		r = r%MINUTES;
		s = (m<10)? s+"0"+m+":":s+m+":";
		long sec = r/SECONDS;
		r = r%SECONDS;
		s = (sec<10)? s+"0"+sec+"."+r: s+sec+"."+r;
		return s;
	}
}
