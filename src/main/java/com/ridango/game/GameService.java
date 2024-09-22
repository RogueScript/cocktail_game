package com.ridango.game;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;


import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

@Service
public class GameService {
    private static final int MAX_ATTEMPTS = 5;
    private static final Random random = new Random();
    private int maxScore = 0;
    private Set<String> usedCocktails = new HashSet<>();
    private String hidden;
    private int attemptsLeft;
    private JsonObject cockTail;
    private Set<String> revealedKeys = new HashSet<>();
    // The string keys to use for revealing additional info regarding the cocktail.
    private String[] infoKeys = {
            "strCategory",
            "strGlass",
            "strAlcoholic",
            "strInstructions"
    };

    @Autowired
    private ApiService apiService;

    public void StartGame() {
        Scanner scanner = new Scanner(System.in);
        boolean keepPlaying = true;
        while (keepPlaying) {
            String cocktailName;
            revealedKeys.clear();
            //Get the cocktail, and check if we have seen it already.
            do {
                cockTail = apiService.GetCockTail();
                if (cockTail == null || cockTail.isEmpty() || cockTail.isJsonNull()){
                    System.out.println("Problem retrieving a cocktail!");
                    GameEnd();
                    return;
                }
                cocktailName = cockTail.get("strDrink").getAsString();
            } while (usedCocktails.contains(cocktailName));
            usedCocktails.add(cocktailName);
            // generate the hidden name replacing characters with underscore, keep spaces though
            hidden = cocktailName.replaceAll("[a-zA-Z]", "_");
            attemptsLeft = MAX_ATTEMPTS;
            System.out.println("Guess the name of the cocktail: " + hidden );
            while (attemptsLeft > 0) {
                System.out.println("You have : " + attemptsLeft + " attempts left! Type 'Exit' without quotes whenever you wish to quit.");
                System.out.println("Enter the name of the cocktail: ");
                String name = scanner.nextLine().trim();

                if (name.trim().equalsIgnoreCase("exit")) {
                    GameEnd();
                    keepPlaying = false;
                    return;
                }

                if (name.equalsIgnoreCase(cocktailName)) {
                    int score = attemptsLeft;
                    System.out.println("You guessed right! You scored: " + score);
                    maxScore += score;

                    break;

                } else {
                    attemptsLeft--;
                    // reveal some letters
                    hidden = RevealLetters(hidden, cocktailName);
                    if (attemptsLeft > 0 ) {
                        System.out.println("You guessed wrong! Try again, some letters have been revealed: " + hidden);
                        System.out.println("Hints:");
                        RevealInfo();

                    } else {
                        System.out.println("You are out of attempts! The cocktail was: " + cockTail.get("strDrink").getAsString());
                    }
                }

            }
            do {
                System.out.println("Try again? (yes/no)");
                String response = scanner.nextLine().trim().toLowerCase();
                System.out.println("Response is " + response);
                if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("exit")) {
                    keepPlaying = false;
                    GameEnd();
                    break;
                } else if (response.equalsIgnoreCase("yes")) {
                    break;
                } else {
                    System.out.println("Please enter 'yes' or 'no'.");
                }
            } while (true);


        }
    }

    public String RevealLetters(String hiddenName, String cocktailName) {
        StringBuilder revealedName = new StringBuilder(hiddenName);
        // we will replace one instance of _ with a letter
        if (revealedName.toString().contains("_")) {
            while (true){
                int index = random.nextInt(cocktailName.length());
                if (hiddenName.charAt(index) == '_'){
                    revealedName.setCharAt(index, cocktailName.charAt(index));
                    break;
                }
            }
        }
        return revealedName.toString();
    }

    private void GameEnd(){
        System.out.println("Game ended!");
        System.out.println("Your final score is: " + maxScore);
        return;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Set<String> getRevealedKeys(){
        return revealedKeys;
    }

    public JsonObject getCockTail(){
        return cockTail;
    }

    public String getHidden(){
        return hidden;
    }


    private void RevealInfo(){
        // loop through our strKey array to reveal additional information
        for (String key: infoKeys) {
            if (cockTail.has(key) && !cockTail.get(key).isJsonNull() && !revealedKeys.contains(key)){
                if (key.equals("strCategory")){
                    System.out.println("The Category is: " + cockTail.get(key).getAsString());
                } else if (key.equals("strGlass")) {
                    System.out.println("The Glass is: " + cockTail.get(key).getAsString());
                } else if (key.equals("strAlcoholic")) {
                    System.out.println("Is it alcoholic? " + cockTail.get(key).getAsString());
                } else if (key.equals("strInstructions")) {
                    System.out.println("And the instructions to make it are: " + cockTail.get(key).getAsString());
                }
                revealedKeys.add(key);
                break;
            }
        }
    }
}
