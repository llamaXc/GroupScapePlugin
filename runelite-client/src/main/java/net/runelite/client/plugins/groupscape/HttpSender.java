package net.runelite.client.plugins.groupscape;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

public class HttpSender {

    String API_END_POINT_BASE;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpSender(String API_END_POINT_BASE, int port){

        //Create the end point
        this.API_END_POINT_BASE = API_END_POINT_BASE + ":" + port + "/";
    }

    public void sendPostRequest(Object pojoObject, String apiPostLocation, String queryParam) {

        //Application JSON
        final MediaType JSON = MediaType.parse("application/json");

        //Attempt to send
        try{
            String jsonString = mapper.writeValueAsString(pojoObject);
            System.out.println("Json string: " + jsonString);

            Request request = new Request.Builder()
                    .post(RequestBody.create(JSON, jsonString))
                    .url(API_END_POINT_BASE + apiPostLocation + "?" + queryParam)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                System.out.println(response.body().string());
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public void sendPostRequest(String jsonString, String apiPostLocation, String queryParam) {

        //Application JSON
        final MediaType JSON = MediaType.parse("application/json");
        final String API_END_POINT_BASE = "http://localhost:3000/";

        //Attempt to send
        try{
            System.out.println("Json string: " + jsonString);

            Request request = new Request.Builder()
                    .post(RequestBody.create(JSON, jsonString))
                    .url(API_END_POINT_BASE + apiPostLocation + "?" + queryParam)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                System.out.println(response.body().string());
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
