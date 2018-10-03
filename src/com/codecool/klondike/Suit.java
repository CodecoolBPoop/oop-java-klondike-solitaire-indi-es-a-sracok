package com.codecool.klondike;

public enum  Suit {
    HEARTS("hearts", "red"), DIAMONDS("diamonds", "red"), SPADES("spades", "black"), CLUBS("clubs", "black");

    private final String cardSuit;

    private  final String color;

    Suit(String cardSuit, String color){
        this.cardSuit=cardSuit;
        this.color=color;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public String getColor() {
        return color;
    }
}
