package com.uteacher.www.uteacherble.TestActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ElasticEase;
import com.uteacher.www.uteacherble.R;

/**
 * Created by cartman on 15/8/21.
 */
public class ChartDialog extends DialogFragment {


    private BarChartView barChartView;
    private HorizontalStackBarChartView horizontalStackBarChartView;
    private LineChartView lineChartView;

    private final String[] mLabelsBarChart= {"","","","","","","","","",""};
    private final float[] mValuesBarChart = {0,1,2,0,1,2,0,1,2,0};

    private final String[] mLabelsStackChart= {""};
    private final float[][] mValuesStackChart = {{10},{-80f}};


    private final String[] mLabelsLineChart= {"","","","","","","","","",""};
    private final float[] mValuesLineChart = {0,0,0,0,0,0,0,0,0,0};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainingPlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartDialog newInstance(String param1, String param2) {
        ChartDialog fragment = new ChartDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ChartDialog() {

    }

    private void initBarChart(BarChartView barChartView) {

        BarSet dataset = new BarSet(mLabelsBarChart, mValuesBarChart);
        dataset.setColor(Color.parseColor("#eb993b"));
        barChartView.addData(dataset);

        barChartView.setBarSpacing(Tools.fromDpToPx(3));

        barChartView.setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        barChartView.show();
    }

    private void initStackChart(HorizontalStackBarChartView barChart) {

        BarSet dataset = new BarSet(mLabelsStackChart, mValuesStackChart[0]);
        dataset.setColor(Color.parseColor("#687E8E"));
        barChart.addData(dataset);

        dataset = new BarSet(mLabelsStackChart, mValuesStackChart[1]);
        dataset.setColor(Color.parseColor("#FF5C8E67"));
        barChart.addData(dataset);

        barChart.setRoundCorners(Tools.fromDpToPx(5));
        barChart.setBarSpacing(Tools.fromDpToPx(8));

        barChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(-80, 80, 10);

        barChart.show();
    }

    private void initLineChart(LineChartView lineChartView) {
        LineSet dataset = new LineSet(mLabelsLineChart, mValuesLineChart);
        dataset.setColor(Color.parseColor("#FF58C674"));
        lineChartView.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        lineChartView.setBorderSpacing(1)
                .setAxisBorderValues(0, 10, 1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FF8E9196"))
                .setXAxis(false)
                .setYAxis(false)
                .setStep(2)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        lineChartView.show();
    }

    private void initContentView(View view) {
        barChartView = (BarChartView) view.findViewById(R.id.barchart);
        horizontalStackBarChartView = (HorizontalStackBarChartView) view.findViewById(R.id.stackedchart);
        lineChartView = (LineChartView) view.findViewById(R.id.linechart);


        initBarChart(barChartView);
        initStackChart(horizontalStackBarChartView);
        initLineChart(lineChartView);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initContentView(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_chart_dialog, container, false);

        handler = new Handler();
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private int preDepth = 0;

    public void updateData(final int position, final int depth) {
        updateLineChart(lineChartView, position);
        if (preDepth != depth) {
            updateStackChart(horizontalStackBarChartView, depth-preDepth);
            updateBarChart(barChartView, depth);
            preDepth = depth;
        }
    }

    public void updateStackChart(ChartView chart, float delta) {
        mValuesStackChart[0][0] = mValuesStackChart[0][0] + delta;
        mValuesStackChart[1][0] = mValuesStackChart[1][0] - delta;

        chart.updateValues(0, mValuesStackChart[0]);
        chart.updateValues(1, mValuesStackChart[1]);

        chart.notifyDataUpdate();
    }

    public void updateBarChart(ChartView chart, float data){
        for (int i=1;i<mValuesBarChart.length;i++) {
            mValuesBarChart[i-1] = mValuesBarChart[i];
        }
        mValuesBarChart[mValuesBarChart.length -1 ] = data;

        chart.updateValues(0, mValuesBarChart);
        chart.notifyDataUpdate();
    }

    public void updateLineChart(LineChartView chart, float data){
        for (int i=1;i<mValuesLineChart.length;i++) {
            mValuesLineChart[i-1] = mValuesLineChart[i];
        }
        mValuesLineChart[mValuesLineChart.length-1] = data;

        chart.updateValues(0, mValuesLineChart);
        chart.notifyDataUpdate();
    }
}
