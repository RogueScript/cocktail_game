package com.ridango;

import com.google.gson.JsonObject;
import com.ridango.game.ApiService;
import com.ridango.game.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private ApiService apiService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testStartGameApiCalledCocktailRetrieved() {
        // we check that an API call is made and we get the object to be our mocked Mojito
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Mojito\nno\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        verify(apiService, times(1)).GetCockTail();
        assertEquals("Mojito", gameService.getCockTail().get("strDrink").getAsString());
    }

    @Test
    public void testRevealLetter() {
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Test\nexit\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        //We check that 1 letter is revealed.
        assertEquals(1, gameService.RevealLetters("_________", "Margarita").replaceAll("_", "").length());
    }

    @Test
    public void testMultipleRevealedLetters() {
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Test\nTest\nTest\nexit\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        //We check that 2 letters are revealed.
        assertEquals(3, gameService.getHidden().replaceAll("_", "").length());
    }

    @Test
    public void testMaxAttemptsLeftZero() {
        // Here we test that the attempts left are 0 after 5 incorrect guesses
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);

        String simulatedInput = "Test\nTest\nTest\nTest\nTest\nno\n"; // 5 incorrect guesses
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        // Expect that attemptsleft are 0
        assertEquals(0, gameService.getAttemptsLeft());
    }

    @Test
    public void testScoreCorrectGuess() {
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Mojito\nno\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        // Expect that score is 5
        assertEquals(5, gameService.getMaxScore());
    }

    @Test
    public void testScoreOneInCorrectGuess() {
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Test\nMojito\nno\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        // Expect that score is 4 since we guessed incorrectly once
        assertEquals(4, gameService.getMaxScore());
    }

    @Test
    public void testInfoReveal() {
        JsonObject mockCocktail = new JsonObject();
        mockCocktail.addProperty("strDrink", "Mojito");
        mockCocktail.addProperty("strCategory", "Test Category");
        when(apiService.GetCockTail()).thenReturn(mockCocktail);
        String simulatedInput = "Test\nMojito\nno\n"; // 5 incorrect guesses
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);
        gameService.StartGame();
        assertTrue(gameService.getRevealedKeys().contains("strCategory"));
    }

    @Test
    public void testNullCocktailObject() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        when(apiService.GetCockTail()).thenReturn(null);
        gameService.StartGame();
        assertTrue(output.toString().contains("Problem retrieving a cocktail!"));
        assertEquals(0, gameService.getMaxScore());
    }






}
