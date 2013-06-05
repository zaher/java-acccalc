package com.parmaja.acccalc;
/* to prevent reset on rotate then add to
 * android:configChanges in manifest for that activity
 * keyboard|keyboardHidden|orientation
 * http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android
 */
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

	private static final int DIALOG_ALERT = 10;
	
	final class Calc extends Calculator {

		@Override
		public void refresh() {
			super.refresh();
			if (txtDisplay != null)	{
				if (this.cOperator.equals("="))
					txtOperator.setText("");
				else if (this.cOperator.equals("*")) 
					txtOperator.setText("×");
				else if (this.cOperator.equals("/")) 
					txtOperator.setText("÷");
				else
					txtOperator.setText(this.cOperator);					
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

		txtDisplay.setHorizontallyScrolling(true);
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
	protected Dialog onCreateDialog(int id) {
	  switch (id) {
	    case DIALOG_ALERT:
	    // Create out AlterDialog
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Accountant Calculator is free to use\nLicensed under:\nCreativeCommons(CC BY-ND 3.0)\nWriten by: Zaher Dirkey");
	    builder.setCancelable(true);
	    builder.setPositiveButton("Ok", new OnAboutOkClickListener());
	    AlertDialog dialog = builder.create();
	    dialog.show();
	   }
	  return super.onCreateDialog(id);
	}
	
	private final class OnAboutOkClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	} 	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())
	    {
	    case R.id.action_exit:
	    	finish();
	        break;
	    case R.id.action_about:
	    	showDialog(DIALOG_ALERT);
	        break;
	    }
	    return true;
	}		
}
