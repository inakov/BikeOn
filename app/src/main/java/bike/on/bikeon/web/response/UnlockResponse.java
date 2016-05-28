package bike.on.bikeon.web.response;

/**
 * Created by inakov on 28.05.16.
 */
public class UnlockResponse {

    private Long lockTime;
    private Long unlockTime;
    private Double price;

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public Long getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Long unlockTime) {
        this.unlockTime = unlockTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
