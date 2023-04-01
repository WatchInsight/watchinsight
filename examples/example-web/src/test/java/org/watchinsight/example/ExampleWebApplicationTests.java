package org.watchinsight.example;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class ExampleWebApplicationTests {
    
    public static void main(String[] args) throws IOException {
        final var client = new OkHttpClient().newBuilder().build();
        final var request = new Request.Builder().url("http://127.0.0.1:8080/agent/trace").get().build();
        final Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
