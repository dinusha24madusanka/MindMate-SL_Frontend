package com.dinusha.mindmate_sl.network;

import com.dinusha.mindmate_sl.model.ChatRequest;
import com.dinusha.mindmate_sl.model.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/chat")
    Call<ChatResponse> sendChatMessage(@Body ChatRequest request);
}