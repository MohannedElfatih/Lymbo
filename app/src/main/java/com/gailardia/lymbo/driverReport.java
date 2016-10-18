package com.gailardia.lymbo;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.h3cker on 14/03/2015.
 */
public class driverReport extends Fragment {
    private float priceD,farD,carErrorD,personalD,sumD;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.driver,container,false);
        pieChart= (PieChart) view.findViewById(R.id.pieD);
        makePieD();
        return  view;
    }

    public void makePieD(){
        List<PieEntry> entries = new ArrayList<>();
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        sumD=shared.getFloat("sumD",0.0f);
        priceD=shared.getFloat("priceD",0.0f);
        farD=shared.getFloat("farD",0.0f);
        personalD=shared.getFloat("personalD",0.0f);
        carErrorD=shared.getFloat("carErrorD",0.0f);
        entries.add(new PieEntry(priceD, "price"));
        entries.add(new PieEntry(farD, "distance"));
        entries.add(new PieEntry(personalD, "Personal"));
        entries.add(new PieEntry(carErrorD, "Car error"));
        PieDataSet set = new PieDataSet(entries, "Results");
        set.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(set);
        pieChart.setData(data);
        data.setValueTextSize(10);
        data.setValueTextColor(R.color.chartBlue);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter());
        pieChart.setEntryLabelTextSize(0);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Driver report statistics");
        pieChart.setDescriptionTextSize(15.0f);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(0.0f);
        pieChart.setTransparentCircleAlpha(1);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        pieChart.getLegend().setTextSize(16.0f);

        pieChart.invalidate();

    }
}
