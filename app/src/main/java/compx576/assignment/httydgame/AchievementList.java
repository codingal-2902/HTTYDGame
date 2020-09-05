package compx576.assignment.httydgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Objects;

public class AchievementList extends AppCompatActivity {

    protected SharedPreferences sp;
    protected String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_list);
        System.out.println("onCreate() from AchievementList was called.");

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_listview, mobileArray);

        ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        String[] testDrive = (String[]) bundle.get("aList");
        System.out.println(Arrays.toString(testDrive));

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            intent.setClass(AchievementList.this, MainActivity.class);
            startActivity(intent);
            AchievementList.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}