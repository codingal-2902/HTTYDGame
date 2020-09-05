package compx576.assignment.httydgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AchievementList extends AppCompatActivity {

    protected SharedPreferences sp;
    protected String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    protected ArrayList<Achievement> achievementList;

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_list);
        System.out.println("onCreate() from AchievementList was called.");
        achievementList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        String[] testDrive = (String[]) bundle.get("aList");

        assert testDrive != null;
        for (String gsonObj : testDrive) {
            Gson gson = new Gson();
            Achievement achievementObj = gson.fromJson(gsonObj, Achievement.class);
            achievementList.add(achievementObj);
        }

        // Create the adapter to convert the array to views
        ListAdapter adapter = new ListAdapter(this, achievementList);
        // Attach the adapter to a ListView
        ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

//        ArrayAdapter adapter = new ArrayAdapter<>(this,
//                R.layout.activity_listview, mobileArray);
//
//        ListView listView = findViewById(R.id.mobile_list);
//        listView.setAdapter(adapter);

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

    class ListAdapter extends ArrayAdapter<Achievement> {
        public ListAdapter(@NonNull Context context, ArrayList<Achievement> aList) {
            super(context, 0, aList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            Achievement achievement = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
            }
            // Lookup view for data population
            TextView label = convertView.findViewById(R.id.label);
            TextView desc = convertView.findViewById(R.id.desc);
            // Populate the data into the template view using the data object
            assert achievement != null;
            label.setText(achievement.getName());
            desc.setText(achievement.getDescription());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}