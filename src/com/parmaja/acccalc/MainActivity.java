package com.parmaja.acccalc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parmaja.acccalc.R;

public class MainActivity extends Activity {
    private TextView txtDisplay;
    private ListView listItems; 
    private LinearLayout padLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Full Screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Load activity
		setContentView(R.layout.activity_main);
			
		txtDisplay = (TextView) findViewById(R.id.display);
		listItems = (ListView) findViewById(R.id.results);
		padLayout = (LinearLayout) findViewById(R.id.pad);
		
		
		//txtDisplay.setText("0".toString());		
		setDislpay("0");
		//listItems.add
		
		assignButtons(padLayout);
	}

	public void setDislpay(String s) {
		txtDisplay.setText(s.toString());
	}
	
	//Global Listener for all buttons
	View.OnClickListener onButtonClick = new View.OnClickListener(){	
		public void onClick(View v)  
		{  
			if (!v.getTag().equals(""))
				setDislpay(v.getTag().toString());
		}
	};	

	public void assignButtons(ViewGroup vg)  
	{  
		View v;
		int i =0;
		int c = vg.getChildCount();
		while (i < c) {
			v = (View) vg.getChildAt(i);
			System.out.println(v.getClass().getName());				
			if (v instanceof ViewGroup){
				assignButtons((ViewGroup) v);
			}
			else {				
				if (v.getTag()!=null && !v.getTag().equals("")) {
					v.setOnClickListener(onButtonClick);
				}				
			}
			i++;
		}
	}

//	   Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} 	

}
