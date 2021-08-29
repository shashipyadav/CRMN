package com.example.myapplication.user_interface.dashboard.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.CircularGauge;
import com.anychart.charts.Funnel;
import com.anychart.charts.Pie;
import com.anychart.core.axes.Circular;
import com.anychart.core.cartesian.series.Area;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.gauge.pointers.Bar;
import com.anychart.core.ui.Crosshair;
import com.anychart.core.ui.Label;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HAlign;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.enums.VAlign;
import com.anychart.graphics.vector.Fill;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.Stroke;
import com.example.myapplication.Constant;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    private static final String DEBUG_TAG = ChartHelper.class.getSimpleName();
    private Context mContext;
    public ChartHelper(Context context){
        this.mContext = context;
    }
    public static final int[] PIE_CHART_COLORS = {
            Color.rgb(0, 100, 90),
            Color.rgb(255, 186, 0),
            Color.rgb(36, 210, 255),
    };

    public static final int[] BAR_CHART_COLOR = {
            Color.rgb(0, 100, 190),

    };

//
//    private Context mContext;
//    public ChartHelper(Context context){
//        this.mContext = context;
//    }

    public Pie setPieChart(List<DataEntry> dataEntries,
                            final String title) {

        Pie pie = AnyChart.pie();
        pie.noData().label().enabled(true);
        pie.data(dataEntries);
        pie.title("");
        pie.labels().position("outside");
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text(title)
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);


        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Intent intent = new Intent(Constant.EXTRA_GET_DRILL_DOWN);
                intent.putExtra(Constant.EXTRA_KEY, event.getData().get("x"));
                intent.putExtra(Constant.EXTRA_VALUE, String.valueOf(event.getData()));
                intent.putExtra(Constant.EXTRA_TITLE, title);
                mContext.sendBroadcast(intent);
            }
        });

        if(dataEntries.isEmpty()){

            Label noDataLabel = pie.noData().label();
            noDataLabel.enabled(true);
            noDataLabel.text("Error: could not connect to data server");
            noDataLabel.background().enabled(true);
            noDataLabel.background().fill("White 0.5");
            noDataLabel.padding(String.valueOf(40));

        }

        return pie;
    }

    public void updatePieData(Pie pie,List<DataEntry> dataEntries){
        Log.e(DEBUG_TAG , "updatePieData called");
        pie.data(dataEntries);
    }

    public void setDonutChart(AnyChartView anyChartView,
                              List<DataEntry> dataEntries,
                              final String title) {

        Pie donut = AnyChart.pie();
        donut.data(dataEntries);
        donut.title("Fruits imported in 2015 (in kg)");
        donut.labels().position("outside");
        donut.innerRadius("40%");

        Label centerLabel = donut.label(0);
        centerLabel.text("Center Label");
        centerLabel.offsetX("50%");
        centerLabel.offsetY("50%");
        centerLabel.anchor("center");

        anyChartView.setChart(donut);

        donut.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Log.e("Event Data",String.valueOf(event.getData()));
                Log.e("Event Value",event.getData().get("value"));

                Intent intent = new Intent(Constant.EXTRA_GET_DRILL_DOWN);
                intent.putExtra(Constant.EXTRA_KEY, event.getData().get("x"));
                intent.putExtra(Constant.EXTRA_VALUE, String.valueOf(event.getData()));
                intent.putExtra(Constant.EXTRA_TITLE, title);
                mContext.sendBroadcast(intent);
            }
        });
    }

    public void setFunnelChart(AnyChartView anyChartView,
                               List<DataEntry> dataEntries,
                               final String title){

        Funnel funnel = AnyChart.funnel();

//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("Website Visits", 528756));
//        data.add(new ValueDataEntry("Downloads", 164052));
//        data.add(new ValueDataEntry("Valid Contacts", 112167));
//        data.add(new ValueDataEntry("Interested to Buy", 79128));
//        data.add(new ValueDataEntry("Purchased", 79128));

        funnel.data(dataEntries);

        funnel.margin(new String[] { "10", "20%", "10", "20%" });
        funnel.baseWidth("70%")
                .neckWidth("17%");

        funnel.labels()
                .position("outsideleft")
                .format("{%X} - {%Value}");
        funnel.animation(true);
        anyChartView.setChart(funnel);
        anyChartView.invalidate();

        funnel.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Log.e("Event Data",String.valueOf(event.getData()));
                Log.e("Event Value",event.getData().get("value"));

                Intent intent = new Intent(Constant.EXTRA_GET_DRILL_DOWN);
                intent.putExtra(Constant.EXTRA_KEY, event.getData().get("x"));
                intent.putExtra(Constant.EXTRA_VALUE, String.valueOf(event.getData()));
                intent.putExtra(Constant.EXTRA_TITLE, title);
                mContext.sendBroadcast(intent);
            }
        });
    }


    public void setBarChart(AnyChartView anyChartView,
                            List<DataEntry> dataEntries,
                            final String title) {

    /*    if(sourceList.isEmpty() && xAxisLabel.isEmpty()){
            mBarChart.clear();
            mBarChart.setNoDataText("No chart data available");
            mBarChart.invalidate();
        }else{

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(xAxisLabel.size());
        xAxis.setDrawLabels(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxisLabel.get((int) value);
            }
        });

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);
        mBarChart.getDescription().setEnabled(false);

        BarDataSet bardataset = new BarDataSet(sourceList, "Count");
        bardataset.setColors(BAR_CHART_COLOR);
        bardataset.setValueTextSize(14f);
        bardataset.setValueFormatter(new DefaultValueFormatter(0));
        BarData data = new BarData(bardataset);

        // bardataset.setColors(ColorTemplate.COLORFUL_COLORS);;
        mBarChart.setData(data);
        mBarChart.animateY(2000);
        }
     */

        Cartesian cartesian = AnyChart.column();
        Column column = cartesian.column(dataEntries);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Top 10 Cosmetic Products by Revenue");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Product");
        cartesian.yAxis(0).title("Revenue");

        anyChartView.setChart(cartesian);
        anyChartView.invalidate();

    }

    public Cartesian setStackedBarChart(List<DataEntry> dataEntries,
                                   final String title){

        Cartesian cartesian = AnyChart.cartesian();

        cartesian.animation(true);

       // cartesian.title("Combination of Stacked Column and Line Chart (Dual Y-Axis)");

        cartesian.yScale().stackMode(ScaleStackMode.VALUE);

        com.anychart.core.axes.Linear extraYAxis = cartesian.yAxis(1d);

        extraYAxis.labels()
                .padding(0d, 0d, 0d, 5d)
                .format("{%Value}%");

        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomDataEntry("P1",  2040, 1200, 1600));
        data.add(new CustomDataEntry("P2",  1794, 1124, 1724));
        data.add(new CustomDataEntry("P3",  2026, 1006, 1806));
        data.add(new CustomDataEntry("P4",  2341, 921, 1621));
        data.add(new CustomDataEntry("P5",  1800, 1500, 1700));
        data.add(new CustomDataEntry("P6", 1507, 1007, 1907));
        data.add(new CustomDataEntry("P7",  2701, 921, 1821));
        data.add(new CustomDataEntry("P8",  1671, 971, 1671));
        data.add(new CustomDataEntry("P9",  1980, 1080, 1880));
        data.add(new CustomDataEntry("P10",  1041, 1041, 1641));
        data.add(new CustomDataEntry("P11", 813, 1113, 1913));
        data.add(new CustomDataEntry("P12",  691, 1091, 1691));

        Set set = Set.instantiate();
        set.data(data);
        Mapping lineData = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping column1Data = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping column2Data = set.mapAs("{ x: 'x', value: 'value3' }");
        Mapping column3Data = set.mapAs("{ x: 'x', value: 'value4' }");

        cartesian.column(column1Data);
        cartesian.crosshair(true);
        cartesian.column(column2Data);

        cartesian.column(column3Data);
        return  cartesian;
    }
    public void setHorizontalBarChart(AnyChartView anyChartView,
                                       List<DataEntry> dataEntries,
                                       final String title){

        Cartesian cartesian = AnyChart.bar();
        cartesian.bar(dataEntries);
        // set titles for axises
        cartesian.xAxis(0).title("Products by Revenue");
        cartesian.yAxis(0).title("Revenue in Dollars");
        cartesian.interactivity().hoverMode("by-x");
        cartesian.tooltip().positionMode("point");
        cartesian.yScale().minimum(0);
        cartesian.container("container");

        anyChartView.setChart(cartesian);
        anyChartView.invalidate();
    }

    public void setGuageChart(AnyChartView anyChartView,
                              List<DataEntry> dataEntries,
                              final String title){

        CircularGauge circularGauge = AnyChart.circular();
        circularGauge.data(new SingleValueDataSet(new String[] { "23", "34", "67", "93", "56", "100"}));
        circularGauge.fill("#fff")
                .stroke(null)
                .padding(0d, 0d, 0d, 0d)
                .margin(100d, 100d, 100d, 100d);
        circularGauge.startAngle(0d);
        circularGauge.sweepAngle(270d);

        Circular xAxis = circularGauge.axis(0)
                .radius(100d)
                .width(1d)
                .fill((Fill) null);
        xAxis.scale()
                .minimum(0d)
                .maximum(100d);
        xAxis.ticks("{ interval: 1 }")
                .minorTicks("{ interval: 1 }");
        xAxis.labels().enabled(false);
        xAxis.ticks().enabled(false);
        xAxis.minorTicks().enabled(false);

        circularGauge.label(0d)
                .text("Temazepam, <span style=\"\">32%</span>")
                .useHtml(true)
                .hAlign(String.valueOf(HAlign.CENTER))
                .vAlign(String.valueOf(VAlign.MIDDLE));
        circularGauge.label(0d)
                .anchor(Anchor.RIGHT_CENTER)
                .padding(0d, 10d, 0d, 0d)
                .height(17d / 2d + "%")
                .offsetY(100d + "%")
                .offsetX(0d);
        Bar bar0 = circularGauge.bar(0d);
        bar0.dataIndex(0d);
        bar0.radius(100d);
        bar0.width(17d);
        bar0.fill(new SolidFill("#64b5f6", 1d));
        bar0.stroke(null);
        bar0.zIndex(5d);
        Bar bar100 = circularGauge.bar(100d);
        bar100.dataIndex(5d);
        bar100.radius(100d);
        bar100.width(17d);
        bar100.fill(new SolidFill("#F5F4F4", 1d));
        bar100.stroke("1 #e5e4e4");
        bar100.zIndex(4d);

        circularGauge.label(1d)
                .text("Guaifenesin, <span style=\"\">34%</span>")
                .useHtml(true)
                .hAlign(String.valueOf(HAlign.CENTER))
                .vAlign(String.valueOf(VAlign.MIDDLE));
        circularGauge.label(1d)
                .anchor(Anchor.RIGHT_CENTER)
                .padding(0d, 10d, 0d, 0d)
                .height(17d / 2d + "%")
                .offsetY(80d + "%")
                .offsetX(0d);
        Bar bar1 = circularGauge.bar(1d);
        bar1.dataIndex(1d);
        bar1.radius(80d);
        bar1.width(17d);
        bar1.fill(new SolidFill("#1976d2", 1d));
        bar1.stroke(null);
        bar1.zIndex(5d);
        Bar bar101 = circularGauge.bar(101d);
        bar101.dataIndex(5d);
        bar101.radius(80d);
        bar101.width(17d);
        bar101.fill(new SolidFill("#F5F4F4", 1d));
        bar101.stroke("1 #e5e4e4");
        bar101.zIndex(4d);

        circularGauge.label(2d)
                .text("Salicylic Acid, <span style=\"\">67%</span>")
                .useHtml(true)
                .hAlign(String.valueOf(HAlign.CENTER))
                .vAlign(String.valueOf(VAlign.MIDDLE));
        circularGauge.label(2d)
                .anchor(Anchor.RIGHT_CENTER)
                .padding(0d, 10d, 0d, 0d)
                .height(17d / 2d + "%")
                .offsetY(60d + "%")
                .offsetX(0d);
        Bar bar2 = circularGauge.bar(2d);
        bar2.dataIndex(2d);
        bar2.radius(60d);
        bar2.width(17d);
        bar2.fill(new SolidFill("#ef6c00", 1d));
        bar2.stroke(null);
        bar2.zIndex(5d);
        Bar bar102 = circularGauge.bar(102d);
        bar102.dataIndex(5d);
        bar102.radius(60d);
        bar102.width(17d);
        bar102.fill(new SolidFill("#F5F4F4", 1d));
        bar102.stroke("1 #e5e4e4");
        bar102.zIndex(4d);

        circularGauge.label(3d)
                .text("Fluoride, <span style=\"\">93%</span>")
                .useHtml(true)
                .hAlign(String.valueOf(HAlign.CENTER))
                .vAlign(String.valueOf(VAlign.MIDDLE));
        circularGauge.label(3d)
                .anchor(Anchor.RIGHT_CENTER)
                .padding(0d, 10d, 0d, 0d)
                .height(17d / 2d + "%")
                .offsetY(40d + "%")
                .offsetX(0d);
        Bar bar3 = circularGauge.bar(3d);
        bar3.dataIndex(3d);
        bar3.radius(40d);
        bar3.width(17d);
        bar3.fill(new SolidFill("#ffd54f", 1d));
        bar3.stroke(null);
        bar3.zIndex(5d);
        Bar bar103 = circularGauge.bar(103d);
        bar103.dataIndex(5d);
        bar103.radius(40d);
        bar103.width(17d);
        bar103.fill(new SolidFill("#F5F4F4", 1d));
        bar103.stroke("1 #e5e4e4");
        bar103.zIndex(4d);

        circularGauge.label(4d)
                .text("Zinc Oxide, <span style=\"\">56%</span>")
                .useHtml(true)
                .hAlign(String.valueOf(HAlign.CENTER))
                .vAlign(String.valueOf(VAlign.MIDDLE));
        circularGauge.label(4d)
                .anchor(Anchor.RIGHT_CENTER)
                .padding(0d, 10d, 0d, 0d)
                .height(17d / 2d + "%")
                .offsetY(20d + "%")
                .offsetX(0d);
        Bar bar4 = circularGauge.bar(4d);
        bar4.dataIndex(4d);
        bar4.radius(20d);
        bar4.width(17d);
        bar4.fill(new SolidFill("#455a64", 1d));
        bar4.stroke(null);
        bar4.zIndex(5d);
        Bar bar104 = circularGauge.bar(104d);
        bar104.dataIndex(5d);
        bar104.radius(20d);
        bar104.width(17d);
        bar104.fill(new SolidFill("#F5F4F4", 1d));
        bar104.stroke("1 #e5e4e4");
        bar104.zIndex(4d);

        circularGauge.margin(50d, 50d, 50d, 50d);
        circularGauge.title()
                .text("Medicine manufacturing progress' +\n" +
                        "    '<br/><span style=\"color:#929292; font-size: 12px;\">(ACME CORPORATION)</span>")
                .useHtml(true);
        circularGauge.title().enabled(true);
        circularGauge.title().hAlign(String.valueOf(HAlign.CENTER));
        circularGauge.title()
                .padding(0d, 0d, 0d, 0d)
                .margin(0d, 0d, 20d, 0d);

        anyChartView.setChart(circularGauge);
    }

    public void setAreaChart(AnyChartView anyChartView,
                              List<DataEntry> dataEntries,
                              final String title){
        Cartesian areaChart = AnyChart.area();

        areaChart.animation(true);

        Crosshair crosshair = areaChart.crosshair();
        crosshair.enabled(true);
        crosshair.yStroke((Stroke) null, null, null, (String) null, (String) null)
                .xStroke("#fff", 1d, null, (String) null, (String) null)
                .zIndex(39d);
        crosshair.yLabel(0).enabled(true);

        areaChart.yScale().stackMode(ScaleStackMode.VALUE);

        areaChart.title("Unaudited Apple Inc. Revenue by Operating Segments");

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("Q2 2014", 17.982, 10.941, 9.835, 4.047, 2.841));
        seriesData.add(new CustomDataEntry("Q3 2014", 17.574, 8.659, 6.230, 2.627, 2.242));
        seriesData.add(new CustomDataEntry("Q4 2014", 19.75, 10.35, 6.292, 3.595, 2.136));
        seriesData.add(new CustomDataEntry("Q1 2015", 30.6, 17.2, 16.1, 5.4, 5.2));
        seriesData.add(new CustomDataEntry("Q2 2015", 21.316, 12.204, 16.823, 3.457, 4.210));
        seriesData.add(new CustomDataEntry("Q3 2015", 20.209, 10.342, 13.23, 2.872, 2.959));
        seriesData.add(new CustomDataEntry("Q4 2015", 21.773, 10.577, 12.518, 3.929, 2.704));
        seriesData.add(new CustomDataEntry("Q1 2016", 29.3, 17.9, 18.4, 4.8, 5.4));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Data = set.mapAs("{ x: 'x', value: 'value3' }");
        Mapping series4Data = set.mapAs("{ x: 'x', value: 'value4' }");
        Mapping series5Data = set.mapAs("{ x: 'x', value: 'value5' }");

        Area series1 = areaChart.area(series1Data);
        series1.name("Americas");
        series1.stroke("3 #fff");
        series1.hovered().stroke("3 #fff");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d)
                .stroke("1.5 #fff");
        series1.markers().zIndex(100d);

        Area series2 = areaChart.area(series2Data);
        series2.name("Europe");
        series2.stroke("3 #fff");
        series2.hovered().stroke("3 #fff");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d)
                .stroke("1.5 #fff");
        series2.markers().zIndex(100d);

        Area series3 = areaChart.area(series3Data);
        series3.name("Greater China");
        series3.stroke("3 #fff");
        series3.hovered().stroke("3 #fff");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d)
                .stroke("1.5 #fff");
        series3.markers().zIndex(100d);

        Area series4 = areaChart.area(series4Data);
        series4.name("Japan");
        series4.stroke("3 #fff");
        series4.hovered().stroke("3 #fff");
        series4.hovered().markers().enabled(true);
        series4.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d)
                .stroke("1.5 #fff");
        series4.markers().zIndex(100d);

        Area series5 = areaChart.area(series5Data);
        series5.name("Rest of Asia Pacific");
        series5.stroke("3 #fff");
        series5.hovered().stroke("3 #fff");
        series5.hovered().markers().enabled(true);
        series5.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d)
                .stroke("1.5 #fff");
        series5.markers().zIndex(100d);

        areaChart.legend().enabled(true);
        areaChart.legend().fontSize(13d);
        areaChart.legend().padding(0d, 0d, 20d, 0d);

        areaChart.xAxis(0).title(false);
        areaChart.yAxis(0).title("Revenue (in Billons USD)");

        areaChart.interactivity().hoverMode(HoverMode.BY_X);
        areaChart.tooltip()
                .valuePrefix("$")
                .valuePostfix(" bln.")
                .displayMode(TooltipDisplayMode.UNION);

        anyChartView.setChart(areaChart);
    }

    public void setLineChart(AnyChartView anyChartView,
                             List<DataEntry> dataEntries,
                             final String title){

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
        seriesData.add(new CustomDataEntry("1995", 12.0, 18.0, 14.4));
        seriesData.add(new CustomDataEntry("1996", 3.2, 15.1, 9.2));
        seriesData.add(new CustomDataEntry("1997", 4.1, 11.3, 5.9));
        seriesData.add(new CustomDataEntry("1998", 6.3, 14.2, 5.2));
        seriesData.add(new CustomDataEntry("1999", 9.4, 13.7, 4.7));
        seriesData.add(new CustomDataEntry("2000", 11.5, 9.9, 4.2));
        seriesData.add(new CustomDataEntry("2001", 13.5, 12.1, 1.2));
        seriesData.add(new CustomDataEntry("2002", 14.8, 13.5, 5.4));
        seriesData.add(new CustomDataEntry("2003", 16.6, 15.1, 6.3));
        seriesData.add(new CustomDataEntry("2004", 18.1, 17.9, 8.9));
        seriesData.add(new CustomDataEntry("2005", 17.0, 18.9, 10.1));
        seriesData.add(new CustomDataEntry("2006", 16.6, 20.3, 11.5));
        seriesData.add(new CustomDataEntry("2007", 14.1, 20.7, 12.2));
        seriesData.add(new CustomDataEntry("2008", 15.7, 21.6, 10));
        seriesData.add(new CustomDataEntry("2009", 12.0, 22.5, 8.9));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Brandy");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Whiskey");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Tequila");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry {


        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

        CustomDataEntry(String x,
                        Number value,
                        Number value2,
                        Number value3,
                        Number value4,
                        Number value5) {

            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
            setValue("value4", value4);
            setValue("value5", value5);
        }


        public CustomDataEntry(String x, Number i, Number i1) {
            super(x, i);
            setValue("value2", i1);
        }
    }

}
