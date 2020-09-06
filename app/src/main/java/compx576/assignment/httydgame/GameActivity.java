package compx576.assignment.httydgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.ArraySet;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    private ImageView background;
    private Button proceedButton;
    private Button returnButton;
    private Button choice1Button;
    private Button choice2Button;
    private TypeWriter dialog;
    private TextView speaker;
    private TypedArray imageResources;
    private List<Dialogue> pages;
    private static List<NPC> characters;
    private Dialogue currentPage;
    private int pageNo;
    private Set<String> loadedFiles;
    protected SharedPreferences sharedPreferences;

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate was called.");
        setContentView(R.layout.activity_main);
        pages = new ArrayList<>();
        characters = new ArrayList<>();
        loadedFiles = new ArraySet<>();

        proceedButton = findViewById(R.id.proceed);
        returnButton = findViewById(R.id.goBack);
        choice1Button = findViewById(R.id.choice1);
        choice2Button = findViewById(R.id.choice2);
        dialog = (TypeWriter) findViewById(R.id.chatter);
        background = findViewById(R.id.myImageView);
        speaker = findViewById(R.id.whoIsTalking);
        imageResources = getResources().obtainTypedArray(R.array.list);

        proceedButton.setVisibility(View.VISIBLE);
        returnButton.setVisibility(View.INVISIBLE);
        choice1Button.setVisibility(View.INVISIBLE);
        choice2Button.setVisibility(View.INVISIBLE);

        JSONArray initialChars = null;

        String spName = "prefs";
        sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE);

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

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        pageNo = bundle.getInt("savedPage", 0);
        loadedFiles = new ArraySet<>(Arrays.asList(Objects.requireNonNull(bundle.getStringArray("files"))));
        if (loadedFiles.size() != 0) {
            for (String storedFile : loadedFiles) {
                try {
                    loadNewScenes(storedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (pageNo > 0) {
            returnButton.setVisibility(View.VISIBLE);
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

        proceedButton.setOnClickListener(view -> {
            pageNo++;
            if (pageNo != 0) {
                returnButton.setVisibility(View.VISIBLE);
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
            grabNextSegment(pageNo);
        });

        returnButton.setOnClickListener(view -> {
            pageNo--;
            proceedButton.setVisibility(View.VISIBLE);
            if (pageNo == 0) {
                returnButton.setVisibility(View.INVISIBLE);
            }
            if (pages.get(pageNo).getWhatIf() == null) {
                invertChoiceAndProceed(true);
            }
            grabPrevSegment(pageNo);
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
        if (currentPage.getWhatIf() == null) {
            invertChoiceAndProceed(true);
        } else {
            choice1Button.setText(currentPage.getWhatIf().getChoice1());
            choice2Button.setText(currentPage.getWhatIf().getChoice2());
        }
//        repo.setCurrentPage(getApplicationContext(), position+1);
//        repo.resetOldPage(getApplicationContext(), position);
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
//        dialog.setText(Html.fromHtml(currentPage.getText()));
        dialog.animateText(Html.fromHtml(currentPage.getText()));
    }

    protected void grabPrevSegment(int position) {
        currentPage = pages.get(position);

        // NOT ALLOWED TO GO BACK AND REDO A CHOICE!
        invertChoiceAndProceed(true);

//        repo.setCurrentPage(getApplicationContext(), position);
//        repo.resetOldPage(getApplicationContext(), position+1);
        background.setImageDrawable(imageResources.getDrawable(currentPage.getBgImage()));
        speaker.setText(Html.fromHtml(currentPage.getCharName()));
//        dialog.setText(Html.fromHtml(currentPage.getText()));
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
            storedChar.setRelationship(storedChar.getRelationship() + (int) relChanges[0]);
        } else {
            for (int i = 0; i < offset; i++) {
                storedChar = characters.get(characters.indexOf(chars.get(i)));
                storedChar.setRelationship(storedChar.getRelationship() + (int) relChanges[(offset*whichChoice) + i]);
            }
        }
        String fileName = c.getNextFile()[whichChoice];
        loadNewScenes(fileName); loadedFiles.add(fileName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("pageNo", pageNo);
        editor.putStringSet("files", loadedFiles);
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

            Dialogue newScene;
            if (isDivergent != 0) {
                newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, c);
            } else {
                newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, null);
            }
            pages.add(newScene);
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