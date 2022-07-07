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
        jsonObject.put("nCrossOver", 5);
        jsonObject.put("interpreterPath", "");
        jsonObject.put("interpreterCommand", "java -jar ");

        try(FileWriter file = new FileWriter("GeneticAlgorithmConfig.json")){
            String jsonString = jsonObject.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static void writeChromosomeConfigFile(){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("geneDataType", "Double");
        jsonObject.put("maxValue", 10.0);
        jsonObject.put("minValue", -10.0);
        JSONArray invalidValues = new JSONArray();
        invalidValues.add(5.0);
        invalidValues.add(10.0);
        jsonObject.put("invalidValues", invalidValues);
        jsonArray.add(jsonObject);

        try(FileWriter file = new FileWriter("ChromosomeConfig.json")){
            JSONObject tempObj = new JSONObject();
            tempObj.put("genes", jsonArray);
            String jsonString = tempObj.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static void writeChromosomeToFile(Chromosome c){
        JSONObject jsonObject = new JSONObject();
        Object[] genes = c.convertFromBin();
        for(int i=0; i < genes.length; i++){
            jsonObject.put("module" + (i+1), genes[i]);
            System.out.println(genes[i].toString());
        }

        try(FileWriter file = new FileWriter("Input.json")){
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
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
    public static final int nCrossOver;
    public static final String interperterPath;
    public static final String interperterCommand;

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

        try{
            res = jsonObject.get("nCrossOver");
        } catch(Exception e){
            e.printStackTrace();
            res = 5;
        } finally{
            nCrossOver = ((Long)res).intValue();
        }

        try{
            String line = (String)jsonObject.get("interpreterPath");
            res = line;
        } catch(Exception e){
            e.printStackTrace();
            res = "";
        } finally{
            interperterPath = (String)res;
        }

        try{
            String line = (String)jsonObject.get("interpreterCommand");
            res = line;
        } catch(Exception e){
            e.printStackTrace();
            res = "";
        } finally{
            interperterCommand = (String)res;
        }
    }

    public static Integer nextInt(Integer bound){
        return random.nextInt(bound);
    }

    public static Boolean nextBoolean(){
        return random.nextBoolean();
    }

    public static Double nextDouble(Double bound){
        return bound*random.nextDouble();
    }

    public static Float nextFloat(Float bound){
        return bound*random.nextFloat();
    }

    public static Character nextCharacter(Integer bound){
        return (char)random.nextInt(bound);
    }

    public static <T> Object nextRandVal(T max, T min, GeneDataType geneDataType){
        return geneDataType.randVal(max, min);
    }
}