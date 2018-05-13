package papajj.scaleapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
            TextView tvConn = (TextView) findViewById(R.id.tvConn);
            TextView tvDisp = (TextView) findViewById(R.id.disp);
            Button bt1 = (Button) findViewById(R.id.bt1);
            Button bt2 = (Button) findViewById(R.id.bt2);
            Button bt3 = (Button) findViewById(R.id.bt3);
            Button bt4 = (Button) findViewById(R.id.bt4);
            Button bt5 = (Button) findViewById(R.id.bt5);
            Button bt6 = (Button) findViewById(R.id.bt6);
            Button bt7 = (Button) findViewById(R.id.bt7);
            Button bt8 = (Button) findViewById(R.id.bt8);
            Button bt9 = (Button) findViewById(R.id.bt9);
            Button bt10 = (Button) findViewById(R.id.bt10);

            CheckBox led1 = (CheckBox) findViewById(R.id.led1);
            CheckBox led2 = (CheckBox) findViewById(R.id.led2);
            CheckBox led3 = (CheckBox) findViewById(R.id.led3);
            CheckBox led4 = (CheckBox) findViewById(R.id.led4);
            CheckBox led5 = (CheckBox) findViewById(R.id.led5);

            if(nc.GetOnline()) {
                tvConn.setText("Připojeno");

                int cmdIn = nc.GetCmd();
                int dispVal = cmdIn & 0xFFFF;
                tvDisp.setText(Float.toString(dispVal / 10.0f));
                int ledState = (cmdIn >> 16) & 0xFF;

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
                nc.SetCmd(cmdOut);

            }
            else {
                tvConn.setText("Nepřipojeno");
                tvDisp.setText("-----");
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
