package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    public static JsonObject loadConfig(String resourcePath) {
        try {
            InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                return null;
            }
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
            stream.close();
            return json;
        } catch (Exception e) {
            return null;
        }
    }
}
