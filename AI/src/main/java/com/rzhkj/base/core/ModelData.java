package com.rzhkj.base.core;

import com.rzhkj.project.entity.MapClassTable;
import lombok.Data;

import java.util.List;

/**
 * Created by jiyan on 2018/9/20.
 */
@Data
public class ModelData {
    private String model;
    private List<MapClassTable> mapClassTables;

    public ModelData() {
    }

    public ModelData(String model, List<MapClassTable> mapClassTables) {
        this.model = model;
        this.mapClassTables = mapClassTables;
    }
}
