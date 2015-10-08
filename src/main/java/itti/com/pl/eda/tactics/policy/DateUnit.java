package itti.com.pl.eda.tactics.policy;

import java.io.Serializable;
import java.util.StringTokenizer;

import itti.com.pl.eda.tactics.policy.TimeCondition.Period;
import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * class represents single date unit
 * date unit contains information about date and time
 * @author marcin
 *
 */
public class DateUnit implements Serializable{

	private static final long serialVersionUID = 1L;

	private int year = -1;
	private int month = -1;
	private int day = -1;

	private int hour = -1;
	private int minute = -1;


	public int getYear() {
		return year;
	}
	public void setYear(int Year) {
		this.year = Year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int Month) {
		this.month = Month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int Day) {
		this.day = Day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int Hour) {
		this.hour = Hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int Minute) {
		this.minute = Minute;
	}


	/**
	 * default constructor
	 * @param year value of the year
	 * @param month value of the month
	 * @param day value of the day
	 * @param hour value of the hour
	 * @param minute value of the minute
	 */
	public DateUnit(int year, int month, int day, int hour, int minute){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	/**
	 * constructs DateUnit from given and well-formatted string
	 * @param string parsed into string DateUnit object
	 * @return instance of DateUnit object, or null if process fails
	 */
	public static DateUnit parseString(String string){

		if(StringUtils.isNullOrEmpty(string)){
			return null;

		}else{

			DateUnit du = null;

			StringTokenizer stDateTime = new StringTokenizer(string, " ");
			if(stDateTime.countTokens() != 2){
				return null;
			}

			String date = stDateTime.nextToken();
			String time = stDateTime.nextToken();

			StringTokenizer stDate = new StringTokenizer(date, "-");

			if(stDate.countTokens() != 3){
				return null;
			}

			String year = stDate.nextToken();
			String month = stDate.nextToken();
			String day = stDate.nextToken();

			int yearInt = -1;
			if(year.equalsIgnoreCase("NULL")){
			}else{
				yearInt = StringUtils.getIntValue(year);
			}

			int monthInt = -1;
			if(month.equalsIgnoreCase("NULL")){
			}else{
				monthInt = StringUtils.getIntValue(month);
			}

			int dayInt = -1;
			if(day.equalsIgnoreCase("NULL")){
			}else{
				dayInt = StringUtils.getIntValue(day);
			}

			StringTokenizer stTime = new StringTokenizer(time, "-");

			if(stTime.countTokens() != 2){
				return null;
			}

			String hour = stTime.nextToken();
			String minute = stTime.nextToken();

			int hourInt = -1;
			if(hour.equalsIgnoreCase("NULL")){
			}else{
				hourInt = StringUtils.getIntValue(hour);
			}

			int minuteInt = -1;
			if(minute.equalsIgnoreCase("NULL")){
			}else{
				minuteInt = StringUtils.getIntValue(minute);
			}

			du = new DateUnit(yearInt, monthInt, dayInt, hourInt, minuteInt);

			return du;
		}
	}

	/**
	 * parses DateUnit object into string
	 * @param dateUnit DateUnit instance
	 * @return parsed DateUnit instance, or null if process fails
	 */
	public static String parseDate(DateUnit dateUnit){

		if(dateUnit == null){
			return null;

		}else{

			StringBuffer sb = new StringBuffer();

			if(dateUnit.getYear() == -1){
				sb.append("NULL-");
			}else{
				sb.append(dateUnit.getYear() + "-");
			}

			if(dateUnit.getMonth() == -1){
				sb.append("NULL-");
			}else{
				sb.append(dateUnit.getMonth() + "-");
			}
			if(dateUnit.getDay() == -1){
				sb.append("NULL ");
			}else{
				sb.append(dateUnit.getDay() + " ");
			}

			if(dateUnit.getHour() == -1){
				sb.append("NULL-");
			}else{
				sb.append(dateUnit.getHour() + "-");
			}

			if(dateUnit.getMinute() == -1){
				sb.append("NULL");
			}else{
				sb.append(dateUnit.getMinute());
			}

			return sb.toString();
		}
	}

	@Override
	public String toString() {
		return year + "-" + month + "-" + day + " " + hour + ":" + minute;
	}


	@Override
	public boolean equals(Object obj) {

		if(obj == null || !(obj instanceof DateUnit)){
			return false;
		}

		DateUnit du = (DateUnit)obj;

		return 
			getYear() == du.getYear() && 
			getMonth() == du.getMonth() &&
			getDay() == du.getDay() &&
			getHour() == du.getHour() &&
			getMinute() == du.getMinute();
	}


	@Override
	public DateUnit clone() {

		DateUnit clone = new DateUnit(getYear(), getMonth(), getDay(), getHour(), getMinute());
		return clone;
	}


	/**
	 * parses and formats current DateUnit object into string based on selected period
	 * @param period indicates which period should be used to parse item
	 * @return well-formatted string, or null if process fails
	 */
	public String getFormattedDate(Period period) {

		if(period != null){
			if(period == Period.Once){
				return StringUtils.getFormattedStringAddDecimals(year, 4) + "-" + 
					StringUtils.getFormattedStringAddDecimals(month, 2) + "-" +
					StringUtils.getFormattedStringAddDecimals(day, 2) + " " +
					StringUtils.getFormattedStringAddDecimals(hour, 2) + ":" +
					StringUtils.getFormattedStringAddDecimals(minute, 2);

			}else if(period == Period.EveryYear){
				return "yyyy-" + 
					StringUtils.getFormattedStringAddDecimals(month, 2) + "-" +
					StringUtils.getFormattedStringAddDecimals(day, 2) + " " +
					StringUtils.getFormattedStringAddDecimals(hour, 2) + ":" +
					StringUtils.getFormattedStringAddDecimals(minute, 2);

			}else if(period == Period.EveryMonth){
				return "yyyy-mm-" + 
					StringUtils.getFormattedStringAddDecimals(day, 2) + " " +
					StringUtils.getFormattedStringAddDecimals(hour, 2) + ":" +
					StringUtils.getFormattedStringAddDecimals(minute, 2);

			}else if(period == Period.EveryDay){
				return "yyyy-mm-dd " + 
					StringUtils.getFormattedStringAddDecimals(hour, 2) + ":" +
					StringUtils.getFormattedStringAddDecimals(minute, 2);

			}else{
				return "yyyy-mm-dd hh:" + 
					StringUtils.getFormattedStringAddDecimals(minute, 2);
			}
		}
		return null;
	}
}
