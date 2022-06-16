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

}
