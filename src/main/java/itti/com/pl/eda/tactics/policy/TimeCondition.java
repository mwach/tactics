package itti.com.pl.eda.tactics.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import itti.com.pl.eda.tactics.exception.TimeConditionException;
import itti.com.pl.eda.tactics.exception.TimeConditionException.TimeConditionExceptionType;
import itti.com.pl.eda.tactics.utils.StringUtils;

public class TimeCondition implements Serializable{


	private static final long serialVersionUID = 1L;

	private static final long mulYear = 12 * 31 * 24 * 60;
	private static final long mulMonth = 31 * 24 * 60;
	private static final int mulDay = 24 * 60;

	public enum Period{
		Once,
		EveryHour,
		EveryDay,
		EveryWeek,
		EveryMonth,
		EveryYear;
	;
//TODO: support for days of the week like Mon, Tue...
	public static Period getValue(String s) {

		if(StringUtils.isNullOrEmpty(s)){
			return null;

		}else{
			for (Period period : Period.values()) {
				if(period.name().equalsIgnoreCase(s)){
					return period;
				}
			}
			return null;
		}
	}

	public static List<String> getList() {

		List<String> list = new ArrayList<String>();
		for (Period period : Period.values()) {
			list.add(period.name());
		}
		return list;
	}};


	private long id = -1;

	//begin date
	private DateUnit begin = null;

	//end date
	private DateUnit end = null;

	//period - when policy should be active
	private Period period = null;


	private List<TimeCondition> conditions = new ArrayList<TimeCondition>();
	private LogicalOperator logicalOperator = LogicalOperator.And;
	private TimeCondition root = null;

	@SuppressWarnings("unused")
	private TimeCondition(){
	}

	public TimeCondition(LogicalOperator operator){
		this.logicalOperator = operator;
	}

	public TimeCondition(DateUnit pBeginDate, DateUnit pEndDate, Period pPeriod) throws TimeConditionException{
		this.begin = pBeginDate;
		this.end = pEndDate;
		this.period = pPeriod;
		validateData();
	}

	@SuppressWarnings("unused")
	private long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	public DateUnit getBegin() {
		return begin;
	}

	public void setBegin(DateUnit begin) {
		this.begin = begin;
	}

	public DateUnit getEnd() {
		return end;
	}

	public void setEnd(DateUnit end) {
		this.end = end;
	}

	public Period getPeriodEnum() {
		return period;
	}
	public String getPeriod() {
		return period != null ? period.name() : null;
	}


