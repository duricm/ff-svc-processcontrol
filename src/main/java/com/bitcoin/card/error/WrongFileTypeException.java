package com.bitcoin.card.error;

public class WrongFileTypeException extends RuntimeException {

    public WrongFileTypeException(String fileType) {
        super("Wrong file type : " + fileType + ". Accepted file types are PDF, JPG and PNG.");
    }

}
