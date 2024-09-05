package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp {
    private String location;
    private double latitude;
    private double longitude;

    // Location is expected to be valid (ie, there exist coordinates for given location)
    public WeatherApp(String location) {
        this.location = location;
        try {
            generateCoordinates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Changes this.location to given location and computes coordinates of new location
    // If coordinates cannot be found, reset this.location to previous location
    public void changeLocation(String location) {
        String previousLocation = this.location;
        this.location = location;
        try {
            generateCoordinates();
        } catch (Exception e) {
            this.location = previousLocation;
        }
    }

    public String getLocation() {
        return this.location;
    }

    // Returns hourly weather data from coordinates this.latitude and this.longitude
    // this.latitude and this.longitude are required to be valid
    public JSONObject findHourlyWeatherData() {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,weather_code,is_day&timezone=auto&forecast_days=1";
        JSONObject ret = null;
        try {
            ret = sendGetRequest(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // Returns current weather data from coordinates this.latitude and this.longitude
    // this.latitude and this.longitude are required to be valid
    public JSONObject findCurrentWeatherData() {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,is_day,weather_code&timezone=auto&forecast_days=1";
        JSONObject ret = null;
        try {
            ret = sendGetRequest(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // Finds coordinates of location specified in this.location and sets them at this.latitude and this.longitude.
    // Also sets this.location to formal location name (proper spelling, capitalisation, no abbreviations)
    // Throws if coordinates could not be found
    private void generateCoordinates() throws Exception {
        String urlSafeLocationName = this.location.replace(' ', '+');
        String urlCoordinateString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + urlSafeLocationName + "&count=1&language=en&format=json";
        try {
            JSONObject response = sendGetRequest(urlCoordinateString);

            JSONArray result = (JSONArray) response.get("results");
            JSONObject res = (JSONObject) result.get(0);
            Double lat = (Double) res.get("latitude");
            Double lon = (Double) res.get("longitude");
            String loc = (String) res.get("name");

            this.latitude = lat;
            this.longitude = lon;
            this.location = loc;
        } catch (Exception e) {
            throw new Exception("Couldn't find coordinates of given location: " + location);
        }
    }

    // Sends a GET request to given endpoint
    // Throws with response code if request fails (not code 200)
    private static JSONObject sendGetRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

            System.out.println(jsonResponse); // todo remove
            return jsonResponse;
        } else {
            throw new Exception("GET request failed with response code: " + responseCode);
        }
    }

}