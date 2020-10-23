package com.aicode.project.entity;

import com.aicode.map.entity.MapClassTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Created by jiyan on 2018/9/20.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModelData {
    private String model;
    private List<MapClassTable> classes;

}
