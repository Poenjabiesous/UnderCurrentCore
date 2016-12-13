package undercurrentcore.persist;

import java.util.List;

/**
 * Created by Niel on 10/21/2015.
 */
public class UCPlayerDTO {

    private String uuid;
    private String playerName;
    private String registrationDate;

    public UCPlayerDTO(String uuid, String playerName, String registrationDate) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.registrationDate = registrationDate;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
}
