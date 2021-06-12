package com.poc.websocket.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.poc.websocket.dto.RequestDto;
import com.poc.websocket.dto.ResponseDto;

public class SerializeDeserializeUtil {

    private static final Gson GSON = new GsonBuilder().create();

    public static RequestDto getMessage(final String data){
        return GSON.fromJson(data, RequestDto.class);
    }

    public static String getResponse(final ResponseDto responseDto){
        return GSON.toJson(responseDto);
    }
}
