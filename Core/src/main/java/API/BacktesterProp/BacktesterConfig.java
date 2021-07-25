package API.BacktesterProp;

import org.json.*;

import java.util.Map;

import javax.annotation.processing.FilerException;

import java.util.ArrayList;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

import java.io.FileReader;

public class BacktesterConfig {

    public int ticks;
    public String frequency;
    public double startAmount;
    public String startCurrency;
    public String spreadType;
    public double spread;

    public double GetSpread() {
        return this.spread;
    };

    public int GetTicks() {
        return this.ticks;
    };

    public BacktesterConfig() {
    };

    public BacktesterConfig(JSONObject config) {
        try {
            this.ticks = config.getInt("ticks");
        } catch (JSONException e) {
            this.ticks = 1;
        }

        try {
            this.frequency = config.getString("frequency");
        } catch (JSONException e) {
            this.frequency = "daily";
        }

        try {
            this.spread = config.getDouble("spread");
        } catch (JSONException e) {
            this.spread = 0.0;
        }

        try {
            this.startAmount = config.getDouble("startAmount");
        } catch (JSONException e) {
            this.startAmount = 10000;
        }

    };

    public BacktesterConfig BacktesterConfigFromFile(String fileName) {
        JSONObject config;
        try {
            config = new JSONObject(new FileReader(fileName));
        } catch (Exception e) {
            config = null;
        }

        return new BacktesterConfig(config);
    }

}
