package com.jacobstechnologies.wordle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GameOverDialog extends DialogFragment {

    int time;
    private SharedPreferences sp;
    boolean won;

    GameOverDialog(int time, Context c, boolean won){
        this.time = time;
        sp = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        this.won = won;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(v);

        Button close = v.findViewById(R.id.close_button);
        HorizontalBarChart barChart = v.findViewById(R.id.bar_chart);
        TextView timeText = v.findViewById(R.id.time_text);
        TextView title = v.findViewById(R.id.dialog_title);
        TextView streak = v.findViewById(R.id.streak);
        TextView rate = v.findViewById(R.id.win_rate);

        close.setOnClickListener(l -> dismiss());

        if (won){
            title.setText("You Win!");
            title.setTextColor(Color.GREEN);
        }
        else{
            title.setText("You Lose!");
            title.setTextColor(Color.RED);
        }

        // set streak
        streak.setText("Streak: " + sp.getInt("streak", 0));

        // set rate
        int r = Math.round(sp.getInt("wins", 0) / sp.getInt("gamesPlayed", 0) * 100);
        rate.setText("Win rate: " + r + "%");

        // set time
        {
            int secss = (time % 60);
            int mins = (time - secss) / 60;
            DecimalFormat f = new DecimalFormat("00");
            String s = f.format(secss);
            int bestTime = sp.getInt("bestTime", 0);
            int bestSecss = (bestTime % 60);
            int bestMins = (bestTime - secss) / 60;
            String s2 = f.format(bestSecss);
            timeText.setText("Time: " + mins + ":" + s + " | Best: " + bestMins + ":" + s2);
        }

        // get bar data
        {
            List<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0; i < 5; i++){
                int d = sp.getInt("tries_" + i, 0);
                barEntries.add(new BarEntry(i, d));
            }

            BarDataSet set = new BarDataSet(barEntries, "Attempt Frequency");
            BarData data = new BarData(set);
            barChart.setData(data);
            set.setColor(Color.GREEN);
            set.setValueTextColor(Color.WHITE);
            set.setValueTextSize(16f);
            barChart.getDescription().setEnabled(false);
            barChart.setFitBars(true);
            XAxis xAxis = barChart.getXAxis();
            List<Integer> xAxisLabel = new ArrayList<>();
            xAxisLabel.add(6);
            xAxisLabel.add(5);
            xAxisLabel.add(4);
            xAxisLabel.add(3);
            xAxisLabel.add(2);
            xAxisLabel.add(1);
            DecimalFormat d = new DecimalFormat("0");
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xAxisLabel.get(Integer.parseInt(d.format((value)))).toString();

                }
            });
        }

        return builder.create();
    }
}
