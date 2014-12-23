package com.chcgp.hpad.util.general;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {
	public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public final static SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public final static SimpleDateFormat formatterTimeSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	final static String[] week_zh_info = { "日", "一", "二", "三", "四", "五", "六" };

	private static DateTimeUtil dateTime = new DateTimeUtil();

	public static DateTimeUtil getInstance() {
		return dateTime;
	}


	public static String getToday() {
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return ff.format(new java.util.Date());
	}


	// return 2006-01-22
	public static String getNowDateStr() {
		return getNowDateStr("-", "");
	}

	public static long getNowDateL() {
		String day = getNowDateStr("-", "");
		java.util.Date dd = null;
		try {
			dd = formatter.parse(day);
		} catch (Exception e) {
			return 0;
		}
		if (dd == null) {
			return 0;
		}
		return dd.getTime();
	}

	// ("-",""(or other)) return 2006-1-22
	// ("/","") return 2006/1/22
	// ("/","0") return 22/1/2006
	// ("/","1") return 1/22/2006
	// other return 2006年01月22日
	public static String getNowDateStr(String para) {
		return getNowDateStr(para, "");
	}

	public static String getNowDateStr(String para, String order) {
		String str = "";

		GregorianCalendar currentDay = new GregorianCalendar();
		int month = currentDay.get(Calendar.MONTH) + 1;
		int year = currentDay.get(Calendar.YEAR);
		int today = currentDay.get(Calendar.DAY_OF_MONTH);

		str = year + para + month + para + today;
		if (para.equals("-")) {
			;
		} else if (para.equals("/")) {
			if (order.trim().equals("0")) {
				str = today + "/" + month + "/" + year;
			} else if (order.trim().equals("1")) {
				str = month + "/" + today + "/" + year;
			}

		} else {
			str = year + "年" + month + "月" + today + "日";
		}
		return str;
	}

	public static boolean examDateWith_(String date) {
		if (date != null && date.indexOf("-") >= 0 && date.length() > 7 && date.length() < 11) {
			return true;
		}
		return false;

	}

	// return{"2000-02-27","2000-02-28","2000-02-29","1","2"...."31","2000-4-1"}
	public static String[] getDays1MonthWithPreAndNext(int year, int month) {
		if (year < 1901 && month == 1) {
			return getDays1MonthWithPreAndNext(year, month, "nofirst");
		} else if (year > 2098 && month == 12) {
			return getDays1MonthWithPreAndNext(year, month, "nolast");
		} else {
			return getDays1MonthWithPreAndNext(year, month, "");
		}
	}

	public static String[] getDays1MonthWithPreAndNext(int year, int month, String para) {
		String[] retu = new String[42];
		String[] firstRetu = new String[42];
		String[] lastRetu = new String[42];
		int firstNull = -2;
		int lastNull = 0;
		int buffMonth = month;
		int buffYear = year;

		for (int i = 0; i < 42; i++) {
			retu[i] = "";
			firstRetu[i] = "";
			lastRetu[i] = "";
		}
		retu = getDays1Month(year, month);

		for (int i = 0; i < 42; i++) {
			if (firstNull < -1 && i < 7 && !retu[i].equals("")) {
				firstNull = i - 1;
			} else if (i > 25 && (retu[i] == null || retu[i].trim().equals(""))) {
				lastNull = i;
				break;
			}
		}
		// System.out.println("firstNull:"+firstNull+" lastNull:"+lastNull);
		if (firstNull > -1 && !(para.indexOf("nofirst") >= 0)) {
			if (month == 1) {
				buffMonth = 12;
				buffYear = year - 1;
			} else {
				buffMonth--;
			}
			// System.out.println("first:buffYear:"+buffYear+"
			// buffMonth:"+buffMonth);
			firstRetu = getDays1Month(buffYear, buffMonth);
			// printSS(firstRetu);
			int lastI = getLastNullStrIndex(firstRetu);
			lastI--;
			// System.out.println("\nlastI:"+lastI+"\n");
			if (lastI >= 0) {
				for (int idx = firstNull; idx >= 0; idx--) {
					retu[idx] = buffYear + "-" + buffMonth + "-" + firstRetu[lastI];
					lastI--;
				}
			}
		}
		if (lastNull != 0 && (lastNull) % 7 != 0 && !(para.indexOf("nolast") >= 0)) {
			buffMonth = month;
			buffYear = year;
			if (month == 12) {
				buffMonth = 1;
				buffYear = year + 1;
			} else {
				buffMonth++;
			}
			int ip = 1;
			for (int idx = lastNull; (idx < 42 && (idx) % 7 != 0); idx++) {
				retu[idx] = buffYear + "-" + buffMonth + "-" + String.valueOf(ip);
				ip++;
			}
		}
		return retu;
	}

	// {"","","1"} return 2;
	public static int getFirstNullStrIndex(String[] ss) {
		int retu = ss.length;
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] != null && !ss[i].equals("")) {
				retu = i - 1;
				break;
			}
		}
		return retu;
	}

	public static int getLastNullStrIndex(String[] ss) {
		int retu = -1;
		for (int i = ss.length - 1; i >= 0; i--) {
			if (ss[i] != null && !ss[i].trim().equals("")) {
				retu = i + 1;
				break;
			}
		}
		return retu;
	}

	public static String[] getDays1Month(int year, int month) {
		String[] retu = new String[42];
		for (int i = 0; i < 42; i++) {
			retu[i] = "";
		}

		Calendar thisMonth = Calendar.getInstance();
		thisMonth.set(Calendar.MONTH, month - 1);
		thisMonth.set(Calendar.YEAR, year);
		thisMonth.setFirstDayOfWeek(Calendar.SUNDAY);
		thisMonth.set(Calendar.DAY_OF_MONTH, 1);
		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		int maxIndex = thisMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		int i = 0;
		for (; i < maxIndex; i++) {
			retu[firstIndex + i] = String.valueOf(i + 1);
		}
		// System.out.println("y:"+year+":m:"+month+"set num:"+i);
		return retu;
	}

	// return "日" or "一"等
	public static String getWeekInfo(String[] days, String day) {
		String retu = "";
		int idx = 0;
		for (int i = 0; i < days.length; i++) {
			if (days[i].equals("1")) {
				for (int j = i; j < days.length; j++) {
					if (days[j].equals(day)) {
						retu = week_zh_info[idx];
						break;
					}

					idx++;
					if (idx % 7 == 0) {
						idx = 0;
					}
				}
				break;
			}
			idx++;
			if (idx % 7 == 0) {
				idx = 0;
			}
		}
		return retu;
	}

	private static void printSS(String[] str) {
		for (int j = 0; j < 6; j++) {
			for (int i = j * 7; i < (j + 1) * 7; i++) {
				if (str[i].equals("")) {
					System.out.print("   ");
				} else if (str[i].length() == 1) {
					System.out.print(" " + str[i] + " ");
				} else {
					System.out.print(str[i] + " ");
				}
			}
			System.out.println();
		}
	}

	// 2006-03-01-四 return 2006年03月01日 星期四
	public static String tranDateWithWeek(String date) {
		if (date == null) {
			return "";
		}
		int idx = 0;
		int p = 0;
		String buff = "";
		idx = date.indexOf("-");
		while (idx >= 0 && p < 3) {
			if (p == 0) {
				buff = "年";
			} else if (p == 1) {
				buff = "月";
			} else if (p == 2) {
				buff = "日 星期";
			}

			date = date.substring(0, idx) + buff + date.substring(idx + 1);
			idx = date.indexOf("-");
			p++;
		}
		return date;
	}
	
	// 2006-03-01-四 return 2006年03月01日
	public static String tranDateToChinese(String date) {
		if (date == null) {
			return "";
		}
		int idx = 0;
		int p = 0;
		String buff = "";
		idx = date.indexOf("-");
		while (idx >= 0 && p < 2) {
			if (p == 0) {
				buff = "年";
			} else if (p == 1) {
				buff = "月";
			}

			date = date.substring(0, idx) + buff + date.substring(idx + 1);
			idx = date.indexOf("-");
			p++;
		}
		return date + "日";
	}
	// 2000-1-1 return2000-01-01
	// 2000-01-01 return2000-01-01
	public static String getFormatDateWith0(String date) {
		if (date == null || date.indexOf("-") < 0) {
			return null;
		}
		String str = date;
		int i = 0;
		int idx = -1;
		while (true) {
			if (i == 0) {
				idx = str.indexOf("-");
			} else {
				idx = str.lastIndexOf("-");
			}

			if (idx < 0) {
				return null;
			}
			if (i == 2) {
				break;
			} else if (idx > 0 && i < 2) {
				if (i == 0) {
					if (str.lastIndexOf("-") <= idx) {
						return null;
					}
					if (str.substring(idx + 1, str.lastIndexOf("-")).length() == 1) {
						str = str.substring(0, idx + 1) + "0" + str.substring(idx + 1);
					} else if (str.substring(idx + 1, str.lastIndexOf("-")).length() != 2) {
						return null;
					}
				} else if (i == 1) {
					if (str.substring(idx + 1).length() == 1) {
						str = str.substring(0, idx + 1) + "0" + str.substring(idx + 1);
					} else if (str.substring(idx + 1).length() != 2) {
						return null;
					}
				}

			} else {
				return null;
			}
			i++;
		}
		return str;
	}

	// ("2006","01","20")return 2006-01-20
	// ("2006","01","2000-06-20")return 2000-06-20
	public static String getFormatDate(String year, String month, String day) {
		String thisYear = year;
		String thisMonth = month;
		String thisValue = day;
		String thisDay = day;
		if (thisValue.indexOf("-") >= 0) {
			return thisValue;
		}
		return year + "-" + month + "-" + day;
	}

	public static String strDateTimeJJ(String dateTime, int min, String sign) {
		java.util.Date dd = null;
		try {
			dd = formatterTime.parse(dateTime);
		} catch (Exception e) {
			return null;
		}
		if (dd == null) {
			return null;
		}

		long l = dd.getTime();

		for (int i = 0; i < min; i++) {
			if (sign.equals("-")) {
				l = l - (60 * 1000);
			} else {
				l = l + (60 * 1000);
			}
		}

		return formatterTime.format(new java.util.Date(l));
	}

	public static String strDateJJ(String date, int day, String sign) {
		if (!examDateWith_(date)) {
			return "";
		}
		java.util.Date dd = null;
		try {
			dd = formatter.parse(date);
		} catch (Exception e) {
			return null;
		}
		if (dd == null) {
			return null;
		}

		long l = dd.getTime();

		for (int i = 0; i < day; i++) {
			if (sign.equals("-")) {
				l = l - (24 * 3600 * 1000);
			} else {
				l = l + (24 * 3600 * 1000);
			}
		}

		return formatter.format(new java.util.Date(l));
	}

	public static String t2s(Timestamp tsDate) {
		return t2s(tsDate, 16);
	}

	public static String t2s(Timestamp tsDate, int retuLen) {
		if (tsDate == null) {
			return null;
		}

		if (retuLen == 19) {
			return String.valueOf(formatterTimeSec.format(tsDate));
		}

		if (retuLen == 16) {
			return String.valueOf(formatterTime.format(tsDate));
		}

		return String.valueOf(formatter.format(tsDate));
	}

	/**
	 * @param tsDate
	 *            - Timestamp
	 * @param dispFormat
	 *            - Display format
	 * @return dateTime String
	 */
	public static String t2s(Timestamp tsDate, String dispFormat) {
		if ((tsDate == null) || (dispFormat == null)) {
			return null;
		}

		SimpleDateFormat tmpFormatter = new SimpleDateFormat(dispFormat);

		return String.valueOf(tmpFormatter.format(tsDate));
	}

	public static int parseMin(String alertpre) {
		int retu = 0;
		int buff = 1;
		if (alertpre == null || alertpre.equals("")) {
			return 0;
		}
		try {
			String para = alertpre.substring(alertpre.length() - 1);
			if (para.equalsIgnoreCase("d")) {
				buff = 24 * 60;
			} else if (para.equalsIgnoreCase("h")) {
				buff = 60;
			} else if (para.equalsIgnoreCase("m")) {
				buff = 1;
			}

			retu = Integer.parseInt(alertpre.substring(0, alertpre.length() - 1));
			retu *= buff;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return retu;
		}

	}

	// return ( 2006-06-06 02:03 )-( 2006-05-01 03:02 )
	public static String compareDate(String dt1, String dt2) {
		return compareDate(dt1, dt2, "week");
	}

	public static String compareDate(String dt1, String dt2, String type) {
		try {
			String retu = "";

			java.util.Date d1 = formatterTime.parse(dt1);
			java.util.Date d2 = formatterTime.parse(dt2);
			// return d1.compareTo(d2);
			long l = d1.getTime() - d2.getTime();
			// System.out.println(String.valueOf(l)+":"+String.valueOf(d1.getTime())+":"+String.valueOf(d2.getTime()));
			if (type.equalsIgnoreCase("year")) {
				long years = l / (1000 * 60 * 60 * 24 * 360l);
				if (years > 0) {
					l = l - years * (1000 * 60 * 60 * 24 * 360l);
					retu += years + "年";
				}
				long mons = l / (1000 * 60 * 60 * 24 * 30l);
				if (mons > 0) {
					l = l - mons * (1000 * 60 * 60 * 24 * 30l);
					retu += mons + "个月";
				}
			} else if (type.equalsIgnoreCase("month")) {
				long mons = l / (1000 * 60 * 60 * 24 * 30l);
				if (mons > 0) {
					l = l - mons * (1000 * 60 * 60 * 24 * 30l);
					retu += mons + "个月";
				}
			} else if (type.equalsIgnoreCase("week")) {
				long weeks = l / (1000 * 60 * 60 * 24 * 7l);
				if (weeks > 0) {
					l = l - weeks * (1000 * 60 * 60 * 24 * 7l);
					retu += weeks + "周";
				}
			}
			long days = l / (1000 * 60 * 60 * 24l);
			if (days > 0) {
				l = l - days * (1000 * 60 * 60 * 24l);
				retu += "" + days + "天";
			}
			long hours = l / (1000 * 60 * 60l);
			if (hours > 0) {
				l = l - hours * (1000 * 60 * 60l);
				retu += "" + hours + "小时";
			}
			long min = l / (1000 * 60l);
			if (min > 0) {
				retu += "" + min + "分";
			}

			return retu.trim();
			// return formatterTime.format(d1.compareTo(d2));
		} catch (Exception e) {
			return "";
		}
	}

	public static int compareOnlyDate(String dt1, String dt2) {
		java.util.Date d1 = s2d(dt1);
		java.util.Date d2 = s2d(dt2);
		return compareOnlyDate(d1, d2);
	}

	public static int compareOnlyDate(java.util.Date d1, String dt2) {
		//java.util.Date d2 = s2d(dt2);
		String dt1=getOnlyDate(d1);
		return compareOnlyDate(dt1, dt2);
	}
	//返回1 or 0 or -1
	public static int compareOnlyDate(String dt1, java.util.Date d2) {
		//java.util.Date d1 = s2d(dt1);
		String dt2=getOnlyDate(d2);
		return compareOnlyDate(dt1, dt2);
	}

	public static int compareOnlyDate(java.util.Date d1, java.util.Date d2) {
		if (d1 == null)
			d1 = new Date();
		if (d2 == null)
			d2 = new Date();
		return d1.compareTo(d2);
	}

	public static boolean isInDate(String d1, String d2, String date) {
		return compareOnlyDate(date, d1) > -1 && compareOnlyDate(d2, date) > -1;
	}

	public static boolean isInDate(java.util.Date d1, java.util.Date d2, String date) {
		return compareOnlyDate(date, d1) > -1 && compareOnlyDate(d2, date) > -1;
	}

	public static int getDay() {
		return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
	}

	// idx:week index,beforeDay:how many days befor idx.
	public int WeekIdxCom(int idx, int beforeDay) {
		int retu = idx;
		for (int i = 0; i < beforeDay; i++) {
			if (retu == 1) {
				retu = 7;
			} else {
				retu--;
			}
		}
		return retu;
	}

	public static boolean examTime(String time) {
		try {
			formatterTime.parse(time);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	//
	public String getTodayOfWeekIndex(int type) {
		int retu = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
		if (type == 0) {
			return week_zh_info[retu]; // return retu : 日，周一
		} else if (type == 1) {
			return "周" + week_zh_info[retu]; // return retu : 周日，周一
		} else if (type == 2) {
			return "星期" + week_zh_info[retu]; // return retu : 星期日，星期一
		} else {
			if (retu == 0) {
				retu = 7; // return retu: = 7周日 ，＝1周一
			}
		}
		return String.valueOf(retu);
	}


	public static String format(java.util.Date date) {
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
		return ff.format(date);
	}
	
	public static String formatUS(java.util.Date date) {
		return new SimpleDateFormat("MM-dd-yyyy").format(date);
	}

	public static String format2(java.util.Date date) {
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return ff.format(date);
	}
	
	public static String format3(java.util.Date date) {
		SimpleDateFormat ff = new SimpleDateFormat("hh:mm");
		return ff.format(date);
	}
	
	public static String format(java.util.Date date, TimeZone tz) {
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
		ff.setTimeZone(tz);
		return ff.format(date);
	}

	public static String format2(java.util.Date date, TimeZone tz) {
		SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		ff.setTimeZone(tz);
		return ff.format(date);
	}
	
	public static String format3(java.util.Date date, TimeZone tz) {
		SimpleDateFormat ff = new SimpleDateFormat("hh:mm");
		ff.setTimeZone(tz);
		return ff.format(date);
	}
	
	public static String format(String str) {
		try {
			if (str.indexOf(" ") == -1) {
				return formatter.format(formatter.parse(str));
			} else if (str.indexOf(":") == -1) {
				return str;
			} else if (str.indexOf(":") == str.lastIndexOf(":")) {
				return formatterTime.format(formatterTime.parse(str));
			} else if (str.indexOf(":") != str.lastIndexOf(":")) {
				return formatterTimeSec.format(formatterTimeSec.parse(str));
			}
		} catch (Exception e) {
			str = "";
		}
		return str;
	}

	public static Date s2d(String str) {
		str = format(str);
		Date date = null;
		try {
			if (str.length() == "yyyy-MM-dd".length()) {
				date = formatter.parse(str);
			} else if (str.length() == "yyyy-MM-dd HH:mm".length()) {
				date = formatterTime.parse(str);
			} else if (str.length() == "yyyy-MM-dd HH:mm:ss".length()) {
				date = formatterTimeSec.parse(str);
			}
		} catch (Exception e) {
			return null;
		}
		return date;
	}

	public static Date add(Date d, int min) {
		return addH(d, min * 60 * 1000);
	}

	public static Date addH(Date d, long h) {// 毫秒
		long L = d.getTime();
		L = L + h;
		return new Date(L);
	}

	//只取得日期，不要时间
	public static String getOnlyDate(Date date){
		if(date==null)return "";
		String d=DateTimeUtil.format(date);
		if(d.indexOf(" ")<1){
			return "";
		}
		return d.substring(0,d.indexOf(" "));
	}

	public static void main(String args[]) {
		try {
			// System.out.println("zhouyi=" + isInDate("2011-4-28",
			// "2011-04-29","2011-4-27"));
			/*
			 * System.out.println(DateTime.getToday()); SimpleDateFormat
			 * formatYMD=new SimpleDateFormat("yyyy-MM-dd"); Date d=new Date();
			 * System.out.println(d); System.out.println(formatYMD.format(d));
			 * 
			 * SimpleDateFormat zhou=new SimpleDateFormat("E");
			 * System.out.println(zhou.format(s2d("2011-4-26")));
			 */
			System.out.println(DateTimeUtil.getToday());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
