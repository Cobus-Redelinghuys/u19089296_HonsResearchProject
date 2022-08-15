import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        System.out.println(graphTitle);
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
        List<Integer> sortedArr = new ArrayList<>(input.values());
        Collections.sort(sortedArr);
        HashMap<Integer, ArrayList<String>> orderedList = new HashMap<>();

        for(Integer i: sortedArr){
            orderedList.put(i, new ArrayList<>());
        }

        for(String str: input.keySet()){
            orderedList.get(input.get(str)).add(str);
        }

        /*for(String str: input.keySet()){
            dataset.addValue(input.get(str), str, "Gene values");
        }*/
        for(Integer i: orderedList.keySet()){
            for(String str: orderedList.get(i)){
                //System.out.println(i + ": " + str);
                dataset.addValue(i, str, "Gene values");
            }
        }

        return dataset;
    }

    public void display(){
        try{
            this.pack();
            this.setVisible(true);
        }
        catch(Exception e){
            //e.printStackTrace();
        }
    }
}