	public void setPeriod(String period) {
		this.period = Period.getValue(period);
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	

	public TimeCondition getRoot(){
		return root;
	}
	public void setRoot(TimeCondition pRoot){
		root = pRoot;
	}

	public void addCondition(TimeCondition cond){
		if(conditions == null){
			conditions = new ArrayList<TimeCondition>();
		}
		if(cond != null){
			conditions.add(cond);
		}
	}

	public List<TimeCondition> getConditions(){
		return conditions;
	}

	public int getConditionsLength(){
		return conditions == null ? 0 : conditions.size();
	}

	public void setConditions(List<TimeCondition> conds){
		conditions = conds;
	}

	public void setLogicalOperator(String oper){
		logicalOperator = LogicalOperator.getValue(oper);
	}
	public String getLogicalOperator(){
		return logicalOperator != null ? logicalOperator.name() : null;
	}
	public LogicalOperator getLogicalOperatorEnum(){
		return logicalOperator;
	}


	@Override
	public boolean equals(Object obj) {

		if((obj == null) || !(obj instanceof TimeCondition)){
			return false;
		}

		boolean result = true;

		TimeCondition tc = (TimeCondition)obj;
		if(getBegin() != null && tc.getBegin() != null){
			result = getBegin().equals(tc.getBegin());
		}
		if(getEnd() != null && tc.getEnd() != null){
			result &= getEnd().equals(tc.getEnd());
		}
		if(getPeriod() != null && tc.getPeriod() != null){
			result &= getPeriod().equals(tc.getPeriod());
		}
		if(getConditions() != null && tc.getConditions() != null && !getConditions().isEmpty() && !tc.getConditions().isEmpty()){
			result &= getConditions().equals(tc.getConditions());
		}

		return result;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(getConditionsLength() == 0){

			sb.append("begin: " + begin + ", end: " + end + ", period: " + period);

		}else{
			for (TimeCondition child : conditions) {
				sb.append("\n");
				sb.append(child.toString());
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	public String getFormattedDate(){

		if(period != null && begin != null && end != null){
			return "period: " + period.name() + 
			", begin: " + begin.getFormattedDate(period) + 
			", end: " + end.getFormattedDate(period);

		}else if(getConditions() != null && !getConditions().isEmpty()){

			StringBuffer sb = new StringBuffer("ITEMS:\n");
			for (TimeCondition cond : getConditions()) {
				sb.append(cond.getFormattedDate());
			}
			return sb.toString();
		}
		return null;
	}

	private void validateData() throws TimeConditionException{

		if(period == null){
			throw new TimeConditionException(TimeConditionExceptionType.PeriodIsNull);
		}

		if(begin == null){
			throw new TimeConditionException(TimeConditionExceptionType.BeginIsNull);
		}

		if(end == null){
			throw new TimeConditionException(TimeConditionExceptionType.EndIsNull);
		}

		int beginYear = begin.getYear();
		int endYear = end.getYear();

		int beginMonth = begin.getMonth();
		int endMonth = end.getMonth();

		int beginDay = begin.getDay();
		int endDay = end.getDay();

		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.YEAR, beginYear);
		cal.set(Calendar.MONTH, beginMonth - 1);
		int beginMaxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		cal.set(Calendar.YEAR, endYear);
		cal.set(Calendar.MONTH, endMonth - 1);
		int endMaxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		int beginHour = begin.getHour();
		int endHour = end.getHour();

		int beginMinute = begin.getMinute();
		int endMinute = end.getMinute();


		//in this case year is important
		if(period == Period.Once){
			if(beginYear <= 2000 || beginYear > 2100){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin year: " + beginYear);
			}
			if(endYear <= 2000){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "End year: " + endYear);
			}
			if(beginYear > endYear){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin year: " + beginYear + " is greater than End year: " + endYear);
			}
		}

		//in these cases month is important
		if(	period == Period.Once || 
			period == Period.EveryYear
		){
			if(beginMonth <= 0 || beginMonth > 12){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin month: " + beginMonth);
			}
			if(endMonth <= 0 || endMonth > 12){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "End month: " + endMonth);
			}
		}


		//in these cases day is important
		if(period == Period.Once ||
			period == Period.EveryYear ||
			period == Period.EveryMonth
		){
			if(beginDay <= 0 || beginDay > beginMaxDaysInMonth){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin day: " + beginDay);
			}

			if(endDay <= 0 || endDay > endMaxDaysInMonth){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "End day: " + endDay);
			}
		}

		//in these cases hours is important
		if(period == Period.Once ||
			period == Period.EveryYear ||
			period == Period.EveryMonth ||
			period == Period.EveryDay
		){
			if(beginHour < 0 || beginHour > 23){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin hour: " + beginHour);
			}
			if(endHour < 0 || endHour > 23){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "End hour: " + endHour);
			}
		}


