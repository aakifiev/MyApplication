package ru.aakifiev.myapplication.model;

import java.io.Serializable;
import java.util.Date;

public class LikadaProtocol implements Serializable {

    private String messageId;
    private String message;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
