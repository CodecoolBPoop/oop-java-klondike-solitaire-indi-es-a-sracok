package com.codecool.klondike;

public enum  Suit {
    HEARTS("hearts"), DIAMONDS("diamonds"), SPADES("spades"), CLUBS("clubs");

    private final String cardSuit;

    private Suit(String cardSuit){
        this.cardSuit=cardSuit;
    }

    public String getCardSuit() {
        return cardSuit;
    }
}
