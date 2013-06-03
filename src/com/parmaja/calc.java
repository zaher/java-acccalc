package com.parmaja;

/*
 * 
 * 
 */
import java.text.DecimalFormat;
import java.util.Arrays;

public class calc {

	protected enum CalcState {
		csFirst, csValid, csError
	};

	protected boolean bStarted = false;
	protected CalcState Status = CalcState.csFirst;
	protected String strNumber = "0";
	protected char charSign = ' ';
	protected char charCurrentOperator = '=';
	protected char charLastOperator = ' ';
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
		String s;
		int KeepZeroes;

		DispNumber = R;
		KeepZeroes = 0;
		int p = strNumber.indexOf('.');

		if (ShouldKeepZeroes && p >= 0) {
			int i = strNumber.length() - 1;
			while (i > p) {
				if (strNumber.charAt(i) == '0')
					KeepZeroes++;
				else
					break;
			}

			DecimalFormat df = new DecimalFormat("#0.0");
			s = df.format(R);

			if (KeepZeroes > 0)
				s = s + repeat('0', KeepZeroes);

			if (s.charAt(1) != '-')
				charSign = ' ';
			else {
				s = s.substring(1);
				charSign = '-';
			}

			if (s.length() > MaxDigits + 1 + MaxDecimals)
				error();
			else {
				if (s.endsWith("."))
					s = s.substring(s.length() - 1);
				strNumber = s;
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
			else if (key == "1/X") {
				if (r == 0)
					error();
				else
					setDisplay(1 / r, false);
			} else if (key == "SQRT") {
				if (r < 0)
					error();
				else
					setDisplay(Math.sqrt(r), false);
			} else if (key == "LOG") {
				if (r <= 0)
					error();
				else
					setDisplay(Math.log(r), false);
			} else if (key == "X^2")
				setDisplay(r * r, false);
			else if (key == "+/-") {
				if (charSign == ' ')
					charSign = '-';
				else
					charSign = ' ';
				r = getDisplay();
				setDisplay(-r, true);
			} else if (key == "M+") {
				Memory = Memory + r;
				HaveMemory = true;
			} else if (key == "M-") {
				Memory = Memory - r;
				HaveMemory = true;
			} else if (key == "MR") {
				check();
				setDisplay(Memory, false);
			} else if (key == "MC") {
				Memory = 0;
				HaveMemory = false;
			}
		} else {
			char k = key.charAt(0);

			if (k >= '0' && k <= '9') {
				if (strNumber.length() < MaxDigits) {
					check();
					if (strNumber == "0")
						strNumber = "";
					strNumber = strNumber + key; // check maybe must k not key
					DispNumber = Double.parseDouble(strNumber);
					// SetDisplay(StrToFloat(Number), True);
				}
			} else if (k == '.') {
				check();
				if (strNumber.indexOf('.') >= 0)
					strNumber = strNumber + '.';
			}
			if (k == '~') // Delete
			{
				check();
				if (strNumber.length() == 1)
					strNumber = "0";
				else
					strNumber = strNumber.substring(0, strNumber.length() - 2);
				setDisplay(Double.valueOf(strNumber), true);// { !!! }
			}
			if (k == 'H') {
				r = getDisplay();
				strNumber = Long.toHexString(Math.round(r));
				HexShown = true;
			}
			if (k == '^' || k == '+' || k == '-' || k == '*' || k == '/'
					|| k == '%' || k == '=') {
				if ((k == '=') && (Status == CalcState.csFirst)) // for repeat
																	// last
																	// operator
				{
					Status = CalcState.csValid;
					r = LastResult;
					charCurrentOperator = charLastOperator;
				} else {
					r = getDisplay();

					if (Status == CalcState.csValid) {
						bStarted = true;
						if (charCurrentOperator == '=')
							s = " ";
						else
							s = String.valueOf(charCurrentOperator);

						DecimalFormat df = new DecimalFormat("#0.0");
						log(s + df.format(r));
						Status = CalcState.csFirst;
						charLastOperator = charCurrentOperator;
						LastResult = r;
						if (k == '%') {
							if (charCurrentOperator == '+'
									|| charCurrentOperator == '-')
								r = Operand * r / 100;
							else if (charCurrentOperator == '*'
									|| charCurrentOperator == '/')
								r = r / 100;
						}

						else if (charCurrentOperator == '^') {
							if ((Operand == 0) && (r <= 0))
								error();
							else
								setDisplay(Math.pow(Operand, r), false);
						} else if (charCurrentOperator == '+')
							setDisplay(Operand + r, false);
						else if (charCurrentOperator == '-')
							setDisplay(Operand - r, false);
						else if (charCurrentOperator == '*')
							setDisplay(Operand * r, false);
						else if (charCurrentOperator == '/') {
							if (r == 0)
								error();
							else
								setDisplay(Operand / r, false);
						}
					}
					if (k == '=')
						log('=' + strNumber);
				}
				charCurrentOperator = k;
				Operand = getDisplay();

				if (k == 'C') {
					check();
					setDisplay(0, true);
				}
			}
			result = false;
		}
		refresh();
		return result;
	}

	void clear() {
		if (bStarted)
			log(repeat('-', MaxDigits + 1 + MaxDecimals));
		bStarted = false;
		Status = CalcState.csFirst;
		strNumber = "0";
		charSign = ' ';
		charCurrentOperator = '=';
	}

	void reset() {
		clear();
		HaveMemory = false;
		Memory = 0;
	}

	void error() {
		Status = CalcState.csError;
		strNumber = "Error";
		charSign = ' ';
		refresh();
	}

	// This methods need to override;
	void log(String S) { // virtual

	}

	void refresh() {
		// virtual
	}
}
