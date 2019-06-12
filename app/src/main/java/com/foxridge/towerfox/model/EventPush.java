package com.foxridge.towerfox.model;

public class EventPush {

    public String message;
    public String type;

    public EventPush(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}