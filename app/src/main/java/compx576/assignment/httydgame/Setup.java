package compx576.assignment.httydgame;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Setup {

    protected Bundle extraData;
    private JSONObject tutorialData;
    private final List<String> tableOfContents = new ArrayList<>();
    private String pointInTime;
    private int changeDayTime = 0;
    private int counter = 1;
    private int chapterCount = 0;

//    void initTOC(Context context) {
//        try {
//            JSONObject toc = loadFile(context.getAssets().open("chapterTOC.json"));
//            JSONArray tocList = (JSONArray) toc.get("chapters");
//            assert tocList != null;
//            for (Object chapterObject : tocList) {
//                String chapter = (String) chapterObject;
//                tableOfContents.add(chapter);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Add achievements to the achievements table
//    void initAchievements(Context context, ArrayList<Achievement> allAchievements) {
//        for (Achievement a : allAchievements) {
//            getGameDB(context).achievementDAO().insertAchievement(a);
//        }
//    }
//
//    // Add characters and scenes/pages to their respective tables
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    void initGame(Context context, Bundle data) {
//        JSONArray initialChars = null;
//
//        try {
//            JSONObject charData = loadFile(context.getAssets().open("initialChars.json"));
//            initialChars = (JSONArray) charData.get("characters");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        assert initialChars != null;
//
//        for (Object character : initialChars) {
//            JSONObject charCopy = (JSONObject) character;
//            String charName = (String) charCopy.get("name");
//            long relLevel = (long) charCopy.get("relationship");
//            NPC newCharacter = new NPC(charName, (int) relLevel);
//            getGameDB(context).npcDAO().insertCharacter(newCharacter);
//        }
//
//        // If there are extra file names in shared preferences, load them as well
//        String loadedFiles = data.getString("files");
//        assert loadedFiles != null;
//        String[] files = loadedFiles.split(",");
//
//        if (!loadedFiles.equals("")) {
//            for (String storedFile : files) {
//                try {
//                    loadNewScenes(context, storedFile);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        loadNewScenes(context, "tutorial.json");
//    }
//
//    // General function for loading JSON files
//    protected JSONObject loadFile(InputStream inputStream) throws IOException, ParseException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        JSONParser parser = new JSONParser();
//        JSONObject fileData = (JSONObject) parser.parse(reader);
//        inputStream.close();
//        reader.close();
//        return fileData;
//    }
//
//    // BIG function
//    // Load new JSON files into Dialogue objects and store them in the database
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    void loadNewScenes(Context context, String fileName) {
//        JSONParser parser = new JSONParser();
//        try {
//            InputStream tutFile = context.getAssets().open(fileName);
//            Reader tutorial = new InputStreamReader(tutFile);
//            tutorialData = (JSONObject) parser.parse(tutorial);
//
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//
//        JSONArray storyData = (JSONArray) tutorialData.get("scenes");
//        assert storyData != null;
//
//        // For every individual scene in a file
//        // Get the dialogue object first, and check if it equals 'LOADNEWCHAPTER' or contains 'convo1'
//        // If either one of these, or both, return true, recursively load a new file
//        for (Object scene : storyData) {
//            JSONObject sceneCopy = (JSONObject) scene;
//            JSONArray chatter = (JSONArray) sceneCopy.get("dialogue");
//            assert chatter != null;
//            String firstLine = (String) chatter.get(0);
//
//            // Break the for loop if a new chapter file is loaded
//            if (firstLine.equals("LOADNEWCHAPTER")) {
//                loadNewScenes(context, tableOfContents.get(chapterCount));
//                chapterCount += 1;
//                break;
//            }
//
//            // Skip to the next iteration after this one if this is true
//            if (firstLine.contains("convo1")) {
//                JSONArray relThreshold = (JSONArray) sceneCopy.get("relLevels");
//                assert relThreshold != null;
//                long[] threshold = new long[relThreshold.size()];
//                for (int i = 0; i < threshold.length; i++) {
//                    threshold[i] = (long) relThreshold.get(i);
//                }
//                String[] plotNames = new String[chatter.size()];
//                for (int i = 0; i < plotNames.length; i++) {
//                    plotNames[i] = (String) chatter.get(i);
//                }
//                // Get the names of characters that are affected by a choice
//                String affectedCharName = (String) sceneCopy.get("affectedCharName");
//                NPC affectedChar = getGameDB(context).npcDAO().findNPCByName(affectedCharName);
//                String divergedConvo = getDivergentConversation(affectedChar, threshold, plotNames);
//                loadNewScenes(context, divergedConvo);
//                continue;
//            }
//
//            // Use a string-builder to concatenate dialogue strings
//            StringBuilder sb = new StringBuilder();
//            for (Object line : chatter) {
//                String lineObj = (String) line;
//                if (!lineObj.equals("")) {
//                    sb.append(lineObj).append("\n");
//                }
//            }
//            String name = (String) sceneCopy.get("speaker");
//            long bgImageID = (long) sceneCopy.get("imageIDIndex");
//
//            if (sceneCopy.get("pointInTime") != null) {
//                pointInTime = (String) sceneCopy.get("pointInTime");
//                changeDayTime = counter;
//            }
//
//            // Load choice objects into a string
//            JSONObject choiceDetails = (JSONObject) sceneCopy.get("choice");
//            Choice c = null;
//            if (choiceDetails != null) {
//                String choice1 = (String) choiceDetails.get("ChoiceOne");
//                String choice2 = (String) choiceDetails.get("ChoiceTwo");
//                JSONArray chars = (JSONArray) choiceDetails.get("charNames");
//                assert chars != null;
//                List<NPC> affectedChars = new ArrayList<>();
//                for (Object charName : chars) {
//                    String nameCopy = (String) charName;
//                    affectedChars.add(getGameDB(context).npcDAO().findNPCByName(nameCopy));
//                }
//                JSONArray changes = (JSONArray) choiceDetails.get("relChange");
//                assert changes != null;
//                long[] relChanges = new long[changes.size()];
//                for (int i = 0; i < changes.size(); i++) {
//                    relChanges[i] = (long) changes.get(i);
//                }
//                JSONArray files = (JSONArray) choiceDetails.get("fileToOpen");
//                assert files != null;
//                String[] additionalScenes = new String[files.size()];
//                for (int j = 0; j < files.size(); j++) {
//                    additionalScenes[j] = (String) files.get(j);
//                }
//                c = new Choice(false, choice1, choice2, affectedChars, relChanges, additionalScenes);
//            }
//
//            // Do the same with achievement objects
//            String achievementDetails = (String) sceneCopy.get("unlockAchievement");
//            Achievement a = null;
//            List<Achievement> achievements = getGameDB(context).achievementDAO().getAllAchievements();
//            if (achievementDetails != null) {
//                a = achievements.stream().filter(e -> e.getName().contains(achievementDetails)).findFirst().orElse(null);
//            }
//
//            Gson gson = new Gson();
//            String choiceString = gson.toJson(c);
//            String achString = gson.toJson(a);
//
//            // Create a new page with all of these details, and store it in the database, and increment the counter
//            Dialogue newScene = new Dialogue(sb.toString(), name, (int) bgImageID, false, choiceString, achString);
//            getGameDB(context).dialogueDAO().insertNewPage(newScene);
//            counter++;
//        }
//    }
//
//    // If a scene changes based on the main character's relationship with a given character, run this function
//    protected String getDivergentConversation(NPC character, long[] threshold, String[] plots) {
//        int length = threshold.length;
//        String plotName = "";
//
//        // If there is
//        if (length == 1) {
//            if (character.getRelationship() <= threshold[0]) {
//                plotName = plots[0];
//            } else {
//                plotName = plots[1];
//            }
//        } else {
//            for (int i = 1; i < length; i++) {
//                if (character.getRelationship() <= threshold[i-1]) {
//                    plotName = plots[i-1];
//                } else if (threshold[i-1] < character.getRelationship() && character.getRelationship() <= threshold[i]) {
//                    plotName = plots[i];
//                } else if (character.getRelationship() > threshold[i]) {
//                    plotName = plots[i];
//                }
//            }
//        }
//        return plotName;
//    }
//
//    // Various getters and (re-)setters
//    public String getPointInTime() {
//        return this.pointInTime;
//    }
//
//    public int getDayTime() { return this.changeDayTime; }
//
//    public void resetDayTime(int dayTime) {
//        this.changeDayTime = dayTime;
//    }
//
//    public void resetPointInTime(String pointInTime) {
//        this.pointInTime = pointInTime;
//    }

}
