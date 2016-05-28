package bike.on.bikeon.web.service;

import android.util.Log;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import bike.on.bikeon.web.requests.Account;
import bike.on.bikeon.web.requests.LockRequest;
import bike.on.bikeon.web.requests.UnlockRequest;
import bike.on.bikeon.web.response.LockResponse;
import bike.on.bikeon.web.response.UnlockResponse;

/**
 * Created by inakov on 28.05.16.
 */
public class BikeOnService {

    private static final String SERVER_URL = "http://localhost:8080/service/";
    private final RestTemplate restTemplate;

    public BikeOnService(){
        restTemplate = new RestTemplate();
        // Add the String message converter
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        Log.i("Initialization", "BikeOnService initialized.");
    }

    public void auth(Account account){
        Gson jsonParser = new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonParser.toJson(account), headers);

        Log.i("auth", "auth request created.");
        restTemplate.postForEntity(SERVER_URL + "auth", entity, String.class);
        Log.i("auth", "auth request sent.");
    }

    public LockResponse lock(LockRequest lockRequest){
        Gson jsonParser = new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonParser.toJson(lockRequest), headers);

        Log.i("lock", "lock request created.");
        String response =
                restTemplate.postForEntity(SERVER_URL + "lock", entity, String.class).getBody();
        Log.i("lock", "lock request sent.");

        return jsonParser.fromJson(response, LockResponse.class);
    }

    public UnlockResponse unlock(UnlockRequest unlockRequest){
        Gson jsonParser = new Gson();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonParser.toJson(unlockRequest), headers);

        Log.i("unlock", "auth request created.");
        String response =
                restTemplate.postForEntity(SERVER_URL + "unlock", entity, String.class).getBody();
        Log.i("unlock", "auth request sent.");

        return jsonParser.fromJson(response, UnlockResponse.class);
    }
}
