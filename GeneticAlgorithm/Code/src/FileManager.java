import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileManager {
    @SuppressWarnings("unchecked")
    static void writeGeneticAlgorithmConfigFile(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("populationSize", 100);
        jsonObject.put("numGenerations", 100);
        jsonObject.put("selectionSize", 10);
        jsonObject.put("reproductionProp", 0.5);
        jsonObject.put("crossoverProp", 0.5);
        jsonObject.put("mutationProp", 0.5);
        jsonObject.put("crossOverType", "OnePointCrossOver");
        jsonObject.put("mutationType", "BitWisInversion");
        jsonObject.put("seed", 0);

        try(FileWriter file = new FileWriter("GeneticAlgorithmConfig.json")){
            String jsonString = jsonObject.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    static void writeChromosomeConfigFile(){

    }
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

@SuppressWarnings("rawtypes")
class ChromosomeConfig{
    public static final GeneConfig[] geneConfigs; 
    static{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("ChromosomeConfig.json"));
        } catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray geneArray = (JSONArray)jsonObject.get("Genes");
        GeneConfig[] tempArr = new GeneConfig[geneArray.size()];
        for(int i=0; i < tempArr.length; i++){
            JSONObject jObject = (JSONObject)geneArray.get(i);
            tempArr[i] = GeneConfig.getGeneConfig(jObject);
        }
        geneConfigs = tempArr;
    }
}

@SuppressWarnings("rawtypes")
class GeneConfig<T>{
    private final GeneDataType geneDataType;
    private final T maxValue;
    private final T minValue;
    public final T[] invalidValues;
    public final Class dataType;

    public T maxValue(){
        return maxValue;
    }

    public T minValue(){
        return minValue;
    }
    
    @SuppressWarnings("unchecked")
    public GeneConfig(JSONObject jsonObject){
        Object res = null;
        try{
            String line = (String)jsonObject.get("geneDataType");
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

            default:
                dataType = Double.class;
                break;
        }

        try{
            res = (T)jsonObject.get("maxValue");
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            maxValue = (T)res;
        }

        try{
            res = (T)jsonObject.get("minValue");
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            minValue = (T)res;
        }

        Object[] resArr = null;
        try{
            JSONArray temp = ((JSONArray)jsonObject.get("invalidValues"));
            resArr = new Object[temp.size()];
            for(int i=0; i < resArr.length; i++) {
                resArr[i] = temp.get(i);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            invalidValues = (T[])resArr;
        }

    }


    static GeneConfig getGeneConfig(JSONObject jsonObject){
        GeneDataType res = null;
        try{
            String line = (String)jsonObject.get("geneDataType");
            for(GeneDataType geneDT: GeneDataType.values()){
                if(geneDT.name().equals(line))
                    res = geneDT;
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        switch (res) {
            case Integer:
                return new GeneConfig<Integer>(jsonObject);
        
            case Character:
                return new GeneConfig<Character>(jsonObject);
                
            case Float:
                return new GeneConfig<Float>(jsonObject);

            case Boolean:
                return new GeneConfig<Boolean>(jsonObject);

            default:
                return new GeneConfig<Double>(jsonObject);
        }
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