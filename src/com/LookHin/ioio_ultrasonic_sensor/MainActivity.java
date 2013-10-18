package com.LookHin.ioio_ultrasonic_sensor;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*
 * TRIG = PIN 34
 * ECHO = PIN 35
 * VCC = PIN +5V
 * GND = PIN GND
 */

public class MainActivity extends IOIOActivity {
	
	private ToggleButton toggleButton1;
	private TextView textView1;
	private double DistanceOutput;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView1 = (TextView) findViewById(R.id.textView1);
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.action_about:
        	//Toast.makeText(getApplicationContext(), "Show About", Toast.LENGTH_SHORT).show();
        	
        	Intent about = new Intent(this, AboutActivity.class);
    		startActivity(about);
    		
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
    
    
    class Looper extends BaseIOIOLooper {
		
    	private DigitalOutput digital_led1;
    	
    	private DigitalOutput UltraSonicTrigger;
		private PulseInput UltraSonicEcho;

		@Override
		protected void setup() throws ConnectionLostException {
			
			digital_led1 = ioio_.openDigitalOutput(0,true);
			
			UltraSonicTrigger = ioio_.openDigitalOutput(34,false);
			UltraSonicEcho = ioio_.openPulseInput(35, PulseMode.POSITIVE);
					
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), "IOIO Connect", Toast.LENGTH_SHORT).show();
				}
			});
			
		}

		@Override
		public void loop() throws ConnectionLostException {
			
			try{
				digital_led1.write(!toggleButton1.isChecked());
				
				
				UltraSonicTrigger.write(false);
				
				TimeUnit.MICROSECONDS.sleep(2);
				
				UltraSonicTrigger.write(true);
				
				TimeUnit.MICROSECONDS.sleep(10);
					
				UltraSonicTrigger.write(false);
			
				// For Centimeters
				DistanceOutput = (UltraSonicEcho.getDuration() * 1000000) / 58;
					
				// For Inches
				//DistanceOutput = (UltraSonicEcho.getDuration() * 1000000) / 148;
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						textView1.setText(String.format("%.02f",DistanceOutput));
					}
				});
	
				
				Thread.sleep(50);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
		}
	}
    

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	    
}