		//in these cases hours is important
		if(period == Period.Once ||
			period == Period.EveryYear ||
			period == Period.EveryMonth ||
			period == Period.EveryDay ||
			period == Period.EveryHour
		){
			if(beginMinute < 0 || beginMinute > 59){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin minute: " + beginMinute);
			}
			if(endMinute < 0 || endMinute > 59){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "End minute: " + endMinute);
			}
		}

		if(period == Period.Once){
			if(
					(beginYear > endYear) ||
					(beginYear == endYear && beginMonth > endMonth) ||
					(beginYear == endYear && beginMonth == endMonth && beginDay > endDay) ||
					(beginYear == endYear && beginMonth == endMonth && beginDay == endDay && beginHour > endHour) ||
					(beginYear == endYear && beginMonth == endMonth && beginDay == endDay && beginHour == endHour && beginMinute >= endMinute)
			){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin: " + begin + " is later than End: " + end);
			}
		}

		if(period == Period.EveryYear){
			if(
					(beginMonth > endMonth) ||
					(beginMonth == endMonth && beginDay > endDay) ||
					(beginMonth == endMonth && beginDay == endDay && beginHour > endHour) ||
					(beginMonth == endMonth && beginDay == endDay && beginHour == endHour && beginMinute >= endMinute)
			){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin: " + begin + " is later than End: " + end);
			}
		}

		if(period == Period.EveryMonth){
			if(
					(beginDay > endDay) ||
					(beginDay == endDay && beginHour > endHour) ||
					(beginDay == endDay && beginHour == endHour && beginMinute >= endMinute)
			){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin: " + begin + " is later than End: " + end);
			}
		}

		if(period == Period.EveryDay){
			if(
					(beginHour > endHour) ||
					(beginHour == endHour && beginMinute >= endMinute)
			){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin: " + begin + " is later than End: " + end);
			}
		}

		if(period == Period.EveryHour){
			if(
					(beginMinute >= endMinute)
			){
				throw new TimeConditionException(TimeConditionExceptionType.InvalidValue, "Begin: " + begin + " is later than End: " + end);
			}
		}
	}

	public boolean isActive(DateUnit currDate) {

		if(period == Period.Once){

			long startTime = mulYear * begin.getYear() + mulMonth * begin.getMonth() + mulDay * begin.getDay() + 60 * begin.getHour() + begin.getMinute();
			long endTime = mulYear * end.getYear() + mulMonth * end.getMonth() + mulDay * end.getDay() + 60 * end.getHour() + end.getMinute();
			long currTime = mulYear * currDate.getYear() + mulMonth * currDate.getMonth() + mulDay * currDate.getDay() + 60 * currDate.getHour() + currDate.getMinute();

			return between(currTime, startTime, endTime);

		}else if(period == Period.EveryYear){

			long startTime = mulMonth * begin.getMonth() + mulDay * begin.getDay() + 60 * begin.getHour() + begin.getMinute();
			long endTime = mulMonth * end.getMonth() + mulDay * end.getDay() + 60 * end.getHour() + end.getMinute();
			long currTime = mulMonth * currDate.getMonth() + mulDay * currDate.getDay() + 60 * currDate.getHour() + currDate.getMinute();

			return between(currTime, startTime, endTime);

		}else if(period == Period.EveryMonth){

			int startTime = mulDay * begin.getDay() + 60 * begin.getHour() + begin.getMinute();
			int endTime = mulDay * end.getDay() + 60 * end.getHour() + end.getMinute();
			int currTime = mulDay * currDate.getDay() + 60 * currDate.getHour() + currDate.getMinute();

			return between(currTime, startTime, endTime);

		}else if(period == Period.EveryDay){

			int startTime = 60 * begin.getHour() + begin.getMinute();
			int endTime = 60 * end.getHour() + end.getMinute();
			int currTime = 60 * currDate.getHour() + currDate.getMinute();

			return between(currTime, startTime, endTime);

		}else if(period == Period.EveryHour){

			return between(currDate.getMinute(), begin.getMinute(), end.getMinute());
			
		}else if(period == null && getConditions() != null && !getConditions().isEmpty()){

			boolean valid = false;
			boolean and = getLogicalOperatorEnum() == LogicalOperator.And;
			if(and){
				valid = true;
			}

			for (TimeCondition condition : getConditions()) {
				if(and){
					valid &= condition.isActive(currDate);
				}else{
					valid |= condition.isActive(currDate);
				}
			}
			return valid;

		}else{
			return false;
		}
	}

	private boolean between(int currVal, int minVal, int maxVal) {
		return currVal >= minVal && currVal <= maxVal;
	}

	private boolean between(long currVal, long minVal, long maxVal) {
		return currVal >= minVal && currVal <= maxVal;
	}
}
