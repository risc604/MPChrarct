package com.example.tomcat.mpchrarct;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnChartGestureListener,
        OnChartValueSelectedListener
{
    LineChart   mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (LineChart) findViewById(R.id.lineChart1);


        setData(30, 10);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }



    @Override
    public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture)
    {

    }

    @Override
    public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture)
    {

    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent)
    {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent)
    {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent)
    {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1)
    {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1)
    {

    }

    @Override
    public void onValueSelected(Entry entry, Highlight highlight)
    {

    }

    @Override
    public void onNothingSelected()
    {

    }
}
