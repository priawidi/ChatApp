package com.mobcom.chatapp.model;

import java.util.Map;

public class Data {
    private Map<String, String> data_id;

    public Data(Map<String, String> data_id) {
        this.data_id = data_id;
    }

    public Map<String, String> getData_id() {
        return data_id;
    }

    public void setData_id(Map<String, String> data_id) {
        this.data_id = data_id;
    }
}
