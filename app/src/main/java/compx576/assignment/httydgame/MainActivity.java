package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.ArraySet;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        System.out.println("onCreate from MainActivity was called.");

        String spName = "prefs";
        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            Intent game = new Intent(MainActivity.this, GameActivity.class);
            if (sharedPreferences.contains("pageNo") && sharedPreferences.contains("files")) {
                game.putExtra("savedPage", sharedPreferences.getInt("pageNo", 0));
                game.putExtra("files", Objects.requireNonNull(sharedPreferences.getStringSet("files", null)).toArray(new String[0]));
            } else {
                game.putExtra("savedPage", 0);
                game.putExtra("files", new String[0]);
            }
            startActivity(game);
        });

        BufferedReader reader;
        try {
            InputStream inputStream = getAssets().open("initAchievements.json");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            JSONParser parser = new JSONParser();
            JSONObject fileData = (JSONObject) parser.parse(reader);
            inputStream.close();
            reader.close();

            JSONArray initAchievements = (JSONArray) fileData.get("achievements");
            ArrayList<Achievement> allAchievements = new ArrayList<>();

            assert initAchievements != null;
            for (Object item : initAchievements) {
                JSONObject itemCopy = (JSONObject) item;
                long id = (long) itemCopy.get("id");
                String name = (String) itemCopy.get("name");
                String description = (String) itemCopy.get("description");
                Achievement newAchievement = new Achievement((int) id, name, description, false);
                allAchievements.add(newAchievement);
            }

            Set<String> achHolder = new ArraySet<>();
            for (Achievement ach : allAchievements) {
                Gson gson = new Gson();
                String achString = gson.toJson(ach);
                achHolder.add(achString);
            }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("aList")) {
            editor.putStringSet("aList", achHolder);
            editor.apply();
        }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Button achievementList = findViewById(R.id.viewAchievements);
        achievementList.setOnClickListener(view -> {
            Intent achievements = new Intent(MainActivity.this, AchievementList.class);
            achievements.putExtra("aList", Objects.requireNonNull(sharedPreferences.getStringSet("aList", null)).toArray(new String[0]));
            startActivity(achievements);
        });
    }
}