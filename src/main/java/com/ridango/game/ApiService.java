package com.ridango.game;

import com.google.gson.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;


@Service
public class ApiService {

    private final RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

public JsonObject GetCockTail(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/random.php";
    try {
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new RuntimeException("API returned a null response.");
        }
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonArray data = jsonObject.getAsJsonArray("drinks");

        if (!data.isEmpty()){
            JsonObject drinkInfo = data.get(0).getAsJsonObject();
            return drinkInfo;
        }
    } catch (RestClientException e) {
        System.err.println("Failed to retrieve cocktail information. Message: " +e.getMessage());
        throw new RuntimeException("API Error." + e);
    }

    return null;
}


}
