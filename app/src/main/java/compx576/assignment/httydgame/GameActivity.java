package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private GameRepository gameRepo;
    private ImageView background;
    private Button proceedButton;
    private Button choice1Button;
    private Button choice2Button;
    private TypeWriter dialog;
    private TextView speaker;
    private View chatterBox;
    private TypedArray imageResources;
    private List<Dialogue> pages;
    private List<NPC> characters;
    private List<Achievement> achievements;
    private List<String> tocCopy;
    private Dialogue currentPage;
    private int pageNo;
    private float relMultiplier;
    private String loadedFiles;
    private String nextChapter;
    protected SharedPreferences sharedPreferences;
    protected Intent intent = new Intent();
    protected Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // LOTS of initialising
        super.onCreate(savedInstanceState);
        System.out.println("onCreate was called.");
        setContentView(R.layout.activity_main);
        gameRepo = new GameRepository();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String spName = "prefs";
        sharedPreferences = getApplicationContext().getSharedPreferences(spName, MODE_PRIVATE);

        gameRepo.initTOC(getApplicationContext());

        if (gameRepo.getAllScenes(getApplicationContext()).size() == 0) {
            gameRepo.initGame(getApplicationContext(), bundle);
        } else {
            gameRepo.resetDayTime(sharedPreferences.getInt("dayTime", 0));
            gameRepo.resetPointInTime(sharedPreferences.getString("pointInTime", ""));
        }

        pages = gameRepo.getAllScenes(getApplicationContext());
        characters = gameRepo.getAllNPCs(getApplicationContext());
        achievements = gameRepo.getAchievements(getApplicationContext());
        loadedFiles = "";
        nextChapter = "";
        relMultiplier = 1;

        proceedButton = findViewById(R.id.proceed);
        choice1Button = findViewById(R.id.choice1);
        choice2Button = findViewById(R.id.choice2);
        dialog = findViewById(R.id.chatter);
        background = findViewById(R.id.myImageView);
        speaker = findViewById(R.id.whoIsTalking);
        chatterBox = findViewById(R.id.dialogueBox);
        imageResources = getResources().obtainTypedArray(R.array.list);

        proceedButton.setVisibility(View.VISIBLE);
        choice1Button.setVisibility(View.INVISIBLE);
        choice2Button.setVisibility(View.INVISIBLE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageNo = sharedPreferences.getInt("pageNo", 0);
        currentPage = pages.get(pageNo);

        convertStringToChoice(currentPage.getWhatIf());

        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText("");
        dialog.setCharacterDelay(50);
        dialog.animateText(Html.fromHtml(currentPage.getText()));
        dialog.setOnClickListener(view -> dialog.removeDelay());

        proceedButton.setOnClickListener(view -> {
            pageNo++;
            if (pageNo == gameRepo.getDayTime()-1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView title = new TextView(this);

                title.setText(gameRepo.getPointInTime());
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);

                builder.setCustomTitle(title);
                builder.setCancelable(true);

                final AlertDialog closedialog = builder.create();

                closedialog.show();
                chatterBox.setVisibility(View.INVISIBLE);
                background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));

                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    public void run() {
                        try {
                            synchronized (this) {
                                runOnUiThread(() -> {
                                    closedialog.dismiss();
                                    timer2.cancel();
                                    chatterBox.setVisibility(View.VISIBLE);
                                    grabNextSegment(pageNo);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
            } else {
                grabNextSegment(pageNo);
            }
            if (currentPage.getWhatIf() == null) {
                invertChoiceAndProceed(true);
            } else {
                convertStringToChoice(currentPage.getWhatIf());
            }
        });

        choice1Button.setOnClickListener(view -> {
            try {
                Choice c = gson.fromJson(currentPage.getWhatIf(), Choice.class);
                makeChoice(c, 0);
                c.setAlreadySeen(true);
                pageNo++;
                grabNextSegment(pageNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        choice2Button.setOnClickListener(view -> {
            try {
                Choice c = gson.fromJson(currentPage.getWhatIf(), Choice.class);
                makeChoice(c, 1);
                c.setAlreadySeen(true);
                pageNo++;
                grabNextSegment(pageNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            intent.setClass(GameActivity.this, MainActivity.class);
            startActivity(intent);
            GameActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void invertChoiceAndProceed(boolean condition) {
        if (condition) {
            proceedButton.setVisibility(View.VISIBLE);
            choice1Button.setVisibility(View.INVISIBLE);
            choice2Button.setVisibility(View.INVISIBLE);
        } else {
            proceedButton.setVisibility(View.INVISIBLE);
            choice1Button.setVisibility(View.VISIBLE);
            choice2Button.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void grabNextSegment(int position) {
        currentPage = gameRepo.getPage(getApplicationContext(), position+1);
        dialog.setCharacterDelay(50);
        convertStringToChoice(currentPage.getWhatIf());
        Achievement a = gson.fromJson(currentPage.hasAchievement(), Achievement.class);
        if (a != null) {
            a = gameRepo.getAchievement(getApplicationContext(), a.getName());
            if (!a.isUnlocked()) {
                displayAchievement(currentPage);
            }
        }
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.animateText(Html.fromHtml(currentPage.getText()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void makeChoice(Choice c, int whichChoice) {
        List<NPC> chars = c.getAffectedChar();
        long[] relChanges = c.getLevelChange();

        int offset = relChanges.length/2;
        NPC storedChar;

        if (chars.size() == relChanges.length) {
            storedChar = characters.get(0);
            storedChar.setRelationship(storedChar.getRelationship() + ((int) relChanges[0]*relMultiplier));
        } else {
            for (int i = 0; i < offset; i++) {
                storedChar = gameRepo.getCharByName(getApplicationContext(), chars.get(i).getCharName());
                float newVal = storedChar.getRelationship() + ((int) relChanges[(offset*whichChoice) + i])*relMultiplier;
                gameRepo.updateRelationship(getApplicationContext(), storedChar.getCharName(), newVal);
            }
        }
        String fileName = c.getNextFile()[whichChoice];
        gameRepo.loadNewScenes(getApplicationContext(), fileName);
        pages = gameRepo.getAllScenes(getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void displayAchievement(Dialogue page) {
        String achString = page.hasAchievement();
        Achievement a = gson.fromJson(achString, Achievement.class);
        if (a != null) {
            a = gameRepo.getAchievement(getApplicationContext(), a.getName());
            setRelMultiplier(a.getMultiplier());
            for (Achievement ach : achievements) {
                if (a.getName().equals(ach.getName())) {
                    gameRepo.unlockAchievement(getApplicationContext(), a.getName());
                    break;
                }
            }
            try {
                gameRepo.loadNewScenes(getApplicationContext(), "chapter1.json");
            } catch (Exception e) {
                e.printStackTrace();
            }

            LayoutInflater inflater = getLayoutInflater();

            View toastLayout = inflater.inflate(R.layout.achievement_toast,
                    findViewById(R.id.toast_root_view));

            TextView header = toastLayout.findViewById(R.id.toast_header);
            header.setText(a.getName());

            TextView body = toastLayout.findViewById(R.id.toast_body);
            body.setText(a.getDescription());

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastLayout);
            toast.show();
        }
    }

    protected void setRelMultiplier(String newMultiplier) {
        float floatVal = Float.parseFloat(newMultiplier);
        relMultiplier += floatVal;
    }

    protected void convertStringToChoice(String chString) {
        Choice c = gson.fromJson(chString, Choice.class);
        if (c == null) {
            invertChoiceAndProceed(true);
        } else {
            if (!c.alreadyBeenSeen()) {
                invertChoiceAndProceed(false);
                choice1Button.setText(c.getChoice1());
                choice2Button.setText(c.getChoice2());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("pageNo", pageNo);
        editor.putString("files", loadedFiles);
        editor.putInt("dayTime", gameRepo.getDayTime());
        editor.putString("pointInTime", gameRepo.getPointInTime());
        editor.apply();
        System.out.println("onPause was called.");
    }
}