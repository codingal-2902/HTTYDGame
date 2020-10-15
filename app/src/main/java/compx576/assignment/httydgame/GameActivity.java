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

// Main activity
public class GameActivity extends AppCompatActivity {

    // Global variables
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
    private Dialogue currentPage;
    private int pageNo;
    private float relMultiplier;
    private String loadedFiles;
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

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String spName = "prefs";
        sharedPreferences = getApplicationContext().getSharedPreferences(spName, MODE_PRIVATE);

        // Initialise database, and create a table of contents
        gameRepo = new GameRepository();
        gameRepo.initTOC(getApplicationContext());

        // If the database is empty, populate it, otherwise re-instantiate some variables
        if (gameRepo.getAllScenes(getApplicationContext()).size() == 0) {
            gameRepo.initGame(getApplicationContext(), bundle);
        } else {
            gameRepo.resetDayTime(sharedPreferences.getInt("dayTime", 0));
            gameRepo.resetPointInTime(sharedPreferences.getString("pointInTime", ""));
        }

        // Initialise global variables
        pages = gameRepo.getAllScenes(getApplicationContext());
        characters = gameRepo.getAllNPCs(getApplicationContext());
        achievements = gameRepo.getAchievements(getApplicationContext());
        loadedFiles = "";
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

        // Create a back button for the user to navigate back to the home screen
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve the page number variable from shared preferences
        // If it is null, set it to 0
        // Set the current page pf the story
        pageNo = sharedPreferences.getInt("pageNo", 0);
        currentPage = pages.get(pageNo);

        checkChoiceVariable(currentPage.getWhatIf());

        // Set the initial background image, the name of the initial person speaking, and the initial dialogue being spoken
        // Also allow the typewriter effect to be skipped
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText("");
        dialog.setCharacterDelay(50);
        dialog.animateText(Html.fromHtml(currentPage.getText()));
        chatterBox.setOnClickListener(view -> dialog.removeDelay());
        dialog.setOnClickListener(view -> dialog.removeDelay());

        // Set up the functionality of the proceed button
        proceedButton.setOnClickListener(view -> {
            // Increment the page number
            pageNo++;
            // If the page number equals one that contains a new day/time alert
            if (pageNo == gameRepo.getDayTime()-1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView title = new TextView(this);

                // Set the alert dialog's values
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

                // Hide the dialogue box
                chatterBox.setVisibility(View.INVISIBLE);
                background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));

                // Automatically dismiss the alert dialog after five seconds
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
            // Otherwise, get the next page as per normal
            } else {
                grabNextSegment(pageNo);
            }
            // If the current page does not contain a choice, keep the choice buttons hidden
            if (currentPage.getWhatIf() == null) {
                invertChoiceAndProceed(true);
            } else {
                checkChoiceVariable(currentPage.getWhatIf());
            }
        });

        // These buttons are hidden most of the time, but when they are visible...
        choice1Button.setOnClickListener(view -> {
            try {
                // Convert the choice object stored in the current page to its intended class
                // It's stored as a string initially, so it doesn't require its own table in the database
                // Set the alreadySeen boolean to true, increment the page number, and get the next page
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

    // Code for the back button that navigates from this screen to the home screen
    // Start the MainActivity class, and exit this one
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

    // Invert the visibility of the proceed and choice buttons
    // If one is visible, hide the other(s)
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

    // Move to the next page
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void grabNextSegment(int position) {
        // Reset the current page variable and the typewriter delay
        currentPage = gameRepo.getPage(getApplicationContext(), position+1);
        dialog.setCharacterDelay(50);
        // Check if the new current page has a choice attached to it
        checkChoiceVariable(currentPage.getWhatIf());
        // Load the achievement object attached to the current page
        // If it is not null, and it hasn't already been unlocked, display it on the screen
        Achievement a = gson.fromJson(currentPage.hasAchievement(), Achievement.class);
        if (a != null) {
            a = gameRepo.getAchievement(getApplicationContext(), a.getName());
            if (!a.isUnlocked()) {
                displayAchievement(currentPage);
            }
        }
        // Get and set the background image, the name of the person speaking, and their dialogue, from the current page
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.animateText(Html.fromHtml(currentPage.getText()));
    }

    //
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

    // Displays an achievement
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void displayAchievement(Dialogue page) {
        // Like the choice object, achievements are stored as strings within a Dialogue object
        // While they do have their own table, this method makes retrieving an achievement from the database easier
        String achString = page.hasAchievement();
        Achievement a = gson.fromJson(achString, Achievement.class);
        if (a != null) {
            a = gameRepo.getAchievement(getApplicationContext(), a.getName());
            // Set the relationship multiplier
            setRelMultiplier(a.getMultiplier());
            for (Achievement ach : achievements) {
                // Unlock the achievement in the database
                if (a.getName().equals(ach.getName())) {
                    gameRepo.unlockAchievement(getApplicationContext(), a.getName());
                    break;
                }
            }

            // Toast setup for the achievement display notification
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

    // Set the multiplier for the relationship values
    // This affects how fast, or how slowly, the player increases or decreases relationships with other character overall.
    protected void setRelMultiplier(String newMultiplier) {
        float floatVal = Float.parseFloat(newMultiplier);
        relMultiplier += floatVal;
    }

    // Check if a given page has a choice variable
    // If it does, only set the choice buttons to visible if it hasn't been seen in-story
    protected void checkChoiceVariable(String chString) {
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

    // This is called when the application is paused
    // It stores a number of variables into shared preferences, so they can be retrieved when the game is resumed
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
    }
}