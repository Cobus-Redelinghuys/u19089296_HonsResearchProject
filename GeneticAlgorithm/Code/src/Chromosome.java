import java.util.ArrayList;

public class Chromosome {
    private final String bitRepresentation;
    
    public Chromosome(){
        bitRepresentation = "";
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
            String str = line.substring(startPos, finalPos);
            result[i] = ChromosomeConfig.geneConfigs[i].convertFromBin(str);
        }

        return result;
    }

}
