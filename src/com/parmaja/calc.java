package com.parmaja;
/*
* 
* 
*/
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Arrays;
import java.math.*;

public class calc {

	protected enum CalcState {csFirst, csValid, csError};

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

	calc() {
		reset();
	}

	public double getDisplay() {
		return DispNumber;
	}

	protected String repeat(char c, int n) {
		char[] chars = new char[n];
		Arrays.fill(chars, c);
		return chars.toString();
	}

	public void setDisplay(double R, boolean ShouldKeepZeroes) {
		String S;
		int KeepZeroes;

		DispNumber = R;
		KeepZeroes = 0;
		int p = Number.indexOf('.');
		
		if (ShouldKeepZeroes && p >= 0) {
			int i = Number.length() - 1;
			while (i > p) {
				if (Number.charAt(i) == '0')
					KeepZeroes++;
				else
					break;
			}

			DecimalFormat df = new DecimalFormat("#0.0");
			S = df.format(R);

			if (KeepZeroes > 0)
				S = S + repeat('0', KeepZeroes);

			if (S.charAt(1) != '-')
				Sign = ' ';
			else {
				S = S.substring(1);
				Sign = '-';
			}

			if (S.length() > MaxDigits + 1 + MaxDecimals)
				error();
			else {
				if (S.endsWith("."))
					S = S.substring(S.length() - 1);
				Number = S;
			}
		}
	}

	protected void check() {		  
	    if (Status == CalcState.csFirst) {		    
	      Status = CalcState.csValid;
	      setDisplay(0, false);
	    }
	  }
	
	boolean process(String key) {	
		
		double r;	
		String s;		
		boolean result = true;
		
		key = key.toUpperCase();
		
		if ((Status == CalcState.csError) && (key != "C")) 
		    key = " ";
		  r = 0;
		  if (HexShown) {		  
		    r = getDisplay();
		    setDisplay(r, false);
		    HexShown = false;
		    if (key == "H")
		      key = " ";
		  }
		  
		  if (key == "X^Y")
		    key = "^";
		  else if (key == "_")
		    key = "+/-";

		  if (key.length() > 1) {	    
		      r = getDisplay();
		      if (key == "AC")
		        clear();
		      else if (key == "ON")
		        reset();
		      else if (key == "1/X")
		      {
		        if (r == 0)
		          error();
		        else
		          setDisplay(1 / r, false);
		      }
		      else if (key == "SQRT") {		      
		        if (r < 0)
		          error();
		        else 
		          setDisplay(Math.sqrt(r), false);
		      }
		      else if (key == "LOG") {		      
		        if (r <= 0)
		        	error(); 
		        else 
		        	setDisplay(Math.log(r), false);
		      }
		      else if (key == "X^2")
		        setDisplay(r * r, false);
		      else if (key == "+/-") {		      
		        if (Sign == ' ')
		          Sign = '-';
		        else
		          Sign = ' ';
		        r = getDisplay();
		        setDisplay(-r, true);
		      }
		      else if (key == "M+") {		      
		        Memory = Memory + r;
		        HaveMemory = true;
		      }
		      else if (key == "M-") {		      
		        Memory = Memory - r;
		        HaveMemory = true;
		      }
		      else if (key == "MR") {		      
		        check();
		        setDisplay(Memory, false);
		      }
		      else if (key == "MC") {		      
		        Memory = 0;
		        HaveMemory = false;
		      }
		  }
	
		  else {		    
			char k = key.charAt(0);  
		    
		    if (k >= '0' && k <= '9') { 
		        if (Number.length() < MaxDigits)
		        {
		          check();
		          if (Number == "0")
		            Number = "";
		          Number = Number + key; //check maybe must k not key
		          DispNumber = Double.parseDouble(Number);
		          //SetDisplay(StrToFloat(Number), True);
		        }
		      }
		    else if (k == '.')
		        {		    			    
		          check();
		          if (Number.indexOf('.') >= 0)
		            Number = Number + '.';
		        };
		      if (k == '~') //Delte
		        {
		          check();
		          if (Number.length() == 1)
		            Number = "0";
		          else
		            Number = LeftStr(Number, Length(Number) - 1);
		          setDisplay(StrToFloat(Number), True);// { !!! }
		        };
		      if (k == 'H')
		        {
		          r = getDisplay();
		          int x = Math.trunc(Math.abs(r));
		          Number = Integer.toHexString(x);
		          HexShown = true;
		        }
		      if (k == '^' || k == '+' || k == '-' || k == '*' || k == '/' || k == '%' || k == '=') 
		        {
		          if ((k == '=') && (Status == CalcState.csFirst)) //for repeat last operator
		          {
		            Status = csValid;
		            r = LastResult;
		            CurrentOperator = LastOperator;
		        }
		          else {
		            r = getDisplay();

		          if (Status == CalcState.csValid) 
		          {
		            Started = true;
		            if (CurrentOperator == '=')
		              s = " ";
		            else
		              s.valueOf(CurrentOperator);
		            Log(s + FloatToStrF(R, ffGeneral, MaxDecimals, MaxDigits));
		            Status = CalcState.csFirst;
		            LastOperator = CurrentOperator;
		            LastResult = r;
		            if (k == '%')
		            {
		              if (CurrentOperator == '+' || CurrentOperator == '-')         
		                r = Operand * r / 100;
		              else if (CurrentOperator == '*' || CurrentOperator == '/')         
		                r = r / 100;
		          }
		        }

		            if (CurrentOperator == '^') {
		              if ((Operand == 0) && (r <= 0)) 
		              	error(); 
		              else 
		                setDisplay(Math.pow(Operand, r), false);
		            }else if (CurrentOperator == '+')  
		              setDisplay(Operand + r, false);
		            else if (CurrentOperator == '-')
		              setDisplay(Operand - r, false);
		            else if (CurrentOperator == '*')
		              setDisplay(Operand * r, false);
		            else if (CurrentOperator == '/') {
		              if (r == 0)
		            	error(); 
		            	else 
		            	setDisplay(Operand / r, false);
		            }
		        }
		            if (k == '=')
		              log('=' + Number);
		  }
		          CurrentOperator = k;
		          Operand = getDisplay();
	}
		      if (k == 'C')
		        {
		          check();
		          setDisplay(0, true);
		        }
		        }
		      result = false;
		      }
		  refresh();
		  }
		  return result;
	}

	void clear() {
		if (Started)
			log(repeat('-', MaxDigits + 1 + MaxDecimals));
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

	void error() {
		Status = CalcState.csError;
		Number = "Error";
		Sign = ' ';
		refresh();
	}

	// This methods need to override;
	void log(String S) { // virtual

	}

	void refresh() {
		// virtual
	}
}
