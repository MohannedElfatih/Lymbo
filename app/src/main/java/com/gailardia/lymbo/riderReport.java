package com.gailardia.lymbo;

import android.content.SharedPreferences;
import android.os.Bundle;
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
 * Created by mohaned on 14/10/2016.
 */
public class riderReport extends Fragment{
    private PieChart pieChart;
    private float priceR,farR,driverR,personalR,sumR;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view=inflater.inflate(R.layout.rider,container,false);
        pieChart= (PieChart) view.findViewById(R.id.pieR);
        makePieR();
        return  view;
    }

    public void makePieR(){
        List<PieEntry> entries = new ArrayList<>();
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        sumR=shared.getFloat("sumR",0.0f);
        priceR=shared.getFloat("priceR",0.0f);
        farR=shared.getFloat("farR",0.0f);
        personalR=shared.getFloat("personalR",0.0f);
        driverR=shared.getFloat("driverR",0.0f);
        entries.add(new PieEntry(priceR, "Price"));
        entries.add(new PieEntry(farR, "Distance"));
        entries.add(new PieEntry(personalR, "Personal"));
        entries.add(new PieEntry(driverR, "No driver"));
        PieDataSet set = new PieDataSet(entries, "Results");
        PieData data = new PieData(set);
        data.setValueTextSize(10);
        data.setValueTextColor(R.color.chartBlue);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter());
        set.setColors(ColorTemplate.PASTEL_COLORS);
        pieChart.setData(data);
        pieChart.setEntryLabelTextSize(0);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Statistical Rider Report");
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
