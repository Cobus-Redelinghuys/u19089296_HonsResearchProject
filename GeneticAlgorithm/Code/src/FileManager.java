import java.io.FileReader;
import java.util.Random;
import java.lang.reflect.TypeVariable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileManager {
    
}


class GeneticAlgorithmConfig{
    public final static int populationSize;
    public static final int numGenerations;
    public static final int selectionSize;
    public static final double reproductionProp;
    public static final double crossoverProp;
    public static final double mutationProp;
    public static final CrossOverType crossOverType;
    public static final MutationType mutationType;
    public static final long seed;
    private static final Random random;

    static{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("GeneticAlgorithmConfig.json"));
        } catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        Object res = null;
            
        try{
            res = jsonObject.get("populationSize");
        } catch(Exception e){
            e.printStackTrace();
            res = 200;
        }finally{
            populationSize = ((Long)res).intValue();    
        }
            
        try{
            res = jsonObject.get("numGenerations");
        } catch(Exception e){
            e.printStackTrace();
            res = 200;
        } finally{
            numGenerations = ((Long)res).intValue();
        }

        try{
            res = jsonObject.get("selectionSize");
        } catch(Exception e){
            e.printStackTrace();
            res = 200;
        } finally{
            selectionSize = ((Long)res).intValue();
        }

        try{
            res = jsonObject.get("reproductionProp");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            reproductionProp = (double)res;
        }

        try{
            res = jsonObject.get("crossoverProp");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            crossoverProp = (double)res;
        }

        try{
            res = jsonObject.get("mutationProp");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            mutationProp = (double)res;
        }
            
        try{
            String line = (String)jsonObject.get("crossOverType");
            for(CrossOverType crossOverType: CrossOverType.values()){
                if(crossOverType.name().equals(line))
                    res = crossOverType;
            }
        } catch(Exception e){
            e.printStackTrace();
            res = CrossOverType.OnePointCrossOver;
        } finally{
            crossOverType = (CrossOverType)res;
        }

        try{
            String line = (String)jsonObject.get("mutationType");
            for(MutationType mutationType: MutationType.values()){
                if(mutationType.name().equals(line))
                    res = mutationType;
            }
        } catch(Exception e){
            e.printStackTrace();
            res = MutationType.BitWisInversion;
        } finally{
            mutationType = (MutationType)res;
        }

        try{
            res = jsonObject.get("seed");
        } catch(Exception e){
            e.printStackTrace();
            res = 200;
        } finally{
            seed = (long)res;
        }
        random = new Random(seed);
    }

    public static int nextInt(int bound){
        return random.nextInt(bound);
    }

    public static boolean nextBoolean(){
        return random.nextBoolean();
    }

    public static double nextDouble(double bound){
        return random.nextDouble(bound);
    }
}

class ChromosomeConfig{

}

@SuppressWarnings("rawtypes")
class GeneConfig{
    public final GeneDataType geneDataType;
    private final Object maxValue;
    private final Object minValue;
    private final Object[] invalidValues;
    private final Class dataType;
    
    @SuppressWarnings("rawtypes")
    public GeneConfig(JSONObject jsonObject){
        Object res = null;
        try{
            String line = (String)jsonObject.get("DataType");
            for(GeneDataType geneDT: GeneDataType.values()){
                if(geneDT.name().equals(line))
                    res = geneDT;
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            geneDataType = (GeneDataType)res;
        }

        switch (geneDataType) {
            case Integer:
                dataType = Integer.class;
                break;
        
            case Character:
                dataType = Character.class;
                break;
                
            case Float:
                dataType = Float.class;
                break;

            case Boolean:
                dataType = Boolean.class;
                break;

            case Double:
                dataType = Double.class;
                break;
            
            default:
                break;
        }
    }

    public <T> T getMaxValue(){
        return (T)maxValue;

    public void test(){
        Double v = getMaxValue();
    }

}

enum CrossOverType{
    OnePointCrossOver,
    NPointCrossOver,
    SegmentedCrossOver,
    UniformCrossOver,
    ShuffleCrossOver
}

enum MutationType{
    SingleBitInversion,
    BitWisInversion,
    RandomSelection
}

enum GeneDataType{
    Integer,
    Character,
    Float,
    Boolean,
    Double
}