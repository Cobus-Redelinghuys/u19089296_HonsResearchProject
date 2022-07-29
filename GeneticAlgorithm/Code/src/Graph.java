import java.util.HashMap;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Graph extends JFrame {
    public JFreeChart lineGraph;
    public Graph(String graphTitle, HashMap<Integer,Double> input, String XLabel, String YLabel, String lineTitle){
        super("GA Testing System");
        lineGraph = ChartFactory.createLineChart(
            graphTitle, 
            XLabel, 
            YLabel, 
            generateDataset(input, lineTitle));

        ChartPanel chartPanel = new ChartPanel(lineGraph);
        setContentPane(chartPanel);
    }

    public Graph(String graphTitle, String XLabel, String YLabel, String lineTitle, HashMap<String, Integer> input){
        super("GA Testing System");
        lineGraph = ChartFactory.createBarChart(graphTitle, XLabel, YLabel, generateDataset(lineTitle, input));
        ChartPanel chartPanel = new ChartPanel(lineGraph);
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset generateDataset(HashMap<Integer, Double> input, String lineTitle){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(Integer i: input.keySet()){
            dataset.addValue(input.get(i), lineTitle, i);
        }
        return dataset;
    }

    private DefaultCategoryDataset generateDataset(String lineTitle, HashMap<String, Integer> input){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(String str: input.keySet()){
            dataset.addValue(input.get(str), str, "Gene values");
        }
        return dataset;
    }

    public void display(){
        this.pack();
        this.setVisible(true);
    }
}