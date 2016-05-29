package bike.on.bikeon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import bike.on.bikeon.web.requests.LockRequest;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_STATION_ID = "bike.on.bikeon.STATION_ID";
    public final static String EXTRA_LOCK_TIME = "bike.on.bikeon.LOCK_TIME";

    private static final String SERVER_URL = "http://185.14.184.35:9999/api/";

    private final Gson jsonParser = new Gson();
    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FB Login
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Default shit
        setContentView(R.layout.activity_main);

        if(!isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
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

    public void lockBike(View view) {

        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String contents = data.getStringExtra("SCAN_RESULT");
                Log.i("QR", "Scan result: " + contents);

                LockRequest lockRequest = new LockRequest();
                lockRequest.setUid(AccessToken.getCurrentAccessToken().getUserId());
                lockRequest.setDevice_id(contents);

                StringEntity entity = new StringEntity(jsonParser.toJson(lockRequest), "UTF-8");
                client.post(MainActivity.this, SERVER_URL + "lock", entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == HttpStatus.SC_OK){
                            Log.i("lock-service", "lock request successful.");
                            moveToUnlockActivity(contents);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i("lock-service", "lock request failed.");
                    }
                });
            }

            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private void moveToUnlockActivity(String stationId){
        Intent intent = new Intent(this, UnlockActivity.class);
        intent.putExtra(EXTRA_STATION_ID, stationId);

        Long locktime = System.currentTimeMillis();
        intent.putExtra(EXTRA_LOCK_TIME, locktime);
        startActivity(intent);
    }
}
