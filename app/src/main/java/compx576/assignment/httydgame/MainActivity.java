package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Objects;

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

        Button achievementList = findViewById(R.id.viewAchievements);
        achievementList.setOnClickListener(view -> {
            Intent achievements = new Intent(MainActivity.this, AchievementList.class);
            startActivity(achievements);
        });
    }
}