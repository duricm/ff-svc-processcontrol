package com.bitcoin.card.error;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super("Card id not found : " + id);
    }

}
