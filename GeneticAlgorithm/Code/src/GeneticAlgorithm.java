public class GeneticAlgorithm {
    Chromosome[] population = new Chromosome[GeneticAlgorithmConfig.populationSize];
    
    public GeneticAlgorithm(){
        for(int i=0; i < population.length; i++){
            population[i] = ChromosomeConfig.generatChromosome();
            System.out.println(population[i]);
            System.out.println(population[i].genesString());
        }
    }
}
