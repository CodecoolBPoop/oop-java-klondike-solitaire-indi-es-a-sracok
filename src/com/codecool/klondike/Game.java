package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK && card.getContainingPile().getTopCard() == card) {
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card + " to the waste.");
        }
        else if (card.getContainingPile().getPileType() == Pile.PileType.TABLEAU && card.getContainingPile().getTopCard() == card && card.isFaceDown()){
            card.flip();
            card.setMouseTransparent(false);
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (activePile.getPileType() == Pile.PileType.STOCK)
            return;
        double offsetX = e.getSceneX() - dragStartX;
        double offsetY = e.getSceneY() - dragStartY;

        draggedCards.clear();
        draggedCards.add(card);

        card.getDropShadow().setRadius(20);
        card.getDropShadow().setOffsetX(10);
        card.getDropShadow().setOffsetY(10);

        card.toFront();
        card.setTranslateX(offsetX);
        card.setTranslateY(offsetY);
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        List<Pile> piles = FXCollections.observableArrayList();
        piles.addAll(tableauPiles);
        piles.addAll(foundationPiles);

        Pile pile = getValidIntersectingPile(card, piles);


        if (pile != null) {
            handleValidMove(card, pile);
            if (isGameWon()) {handleGameWon();}
        } else {
            draggedCards.forEach(MouseUtil::slideBack);
            draggedCards.clear();
        }
    };

    private void handleGameWon() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You won. Would you like to play again?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            newGame();

        }
    }

    public void newGame() {
        deck.clear();
        foundationPiles.clear();
        tableauPiles.clear();
        stockPile.clear();
        discardPile.clear();
        getChildren().clear();
//        List<Card> cards = FXCollections.observableArrayList();
/*        for (Pile pile: foundationPiles) {
            deck.addAll(pile.getCards());
            pile.clear();
        }
        Collections.shuffle(deck);
        dealCards();*/
//        for (Card card : cards) {
//            stockPile.addCard(card);
//            card.flip();
//        }

        deck = Card.createNewDeck();
        Collections.shuffle(deck);
        initPiles();
        dealCards();

    }

    public boolean isGameWon() {
        int completedPiles = 0;
        int pileOneToComplete = 0;
        for (Pile pile : foundationPiles) {
            if (pile.numOfCards() == 13) {
                completedPiles++;
            } else if (pile.numOfCards() == 12) {
                pileOneToComplete++;

            }
        }
         return (completedPiles == 3 && pileOneToComplete ==1);
    }

    public Game() {
        deck = Card.createNewDeck();
        initPiles();
        dealCards();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }




    public void refillStockFromDiscard() {
        ObservableList<Card> discardedCards = discardPile.getCards();
        for (int i = discardPile.numOfCards()-1; i > -1; i--) {
            Card card = discardedCards.get(i);
            card.flip();
            stockPile.addCard(discardedCards.get(i));
        }
        discardPile.clear();

        //DONE
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        Card topCard = destPile.getTopCard();
        Rank cardRank = card.getRank();



        //  System.out.println(topCard.getRank().getCardRank() , topCard.getSuit().getColor());
        if (tableauPiles.contains(destPile)) {

            if (cardRank.equals(Rank.KING) && destPile.numOfCards()==0) {
                return true;
            } else if ((!cardRank.equals(Rank.KING)) && destPile.numOfCards()==0){
                return false;

            } else {
                return Card.isOppositeColor(card, topCard) && Card.isLower(card, topCard);
            }
        } else if (foundationPiles.contains(destPile)) {
            if (cardRank.equals(Rank.ACE) && destPile.numOfCards()==0) {
                return true;
            } else if ((!cardRank.equals(Rank.ACE)) && destPile.numOfCards()==0){
                return false;

            } else {
                return Card.isSameSuit(card, topCard) && Card.isLower(topCard, card);
            }
            //DONE
        }
        return true;
    }
    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
    }


    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(115);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(305);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(630 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(115 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
    Iterator<Card> deckIterator = deck.iterator();
        //TODO



        for (int i = 0; i < 7; i++) {
            for (int j = 0; j <= i; j++) {
                Card card = deckIterator.next();
                if (j == i) {
                    card.flip();
                }
                tableauPiles.get(i).addCard(card);
                addMouseEventHandlers(card);
                getChildren().add(card);
            }
        }



        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

    }

    public void setCardBack() {
        Card.cardBackImage = new Image("card_images/card_back_rainbow.png");
        List<Card> cards = FXCollections.observableArrayList();
        cards.addAll(stockPile.getCards());
        for (Pile pile: tableauPiles) {
            cards.addAll(pile.getCards());
        }
        for (Card card: cards) {
            if (card.isFaceDown()){
                card.setImage(Card.cardBackImage);
            }
        }
    }

}
