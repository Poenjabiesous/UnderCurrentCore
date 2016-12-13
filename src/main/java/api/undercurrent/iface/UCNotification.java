package api.undercurrent.iface;

import api.undercurrent.iface.notification.NotificationCheckResult;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public abstract class UCNotification {

    private String name;
    private String displayName;
    private String description;
    public boolean active;

    public UCNotification(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }

    public abstract NotificationCheckResult check() throws Exception;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
