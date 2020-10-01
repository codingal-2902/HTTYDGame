package compx576.assignment.httydgame;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class GameRepository {

    private static GameDatabase gameDB;
    private static final Object LOCK = new Object();
    private Context dbContext;
    protected Bundle extraData;
    private JSONObject tutorialData;
    private List<String> tableOfContents = new ArrayList<>();
    private String pointInTime;
    private int changeDayTime = 0;
    private int counter = 1;
    private int chapterCount = 0;

    public synchronized GameDatabase getGameDB(Context context) {
        if (gameDB == null) {
            dbContext = context;
            synchronized (LOCK) {
                if (gameDB == null) {
                    gameDB = Room.databaseBuilder(dbContext,
                            GameDatabase.class, "Game Database")
                            .fallbackToDestructiveMigration()
                            .addCallback(dbCallback)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return gameDB;
    }

    private RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Executors.newSingleThreadScheduledExecutor().execute(() -> initGame(dbContext, extraData));
        }
    };

    void initTOC(Context context) {
        try {
            JSONObject toc = loadFile(context.getAssets().open("chapterTOC.json"));
            JSONArray tocList = (JSONArray) toc.get("chapters");
            assert tocList != null;
            for (Object chapterObject : tocList) {
                String chapter = (String) chapterObject;
                tableOfContents.add(chapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initAchievements(Context context, ArrayList<Achievement> allAchievements) {
        for (Achievement a : allAchievements) {
            getGameDB(context).achievementDAO().insertAchievement(a);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void initGame(Context context, Bundle data) {
        JSONArray initialChars = null;

        try {
            JSONObject charData = loadFile(context.getAssets().open("initialChars.json"));
            initialChars = (JSONArray) charData.get("characters");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert initialChars != null;

        for (Object character : initialChars) {
            JSONObject charCopy = (JSONObject) character;
            String charName = (String) charCopy.get("name");
            long relLevel = (long) charCopy.get("relationship");
            NPC newCharacter = new NPC(charName, (int) relLevel);
            getGameDB(context).npcDAO().insertCharacter(newCharacter);
        }

        String loadedFiles = data.getString("files");
        assert loadedFiles != null;
        String[] files = loadedFiles.split(",");

        if (!loadedFiles.equals("")) {
            for (String storedFile : files) {
                try {
                    loadNewScenes(context, storedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        loadNewScenes(context, "tutorial.json");
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
    void loadNewScenes(Context context, String fileName) {
        JSONParser parser = new JSONParser();
        try {
            InputStream tutFile = context.getAssets().open(fileName);
            Reader tutorial = new InputStreamReader(tutFile);
            tutorialData = (JSONObject) parser.parse(tutorial);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        JSONArray storyData = (JSONArray) tutorialData.get("scenes");
        assert storyData != null;

        for (Object scene : storyData) {
            JSONObject sceneCopy = (JSONObject) scene;
            JSONArray chatter = (JSONArray) sceneCopy.get("dialogue");
            assert chatter != null;
            String firstLine = (String) chatter.get(0);

            if (firstLine.equals("LOADNEWCHAPTER")) {
                loadNewScenes(context, tableOfContents.get(chapterCount));
                chapterCount += 1;
                break;
            }

            if (firstLine.contains("convo1")) {
                JSONArray relThreshold = (JSONArray) sceneCopy.get("relLevels");
                assert relThreshold != null;
                long[] threshold = new long[relThreshold.size()];
                for (int i = 0; i < threshold.length; i++) {
                    threshold[i] = (long) relThreshold.get(i);
                }
                String[] plotNames = new String[chatter.size()];
                for (int i = 0; i < plotNames.length; i++) {
                    plotNames[i] = (String) chatter.get(i);
                }
                String affectedCharName = (String) sceneCopy.get("affectedCharName");
                NPC affectedChar = getGameDB(context).npcDAO().findNPCByName(affectedCharName);
                String divergedConvo = getDivergentConversation(affectedChar, threshold, plotNames);
                loadNewScenes(context, divergedConvo);
                continue;
            }

            StringBuilder sb = new StringBuilder();
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
                changeDayTime = counter;
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
                    affectedChars.add(getGameDB(context).npcDAO().findNPCByName(nameCopy));
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
            List<Achievement> achievements = getGameDB(context).achievementDAO().getAllAchievements();
            if (achievementDetails != null) {
                a = achievements.stream().filter(e -> e.getName().contains(achievementDetails)).findFirst().orElse(null);
            }

            Gson gson = new Gson();
            String choiceString = gson.toJson(c);
            String achString = gson.toJson(a);

            Dialogue newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, choiceString, achString);
            getGameDB(context).dialogueDAO().insertNewPage(newScene);
            counter++;
        }
    }

    protected String getDivergentConversation(NPC character, long[] threshold, String[] plots) {
        int length = threshold.length;
        String plotName = "";

        if (length == 1) {
            if (character.getRelationship() <= threshold[0]) {
                plotName = plots[0];
            } else {
                plotName = plots[1];
            }
        } else {
            for (int i = 1; i < length; i++) {
                if (character.getRelationship() <= threshold[i-1]) {
                    plotName = plots[i-1];
                } else if (threshold[i-1] < character.getRelationship() && character.getRelationship() <= threshold[i]) {
                    plotName = plots[i];
                } else if (character.getRelationship() > threshold[i]) {
                    plotName = plots[i];
                }
            }
        }
        return plotName;
    }

    public String getPointInTime() {
        return this.pointInTime;
    }

    public int getDayTime() { return this.changeDayTime; }

    public int getChapterNumber() {
        return this.chapterCount;
    }

    public void resetDayTime(int dayTime) {
        this.changeDayTime = dayTime;
    }

    public void resetPointInTime(String pointInTime) {
        this.pointInTime = pointInTime;
    }

    public void resetChapterCount(int chapterNo) {
        this.chapterCount = chapterNo;
    }

    public Dialogue getPage(Context context, int position) {
        return getGameDB(context).dialogueDAO().getPage(position);
    }

    public List<Dialogue> getAllScenes(Context context) {
        return getGameDB(context).dialogueDAO().getAll();
    }

    public NPC getCharByName(Context context, String name) {
        return getGameDB(context).npcDAO().findNPCByName(name);
    }

    public void updateRelationship(Context context, String charName, float newValue) {
        getGameDB(context).npcDAO().updateRelationship(charName, newValue);
    }

    public List<NPC> getAllNPCs(Context context) {
        return getGameDB(context).npcDAO().getAllNPCs();
    }

    public Achievement getAchievement(Context context, String achName) {
        return getGameDB(context).achievementDAO().getAchievementByName(achName);
    }

    public void unlockAchievement(Context context, String achName) {
        getGameDB(context).achievementDAO().setUnlocked(achName);
    }

    public List<Achievement> getAchievements(Context context) {
        return getGameDB(context).achievementDAO().getAllAchievements();
    }
}
