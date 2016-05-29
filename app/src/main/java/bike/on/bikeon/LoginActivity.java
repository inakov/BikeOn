package bike.on.bikeon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import bike.on.bikeon.web.requests.Account;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://185.14.184.35:9999/api/";

    private final Gson jsonParser = new Gson();
    private AsyncHttpClient client = new AsyncHttpClient();

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        if(!isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton)findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("facebook", "authentication was successful.");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                final String userId = loginResult.getAccessToken().getUserId();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {
                                    String userName = object.getString("name");

                                    Toast.makeText(getApplicationContext(), "Name " + userName, Toast.LENGTH_LONG).show();

                                    Account acc = new Account();
                                    acc.setUid(userId);
                                    acc.setName(userName);
                                    Log.i("facebook", "profile information obtained.");

                                    Log.i("auth", "auth request created.");
                                    StringEntity entity = new StringEntity(jsonParser.toJson(acc), "UTF-8");
                                    client.post(LoginActivity.this, SERVER_URL + "auth", entity, "application/json", new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if(statusCode == HttpStatus.SC_OK)
                                                Log.i("auth-service", "auth request successful.");
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Log.i("auth-service", "auth request failed.");
                                        }
                                    });
                                    Log.i("auth", "auth request sent.");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.w("Login", "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.w("Login", "Login attempt faild.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
