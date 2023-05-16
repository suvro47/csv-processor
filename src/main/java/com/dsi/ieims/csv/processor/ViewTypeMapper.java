package com.dsi.ieims.csv.processor;


import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class ViewTypeMapper {

    private final Map<String, String> viewTypeMapper;

    public ViewTypeMapper() {
        this.viewTypeMapper = new HashMap<>();
        constructMapper();
    }

    private void constructMapper() {
        viewTypeMapper.put("Catchment Area Map", "CATCHMENT_AREA_MAP");
        viewTypeMapper.put("Catchment area Map", "CATCHMENT_AREA_MAP");
        viewTypeMapper.put("Gate", "GATE");
        viewTypeMapper.put("Gate Boundary", "GATE_BOUNDARY");
        viewTypeMapper.put("Long Full View of Total School Premise", "SCHOOL_FULL_VIEW");
        viewTypeMapper.put("School Coordinate", "SCHOOL_COORDINATE");
        viewTypeMapper.put("School Corinate", "SCHOOL_COORDINATE");
        viewTypeMapper.put("Shahid Minar", "SHAHID_MINAR");
        viewTypeMapper.put("Unused old building", "UNUSED_BUILDING");
        viewTypeMapper.put("Boundary", "BOUNDARY");
        viewTypeMapper.put("Others", "OTHERS");
        viewTypeMapper.put("Close Front View of Each Building", "BUILDING_FRONT_VIEW");
        viewTypeMapper.put("Close Rear View of Each Building", "BUILDING_REAR_VIEW");
        viewTypeMapper.put("Wash Block", "WASH_BLOCK");
    }
}
