package api.undercurrent.iface.notification;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public class NotificationCheckResult {

    private boolean shouldPublish;
    private String message;

    public NotificationCheckResult(boolean shouldPublish, String message) {
        this.shouldPublish = shouldPublish;
        this.message = message;
    }

    public boolean isShouldPublish() {
        return shouldPublish;
    }

    public String getMessage() {
        return message;
    }
}
