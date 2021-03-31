package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    // Global shared preferences variable
    protected SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String spName = "prefs";
        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);
        ArrayList<Achievement> allAchievements = new ArrayList<>();

        // Exit the game
        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });

        Button loadSaveButton = findViewById(R.id.resume_game);
        loadSaveButton.setOnClickListener(view -> {
            if (sharedPreferences.contains("pageNo") && sharedPreferences.contains("files")) {
                Intent game = new Intent(MainActivity.this, GameActivity.class);
                game.putExtra("pageNo", sharedPreferences.getInt("pageNo", 0));
                game.putExtra("files", sharedPreferences.getString("files", ""));
                game.putExtra("dayTime", sharedPreferences.getInt("dayTime", 0));
                game.putExtra("pointInTime", sharedPreferences.getString("pointInTime", ""));
                startActivity(game);
            } else {
                LayoutInflater inflater = getLayoutInflater();

                View toastLayout = inflater.inflate(R.layout.achievement_toast,
                        findViewById(R.id.toast_root_view));

                TextView header = toastLayout.findViewById(R.id.toast_header);
                header.setText("Error!");

                TextView body = toastLayout.findViewById(R.id.toast_body);
                body.setText("No saved game detected.");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(toastLayout);
                toast.show();
            }
        });

        BufferedReader reader;
        try {
            // Open JSON file with achievements data, and process it
            InputStream inputStream = getAssets().open("initAchievements.json");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            JSONParser parser = new JSONParser();
            JSONObject fileData = (JSONObject) parser.parse(reader);
            inputStream.close();
            reader.close();

            JSONArray initAchievements = (JSONArray) fileData.get("achievements");

            assert initAchievements != null;
            for (Object item : initAchievements) {
                JSONObject itemCopy = (JSONObject) item;
                long id = (long) itemCopy.get("id");
                String name = (String) itemCopy.get("name");
                String description = (String) itemCopy.get("description");
                String floatStr = (String) itemCopy.get("multiplier");
                Achievement newAchievement = new Achievement((int) id, name, description, floatStr,false);
                allAchievements.add(newAchievement);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Functionality for "Load Game" button
        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            Intent game = new Intent(MainActivity.this, GameActivity.class);
            // If shared preferences contains these keys, send them to the game activity
            // Otherwise, send empty or zero values
            game.putExtra("pageNo", 0);
            game.putExtra("files", "");
            game.putExtra("dayTime", 0);
            game.putExtra("pointInTime", "");
            startActivity(game);
        });


        // Functionality for "View Achievements" button
        Button achievementList = findViewById(R.id.viewAchievements);
        achievementList.setOnClickListener(view -> {
            Intent achievements = new Intent(MainActivity.this, AchievementList.class);
            achievements.putParcelableArrayListExtra("aList", allAchievements);
            startActivity(achievements);
        });
    }
}