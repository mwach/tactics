package itti.com.pl.eda.tactics.policy;

import java.io.Serializable;

public interface ILMeasureInterface extends Serializable{

    public abstract void setConditions(PolicyTriple conditions);

    public abstract void setActions(PolicyTriple conditions);
}
