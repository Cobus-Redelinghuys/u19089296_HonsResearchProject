import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jfree.chart.ChartUtils;

public class GeneticAlgorithm {
    Chromosome[] population = new Chromosome[GeneticAlgorithmConfig.populationSize];
    
    public GeneticAlgorithm(){
        for(int i=0; i < population.length; i++){
            population[i] = ChromosomeConfig.generatChromosome();
        }
    }

    public void runAlgorithm(){
        for(int i=0; i < GeneticAlgorithmConfig.numGenerations; i++){
            run(i);
        }
    }

    public void run(int gen){
        HashMap<Chromosome,Chromosome> replacementMap = new HashMap<>();
        for(Chromosome chromosome: population){
            replacementMap.put(chromosome, chromosome);
        }
        ArrayList<Chromosome>[] selected = tournamentSelection(gen);
        for(int i=0; i < GeneticAlgorithmConfig.selectionSize; i++){
            if(GeneticAlgorithmConfig.crossoverProp < GeneticAlgorithmConfig.nextDouble(1.0) && i+1 < selected.length){
                Chromosome[] offspring = Chromosome.crossOver(selected[0].get(i), selected[0].get(i+1));
                replacementMap.replace(selected[1].get(i), offspring[0]);
                replacementMap.replace(selected[1].get(i+1), offspring[i+1]);
                i++;
            } else if(GeneticAlgorithmConfig.mutationProp < GeneticAlgorithmConfig.nextDouble(1.0)) {
                Chromosome offspring = Chromosome.mutate(selected[0].get(i));
                replacementMap.replace(selected[1].get(i), offspring);
            } else {
                replacementMap.replace(selected[1].get(i), selected[0].get(i).clone());
            }
        }
        population = replacementMap.values().toArray(new Chromosome[0]);
        System.out.println("Generation: " + gen);
        Double[] arr = calculateAverage(gen);
        System.out.println("Average inf: " + arr[0]);
        Summary.avgInf.put(gen, arr[0]);
        System.out.println("Average: " + arr[1]);
        Summary.avg.put(gen, arr[1]);
        arr = calculateStd(gen);
        System.out.println("Std inf: " + arr[0]);
        Summary.stdInf.put(gen, arr[0]);
        System.out.println("Std: " + arr[1]);
        Summary.std.put(gen, arr[1]);
        double var = variance();
        System.out.println("Variance: " + var);
        Summary.variance.put(gen, var);

    }

    public float variance(){
        ArrayList<String> unique = new ArrayList<>();
        for(Chromosome chromosome: population){
            if(!unique.contains(chromosome.toString())){
                unique.add(chromosome.toString());
            }
        }
        return (float)unique.size() / (float)population.length;
    } 

    @SuppressWarnings("unchecked")
    public ArrayList<Chromosome>[] tournamentSelection(int gen){
        ArrayList<Chromosome>[] result = new ArrayList[2];
        ArrayList<Chromosome> selected = new ArrayList<>();
        ArrayList<Chromosome> winners = new ArrayList<>();
        ArrayList<Chromosome> losers = new ArrayList<>();
        for(int i=0; i < GeneticAlgorithmConfig.selectionSize; i++){
            Chromosome[] selection = new Chromosome[GeneticAlgorithmConfig.tournamentSize];
            for(int j=0; j < selection.length; j++){
                Chromosome sel;
                do{
                    sel = population[GeneticAlgorithmConfig.nextInt(population.length)];
                }while(selected.contains(sel));
                selected.add(sel);
                selection[j] = sel;
            }
            Chromosome[] selectionResults = determinChromosomes(selection, gen);
            winners.add(selectionResults[0]);
            losers.add(selectionResults[1]);
        }
        result[0] = winners;
        result[1] = losers;

        return result;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Double>[] totalFitnesses = new ArrayList[GeneticAlgorithmConfig.numGenerations];

    private Chromosome[] determinChromosomes(Chromosome[] selection, int gen){
        totalFitnesses[gen] = new ArrayList<>();
        HashMap<Double,ArrayList<Chromosome>> fitnesses = new HashMap<>();
        for(Chromosome chromosome: selection){
            double fitness = Fitness.determineFitness(chromosome, gen);
            totalFitnesses[gen].add(fitness);
            if(fitnesses.containsKey(fitness)){
                fitnesses.get(fitness).add(chromosome);
            } else {
                ArrayList<Chromosome> temp = new ArrayList<>();
                temp.add(chromosome);
                fitnesses.put(fitness, temp);
            }
        }
        Double[] sortedArray = fitnesses.keySet().toArray(new Double[0]);
        Arrays.sort(sortedArray); 
        Chromosome[] res = new Chromosome[2];
        res[0] = fitnesses.get(sortedArray[0]).get(GeneticAlgorithmConfig.nextInt(fitnesses.get(sortedArray[0]).size()));
        res[1] = fitnesses.get(sortedArray[sortedArray.length-1]).get(GeneticAlgorithmConfig.nextInt(fitnesses.get(sortedArray[sortedArray.length-1]).size()));
        return res;
    }

    private Double[] calculateAverage(int gen){
        double sum = 0;
        double sumInf = 0;
        int count = 0;
        for(double v: totalFitnesses[gen]){
            sumInf += v;
            if(Double.isFinite(v) && !Double.isNaN(v)){
                sum += v;
                count++;
            }
        }
        Double[] arr = new Double[2];
        arr[0] = sumInf/totalFitnesses[gen].size();
        if(count != 0)
            arr[1] = sum/count;
        else 
            arr[1] = 0.0;
        return arr;
    }

    private Double[] calculateStd(int gen){
        Double[] avg = calculateAverage(gen);
        double sum = 0;
        int count = 0;
        double sumInf = 0;
        for(double v: totalFitnesses[gen]){
            sumInf = Math.pow(v-avg[0], 2);
            if(Double.isFinite(v) && !Double.isNaN(v)){
                sum = Math.pow(v-avg[1], 2);
                count++;
            } 
        }
        Double[] arr = new Double[2];
        arr[0] = Math.sqrt(sumInf/totalFitnesses[gen].size());
        if(count != 0)
            arr[1] = Math.sqrt(sum/count);
        else 
            arr[1] = 0.0;
        return arr;
    }

    public void printDatabase(){
        FitnessMemory.jsonSummary();
    }

    public void DBAnalysis(){
        FitnessMemory.DBAnalysis();
    }

    public void showGraphs(){
        Summary.displayAvg();
        Summary.displayAvgInf();
        Summary.displayStd();
        Summary.displayStdInf();
        Summary.displayVariance();
        Summary.displayDBSummary();
    }
}

class Summary{
    static HashMap<Integer, Double> avg;
    static HashMap<Integer, Double> avgInf;
    static HashMap<Integer, Double> std;
    static HashMap<Integer, Double> stdInf;
    static HashMap<Integer, Double> variance;

