package com.parmaja;

public class calc {
	enum CalcState {csFirst, csValid, csError};

    protected boolean Started = false;
    protected CalcState Status = CalcState.csFirst;
	protected String Number = "0";
	protected char Sign = ' ';	
	protected char CurrentOperator = '=';
	protected char LastOperator;
	protected double LastResult;
	protected double Operand;
	protected double Memory = 0;
	protected boolean HaveMemory = false;
	protected double DispNumber;
	protected boolean HexShown = false;
	
    public int MaxDecimals = 10;
	public int MaxDigits = 30;
	
	void calc() {
		reset();
	}
	
	boolean process(String sKey) {
	  return false;
	}
	
	void clear() {
	  if (Started) 
	    log("---------------------");
	  Started = false;
	  Status = CalcState.csFirst;
	  Number = "0";
	  Sign = ' ';
	  CurrentOperator = '=';		
	}
	
	void reset() {
	  clear();
	  HaveMemory = false;
	  Memory = 0;		
	}
	
	//This methods need to override;
	void log(String S){ //virtual;
		
	}
	
	void refresh() {; //virtual;
	
	}
}
