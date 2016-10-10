package com.gailardia.lymbo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

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
        entries.add(new PieEntry(priceR, "Expensive price"));
        entries.add(new PieEntry(farR, "Far driver"));
        entries.add(new PieEntry(personalR, "Personal issue"));
        entries.add(new PieEntry(driverR, "No available driver"));
        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.setDescription("Statistical Rider Report");
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(0.0f);
        pieChart.invalidate();
    }
}
