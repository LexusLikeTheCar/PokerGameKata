
import java.util.Scanner;
import java.util.*;
import java.util.Arrays;

public class PokerGame {

//these are the cards in order of winning value
enum Hand {NONE, HIGHCARD, PAIR, TWOPAIR, THREEKIND, STRAIGHT, FLUSH, FULLHOUSE,FOURKIND, STRAIGHTFLUSH}

public static void main(String args[]) {

//get the user input
//it is assumed the user input will be entered in as specified
 Scanner sc = new Scanner(System.in);

 String info = sc.nextLine();
 //remove the whitespaces
  info = info.replaceAll("\\s+","");
  //create the first player
  //we only want the black cards info so were going to only evaluate Black: ... W
  Player black = createPlayer(info, 0, info.indexOf(":"), info.indexOf("W"));
  // create the second player
  //we want the white cards info so were going to only evaluate White: .... (end)
  info = info.substring(info.indexOf("W"),info.length());
  Player white = createPlayer(info, info.indexOf("W"), info.indexOf(":"), info.length());

  //evaluate black and white hands
  evaluatePlayers(black);
  evaluatePlayers(white);

  //compare hands here
  if(white.hand.ordinal() > black.hand.ordinal()) {
    printWinner(white);
  }
 if(white.hand == black.hand) {
   //there are many ways to tie...
    solveTie(white,black,white.hand);
  }
  if(black.hand.ordinal() > white.hand.ordinal()) {
    printWinner(black);
  }

}

//pairs is a function that takes the player object and the players string array of cards. The idea is to
//find if the hand contains anything involving pairs (ie pair, two pair, three of a kind, four of a kind or full house)
//and assign the hand, the highest value, and if needed the alt value to the player to use later
//no value returned, just the players are updated
public static void pairs(Player turn,String [] cards) {
  //count,count2, and count3 will count the amount of times a number appears in order more than once.
  //count is used as the actual indexed counter while count2 and count3 will hold old values but all will be compared
  //in 2 pair to ensure that there are actually 2 sets of 2
  //the logic behind having 3 counters is that there is no set that will exceed the need for 3 counters (note we are not focused on the cards that only have a count of 1)
  //2 pair will return 2,2,1 one pair will be 2,1,1,1 3 of a kind will be 3,1,1 four of a kind will be 4,1 full house will be 3,2
  int count =1;
  //highest, highest2, and highest3 will record the values of the numbers we are counting (same logic as count)
  int highest=0;
  int count2 = 0;
  int count3 =0;
  int highest2 =0;
  int highest3 =0;
  //the idea behind this loop is that if a card equals the card next to it, we count them
  //if they dont, we add them to a string list and then reset the count
  //this allows us to keep track of the pairs, if there is a second pair, and all the values not in a pair
  for(int i=0;i<cards.length-1;i++) {
    //check to see if card and card after it are equal
    if(Integer.parseInt(cards[i]) == Integer.parseInt(cards[i+1])){
      //if yes, record values
      count++;
      highest = Integer.parseInt(cards[i]);


    }
    else {
      //since we arent using the last index, if the card at the end is in a pair it needs to be accounted for too
      if(i == cards.length-2 && Integer.parseInt(cards[i]) == Integer.parseInt(cards[i+1])){
        count++;
      }
      //not focused on cards that only appear once
      if(count > 1) {
        highest3 = highest2;
        highest2 = highest;
        count3 = count2;
        count2 = count;
    }
    //record the cards that only appear once for later and reset the count
      turn.leftOvers+= turn.cards[i];
      count=1;

    }
}

  //keep track of the counts and the highest counts are the ones that will determine the hands
  int [] counts = new int[]{highest,highest2,highest3};
  //sort from lowest to highest
  Arrays.sort(counts);

  //using count and count2 we will evaulate them and assign them to the correct rules based on hands
  //pair: 2 cards match value
  if((count ==2 || count2 ==2)){
    turn.hand = Hand.PAIR;
    if(count ==2) {
      turn.highest = highest;
    }
    if(count2 == 2) {
      turn.highest = highest2;
    }
  }
  //2 pair: 2 sets of 2 cards match values
   if((count ==2 && count2 ==2) || (count3 ==2 && count ==2) || (count2 == 2 &&count3==2)) {
    turn.hand = Hand.TWOPAIR;
    //here we are setting the alt number to be the number of the other pair in the hand, whichever one is higher
    //needs to be recorded and the other number needs to be set to alt to solve a tie
    turn.highest = counts[counts.length-1];
    if(counts[counts.length-1] == counts[counts.length-2]){
      turn.alt = counts[counts.length-3];
    }
    else {
    turn.alt = counts[counts.length-2];
  }
  }
  //3 of a kind: 3 cards match in value
   if(count ==3 || count2 ==3){
    turn.hand = Hand.THREEKIND;

    if(count ==3) {
      turn.highest = highest;

    }
    if(count2 == 3) {
      turn.highest = highest2;
    }
  }

  //4 of a kind: 4 cards match in value
  if(count ==4 || count2 ==4) {
    turn.hand = Hand.FOURKIND;
    if(highest> highest2) {
      turn.highest = highest;

    }
    else {
      turn.highest = highest2;
    }
  }
  //full house: 3 cards match in value and the other 2 cards match in value
   if((count ==3 && count2 ==2) || (count2 ==3 && count ==2)) {
    turn.hand = Hand.FULLHOUSE;
    if(count ==3 && count2 ==2) {
      turn.highest = highest;
      turn.alt = highest2;
    }else {
      turn.highest = highest2;
      turn.alt = highest;
    }

  }

}

//straight returns true if all of the cards are in consutive values so we check the cards from the player to see if they match the one above it
public static Boolean straight(String [] cards){
   return ((Integer.parseInt(cards[0])+1) == Integer.parseInt(cards[1]) &&  (Integer.parseInt(cards[1])+1) ==  Integer.parseInt(cards[2]) && (Integer.parseInt(cards[2])+1)== Integer.parseInt(cards[3]) && (Integer.parseInt(cards[3])+1) ==  Integer.parseInt(cards[4]));

}
//flush returns true if all of the strings in the suits from the players cards are the same
public static Boolean flush(String [] suits) {
  return(suits[0].equals(suits[1])&& suits[1].equals(suits[2]) && suits[2].equals(suits[3]) && suits[3].equals(suits[4]));
}

//printWinner will print the winning message as stated in the example provided by the evaluation of the players hands
public static void printWinner(Player winner){
  //here we are getting the straight in a string with all of the correct words (ie 10 is Ten, 11 is Jack and so on)
  //this is the same idea for correctValue
  String straight = correctStraight(winner.cards);

  switch(winner.hand) {
    case HIGHCARD:
      System.out.println(winner.username+" wins. - with high card: " + correctValue(winner.highest));
      break;
    case PAIR:
      System.out.println(winner.username+" wins. - with pair: " + correctValue(winner.highest));
      break;
    case TWOPAIR:
      System.out.println(winner.username+" wins. - with two pair: " + correctValue(winner.highest)+ " and " + correctValue(winner.alt));
      break;
    case THREEKIND:
      System.out.println(winner.username+" wins. - with three of a kind: " + correctValue(winner.highest));
      break;
    case FOURKIND:
      System.out.println(winner.username+" wins. - with four of a kind: " + correctValue(winner.highest));
      break;
    case FULLHOUSE:
      System.out.println(winner.username+" wins. - with full house: " + correctValue(winner.highest) + " over "+ correctValue(winner.alt));
      break;
    case FLUSH:
      System.out.println(winner.username+" wins. - with flush: " + winner.suit);
      break;
    case STRAIGHT:
      System.out.println(winner.username+" wins. - with straight: " + straight);
      break;
    case STRAIGHTFLUSH:
      System.out.println(winner.username+" wins. - with straight flush:" + straight +" with suit: "+ winner.suit);
      break;

  }

}
//solveTie will take 2 players and based on the rules for the tiedhand breaking the tie, evaulate who will win and then print the
//appropriate message
public static void solveTie(Player one, Player two,Hand tiedHand) {
  //get the straights
  String straightOne = correctStraight(one.cards);
  String straightTwo = correctStraight(two.cards);

  switch(tiedHand) {
    case HIGHCARD:
      //high card, highest card wins, if the high cards are the same, pick the next highest until there is a winner
      //if all the cards are the same, tie
      highCardTie(one,two, one.cards,two.cards,tiedHand);
      break;
    case PAIR:
    if(one.highest >two.highest) {
      System.out.println(one.username+" wins. - with pair: " + correctValue(one.highest));
    }
    if(one.highest < two.highest) {
    System.out.println(two.username+" wins. - with pair: " + correctValue(two.highest));
    }
    //pair, highest pair value win, if the pair values are the same, pick the next highest card in the hand until there is a winner
    //if all the cards are the same, tie
    //splitting the leftovers means that we are evaluating the remaining cards that are not in a pair
    if(one.highest == two.highest){
      highCardTie(one,two,one.leftOvers.split(""),two.leftOvers.split(""), tiedHand);
    }
      break;
    case TWOPAIR:

    //two pair, highest pair value win, if the highesy pair values are the same,the second highest pair value wins
    // if the second highest pair values are the same, evaluate the last card (leftovers) in the hand
    //if all the cards are the same, tie
      if(one.highest >two.highest) {
        System.out.println(one.username+" wins. - with two pair: " + correctValue(one.highest)+ " and " + correctValue(one.alt));
      }
      if(one.highest < two.highest) {
      System.out.println(two.username+" wins. - with two pair: " + correctValue(two.highest)+ " and " + correctValue(two.alt));
      }
      if(one.highest ==two.highest) {
        if(one.alt >two.alt){
          System.out.println(one.username+" wins. - with two pair: " + correctValue(one.highest)+ " and " + correctValue(one.alt));
        }
        if(one.alt < two.alt){
          System.out.println(two.username+" wins. - with two pair: " + correctValue(two.highest)+ " and " + correctValue(two.alt));
        }
        if(one.alt == two.alt){
          if(Integer.parseInt(one.leftOvers) < Integer.parseInt(two.leftOvers)){
            System.out.println(two.username+" wins. - with two pair: " + correctValue(two.highest)+ " and " +correctValue(two.alt));
          }
          if(Integer.parseInt(one.leftOvers) > Integer.parseInt(two.leftOvers)){
            System.out.println(one.username+" wins. - with two pair: " + correctValue(one.highest)+ " and " + correctValue(one.alt));
          }
          if(Integer.parseInt(one.leftOvers) ==Integer.parseInt(two.leftOvers)){
            System.out.println("Tie.");
          }
        }
      }
      break;
    case THREEKIND:
    //three of a kind, highest trio value win, if the trio values are the same, pick the next highest card in the hand until there is a winner
    //if all the cards are the same, tie
    //splitting the leftovers means that we are evaluating the remaining cards that are not in a pair
    if(one.highest > two.highest){
      System.out.println(one.username+" wins. - with three of a kind: " + correctValue(one.highest));
    }
    if(one.highest < two.highest){
      System.out.println(two.username+" wins. - with three of a kind: " + correctValue(two.highest));
    }
    if(one.highest == two.highest){
      highCardTie(one,two,one.leftOvers.split(""),two.leftOvers.split(""), tiedHand);
    }
      break;
    case FOURKIND:
    //four of a kind, highest quad value win, if the quad values are the same, the last card (leftovers) will determine the winner
    //if all the cards are the same, tie
    if(one.highest > two.highest){
      System.out.println(one.username+" wins. - with four of a kind: " + correctValue(one.highest));
    }
    if(one.highest < two.highest){
      System.out.println(two.username+" wins. - with four of a kind: " + correctValue(two.highest));
    }
    if(one.highest == two.highest){
      if(Integer.parseInt(one.leftOvers) > Integer.parseInt(two.leftOvers)) {
      System.out.println(one.username+" wins. - with four of a kind: " + correctValue(one.highest));
      }
      if(Integer.parseInt(one.leftOvers) < Integer.parseInt(two.leftOvers)) {
        System.out.println(two.username+" wins. - with four of a kind: " + correctValue(two.highest));
      }
      if(Integer.parseInt(one.leftOvers) == Integer.parseInt(two.leftOvers)){
        System.out.println("Tie.");
      }
    }
      break;
    case FULLHOUSE:
    //full house, highest trio value win, if the trio values are the same, the highest pair value will win
    //if the trio and the pair values are the same, tie
    if(one.highest > two.highest) {
      System.out.println(one.username+" wins. - with full house: " + correctValue(one.highest) + " over "+ correctValue(one.alt));
    }
     if(two.highest > one.highest) {
      System.out.println(two.username+" wins. - with full house: " + correctValue(two.highest) + " over "+ correctValue(two.alt));
    }
   if(two.highest == one.highest) {
      if(one.alt > two.alt) {
        System.out.println(one.username+" wins. - with full house: " + correctValue(one.highest) + " over "+ correctValue(one.alt));
      }
     if(two.alt > one.alt)  {
        System.out.println(two.username+" wins. - with full house: " + correctValue(two.highest) + " over "+ correctValue(two.alt));
      }
      if(two.alt == one.alt) {
        System.out.println("Tie.");
      }
    }

      break;
    case FLUSH:
    //flush, if the suits are the same, the high card wins
    // if the hands have the same high cards, pick the next highest until there is a winner
      highCardTie(one,two,one.cards,two.cards, Hand.FLUSH);
      break;
    case STRAIGHT:
    //straight, the highest value of the straight wins
    //if the cards are the same, tie
    if(one.highest > two.highest) {
      System.out.println(one.username+" wins. - with straight: " + straightOne);
    }
   if(two.highest > one.highest) {
      System.out.println(two.username+" wins. - with straight: " + straightTwo);
    }
    if(two.highest == one.highest) {
      System.out.println("Tie.");
    }
      break;
    case STRAIGHTFLUSH:

    //straight flush, same rules are straight
    if(one.highest > two.highest) {
      System.out.println(one.username+" wins. - with straight flush: " + straightOne+" with suit: " +one.suit);
    }
    if(two.highest > one.highest) {
      System.out.println(two.username+" wins. - with straight flush: " + straightTwo+" with suit: "+ two.suit);
    }
    if(one.highest == two.highest) {
      System.out.println("Tie.");
    }
      break;

  }
}

//high card tie will evaluate the players black and white, their cards (one and two), and using the hand provided
//will determine the winner and print the output message by evaluating the remaining cards in the hand
public static void highCardTie(Player black, Player white, String [] one, String [] two, Hand hand) {
  String winningMessage = "";
  switch(hand) {
    case HIGHCARD:
       winningMessage =" wins. - with high card: ";
      break;
    case PAIR:
      winningMessage =" wins. - with pair: ";
      break;
    case THREEKIND:
      winningMessage = " wins. - with three of a kind: ";
      break;
    case FLUSH:
     winningMessage = " wins. - with flush: ";
    break;
  }

//this loop is counting to see if the cards in the two arrays are the same, once they arent or we reach the
//array size, it will stop
  int i=1;
  while(one[one.length-i].equals(two[two.length-i]) && i < one.length && i<two.length) {
    i++;
  }
  //were testing the cards in the array where we found above that they arent the same
  //if one is greater than the other, that means we have found the next highest value in the hand
  //if we are looking at a flush, we need to use the suit in the printed message answer
  if(hand.equals(Hand.FLUSH)) {

    if(Integer.parseInt(one[one.length-i]) > Integer.parseInt(two[two.length-i])){
        System.out.println(black.username+ winningMessage + black.suit);
    }
    if(Integer.parseInt(one[one.length-i]) < Integer.parseInt(two[two.length-i])) {
        System.out.println(white.username+ winningMessage + white.suit);
    }

  }
  //else we need to print the highest card
  else {
    if(Integer.parseInt(one[one.length-i]) > Integer.parseInt(two[two.length-i])){
        System.out.println(black.username+ winningMessage + correctValue(black.highest));
    }
    if(Integer.parseInt(one[one.length-i]) < Integer.parseInt(two[two.length-i])) {
        System.out.println(white.username+ winningMessage + correctValue(white.highest));
    }
  }
//if we reached the end of the array and the cards are still the same, theyre all the same
//weve tied
  if (Integer.parseInt(one[one.length-i]) == Integer.parseInt(two[two.length-i])) {
    System.out.println("Tie.");
  }
}

//create player will create the player from the player class based on the string of info from the user
//the indexes of the start of where we need to parse the info, the middle of the info where the name ends and the cards start,
//then the end of the players cards
public static Player createPlayer(String info, int start, int middle, int end) {
  String name = info.substring(start, middle);
  String cards = info.substring(middle+1, end);

  //split the cards into suits for the player information
  String [] suits = cards.replaceFirst("[123456789KQJAT]", "").split("[123456789KQJAT]+");
  //replace the letters with their number equilivents
  cards=cards.replaceAll("K","13");
  cards=cards.replaceAll("Q","12");
  cards=cards.replaceAll("J","11");
  cards=cards.replaceAll("A","14");
  cards=cards.replaceAll("T","10");
  //split the info of cards into just the numbers
  String [] cardArr =cards.split("\\D");
  //sort the array so the numbers are in lexiconical order
  //this Comparator compares the values of the strings are integers
  Arrays.sort(cardArr,new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
        }
    });
    //return an empty player, here we set the values of the cards, the suits of the cards,
    //the name of the user, the hand the user currently has (in this case the user has no hand),
    //the highest value of the users pair cards, the secondary value of the users secondary pair
    //cards,the string of the suit if the user has a flush, and the string of the numbers from the hands
    //of the users that are not in pairs
  return (new Player(cardArr, suits, name, Hand.NONE, 0,0,null,""));
}


