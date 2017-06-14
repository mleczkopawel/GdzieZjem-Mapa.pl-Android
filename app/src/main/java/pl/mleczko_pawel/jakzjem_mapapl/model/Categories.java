package pl.mleczko_pawel.jakzjem_mapapl.model;

import com.orm.SugarRecord;

/**
 * Created by mlecz on 01.05.2017.
 */

public class Categories extends SugarRecord {
    private Integer remoteId;
    private String name;
    private String plName;

    public Integer getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlName() {
        if (plName == null) {
            return name;
        } else {
            return plName;
        }
    }

    public void setPlName(String plName) {
        this.plName = plName;
    }
}
