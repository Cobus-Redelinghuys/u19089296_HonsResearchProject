import java.util.ArrayList;
import java.util.HashMap;

public class GeneticAlgorithm {
    Chromosome[] population = new Chromosome[GeneticAlgorithmConfig.populationSize];
    
    public GeneticAlgorithm(){
        for(int i=0; i < population.length; i++){
            population[i] = ChromosomeConfig.generatChromosome();
        }
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
}
