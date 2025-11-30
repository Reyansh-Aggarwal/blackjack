import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
class BlackJack {

    private Random random = new Random();
    private class Card {
        //Properties
        String value;
        String type;

        //Constructor
        Card (String value, String type){
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value + "-" + type;
        }

        public int getValue(){
            if ("AJQK".contains(value)){
                if (value == "A"){
                    return 11;
                }
                else{
                    return 10;
                }
            }
            return Integer.parseInt(value);
        }

        public boolean  isAce(){
            return "A".contains(value);
        }

        public String getImagePath(){
            return "cards/" + toString() + ".png";
        }
    }
    ArrayList<Card> deck;
    //Dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount; //incase change from 11 to 1 is needed

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = 600;

    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("black jack");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            //draw cards
            try {
                Image hiddenCardImage;
                if (standButton.isEnabled()){
                    hiddenCardImage = new ImageIcon(getClass().getResource("cards/BACK.png")).getImage();
                } else {
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();

                    String message = "";
                    if (playerSum > 21){
                        message = "You Lose!";
                    } 
                    else if (dealerSum > 21) {
                        message = "You Win!";
                    } 
                    else if (dealerSum > playerSum){
                        message = "You Lose!";
                    }
                    else if (playerSum > dealerSum){
                        message = "You Win!";
                    }
                    else {
                        message = "Tie!";
                    }

                    g.setColor(Color.white);
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.drawString(message, 220, 250);
                }
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);
                Card card;
                Image cardImage;
                //dealer cards
                for (int i = 0; i < dealerHand.size(); i++){
                    card = dealerHand.get(i);
                    cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 10) * (i+1), 20, cardWidth, cardHeight, null);
                } 

                //player cards
                for (int i = 0; i < playerHand.size(); i++){
                    card = playerHand.get(i);
                    cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 10) * i, 320, cardWidth, cardHeight, null);
                }


            } catch (Exception e){
                e.printStackTrace();
            }

            
        }
    };
        
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("HIT");
    JButton standButton = new JButton("STAND");

    BlackJack () {
        startGame();
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        frame.setVisible(true);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        standButton.setFocusable(false);
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size() -1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reduceAce(true) > 21){
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        standButton.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e){
                 hitButton.setEnabled(false);
                 standButton.setEnabled(false);
                 Card card;
                 while (dealerSum < 16){
                    card = deck.remove(deck.size() -1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1: 0;
                    dealerHand.add(card);
                 }
                 gamePanel.repaint();
            }
        });

        gamePanel.repaint();

    }

    public void startGame(){
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() -1); //remove last (top) card
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() -1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;

        dealerHand.add(card);

        System.out.println("DEALER HAND:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerAceCount);
        System.out.println(dealerSum);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i< 2; i++){
            card = deck.remove(deck.size() -1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("PLAYER HAND:");
        System.out.println(playerHand);
        System.out.println(playerAceCount);
        System.out.println(playerSum);
    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++){
            for (int j = 0; j < values.length; j++){
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
        System.out.println("Build Deck:");
        System.out.println(deck);
    }

    public void shuffleDeck(){
        for (int i = 0; i < deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);

            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("aFTER SUFFLE");
        System.out.println(deck);
    }

    public int reduceAce(boolean player){
        if (player){
            while (playerSum > 21 && playerAceCount > 0){
                playerSum -= 10;
                playerAceCount -= 1;
            }
            return playerSum;
        } else{
            while (dealerSum > 21 && dealerAceCount > 0){
                dealerSum -= 10;
                dealerAceCount -= 1;
            }
            return playerSum;
        }
    }
}