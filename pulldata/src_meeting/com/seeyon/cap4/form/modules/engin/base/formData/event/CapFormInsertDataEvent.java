package com.seeyon.cap4.form.modules.engin.base.formData.event;


import com.seeyon.ctp.event.Event;

import java.util.List;
import java.util.Map;

public class CapFormInsertDataEvent extends Event {

    private Long id;
    private Map<String,Object> dataMap;
    private List<String> list;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public CapFormInsertDataEvent(Object source) {
        super(source);
    }
}