    static{
        avg = new HashMap<>();
        avgInf = new HashMap<>();
        std = new HashMap<>();
        stdInf = new HashMap<>();
        variance = new HashMap<>();
    }

    static void displayAvg(){
        Graph g = new Graph("Averages", avg, "Generations", "Average", "Average Accuracies");
        g.display();
        saveToFile("average.png", g);
    }

    static void displayAvgInf(){
        Graph g = new Graph("Averages with infinites", avgInf, "Generations", "Average", "Average Accuracies");
        g.display();
        saveToFile("averageInf.png", g);
    }

    static void displayStd(){
        Graph g = new Graph("Standard Deviations", std, "Generations", "STD", "Standard deviations of Accuracies");
        g.display();
        saveToFile("std.png", g);
    }

    static void displayStdInf(){
        Graph g = new Graph("Standard Deviations", stdInf, "Generations", "STD", "Standard deviations of Accuracies");
        g.display();
        saveToFile("stdInf.png", g);
    }

    static void displayVariance(){
        Graph g = new Graph("Variance in population", variance, "Generations", "%", "Variance");
        g.display();
        saveToFile("variance.png", g);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void displayDBSummary(){
        HashMap<GeneConfig,HashMap<String,HashMap<Boolean,ArrayList<String>>>> db = FitnessMemory.getDB();
        for(GeneConfig geneConfig: db.keySet()){
            HashMap<String, Integer> vals = new HashMap<>();
            for(String str: db.get(geneConfig).keySet()){
                if(db.get(geneConfig).get(str).containsKey(true)){
                    vals.put(str, 
                        db.get(geneConfig).get(str).get(true).size());
                }
            }
            Graph g = new Graph(((Integer)ChromosomeConfig.indexOfGeneConfig(geneConfig)).toString(), "Gene value", "Occurence", "Gene", vals);
            g.display();
            saveToFile("Gene_" + ((Integer)ChromosomeConfig.indexOfGeneConfig(geneConfig)).toString() + ".png", g);
        }
    }   

    private static void saveToFile(String fileName, Graph chart){
        PrintOutThread printOutThread = new PrintOutThread(fileName, chart);
        printOutThread.start();
        Timer timer = new Timer();
        PrintOutTimer printTimer = new PrintOutTimer(printOutThread, timer);
        timer.schedule(printTimer, 300);
        //TODO: Fix infinite loop issue
    }

    
}

class PrintOutThread extends Thread{
    private String fileName;
    private Graph chart;
    
    public PrintOutThread(String fileName, Graph graph){
        this.fileName = fileName;
        this.chart = graph;
    }

    @Override
    public void run() {
        try{
            File file = new File(GeneticAlgorithmConfig.runDir+"\\"+GeneticAlgorithmConfig.runName + "_" + fileName);
            ChartUtils.saveChartAsPNG(file, chart.lineGraph, 3840, 2160);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public PrintOutThread construct(String fileName, Graph graph){
        return new PrintOutThread(fileName, graph);
    }
}

class PrintOutTimer extends TimerTask{
    private Thread thread;
    private Timer timer;

    public PrintOutTimer(Thread thread, Timer timer){
        this.thread = thread;
        this.timer = timer;
    }

    @Override
    public void run() {
        if(thread != null && thread.isAlive()){
            thread.interrupt();
            timer.cancel();
        }
    }
}