package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    private static List<NPC> characters;
    private ArrayList<Choice> divergencePoints;
    private Dialogue currentPage;
    private int pageNo;
    private String spName = "prefs";
    protected SharedPreferences sharedPreferences;
    private boolean firstDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate was called.");
        setContentView(R.layout.activity_main);
        pages = new ArrayList<>();
        characters = new ArrayList<>();
        firstDialog = true;
        divergencePoints = new ArrayList<>();

        Button proceedButton = findViewById(R.id.proceed);
        returnButton = findViewById(R.id.goBack);
        returnButton.setVisibility(View.INVISIBLE);
        dialog = findViewById(R.id.chatter);
        background = findViewById(R.id.myImageView);
        speaker = findViewById(R.id.whoIsTalking);
        imageResources = getResources().obtainTypedArray(R.array.list);

        JSONArray openingScenes = null;
        JSONArray initialChars = null;
        JSONArray noGoingBack = null;

        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

        try {
            JSONObject charData = loadFile(getApplicationContext().getAssets().open("initialChars.json"));
            initialChars = (JSONArray) charData.get("characters");
            loadNewScenes("tutorial.json");
            loadChoicesFile("choice1.json");
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
                makeChoice(pages.get(pageNo), 0);
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

    protected void makeChoice(Dialogue page, int choiceNo) {
//        FragmentManager fm = getSupportFragmentManager();
//        Choice c = divergencePoints.get(choiceNo);
//        ChoiceDialog cd = ChoiceDialog.newInstance(c);
//        cd.show(fm, "fm");
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void loadNewScenes(String fileName) throws IOException, ParseException {
        JSONObject sceneData = loadFile(getApplicationContext().getAssets().open(fileName));
        JSONArray newScenes = (JSONArray) sceneData.get("scenes");
        assert newScenes != null;
        for (Object scene : newScenes) {
            boolean branchPoint;
            JSONObject sceneCopy = (JSONObject) scene;
            if (sceneCopy.containsKey("choice")) {
                System.out.println(sceneCopy.get("choice"));
            }
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void loadChoicesFile(String fileName) throws IOException, ParseException {
        JSONObject choiceData = loadFile(getApplicationContext().getAssets().open(fileName));
        JSONArray newChoices = (JSONArray) choiceData.get("choice");
        assert newChoices != null;
        for (Object choice : newChoices) {
            JSONObject choiceCopy = (JSONObject) choice;
            String choiceText = (String) choiceCopy.get("text");
            String choice1 = (String) choiceCopy.get("ChoiceOne");
            String choice2 = (String) choiceCopy.get("ChoiceTwo");
            List<String> chars = (List<String>) choiceCopy.get("charNames");
            List<NPC> affectedChars = new ArrayList<>();
            assert chars != null;
            chars.forEach(charName -> affectedChars.add(findNPCinDB(charName)));
            JSONArray changes = (JSONArray) choiceCopy.get("relChange");
            assert changes != null;
            long[] relChanges = new long[changes.size()];
            for (int i = 0; i < changes.size(); i++) {
                relChanges[i] = (long) changes.get(i);
            }
            Choice c = new Choice(choiceText, choice1, choice2, affectedChars, relChanges);
            divergencePoints.add(c);
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

//    public static class ChoiceDialog extends DialogFragment {
//        public ChoiceDialog() {
//
//        }
//
//        static ChoiceDialog newInstance(Choice choice) {
//            ChoiceDialog box = new ChoiceDialog();
//            Bundle args = new Bundle();
//            args.putParcelable("choice", choice);
//            box.setArguments(args);
//            return box;
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder choiceDialogBuilder = new AlertDialog.Builder(getActivity());
//            choiceDialogBuilder.setTitle("Time to make a decision.");
//            assert getArguments() != null;
//            Choice c = (Choice) getArguments().get("choice");
//            assert c != null;
//            choiceDialogBuilder.setMessage(Html.fromHtml(c.getText()))
//                    .setNeutralButton(c.getChoice1(), (dialogInterface, i) -> {
//
//                    });
//            return choiceDialogBuilder.create();
//        }
//    }
}