package ch.apptiva.watchdog.domain.core.model;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SiteStatisticsChart {

    public static byte[] generateStatImage(SiteStatistic statistics) throws IOException {
        XYSeries series = new XYSeries(statistics.url().toString());
        statistics.data().forEach(series::add);
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        seriesCollection.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(statistics.url().toString(), "minutes ago", "milliseconds", seriesCollection,
            PlotOrientation.VERTICAL, false, false, false
        );

        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setBackgroundPaint(new Color(250, 250, 250));
        xyPlot.setDomainGridlinePaint(Color.lightGray);
        xyPlot.setRangeGridlinePaint(Color.lightGray);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 400, 300);
        return outputStream.toByteArray();
    }

}
