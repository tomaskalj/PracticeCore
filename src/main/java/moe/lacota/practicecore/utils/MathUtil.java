package moe.lacota.practicecore.utils;

public class MathUtil {
	public static double millisToRoundedTime(long time) {
		double newTime = ((double) time) / ((double) 1000);
		return roundOff(newTime, 1);
	}

	public static double roundOff(double x, int places) {
		double pow = Math.pow(10, places);
		return Math.round(x * pow) / pow;
	}

	public static boolean isWithin(int x, int min, int max) {
		return x >= min && x <= max;
	}
}
