package pl.mleczko_pawel.jakzjem_mapapl.model;

import com.orm.SugarRecord;

/**
 * Created by mlecz on 01.05.2017.
 */

public class CategoriesToPoints extends SugarRecord {
    private Integer categoryId;
    private Integer pointId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
}
