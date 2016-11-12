package sample.kojimamasahiro.gsontest;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                long start;
                long objectTime;
                long gsonTime;
                Gson gson = new Gson();

                try {
                    AssetManager as = getResources().getAssets();
                    InputStream is = as.open("owm_data1.json");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String json = br.readLine();
                    start = System.currentTimeMillis();
                    for (int j = 0; j < 10000; j++) {
                        analyzerJson(json);
                    }
                    objectTime = System.currentTimeMillis() - start;

                    start = System.currentTimeMillis();
                    for (int i = 0; i < 10000; i++) {
                        gson.fromJson(json, OpenWeatherMapForGson.class);
                    }
                    gsonTime = System.currentTimeMillis() - start;

                    Log.d("測定", "object: " + objectTime + " gson: " + gsonTime);
                } catch (IOException | JSONException ignore) {
                }
            }
        });
    }

    private OpenWeatherMap analyzerJson(String text) throws JSONException {
        OpenWeatherMap openWeatherMap = new OpenWeatherMap();

        JSONObject result = new JSONObject(text);

        JSONObject coord = result.getJSONObject("coord");
        openWeatherMap.coordLon = coord.getDouble("lon");
        openWeatherMap.coordLat = coord.getDouble("lat");

        JSONObject sys = result.getJSONObject("sys");
        openWeatherMap.sysCountry = sys.getString("country");
        openWeatherMap.sysSunrise = sys.getLong("sunrise");
        openWeatherMap.sysSunset = sys.getLong("sunset");

        List<DetailWeather> detailWeathers = new ArrayList<>();
        JSONArray weathers = result.getJSONArray("weather");
        for (int i=0; i<weathers.length(); i++) {
            JSONObject weather = weathers.getJSONObject(i);

            DetailWeather detailWeather = new DetailWeather();
            detailWeather.id = weather.getInt("id");
            detailWeather.main = weather.getString("main");
            detailWeather.description = weather.getString("description");
            detailWeather.icon = weather.getString("icon");

            detailWeathers.add(detailWeather);
        }
        openWeatherMap.weather = detailWeathers;

        JSONObject main = result.getJSONObject("main");
        openWeatherMap.mainTemp = main.getDouble("temp");
        openWeatherMap.mainHumidity = main.getInt("humidity");
        openWeatherMap.mainPressure = main.getInt("pressure");
        openWeatherMap.mainTempMin = main.getDouble("temp_min");
        openWeatherMap.mainTempMax = main.getDouble("temp_max");

        JSONObject wind = result.getJSONObject("wind");
        openWeatherMap.windSpeed = wind.getDouble("speed");
        openWeatherMap.windDeg = wind.getDouble("deg");

        JSONObject rain = result.getJSONObject("rain");
        openWeatherMap.rain3h = rain.getInt("3h");

        JSONObject clouds = result.getJSONObject("clouds");
        openWeatherMap.cloudsAll = clouds.getInt("all");

        openWeatherMap.dt = result.getLong("dt");
        openWeatherMap.name = result.getString("name");
        openWeatherMap.cod = result.getInt("cod");

        return openWeatherMap;
    }

    public static class OpenWeatherMap {
        public double coordLon;
        public double coordLat;

        public String sysCountry;
        public long sysSunrise;
        public long sysSunset;

        public List<DetailWeather> weather;

        public double mainTemp;
        public int mainHumidity;
        public int mainPressure;
        public double mainTempMin;
        public double mainTempMax;

        public double windSpeed;
        public double windDeg;

        public int rain3h;

        public int cloudsAll;

        public long dt;
        public long id;
        public String name;
        public int cod;
    }

    public static class DetailWeather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    /**
     * Gson用
     */
    public static class OpenWeatherMapForGson {
        public Coord coord;
        public Sys sys;
        public List<Weather> weather;
        public Main main;
        public Wind wind;
        public Rain rain;
        public Clouds clouds;
        public long dt;
        public long id;
        public String name;
        public int cod;

        public class Coord {
            public double lon;
            public double lat;
        }

        public class Sys {
            public String country;
            public long sunrise;
            public long sunset;
        }

        public class Weather {
            public int id;
            public String main;
            public String description;
            public String icon;
        }

        public class Main {
            public double temp;
            public int humidity;
            public int pressure;
            public double temp_min;
            public double temp_max;
        }

        public class Wind {
            public double speed;
            public double deg;
        }

        public class Rain {
            @SerializedName("3h")
            public String three;
        }

        public class Clouds {
            public int all;
        }
    }
}
