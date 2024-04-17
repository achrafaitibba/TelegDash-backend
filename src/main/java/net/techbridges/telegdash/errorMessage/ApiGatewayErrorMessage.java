package net.techbridges.telegdash.errorMessage;

public enum ApiGatewayErrorMessage {
    USER_NOT_FOUND("Error message hh...");

    private final String msg;
    ApiGatewayErrorMessage(String msg){
        this.msg = msg;
    }
    public String getMessage(){
        return this.msg;
    }
}
