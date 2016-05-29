package bike.on.bikeon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import bike.on.bikeon.web.requests.UnlockRequest;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UnlockActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://185.14.184.35:9999/api/";

    private final Gson jsonParser = new Gson();
    private AsyncHttpClient client = new AsyncHttpClient();

    TextView timerMin;
    TextView timerSec;
    long startTime = 0;
    String stationId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        if(!isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_unlock);

        Intent intent = getIntent();
        startTime = intent.getLongExtra(MainActivity.EXTRA_LOCK_TIME, System.currentTimeMillis());
        stationId = intent.getStringExtra(MainActivity.EXTRA_STATION_ID);

        timerMin = (TextView) findViewById(R.id.timerMin);
        timerSec = (TextView) findViewById(R.id.timerSec);
        timerHandler.postDelayed(timerRunnable, 0);

    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerMin.setText(String.format("%02d:", minutes));
            timerSec.setText(String.format("%02d", seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    public void unlockBike(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Unlock")
                .setMessage("Are you sure you want to unlock your bike?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UnlockRequest unlockRequest = new UnlockRequest();
                        unlockRequest.setDevice_id(stationId);
                        unlockRequest.setUid(AccessToken.getCurrentAccessToken().getUserId());

                        StringEntity entity = new StringEntity(jsonParser.toJson(unlockRequest), "UTF-8");
                        client.post(UnlockActivity.this, SERVER_URL + "unlock", entity, "application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if(statusCode == HttpStatus.SC_OK) {
                                    Log.i("unlock-service", "unlock request successful.");
                                    moveTolockActivity();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.i("unlock-service", "lock request failed.");
                            }
                        });

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void moveTolockActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

}