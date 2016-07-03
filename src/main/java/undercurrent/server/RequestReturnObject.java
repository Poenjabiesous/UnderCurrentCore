package undercurrent.server;

import com.google.common.base.Throwables;
import com.google.gson.JsonElement;

/**
 * Created by Niel on 10/16/2015.
 */
public class RequestReturnObject {

    private boolean status;
    private JsonElement data;
    private String error_message;

    public RequestReturnObject(boolean status, JsonElement data) {
        this.status = status;
        this.data = data;
    }

    public RequestReturnObject(Throwable e) {
        this.status = false;
        this.error_message = Throwables.getStackTraceAsString(e);
    }

    public RequestReturnObject(boolean status) {
        this.status = status;
    }

    public RequestReturnObject(boolean status, String error_message) {
        this.status = status;
        this.error_message = error_message;
    }


    public boolean isStatus() {
        return status;
    }

    public JsonElement getData() {
        return data;
    }

    public String getError_message() {
        return error_message;
    }
}
