package com.jacobstechnologies.wordle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TilesFragmentClass extends Fragment {

    private TextView errorText;
    private TextView timerText;

    private List<EditText> editTexts = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();
    public static List<String> dict;
    public static String word;
    public static int timeInSeconds;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int index = 0;
    Timer timer = new Timer();
    timerTask t = new timerTask();


    public static TilesFragmentClass TilesFragmentClass(Context c){
        dict = new Dictionary().getDict(c);
        int i = new Random().nextInt(dict.size());
        word = dict.get(i);
        timeInSeconds = 0;
        return new TilesFragmentClass();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        findViews(v);
        reset();
        sp = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        editor = sp.edit();

        errorText.setVisibility(View.INVISIBLE);

        // set all texts = to 0
        for (int i = 0; i < editTexts.size(); i++) {
            final int x = i - (index * 5);
            editTexts.get(i).setText("");
            editTexts.get(i).setEnabled(i < 5);
            ((EditText) editTexts.get(i)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 1) {
                        if ( x < (5 * (index + 1)) - 1) {
                            ((EditText) editTexts.get(x)).clearFocus();
                            ((EditText) editTexts.get(x + 1)).requestFocus();
                        }
                        sb.append(charSequence);
                    }
                }

                @Override
                public void afterTextChanged(Editable charSequence) {

                }
            });
            ((EditText) editTexts.get(i)).setOnKeyListener((view, i12, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    index = checkWord(sb, index);
                    return true;
                }
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL){
                    if (x > 0 && ((EditText) editTexts.get(x - 1)).isEnabled()) {
                        if (((EditText) editTexts.get(x)).getText().length() == 1) {
                            ((EditText) editTexts.get(x)).setText("");
                            //sb.deleteCharAt(x - (index * 5));
                        }
                        else{
                            ((EditText) editTexts.get(x)).clearFocus();
                            ((EditText) editTexts.get(x - 1)).requestFocus();
                            ((EditText) editTexts.get(x - 1)).setText("");
                            try{
                                //sb.deleteCharAt(x - 1 - (index * 5));
                            } catch (StringIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if (x == 0){
                        ((EditText) editTexts.get(x)).setText("");
                        //sb.delete(0, sb.length());
                    }
                    sb.delete(0, sb.length());
                    for (int y = 0; y < 5; y++){
                        if (!((EditText) editTexts.get((index * 5) + y)).getText().toString().matches("")){
                            sb.append(((EditText) editTexts.get((index * 5 ) + y)).getText().toString().charAt(0));
                        }
                    }
                    return true;
                }
                return false;
            });
        }
        editTexts.get(0).requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTexts.get(0), InputMethodManager.SHOW_IMPLICIT);
        return v;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private int checkWord(StringBuilder sb, int index){
        String s = sb.toString();
        if (dict.contains(s)){
            errorText.setVisibility(View.INVISIBLE);
            for (int i = index * 5; i < (index * 5) + 5; i++){
                EditText t = editTexts.get(i);

                ObjectAnimator a = ObjectAnimator.ofFloat(t, "rotationY", 0, 90);
                a.setDuration(400);
                a.setStartDelay((i - index * 5L) * 300L);
                a.setRepeatMode(ValueAnimator.REVERSE);
                a.setRepeatCount(1);
                a.setInterpolator(new AccelerateInterpolator());

                final int finalIndex = index;
                final int finalI = i;
                a.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        char c = s.charAt(finalI - (finalIndex * 5));
                        if (word.contains(Character.toString(c))){ // letter inside word
                            if (word.charAt(finalI - (finalIndex * 5)) == c){ // letter matches letter in word in same place
                                t.setBackground(getResources().getDrawable(R.drawable.tile_bg_green, getContext().getTheme()));
                            }
                            else{
                                t.setBackground(getResources().getDrawable(R.drawable.tile_bg_red, getContext().getTheme()));
                            }
                        }
                        else{
                            t.setBackground(getResources().getDrawable(R.drawable.tile_bg_light_grey, getContext().getTheme()));
                        }
                        if (finalI - (finalIndex * 5) == 4) { // last tile
                            if (word.equals(s)) {
                                Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    v.vibrate(500);
                                }
                                Toast.makeText(getContext(), "YOU WON!!!!!", Toast.LENGTH_LONG).show();
                                int wins = sp.getInt("wins", 0);
                                int streak = sp.getInt("streak", 0);
                                int triesDist = sp.getInt("tries_" + finalIndex, 0);
                                int bestTime = sp.getInt("bestTime", 0);
                                editor.putInt("wins", wins + 1);
                                editor.putInt("streak", streak + 1);
                                editor.putInt("time", timeInSeconds);
                                editor.putInt("tries_" + finalIndex, triesDist + 1);
                                if (timeInSeconds < bestTime && bestTime != 0) {
                                    editor.putInt("bestTime", timeInSeconds);
                                }
                                int gamesPlayed = sp.getInt("gamesPlayed", 0);
                                editor.putInt("gamesPlayed", gamesPlayed + 1);
                                editor.commit();
                                showGraph(timeInSeconds, true);
                            } else if (finalIndex == 5) {
                                Toast.makeText(getContext(), "YOU LOSE!!!!!", Toast.LENGTH_LONG).show();
                                int losses = sp.getInt("losses", 0);
                                editor.putInt("losses", losses + 1);
                                editor.putInt("streak", 0);
                                editor.commit();
                                int gamesPlayed = sp.getInt("gamesPlayed", 0);
                                editor.putInt("gamesPlayed", gamesPlayed + 1);
                                editor.commit();
                                showGraph(timeInSeconds, false);
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                a.start();
                editTexts.get((finalIndex * 5) + 4).clearFocus();
                if (finalIndex < 5) {
                    editTexts.get((finalIndex * 5) + 5).requestFocus();
                }
                sb.delete(0, sb.length());
            }
            index++;
        }
        else{
            errorText.setVisibility(View.VISIBLE);
            if (s.length() != 5){
                errorText.setText("Word has to be 5 letters!");
            }
            if (!dict.contains(s)) {
                editTexts.get(((index) * 5) + 4).requestFocus();
                errorText.setText(s + " is not in our dictionary!");
            }
        }

        
        for (int i = 0; i < editTexts.size(); i++){
            editTexts.get(i).setEnabled((i >= (index * 5)) && (i < ((index * 5) + 5)));
        }
        return index;
    }

    private void findViews(View v){
        for (int i = 1; i <= 6; i++){
            int id = getResources().getIdentifier("ll_" + i, "id", getContext().getPackageName());
            LinearLayout root = v.findViewById(id);
            int count = root.getChildCount();
            for (int j = 0; j < count; j++){
                View c = root.getChildAt(j);
                editTexts.add((EditText) c);
            }
        }
        errorText = v.findViewById(R.id.error_text);
        timerText = v.findViewById(R.id.timer);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void reset(){
        sb.delete(0, sb.length());
        index = 0;
        errorText.setVisibility(View.INVISIBLE);
        for (int i = 0; i < editTexts.size(); i++){
            ((EditText) editTexts.get(i)).setText("");
            editTexts.get(i).setEnabled(i < 5);
            ((EditText) editTexts.get(i)).setBackground(getResources().getDrawable(R.drawable.tile_bg, getContext().getTheme()));
        }
        int i = new Random().nextInt(dict.size());
        word = dict.get(i);
        t.reset();
        timer.schedule(t, 0, 1000);
    }

    private class timerTask extends TimerTask{
        private boolean go = false;

        private void reset(){
            go = true;
            timeInSeconds = 0;
        }

        private void stop(){
            go = false;
        }

        @Override
        public void run() {
            if (go) {
                timeInSeconds++;
                int secss = (timeInSeconds % 60);
                int mins = (timeInSeconds - secss) / 60;
                DecimalFormat f = new DecimalFormat("00");
                String s = f.format(secss);
                timerText.setText(mins + ":" + s);
            }
        }
    }

    private void showGraph(int time, boolean won){
        t.stop();
        GameOverDialog d = new GameOverDialog(time, requireContext(), won);
        d.show(getChildFragmentManager(), "");
    }
}
