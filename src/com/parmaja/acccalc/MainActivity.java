package com.parmaja.acccalc;
/* to prevent reset on rotate then add to
 * android:configChanges in manifest for that activity
 * keyboard|keyboardHidden|orientation
 * http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android
 */
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parmaja.acccalc.R;
import com.parmaja.mini.Calculator;

public class MainActivity extends Activity {
	private TextView txtDisplay;
	private TextView txtMemory;
	private TextView txtOperator;
	private ListView listHistory;
	private LinearLayout padLayout;
	private ArrayList<String> history_array;
	private ArrayAdapter<String> history;

	final class Calc extends Calculator {

		@Override
		public void refresh() {
			super.refresh();
			if (txtDisplay != null)	{
				if (!this.cOperator.equals("="))
					txtOperator.setText(this.cOperator);
				else
					txtOperator.setText("");
				if (this.bHaveMemory)
					txtMemory.setText("M");
				else
					txtMemory.setText("");
				txtDisplay.setText(toString());
			}
		}

		@Override
		public void log(String S) {
			history.add(S);
		}

	}
	
	private Calc calc = new Calc();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Full Screen
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Load activity
		setContentView(R.layout.activity_main);

		txtDisplay = (TextView) findViewById(R.id.display);
		txtMemory = (TextView) findViewById(R.id.mem);
		txtOperator = (TextView) findViewById(R.id.opr);
		listHistory = (ListView) findViewById(R.id.history);
		padLayout = (LinearLayout) findViewById(R.id.pad);

		assignButtons(padLayout);

		history_array = new ArrayList<String>();		
		history = new ArrayAdapter<String>(this, R.layout.history, history_array);		
		
		listHistory.setAdapter(history);	
	  
		calc.clear();
	}

	@Override
	// Catch the keyboard too
		public boolean dispatchKeyEvent(KeyEvent KEvent) {
		int keyaction = KEvent.getAction();

		if (keyaction == KeyEvent.ACTION_DOWN) {
			// int keycode = KEvent.getKeyCode();
			int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState());
			char character = (char) keyunicode;
			calc.process(String.valueOf(character));

			// System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE="
			// + keycode);
		}
		return super.dispatchKeyEvent(KEvent);
	}

	public void setDislpay(String s) {
		txtDisplay.setText(s.toString());
	}

	// Global Listener for all buttons
	View.OnClickListener onButtonClick = new View.OnClickListener() {
		public void onClick(View v) {
			if (!v.getTag().equals("")) {
				calc.process(v.getTag().toString());
			}
		}
	};

	public void assignButtons(ViewGroup vg) {
		View v;
		int i = 0;
		int c = vg.getChildCount();
		while (i < c) {
			v = (View) vg.getChildAt(i);
			System.out.println(v.getClass().getName());
			if (v instanceof ViewGroup) {
				assignButtons((ViewGroup) v);
			} else {
				if (v.getTag() != null && !v.getTag().equals("")) {
					v.setOnClickListener(onButtonClick);
				}
			}
			i++;
		}
	}

	// Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
