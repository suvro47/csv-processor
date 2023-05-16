package com.dsi.ieims.csv.processor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchoolCodeSchoolIdMapper {

    @Autowired
    private JdbcTemplate ieimsTemplate;

    String SELECT_CODE_ID = "SELECT DISTINCT s.SCHOOL_ID , s.EMIS_CODE  FROM SCHOOL s";

    HashMap<String,String> map = new HashMap<>();

    public HashMap<String, String> getSchoolCodeSchoolIdMap() {
        List<Map<String, Object>> schools = ieimsTemplate.queryForList(SELECT_CODE_ID);
        for (Map<String, Object> school : schools) {
            String schoolId = String.valueOf(school.get("SCHOOL_ID"));
            String schoolCode = String.valueOf(school.get("EMIS_CODE"));
            map.put(schoolCode, schoolId);
        }
        return map;
    }

}
