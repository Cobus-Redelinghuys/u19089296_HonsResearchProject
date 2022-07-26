public class Chromosome {
    private final String bitRepresentation;
    
    @SuppressWarnings({"rawtypes"})
    public Chromosome(){
        String bits = "";
        for(GeneConfig geneConfig : ChromosomeConfig.geneConfigs){
            bits += geneConfig.generateGene();
        }

        bitRepresentation = bits;
    }

    public Chromosome(String bits){
        bitRepresentation = bits;
    }

    public Object[] convertFromBin(){
        Object[] result = new Object[ChromosomeConfig.geneConfigs.length];
        String line = bitRepresentation;
        for(int i=0; i < ChromosomeConfig.geneConfigs.length; i++){
            int startPos;
            int finalPos;
            if(i == 0)
                startPos = 0;
            else
                startPos = ChromosomeConfig.geneConfigs[i-1].numBits();

            finalPos = ChromosomeConfig.geneConfigs[i].numBits();
            String str = line.substring(startPos, startPos + finalPos);
            result[i] = ChromosomeConfig.geneConfigs[i].convertFromBin(str);
        }

        return result;
    }

    @Override
    public String toString() {
        return bitRepresentation;
    }

    public String genesString(){
        String line = "";
        for(Object obj: convertFromBin()){
            line += obj + "|";
        }
        return line;
    }

    public static Chromosome[] crossOver(Chromosome c1, Chromosome c2){
        Chromosome[] res = GeneticAlgorithmConfig.crossOverType.crossOver(c1, c2);
        while((!validateChromosome(res[0])) && (!validateChromosome(res[1]))){
            res = GeneticAlgorithmConfig.crossOverType.crossOver(c1, c2);
        }
        return res;
    }

    public static Chromosome mutate(Chromosome c){
        Chromosome res = GeneticAlgorithmConfig.mutationType.mutate(c);
        while((!validateChromosome(res))){
            res = GeneticAlgorithmConfig.mutationType.mutate(c);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static boolean validateChromosome(Chromosome c){
        try{
            Object[] genes = c.convertFromBin();
            for(int i=0; i < ChromosomeConfig.geneConfigs.length; i++){
                if(!ChromosomeConfig.geneConfigs[i].validate(genes[i]))
                    return false;
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public Chromosome clone(){
        return new Chromosome(toString());
    }

}
