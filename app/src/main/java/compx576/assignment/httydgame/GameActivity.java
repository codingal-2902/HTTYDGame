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
import android.util.ArraySet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private ImageView background;
    private Button proceedButton;
    private Button choice1Button;
    private Button choice2Button;
    private TypeWriter dialog;
    private TextView speaker;
    private View chatterBox;
    private TypedArray imageResources;
    private List<Dialogue> pages;
    private static List<NPC> characters;
    private Dialogue currentPage;
    private int pageNo;
    private int changeDayTime;
    private float relMultiplier;
    private String pointInTime;
    private String loadedFiles;
    private ArrayList<Achievement> achievements;
    protected SharedPreferences sharedPreferences;
    protected Intent intent = new Intent();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate was called.");
        setContentView(R.layout.activity_main);
        pages = new ArrayList<>();
        characters = new ArrayList<>();
        loadedFiles = "";
        achievements = new ArrayList<>();
        pointInTime = "";
        changeDayTime = 0;
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

        JSONArray initialChars = null;

        String spName = "prefs";
        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String[] testDrive = (String[]) bundle.get("aList");

        assert testDrive != null;
        for (String gsonObj : testDrive) {
            Gson gson = new Gson();
            Achievement achievementObj = gson.fromJson(gsonObj, Achievement.class);
            achievements.add(achievementObj);
        }

        try {
            JSONObject charData = loadFile(getApplicationContext().getAssets().open("initialChars.json"));
            initialChars = (JSONArray) charData.get("characters");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert initialChars != null;

        for (Object character : initialChars) {
            JSONObject charCopy = (JSONObject) character;
            String charName = (String) charCopy.get("name");
            long relLevel = (long) charCopy.get("relationship");
            NPC newCharacter = new NPC(charName, (int) relLevel, false);
            characters.add(newCharacter);
        }

        try {
            loadNewScenes("tutorial.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        pageNo = bundle.getInt("savedPage", 0);
        loadedFiles = bundle.getString("files");
        assert loadedFiles != null;
        String[] files = loadedFiles.split(",");

        if (!loadedFiles.equals("")) {
            for (String storedFile : files) {
                try {
                    loadNewScenes(storedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (pageNo > 0) {
            proceedButton.setVisibility(View.VISIBLE);
        }

        if (pages.get(pageNo).getWhatIf() != null) {
            if (!pages.get(pageNo).getWhatIf().alreadyBeenSeen()) {
                invertChoiceAndProceed(false);
                choice1Button.setText(pages.get(pageNo).getWhatIf().getChoice1());
                choice2Button.setText(pages.get(pageNo).getWhatIf().getChoice2());
            }
        } else {
            invertChoiceAndProceed(true);
        }

        currentPage = pages.get(pageNo);

        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText("");
        dialog.setCharacterDelay(50);
        dialog.animateText(Html.fromHtml(currentPage.getText()));
        dialog.setOnClickListener(view -> dialog.removeDelay());

        proceedButton.setOnClickListener(view -> {
            pageNo++;
            if (pageNo == changeDayTime) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView title = new TextView(this);

                title.setText(pointInTime);
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
                background.setImageDrawable(imageResources.getDrawable(pages.get(pageNo).getBgImage()));

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
            if (pages.get(pageNo).getWhatIf() == null) {
                invertChoiceAndProceed(true);
            } else {
                if (pages.get(pageNo).getWhatIf().alreadyBeenSeen()) {
                    invertChoiceAndProceed(true);
                } else {
                    invertChoiceAndProceed(false);
                }
            }
        });

        choice1Button.setOnClickListener(view -> {
            try {
                makeChoice(pages.get(pageNo).getWhatIf(), 0);
                pages.get(pageNo).getWhatIf().setAlreadySeen(true);
                pageNo++;
                grabNextSegment(pageNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        choice2Button.setOnClickListener(view -> {
            try {
                makeChoice(pages.get(pageNo).getWhatIf(), 1);
                pages.get(pageNo).getWhatIf().setAlreadySeen(true);
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

    protected void grabNextSegment(int position) {
        currentPage = pages.get(position);
        dialog.setCharacterDelay(50);
        if (currentPage.getWhatIf() == null) {
            invertChoiceAndProceed(true);
        } else {
            choice1Button.setText(currentPage.getWhatIf().getChoice1());
            choice2Button.setText(currentPage.getWhatIf().getChoice2());
        }
        if (currentPage.getAchievement() != null) {
            if (!currentPage.getAchievement().isUnlocked()) {
                displayAchievement(currentPage);
            }
        }
//        repo.setCurrentPage(getApplicationContext(), position+1);
//        repo.resetOldPage(getApplicationContext(), position);
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.animateText(Html.fromHtml(currentPage.getText()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void makeChoice(Choice c, int whichChoice) throws IOException, ParseException {
        List<NPC> chars = c.getAffectedChar();
        long[] relChanges = c.getLevelChange();

        int offset = relChanges.length/2;
        NPC storedChar;

        if (chars.size() == relChanges.length) {
            storedChar = characters.get(characters.indexOf(chars.get(0)));
            storedChar.setRelationship(storedChar.getRelationship() + ((int) relChanges[0]*relMultiplier));
        } else {
            for (int i = 0; i < offset; i++) {
                storedChar = characters.get(characters.indexOf(chars.get(i)));
                storedChar.setRelationship(storedChar.getRelationship() + ((int) relChanges[(offset*whichChoice) + i])*relMultiplier);
            }
        }
        String fileName = c.getNextFile()[whichChoice];
        loadNewScenes(fileName);
        if (loadedFiles.equals("")) {
            loadedFiles = fileName;
        } else {
            loadedFiles = loadedFiles.concat(","+fileName);
        }
    }

    protected void displayAchievement(Dialogue page) {
        Achievement a = page.getAchievement();
        setRelMultiplier(a.getMultiplier());
        achievements.get(achievements.indexOf(a)).setUnlocked(true);

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

    protected void setRelMultiplier(String newMultiplier) {
        float floatVal = Float.parseFloat(newMultiplier);
        relMultiplier += floatVal;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();

        Set<String> achHolder = new ArraySet<>();
        for (Achievement ach : achievements) {
            Gson gson = new Gson();
            String achString = gson.toJson(ach);
            achHolder.add(achString);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("pageNo", pageNo);
        editor.putString("files", loadedFiles);
        editor.putStringSet("aList", achHolder);
        editor.apply();
        System.out.println("onPause was called.");
    }

    protected JSONObject loadFile(InputStream inputStream) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject fileData = (JSONObject) parser.parse(reader);
        inputStream.close();
        reader.close();
        return fileData;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void loadNewScenes(String fileName) throws IOException, ParseException {
        JSONObject sceneData = loadFile(getApplicationContext().getAssets().open(fileName));
        JSONArray newScenes = (JSONArray) sceneData.get("scenes");
        assert newScenes != null;
        int x = 0;
        for (Object scene : newScenes) {
            JSONObject sceneCopy = (JSONObject) scene;
            JSONArray chatter = (JSONArray) sceneCopy.get("dialogue");
            StringBuilder sb = new StringBuilder();
            assert chatter != null;
            for (Object line : chatter) {
                String lineObj = (String) line;
                if (!lineObj.equals("")) {
                    sb.append(lineObj).append("\n");
                }
            }
            String name = (String) sceneCopy.get("speaker");
            long bgImageID = (long) sceneCopy.get("imageIDIndex");

            if (sceneCopy.get("pointInTime") != null) {
                pointInTime = (String) sceneCopy.get("pointInTime");
                changeDayTime = x;
            }

            JSONObject choiceDetails = (JSONObject) sceneCopy.get("choice");
            Choice c = null;
            if (choiceDetails != null) {
                String choice1 = (String) choiceDetails.get("ChoiceOne");
                String choice2 = (String) choiceDetails.get("ChoiceTwo");
                JSONArray chars = (JSONArray) choiceDetails.get("charNames");
                assert chars != null;
                List<NPC> affectedChars = new ArrayList<>();
                for (Object charName : chars) {
                    String nameCopy = (String) charName;
                    affectedChars.add(findNPCinDB(nameCopy));
                }
                JSONArray changes = (JSONArray) choiceDetails.get("relChange");
                assert changes != null;
                long[] relChanges = new long[changes.size()];
                for (int i = 0; i < changes.size(); i++) {
                    relChanges[i] = (long) changes.get(i);
                }
                JSONArray files = (JSONArray) choiceDetails.get("fileToOpen");
                assert files != null;
                String[] additionalScenes = new String[files.size()];
                for (int j = 0; j < files.size(); j++) {
                    additionalScenes[j] = (String) files.get(j);
                }
                c = new Choice(false, choice1, choice2, affectedChars, relChanges, additionalScenes);
            }

            String achievementDetails = (String) sceneCopy.get("unlockAchievement");
            Achievement a = null;
            if (achievementDetails != null) {
                a = achievements.stream().filter(e->e.getName().contains(achievementDetails)).findFirst().orElse(null);
            }

            Dialogue newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, c, a);

            pages.add(newScene);
            x++;
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    protected String parseDialogue(JSONArray obj) {
//        StringBuilder sb = new StringBuilder();
//        obj.forEach(line -> {
//            String lineObj = (String) line;
//            if (!lineObj.equals("")) {
//                sb.append(lineObj).append("\n");
//            }
//        });
//        return sb.toString();
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NPC findNPCinDB(String name) {
        return characters.stream().filter(e->e.getCharName().equals(name)).findFirst().orElse(null);
    }
}