package com.jhchoe.aceit;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jhchoe on 10/18/14.
 */
public class ACEitApplication extends Application {

    private final static int NUMBER_OF_LEVELS = 4;
    private static final int NEW_UNLOCKED_LEVEL = 1;
    private static final float NEW_TOTAL_COUNT = 0;
    private static final float NEW_RIGHT_COUNT = 0;
    private static final float NEW_SKIPPED_COUNT = 0;

    private ArrayList<String> wordList;
    private ArrayList<String> incorrectList;

    private boolean playIncorrect = false;

    private String[] accounts;
    private String currentAccount;
    private int unlockedLevel;
    private float[] totalCount;
    private float[] rightCount;
    private float[] skippedCount;

    /* Getters and Setters */
    public ArrayList<String> getWordList() {
        return wordList;
    }

    public ArrayList<String> getIncorrectList() {
        if (incorrectList == null) {
            setIncorrectList(wordList);
        }
        return incorrectList;
    }

    public void setIncorrectList(ArrayList<String> incorrectList) {
        this.incorrectList = incorrectList;
    }

    public boolean isPlayingIncorrect() {
        return playIncorrect;
    }

    public void setPlayIncorrect(boolean playIncorrect) {
        this.playIncorrect = playIncorrect;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    public void setUnlockedLevel(int unlockedLevel) {
        this.unlockedLevel = unlockedLevel;
    }

    public int getNumberOfLevels() {
        return NUMBER_OF_LEVELS;
    }

    public String[] getAccounts() {
        return accounts;
    }

    public void setAccounts(String[] accounts) {
        this.accounts = accounts;
    }

    public float[] getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(float[] totalCount) {
        this.totalCount = totalCount;
    }

    public float[] getRightCount() {
        return rightCount;
    }

    public void setRightCount(float[] rightCount) {
        this.rightCount = rightCount;
    }

    public float[] getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(float[] skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getKey() {
        if (getCurrentAccount() != null) {
            if (currentAccount.equals("Challenge")) {
                return "Challenge";
            }
            return "com.jhchoe.aceit." + currentAccount;
        }
        else {
            return "com.jhchoe.aceit.first,com.jhchoe.aceit.second";
        }
    }

    public void save() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(getKey(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (getKey().equals("com.jhchoe.aceit.first,com.jhchoe.aceit.second")) {
            String s[] = getKey().split(",");
            String a[] = getAccounts();
            editor.putString(s[0], a[0]);
            editor.putString(s[1], a[1]);
            editor.putLong("lastSavedTime", System.currentTimeMillis());
        }
        else if ( getCurrentAccount() != null && ! getKey().equals("Challenge")) {
            editor.putInt("level", getUnlockedLevel());
            for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                editor.putFloat("total" + i, getTotalCount()[i]);
                editor.putFloat("right" + i, getRightCount()[i]);
                editor.putFloat("skip" + i, getSkippedCount()[i]);
            }
            editor.putLong("lastSavedTime", System.currentTimeMillis());
        }
        else {
            for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                editor.putFloat("total" + i, getTotalCount()[i]);
                editor.putFloat("right" + i, getRightCount()[i]);
                editor.putFloat("skip" + i, getSkippedCount()[i]);
            }
            editor.putLong("lastSavedTime", System.currentTimeMillis());
        }
        editor.apply();
    }

    public void load() {
        if (getCurrentAccount() != null) {
            if (getKey().equals("Challenge")) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(getKey(), MODE_PRIVATE);
                setUnlockedLevel(NEW_UNLOCKED_LEVEL);
                float t[] = {0,0,0,0}, r[] = {0,0,0,0}, s[] = {0,0,0,0};
                for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                    t[i] = prefs.getFloat("total" + i, NEW_TOTAL_COUNT);
                    r[i] = prefs.getFloat("right" + i, NEW_RIGHT_COUNT);
                    s[i] = prefs.getFloat("skip" + i, NEW_SKIPPED_COUNT);
                }
                setTotalCount(t);
                setRightCount(r);
                setSkippedCount(s);
            }
            else {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(getKey(), MODE_PRIVATE);
                long lastSavedTime = prefs.getLong("lastSavedTime", 0);

                if (lastSavedTime == 0) {
                    // Have never saved state. Initialize.
                    setUnlockedLevel(NEW_UNLOCKED_LEVEL);
                    float t[] = {0,0,0,0}, r[] = {0,0,0,0}, s[] = {0,0,0,0};
                    setTotalCount(t);
                    setRightCount(r);
                    setSkippedCount(s);
                }
                else {
                    setUnlockedLevel(prefs.getInt("level", NEW_UNLOCKED_LEVEL));
                    float t[] = {0,0,0,0}, r[] = {0,0,0,0}, s[] = {0,0,0,0};
                    for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                        t[i] = prefs.getFloat("total" + i, NEW_TOTAL_COUNT);
                        r[i] = prefs.getFloat("right" + i, NEW_RIGHT_COUNT);
                        s[i] = prefs.getFloat("skip" + i, NEW_SKIPPED_COUNT);
                    }
                    setTotalCount(t);
                    setRightCount(r);
                    setSkippedCount(s);
                }
            }
        }
        else {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(getKey(), MODE_PRIVATE);
            long lastSavedTime = prefs.getLong("lastSavedTime", 0);

            String s[] = getKey().split(",");
            // Have never saved state. Initialize.
            String a[] = {"New Account", "New Account"};
            if (lastSavedTime != 0) {
                a[0] = prefs.getString(s[0], "New Account");
                a[1] = prefs.getString(s[1], "New Account");
            }
            setAccounts(a);
        }
    }

    public void delete(String account) {
        if (account.equals("Challenge")) {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(account, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            setUnlockedLevel(NEW_UNLOCKED_LEVEL);
            float t[] = {0,0,0,0}, r[] = {0,0,0,0}, s[] = {0,0,0,0};
            for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                editor.putFloat("total" + i, NEW_TOTAL_COUNT);
                editor.putFloat("right" + i, NEW_RIGHT_COUNT);
                editor.putFloat("skip" + i, NEW_SKIPPED_COUNT);
            }
            editor.putLong("lastSavedTime", 0);
            editor.apply();
        }
        else {
            SharedPreferences acc = getApplicationContext().getSharedPreferences(getKey(), MODE_PRIVATE);
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.jhchoe.aceit." + account, MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("level", NEW_UNLOCKED_LEVEL);
            for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
                editor.putFloat("total" + i, NEW_TOTAL_COUNT);
                editor.putFloat("right" + i, NEW_RIGHT_COUNT);
                editor.putFloat("skip" + i, NEW_SKIPPED_COUNT);
            }
            editor.putLong("lastSavedTime", 0);
            editor.apply();

            SharedPreferences.Editor edit = acc.edit();
            String a[] = {"com.jhchoe.aceit.first", "com.jhchoe.aceit.second"};
            if (acc.getString(a[0], "New Account").equals(account)) {
                edit.putString(a[0], "New Account");
            } else if (acc.getString(a[1], "New Account").equals(account)) {
                edit.putString(a[1], "New Account");
            }
            edit.apply();
        }
    }

    public String validate(String input) {
        String ret = input;
        if (input.equals("porn") || input.equals("or")) {
            ret = "corn";
        }
        else if (input.equals("i") || input.equals("I")) {
            ret = "eye";
        }
        else if (input.equals("so") || input.equals("coyote") || input.equals("cunt")) {
            ret = "coat";
        }
        else if (input.equals("9")) {
            ret = "night";
        }
        else if (input.equals("how")) {
            ret = "cow";
        }
        else if (input.equals("if i")) {
            ret = "goodbye";
        }
        else if (input.equals("that")) {
            ret = "back";
        }
        else if (input.equals("hey")) {
            ret = "pig";
        }
        else if (input.equals("rebbit")) {
            ret = "rabbit";
        }
        else if (input.equals("paul")) {
            ret = "ball";
        }
        else if (input.equals("hell")) {
            ret = "bell";
        }
        else if (input.equals("games")) {
            ret = "game";
        }
        else if (input.equals("eggs") || input.equals("tag")) {
            ret = "egg";
        }
        else if (input.equals("the")) {
            ret = "duck";
        }
        else if (input.equals("no")) {
            ret = "snow";
        }
        else if (input.equals("c")) {
            ret = "seed";
        }
        else if (input.equals("then")) {
            ret = "men";
        }
        else if (input.equals("red") || input.equals("brad")) {
            ret = "bread";
        }
        else if (input.equals("p")) {
            ret = "feet";
        }
        else if (input.equals("dick")) {
            ret = "stick";
        }
        else if (input.equals("and")) {
            ret = "man";
        }
        else if (input.equals("when")) {
            ret = "wind";
        }
        else if (input.equals("armor")) {
            ret = "farmer";
        }
        else if (input.equals("dead")) {
            ret = "bed";
        }
        else if (input.equals("you")) {
            ret = "shoe";
        }
        return ret;
    }

    public void onCreate() {
        wordList = new ArrayList<String>();
        wordList.add("apple");      wordList.add("coat");   wordList.add("girl");       wordList.add("night");
        wordList.add("baby");       wordList.add("corn");   wordList.add("goodbye");   wordList.add("paper");
        wordList.add("back");       wordList.add("cow");    wordList.add("grass");      wordList.add("party");
        wordList.add("ball");       wordList.add("day");    wordList.add("ground");     wordList.add("picture");
        wordList.add("bear");       wordList.add("dog");    wordList.add("hand");       wordList.add("pig");
        wordList.add("bed");        wordList.add("doll");   wordList.add("head");       wordList.add("rabbit");
        wordList.add("bell");       wordList.add("door");   wordList.add("hill");       wordList.add("rain");
        wordList.add("bird");       wordList.add("duck");   wordList.add("home");       wordList.add("ring");
        wordList.add("birthday");   wordList.add("egg");    wordList.add("horse");      wordList.add("santa_claus");
        wordList.add("boat");       wordList.add("eye");    wordList.add("house");      wordList.add("school");
        wordList.add("box");        wordList.add("farm");   wordList.add("kitty");      wordList.add("seed");
        wordList.add("boy");        wordList.add("farmer"); wordList.add("leg");        wordList.add("sheep");
        wordList.add("bread");      wordList.add("father"); wordList.add("letter");     wordList.add("shoe");
        wordList.add("brother");    wordList.add("feet");   wordList.add("man");        wordList.add("sister");
        wordList.add("car");        wordList.add("fire");   wordList.add("men");        wordList.add("snow");
        wordList.add("cat");        wordList.add("fish");   wordList.add("milk");       wordList.add("song");
        wordList.add("chair");      wordList.add("floor");  wordList.add("money");      wordList.add("squirrel");
        wordList.add("chicken");    wordList.add("flower"); wordList.add("morning");    wordList.add("stick");
        wordList.add("children");   wordList.add("game");   wordList.add("mother");     wordList.add("street");
        wordList.add("christmas");  wordList.add("garden"); wordList.add("nest");       wordList.add("sun");

        wordList.add("table");      wordList.add("wind");
        wordList.add("time");       wordList.add("window");
        wordList.add("water");      wordList.add("wood");

        setUnlockedLevel(-1);

        load();
    }
}
