package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.exception.TimeConditionException;
import itti.com.pl.eda.tactics.exception.TimeConditionException.TimeConditionExceptionType;
import itti.com.pl.eda.tactics.policy.TimeCondition;
import itti.com.pl.eda.tactics.time.controller.TimeEvent;

public class TimeEventTranslator {
//TODO: improve the whole process

	public static TimeEvent getTimeEvent(PolicyHighLevel phl) throws TimeConditionException{

		if(phl != null && phl.getTimeConditions() != null){

			if(phl.getId() < 0 || phl.getAuthor() == null || phl.getAuthor().getId() < 0){
				throw new TimeConditionException(TimeConditionExceptionType.TranslateException, "Id: " + phl.getId() + ", uId: " + (phl.getAuthor() == null ? "null" : phl.getAuthor().getId()));
			}

			TimeCondition timeCond = phl.getTimeConditions();

			TimeEvent te = new TimeEvent(phl.getId(), phl.getAuthor().getId(), timeCond);
			return te;

		}else{
			return null;
		}
	}
}
