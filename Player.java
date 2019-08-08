//public class player represents the player information
public class Player {

String [] cards; //the card values in asending order
String [] suits; //the suits of the cards
String leftOvers; //the remaining cards that are not in a pair
String username; //the users name
PokerGame.Hand hand; //the hand the user has
int highest; //the highest value of the users hand or the value of the highest pair
int alt; //this is needed for hands with 2 pairs, this records the value of the second pair
String suit; //this is used for flush, this is the winning suit in the hand


Player(String [] input, String [] colors, String user, PokerGame.Hand starting, int initial, int holder, String temp, String notPairs ) {
  cards = input;
  suits = colors;
  username = user;
  hand = starting;
  highest = initial;
  alt = holder;
  suit = temp;
  leftOvers = notPairs;

}

}
