package com.cointr.websocket;

public class OnMessageEvent {
    private String message;

    public OnMessageEvent(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
