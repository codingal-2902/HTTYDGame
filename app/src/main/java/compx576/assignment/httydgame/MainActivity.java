package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    protected int savedPage;
    private String spName = "prefs";
    protected SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        System.out.println("onCreate from MainActivity was called.");

        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            Intent game = new Intent(MainActivity.this, GameActivity.class);
            if (sharedPreferences.contains("pageNo")) {
                game.putExtra("savedPage", sharedPreferences.getInt("pageNo", 0));
            } else {
                game.putExtra("savedPage", 0);
            }
            startActivity(game);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause from MainActivity was called.");
//        Bundle bundle = getIntent().getExtras();
//        assert bundle != null;
//        System.out.println(bundle.get("pageNo"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop from MainActivity was called.");
//        Bundle bundle = getIntent().getExtras();
//        assert bundle != null;
//        System.out.println(bundle.get("pageNo"));
    }
}