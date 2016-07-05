package undercurrentcore.persist;

import java.util.List;

/**
 * Created by Niel on 10/21/2015.
 */
public class UCPlayerDTO {

    private String uuid;
    private List<UCBlockDTO> blocks;

    public String getUuid() {

        return uuid;
    }

    public void setUuid(String uuid) {

        this.uuid = uuid;
    }

    public List<UCBlockDTO> getBlocks() {

        return blocks;
    }

    public void setBlocks(List<UCBlockDTO> blocks) {

        this.blocks = blocks;
    }
}
