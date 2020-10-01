package compx576.assignment.httydgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AchievementList extends AppCompatActivity {

    protected ArrayList<Achievement> achievementList = new ArrayList<>();
    protected Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_list);
        GameRepository repo = new GameRepository();
        List<Achievement> originalAchievementList = repo.getAchievements(getApplicationContext());
        achievementList.addAll(originalAchievementList);

        ListAdapter adapter = new ListAdapter(this, achievementList);
        ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

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

    static class ListAdapter extends ArrayAdapter<Achievement> {

        public ListAdapter(@NonNull Context context, ArrayList<Achievement> aList) {
            super(context, 0, aList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Achievement achievement = getItem(position);
            assert achievement != null;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
            }

            TextView label = convertView.findViewById(R.id.label);
            TextView desc = convertView.findViewById(R.id.desc);

            label.setText(achievement.getName());
            desc.setText(achievement.getDescription());

            if (achievement.isUnlocked()) {
                desc.setTextColor(Color.GREEN);
            } else {
                desc.setTextColor(Color.parseColor("#bebebe"));
            }

            return convertView;
        }
    }
}