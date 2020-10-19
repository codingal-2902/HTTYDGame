package compx576.assignment.httydgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

public class LoadSaveGame extends AppCompatActivity {

    protected Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_save_game);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            intent.setClass(LoadSaveGame.this, MainActivity.class);
            startActivity(intent);
            LoadSaveGame.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}