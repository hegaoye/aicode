package io.aicode.project.entity;

import lombok.Data;

import java.util.List;

/**
 * Created by jiyan on 2018/9/20.
 */
@Data
public class ModelData {
    private String model;
    private List<MapClassTable> classes;

    public ModelData() {
    }

    public ModelData(String model, List<MapClassTable> classes) {
        this.model = model;
        this.classes = classes;
    }
}
