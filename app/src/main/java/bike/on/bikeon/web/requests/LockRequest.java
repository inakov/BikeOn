package bike.on.bikeon.web.requests;

/**
 * Created by inakov on 28.05.16.
 */
public class LockRequest {
    private String type = "lock";
    private String uid;
    private String device_id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
