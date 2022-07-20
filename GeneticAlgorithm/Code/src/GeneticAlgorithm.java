import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
                Chromosome[] offspring = GeneticAlgorithmConfig.crossOverType.crossOver(selected[0].get(i), selected[0].get(i+1));
                replacementMap.replace(selected[1].get(i), offspring[0]);
                replacementMap.replace(selected[1].get(i+1), offspring[i+1]);
                i++;
            } else if(GeneticAlgorithmConfig.mutationProp < GeneticAlgorithmConfig.nextDouble(1.0)) {
                Chromosome offspring = GeneticAlgorithmConfig.mutationType.mutate(selected[0].get(i));
                replacementMap.replace(selected[1].get(i), offspring);
            } else {
                replacementMap.replace(selected[1].get(i), selected[0].get(i).clone());
            }
        }
        population = replacementMap.values().toArray(new Chromosome[0]);
        System.out.println("Generation: " + gen);
        System.out.println("Average: " + calculateAverage(gen));
        System.out.println("Std: " + calculateStd(gen));
        System.out.println("Variance: " + variance());
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

    private double calculateAverage(int gen){
        double sum = 0;
        for(double v: totalFitnesses[gen]){
            sum += v;
        }
        return sum/totalFitnesses[gen].size();
    }

    private double calculateStd(int gen){
        double avg = calculateAverage(gen);
        double sum = 0;
        for(double v: totalFitnesses[gen]){
            sum = Math.pow(v-avg, 2);
        }
        return Math.sqrt(sum/totalFitnesses[gen].size());
    }

    public void printDatabase(){
        FitnessMemory.jsonSummary();
    }
}
