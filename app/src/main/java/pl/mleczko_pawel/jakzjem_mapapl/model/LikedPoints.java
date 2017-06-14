package pl.mleczko_pawel.jakzjem_mapapl.model;

import com.orm.SugarRecord;

/**
 * Created by mlecz on 01.05.2017.
 */

public class LikedPoints extends SugarRecord {
    private Long pointId;

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }
}
