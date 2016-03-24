package mr_anter.moviesapp.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mr_anter.moviesapp.constants.Constants;
import mr_anter.moviesapp.models.MoviesPojo;
import mr_anter.moviesapp.models.ReviewModel;

/**
 * Created by mostafa on 20/03/16.
 */
public class JsonParser{
    public static List<MoviesPojo> parseJsonFeed(String feed){

        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonMoviesArray = jsonRootObject.optJSONArray("results");
            List<MoviesPojo> flowerList = new ArrayList<>();
            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonObject = jsonMoviesArray.getJSONObject(i);
                String poster_path = Constants.IMAGE_SOURCE + jsonObject.optString("poster_path");
                String backdrop_path = Constants.IMAGE_SOURCE + jsonObject.optString("backdrop_path");
                String overview = jsonObject.optString("overview");
                String release_date = jsonObject.optString("release_date");
                String id = jsonObject.optString("id");
                String vote_average = jsonObject.optString("vote_average");
                String original_title = jsonObject.optString("original_title");
                MoviesPojo moviesPojo = new MoviesPojo();
                moviesPojo.setPoster_path(poster_path);
                moviesPojo.setBackdrop_path(backdrop_path);
                moviesPojo.setOverview(overview);
                moviesPojo.setRelease_date(release_date);
                moviesPojo.setId(id);
                moviesPojo.setVote_average(vote_average);
                moviesPojo.setOriginal_title(original_title);
                flowerList.add(moviesPojo);
            }
            return flowerList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String parseTrailer(String feed){
        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonMoviesArray = jsonRootObject.optJSONArray("results");
            String code = "";
            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonObject = jsonMoviesArray.getJSONObject(i);
                String type = jsonObject.optString("type");

                if (type.equalsIgnoreCase("trailer"))
                    code = jsonObject.optString("key");
            }
            return code;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<ReviewModel> parseJsonReview(String feed){

        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonMoviesArray = jsonRootObject.optJSONArray("results");
            List<ReviewModel> flowerList = new ArrayList<>();
            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonObject = jsonMoviesArray.getJSONObject(i);
                String author = jsonObject.optString("author");
                String content = jsonObject.optString("content");
                String url = jsonObject.optString("url");

                ReviewModel moviesPojo = new ReviewModel();
                moviesPojo.setAuthor(author);
                moviesPojo.setUrl(url);
                moviesPojo.setContent(content);
                flowerList.add(moviesPojo);
            }
            return flowerList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }


}
