package papajj.scaleapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    NetClient nc;
    final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // for hiding title

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        nc = new NetClient("192.168.2.1", 2000);
        nc.start();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {UpdateGUI();}
        }, 0, 20);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            TextView tvDisp = (TextView) findViewById(R.id.disp);
            ImageButton bt1 = (ImageButton) findViewById(R.id.bt1);
            ImageButton bt2 = (ImageButton) findViewById(R.id.bt2);
            ImageButton bt3 = (ImageButton) findViewById(R.id.bt3);
            ImageButton bt4 = (ImageButton) findViewById(R.id.bt4);
            ImageButton bt5 = (ImageButton) findViewById(R.id.bt5);
            ImageButton bt6 = (ImageButton) findViewById(R.id.bt6);
            ImageButton bt7 = (ImageButton) findViewById(R.id.bt7);
            ImageButton bt8 = (ImageButton) findViewById(R.id.bt8);
            ImageButton bt9 = (ImageButton) findViewById(R.id.bt9);
            ImageButton bt10 = (ImageButton) findViewById(R.id.bt10);
            ImageButton bt11 = (ImageButton) findViewById(R.id.bt11);

            CheckBox led1 = (CheckBox) findViewById(R.id.led1);
            CheckBox led2 = (CheckBox) findViewById(R.id.led2);
            CheckBox led3 = (CheckBox) findViewById(R.id.led3);
            CheckBox led4 = (CheckBox) findViewById(R.id.led4);

            if(nc.GetOnline()) {
                int cmdIn = nc.GetCmd();
                int dispVal = cmdIn & 0xFFFF;
                tvDisp.setText(Float.toString(dispVal / 10.0f));
                int ledState = (cmdIn >> 16) & 0xFF;
                if ((ledState & 1 << 0) != 0) led1.setChecked(true); else led1.setChecked(false);
                if ((ledState & 1 << 1) != 0) led2.setChecked(true); else led2.setChecked(false);
                if ((ledState & 1 << 2) != 0) led3.setChecked(true); else led3.setChecked(false);
                if ((ledState & 1 << 3) != 0) led4.setChecked(true); else led4.setChecked(false);

                int cmdOut = 0;
                if (bt1.isPressed()) cmdOut = 1;
                if (bt2.isPressed()) cmdOut |= 1 << 1;
                if (bt3.isPressed()) cmdOut |= 1 << 2;
                if (bt4.isPressed()) cmdOut |= 1 << 3;
                if (bt5.isPressed()) cmdOut |= 1 << 4;
                if (bt6.isPressed()) cmdOut |= 1 << 5;
                if (bt7.isPressed()) cmdOut |= 1 << 6;
                if (bt8.isPressed()) cmdOut |= 1 << 7;
                if (bt9.isPressed()) cmdOut |= 1 << 8;
                if (bt10.isPressed()) cmdOut |= 1 << 9;
                if (bt11.isPressed()) cmdOut |= 1 << 10;
                nc.SetCmd(cmdOut);
            }
            else {
                tvDisp.setText("0000000");
            }
        }
    };

    private void UpdateGUI() {
        myHandler.post(myRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
