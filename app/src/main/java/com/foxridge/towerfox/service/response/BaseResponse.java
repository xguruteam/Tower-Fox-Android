package com.foxridge.towerfox.service.response;

public class BaseResponse {
    private String ServiceStatus;
    private String ServiceMessage;

    public String getServiceStatus() {
        return ServiceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        ServiceStatus = serviceStatus;
    }

    public String getServiceMessage() {
        return ServiceMessage;
    }

    public void setServiceMessage(String serviceMessage) {
        ServiceMessage = serviceMessage;
    }
}
