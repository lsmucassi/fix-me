package com.lmucassi.app.util.Validation;

public class MessageForwarding implements MessageValidationHandler{


    @Override
    public void setNextHandler(MessageValidationHandler nextHandler) {

    }

    @Override
    public boolean validateMessage(FixMessageValidator validMessage){
        return true;
    }

}
