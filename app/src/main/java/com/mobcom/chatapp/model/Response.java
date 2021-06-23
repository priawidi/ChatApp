package com.mobcom.chatapp.model;

import javax.xml.transform.Result;

public class Response {
    private String multicast_id;
    private String success;
    private String failure;
    private String canonical_ids;
    private Results results;
    private Long message_id;

    public Long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Long message_id) {
        this.message_id = message_id;
    }

    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public String getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(String canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }
}
