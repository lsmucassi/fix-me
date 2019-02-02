package com.lmucassi.app.util.Validation;

public interface MessageValidationHandler {

    void setNextHandler(MessageValidationHandler nextHandler);
    boolean validateMessage(FixMessageValidator validMessage);
}
