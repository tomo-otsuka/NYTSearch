package com.codepath.nytsearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Article {

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    String webUrl;
    String headline;
    String thumbnail;

    public Article() {
    }

    public Article(JSONObject jsonObject) throws JSONException {
        this.webUrl = jsonObject.getString("web_url");
        this.headline = jsonObject.getJSONObject("headline").getString("main");

        JSONArray multimedia = jsonObject.getJSONArray("multimedia");

        if (multimedia.length() > 0) {
            String thumbnailUrl = null;
            for (int i = 0; i < multimedia.length(); i++) {
                JSONObject multimediaJson = multimedia.getJSONObject(i);
                if (multimediaJson.getString("subtype") == "thumbnail") {
                    thumbnailUrl = multimediaJson.getString("url");
                    break;
                }
            }
            if (thumbnailUrl == null) {
                thumbnailUrl = multimedia.getJSONObject(0).getString("url");
            }
            this.thumbnail = "http://www.nytimes.com/" + thumbnailUrl;
        } else {
            this.thumbnail = "";
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Article(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
