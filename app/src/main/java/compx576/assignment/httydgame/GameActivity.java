package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class GameActivity extends AppCompatActivity {

    private ImageView background;
    private Button returnButton;
    private TextView dialog;
    private TextView speaker;
    private TypedArray imageResources;
    private List<Dialogue> pages;
    private List<NPC> characters;
    private Dialogue currentPage;
    private int pageNo;
    private String spName = "prefs";
    protected SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate was called.");
        setContentView(R.layout.activity_main);
        pages = new ArrayList<>();
        characters = new ArrayList<>();

        Button proceedButton = findViewById(R.id.proceed);
        returnButton = findViewById(R.id.goBack);
        returnButton.setVisibility(View.INVISIBLE);
        dialog = findViewById(R.id.chatter);
        background = findViewById(R.id.myImageView);
        speaker = findViewById(R.id.whoIsTalking);
        imageResources = getResources().obtainTypedArray(R.array.list);

        JSONArray openingScenes = null;
        JSONArray initialChars = null;

        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

        try {
            JSONObject sceneData = loadFile(getApplicationContext().getAssets().open("tutorial.json"));
            JSONObject charData = loadFile(getApplicationContext().getAssets().open("initialChars.json"));
            openingScenes = (JSONArray) sceneData.get("scenes");
            initialChars = (JSONArray) charData.get("characters");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert openingScenes != null;
        assert initialChars != null;

        for (Object scene : openingScenes) {
            boolean branchPoint;
            JSONObject sceneCopy = (JSONObject) scene;
            JSONArray chatter = (JSONArray) sceneCopy.get("dialogue");
            StringBuilder sb = new StringBuilder();
            assert chatter != null;
            chatter.forEach(line -> {
                String lineObj = (String) line;
                if (!lineObj.equals("")) {
                    sb.append(lineObj).append("\n");
                }
            });
            String name = (String) sceneCopy.get("speaker");
            long bgImageID = (long) sceneCopy.get("imageIDIndex");
            long isDivergent = (long) sceneCopy.get("isDivergencePoint");
            branchPoint = (int) isDivergent != 0;
            Dialogue newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, branchPoint);
            pages.add(newScene);
        }

        for (Object character : initialChars) {
            JSONObject charCopy = (JSONObject) character;
            String charName = (String) charCopy.get("name");
            long relLevel = (long) charCopy.get("relationship");
            NPC newCharacter = new NPC(charName, (int) relLevel, false);
            characters.add(newCharacter);
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        pageNo = bundle.getInt("savedPage", 0);
        currentPage = pages.get(pageNo);
        if (pageNo > 0) {
            returnButton.setVisibility(View.VISIBLE);
        }

        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText(Html.fromHtml(currentPage.getText()));

        proceedButton.setOnClickListener(view -> {
            pageNo++;
            if (pageNo != 0) {
                returnButton.setVisibility(View.VISIBLE);
            }
            if (pages.get(pageNo).hasChoice()) {
                makeChoice(pages.get(pageNo));
            } else {
                grabNextSegment(pageNo);
            }
        });

        returnButton.setOnClickListener(view -> {
            pageNo--;
            if (pageNo == 0) {
                returnButton.setVisibility(View.INVISIBLE);
            }
            grabPrevSegment(pageNo);
        });
    }

    protected void grabNextSegment(int position) {
        currentPage = pages.get(position);
//        repo.setCurrentPage(getApplicationContext(), position+1);
//        repo.resetOldPage(getApplicationContext(), position);
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText(Html.fromHtml(currentPage.getText()));
    }

    protected void grabPrevSegment(int position) {
        currentPage = pages.get(position);
//        repo.setCurrentPage(getApplicationContext(), position);
//        repo.resetOldPage(getApplicationContext(), position+1);
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
        dialog.setText(Html.fromHtml(currentPage.getText()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("pageNo", pageNo);
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

    protected void makeChoice(Dialogue page) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected NPC getNPC(String name) {
        return characters.stream().filter(e->e.getCharName().equals(name)).findAny().orElse(null);
    }
}