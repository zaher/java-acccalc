package com.parmaja;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.Format;
import java.util.Arrays;

import android.text.TextUtils.StringSplitter;

public class calc {

	protected enum CalcState {
		csFirst, csValid, csError
	};

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
		if (ShouldKeepZeroes && p > 0) {
			int i = Number.length() - 1;
			while (i > (p + 1)) {
				if (Number.charAt(i) == '0')
					KeepZeroes++;
				else
					break;
			}

			NumberFormat df = new DecimalFormat("#0.0");
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
				Error();
			else {
				if (S.endsWith("."))
					S = S.substring(S.length() - 1);
				Number = S;
			}
		}
	}

	boolean process(String key) {
		double r;
		int x;
		void CheckFirst() {		  
		    if (Status == csFirst) {		    
		      Status = CalcState.csValid;
		      setDisplay(0, false);
		    }
		  }
		String s;
		
		boolean result = true;
		
		key = UpperCase(key);
		
		if ((Status == CalcState.csError) && (key != 'C')) 
		    key = ' ';
		  r = 0;
		  if (HexShown) {		  
		    r = getDisplay();
		    setDisplay(r, false);
		    HexShown = False;
		    if (key == 'H')
		      key = ' ';
		  }
		  
		  if (key == "X^Y")
		    key = "^";
		  else if (Key == "_")
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
		          setDisplay(sqrt(r), false);
		      }
		      else if (key == "LOG") {		      
		        if (r <= 0)
		        	error(); 
		        else 
		        	setDisplay(ln(R), false);
		      }
		      else if (key == "X^2")
		        setDisplay(R * R, false);
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
		        CheckFirst();
		        setDisplay(Memory, false);
		      }
		      else if (key == "MC") {		      
		        Memory = 0;
		        HaveMemory = false;
		      }
		  }
	}
		  else
		    case key[1] of
		      '0'..'9':
		        if Length(Number) < MaxDigits then
		        begin
		          CheckFirst;
		          if Number = '0' then
		            Number := '';
		          Number := Number + Key;
		          DispNumber := StrToFloat(Number);
		          //SetDisplay(StrToFloat(Number), True);
		        end;
		      '.':
		        begin
		          CheckFirst;
		          if Pos('.', Number) = 0 then
		            Number := Number + '.';
		        end;
		      #8:
		        begin
		          CheckFirst;
		          if Length(Number) = 1 then
		            Number := '0'
		          else
		            Number := LeftStr(Number, Length(Number) - 1);
		          SetDisplay(StrToFloat(Number), True); { !!! }
		        end;
		      'H':
		        begin
		          GetDisplay(R);
		          X := trunc(abs(R));
		          Number := IntToHex(longint(X), 8);
		          HexShown := True;
		        end;
		      '^', '+', '-', '*', '/', '%', '=':
		        begin
		          if (Key[1] = '=') and (Status = csFirst) then //for repeat last operator
		          begin
		            Status := csValid;
		            R := LastResult;
		            CurrentOperator := LastOperator;
		          end
		          else
		            GetDisplay(R);

		          if (Status = csValid) then
		          begin
		            Started := True;
		            if CurrentOperator = '=' then
		              s := ' '
		            else
		              s := CurrentOperator;
		            Log(s + FloatToStrF(R, ffGeneral, MaxDecimals, MaxDigits));
		            Status := csFirst;
		            LastOperator := CurrentOperator;
		            LastResult := R;
		            if Key = '%' then
		            begin
		              case CurrentOperator of
		                '+', '-': R := Operand * R / 100;
		                '*', '/': R := R / 100;
		              end;
		            end;

		            case CurrentOperator of
		              '^': if (Operand = 0) and (R <= 0) then Error else SetDisplay(Power(Operand, R), False);
		              '+': SetDisplay(Operand + R, False);
		              '-': SetDisplay(Operand - R, False);
		              '*': SetDisplay(Operand * R, False);
		              '/': if R = 0 then Error else SetDisplay(Operand / R, False);
		            end;
		            if (Key[1] = '=') then
		              Log('=' + Number);
		          end;
		          CurrentOperator := Key[1];
		          GetDisplay(Operand);
		        end;
		      'C':
		        begin
		          CheckFirst;
		          SetDisplay(0, True);
		        end;
		    else
		      Result := False;
		    end;
		  Refresh;
		end;
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

	void Error() {
		Status = CalcState.csError;
		Number = "Error";
		Sign = ' ';
		refresh();
	}

	// This methods need to override;
	void log(String S) { // virtual;

	}

	void refresh() {
		; // virtual;

	}
}
