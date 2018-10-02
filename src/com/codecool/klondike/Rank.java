package com.codecool.klondike;

public enum Rank {
    ONE(1, 0), TWO(2, 1), THREE(3, 2), FOUR(4, 3), FIVE(5, 4), SIX(6, 5), SEVEN(7, 6), EIGHT(8, 7), NINE(9, 8), TEN(10, 9), ELEVEN(11, 10), TWELVE(12, 11), THIRTEEN(13, 12);

    private final int cardRank;

    private final int index;

    private Rank(int cardRank, int index){
        this.cardRank=cardRank;
        this.index=index;
    }

    public int getCardRank() {
        return cardRank;
    }

    public int getIndex() {
        return index;
    }
}