//evaluatePlayers will take the player and go through all of the hand checks to see which hand the user has
public static void evaluatePlayers(Player turn) {

  pairs(turn,turn.cards);

//once they pass the test, record their hand and highest values
  if(straight(turn.cards)){
    turn.hand = Hand.STRAIGHT;
    turn.highest = Integer.parseInt(turn.cards[turn.cards.length-1]);

  }
  //or their suits
  if(flush(turn.suits) && turn.hand.ordinal() < Hand.FULLHOUSE.ordinal()){
    turn.hand = Hand.FLUSH;
    switch(turn.suits[0]) {
    case "S":
      turn.suit = "Spades";
    break;
    case "D":
      turn.suit = "Diamonds";
    break;
    case "C":
      turn.suit = "Clubs";
    break;
    case "H":
      turn.suit = "Hearts";
    break;
  }


  }
   if(straight(turn.cards) &&flush(turn.suits)) {
     turn.highest = Integer.parseInt(turn.cards[turn.cards.length-1]);
    turn.hand = Hand.STRAIGHTFLUSH;
  }
  //if the user doesnt have a hand, they have a high card hand
  if(turn.hand == Hand.NONE) {
    //find the highest card
    turn.hand = Hand.HIGHCARD;
    turn.highest = Integer.parseInt(turn.cards[turn.cards.length-1]);
  }

}
//correctValue will take the integer number and return the string that the number should be
//this is mostly to change the face card values into their proper names
//ie 10 is Ten, 11 is Jack, 12 is Queen, 13 is King, 14 is Ace
public static String correctValue(int number) {
  String name = Integer.toString(number);
  switch(number) {
    case 10:
       name = "Ten";
      break;
    case 11:
      name = "Jack";
      break;
    case 12:
      name = "Queen";
      break;
    case 13:
     name = "King";
    break;
    case 14:
    name = "Ace";
      break;
  }
  return name;
}

//this is the same idea as correct value but were going to take the array of cards and make sure all of the
//values are correctly outputed
public static String correctStraight(String [] array) {
  StringBuilder builder = new StringBuilder();
  for (String value : array) {
    builder.append(value+", ");
  }

  String straight = builder.toString();
  straight = straight.substring(0,straight.length()-2);
  straight =straight.replaceAll("10","Ten");
  straight =straight.replaceAll("11","Jack");
  straight =straight.replaceAll("12","Queen");
  straight = straight.replaceAll("13","King");
  straight =straight.replaceAll("14","Ace");
  return straight;
}

}
