package com.example.smarthouse.api;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Твой URL из Supabase
    private static final String BASE_URL = "https://rvviivcrurdxeaddwihg.supabase.co/rest/v1/";

    // Твой API ключ (anon public) из Supabase
    private static final String API_KEY = "sb_publishable_4hScN4Yu1OTVoH0a9QpXRg_KaRGHt1h";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    // Этот метод мы возвращаем, чтобы старый код в Activity снова заработал!
    public static ApiService getInstance() {
        if (apiService == null) {
            if (retrofit == null) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request originalRequest = chain.request();
                                Request.Builder builder = originalRequest.newBuilder()
                                        .header("apikey", API_KEY)
                                        .header("Authorization", "Bearer " + API_KEY)
                                        .header("Content-Type", "application/json");

                                // Если в запросе еще нет заголовка Prefer, добавляем стандартный
                                if (originalRequest.header("Prefer") == null) {
                                    builder.header("Prefer", "return=representation");
                                }
                                
                                return chain.proceed(builder.build());
                            }
                        })
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    // Оставляем геттер для самого клиента на всякий случай
    public static Retrofit getClient() {
        if (retrofit == null) {
            getInstance();
        }
        return retrofit;
    }
}
