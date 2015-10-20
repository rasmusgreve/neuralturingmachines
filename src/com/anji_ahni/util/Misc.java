package com.anji_ahni.util;

import java.text.DecimalFormat;

public class Misc {
	static DecimalFormat nf1 = new DecimalFormat("00");

	public static String formatTimeInterval(long duration) {
		long days = duration / (24 * 60 * 60);
		duration -= days * 24 * 60 * 60;
		long hours = duration / (60 * 60);
		duration -= hours * 60 * 60;
		long minutes = duration / 60;
		duration -= minutes * 60;
		long seconds = duration;

		return days + " " + nf1.format(hours) + ":" + nf1.format(minutes) + ":" + nf1.format(seconds);
	}
	
	public static String repeatString(String str, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++)
			sb.append(str);
		return sb.toString();
	}
}
