import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
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
            nCrossOver = (int)res;
        }
    }

    public static Integer nextInt(Integer bound){
        return random.nextInt(bound);
    }

    public static Boolean nextBoolean(){
        return random.nextBoolean();
    }

    public static Double nextDouble(Double bound){
        return random.nextDouble(bound);
    }

    public static Float nextFloat(Float bound){
        return random.nextFloat(bound);
    }

    public static Character nextCharacter(Integer bound){
        return (char)random.nextInt(bound);
    }

    public static <T> Object nextRandVal(T max, T min, GeneDataType geneDataType){
        return geneDataType.randVal(max, min);
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
        JSONArray geneArray = (JSONArray)jsonObject.get("genes");
        GeneConfig[] tempArr = new GeneConfig[geneArray.size()];
        for(int i=0; i < tempArr.length; i++){
            JSONObject jObject = (JSONObject)geneArray.get(i);
            tempArr[i] = GeneConfig.getGeneConfig(jObject);
        }
        geneConfigs = tempArr;
    }

    public static Chromosome generatChromosome(){
        return new Chromosome();
    }
}

@SuppressWarnings("rawtypes")
class GeneConfig<T>{
    public final GeneDataType geneDataType;
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
            res = geneDataType.convert(jsonObject.get("maxValue"));
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            maxValue = (T)res;
        }

        try{
            res = geneDataType.convert(jsonObject.get("minValue"));
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

    public T convertFromBin(String str){
        T val = geneDataType.convertFromBin(str); 
        return val;
    }

    public int numBits(){
        return geneDataType.numBits();
    }

    public String generateGene(){
        T val = geneDataType.randVal(maxValue, minValue);
        List<T> notAllowed = Arrays.asList(invalidValues);
        while(notAllowed.contains(val)){
            val = geneDataType.randVal(maxValue, minValue);
        }
        return geneDataType.convertToBinary(val);
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

@SuppressWarnings("unchecked")
enum GeneDataType{
    Integer{
        @Override
        public Integer convertFromBin(String str) {
            Long l = Long.parseLong(str, 2);
            return l.intValue();
        }

        @Override
        public int numBits() {
            return 32;
        }

        @Override
        public Integer randVal(Object max, Object min) {
            return GeneticAlgorithmConfig.nextInt((Integer)max - (Integer)min) + (Integer)min;
        }

        @Override
        public String convertToBinary(Object val) {
            String temp = java.lang.Integer.toBinaryString((java.lang.Integer)val);
            return pad(temp, numBits());
            //long l = java.lang.Integer.toUnsignedLong((java.lang.Integer)val);
            //return Long.toBinaryString(l);
        }

        @Override
        public Integer convert(Long val) throws IncorrectValueException {
            return val.intValue();
        }

        @Override
        public Integer convert(java.lang.Double val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Integer convert(String val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Integer convert(java.lang.Boolean val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }
    },
    Character{
        @Override
        public Character convertFromBin(String str) {
            int val = Integer.convertFromBin(str);
            return (char)val; 
        }

        @Override
        public int numBits() {
            return 16;
        }

        @Override
        public Character randVal(Object max, Object min) {
            int maxVal = (char)max;
            int minVal = (char)min;
            return (char)(GeneticAlgorithmConfig.nextCharacter(maxVal-minVal)+minVal);
        }

        @Override
        public String convertToBinary(Object val) {
            int v = (char)val;
            String temp = Integer.convertToBinary(v);
            return pad(temp, numBits());
        }

        @Override
        public Character convert(Long val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Character convert(java.lang.Double val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Character convert(String val) throws IncorrectValueException {
            if(val.length() > 0)
                throw new IncorrectValueException(val, this);
            return val.charAt(0);
        }

        @Override
        public Character convert(java.lang.Boolean val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }
    },
    Float{
        @Override
        public Float convertFromBin(String str) {
            int v = java.lang.Integer.parseInt(str,2);
            return java.lang.Float.intBitsToFloat(v);
        }

        @Override
        public int numBits() {
            return 32;
        }

        @Override
        public Float randVal(Object max, Object min) {
            return GeneticAlgorithmConfig.nextFloat((Float)max - (Float)min) + (Float)min;
        }

        @Override
        public String convertToBinary(Object val) {
            int v = java.lang.Float.floatToIntBits((Float)val);
            return pad(Integer.convertToBinary(v), numBits());
        }

        @Override
        public Float convert(Long val) {
            int v = val.intValue();
            return (float)v;
        }

        @Override
        public Float convert(java.lang.Double val) {
            double v = val;
            return (float)v;
        }

        @Override
        public Float convert(String val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Float convert(java.lang.Boolean val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }
    },
    Boolean{
        @Override
        public Boolean convertFromBin(String str) {
            if(str.equals("1"))
                return true;
            else 
                return false;
        }

        @Override
        public int numBits() {
            return 1;
        }

        @Override
        public Boolean randVal(Object max, Object min) {
            return GeneticAlgorithmConfig.nextBoolean();
        }

        @Override
        public String convertToBinary(Object val) {
            if((Boolean)val)
                return "1";
            else 
                return "0";
        }

        @Override
        public Boolean convert(Long val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Boolean convert(java.lang.Double val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Boolean convert(String val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Boolean convert(java.lang.Boolean val) throws IncorrectValueException {
            return val;
        }
    },
    Double{
        @Override
        public Double convertFromBin(String str) {
            String temp = "0" + str.substring(1);
            Long v = Long.valueOf(temp, 2);
            if(str.charAt(0) == '1')
                return -1*java.lang.Double.longBitsToDouble(v);
            return java.lang.Double.longBitsToDouble(v);
        }

        @Override
        public int numBits() {
            return 64;
        }

        @Override
        public Double randVal(Object max, Object min) {
            return GeneticAlgorithmConfig.nextDouble((Double)max - (Double)min) + (Double)min;
        }

        @Override
        public String convertToBinary(Object val) {
            long v = java.lang.Double.doubleToLongBits((Double)val);
            return pad(java.lang.Long.toBinaryString(v), numBits());
        }

        @Override
        public Double convert(Long val) {
            int v = val.intValue();
            return (double)v;
        }

        @Override
        public Double convert(java.lang.Double val){
            return val;
        }

        @Override
        public Double convert(String val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }

        @Override
        public Double convert(java.lang.Boolean val) throws IncorrectValueException {
            throw new IncorrectValueException(val, this);
        }
    };

    public abstract <T> T convertFromBin(String str);

    public abstract int numBits();

    public abstract <T> T randVal(Object max, Object min);

    public abstract String convertToBinary(Object val);

    public abstract <T> T convert(Long val) throws IncorrectValueException;

    public abstract <T> T convert(Double val) throws IncorrectValueException;

    public abstract <T> T convert(String val) throws IncorrectValueException;

    public abstract <T> T convert(Boolean val) throws IncorrectValueException;

    public <T> T convert(Object val) throws IncorrectValueException{
        if(val instanceof Long)
            return convert((Long)val);
        if(val instanceof java.lang.Double)
            return convert((java.lang.Double)val);
        if(val instanceof String)
            return convert((String)val);
        if(val instanceof Boolean)
            return convert((Boolean)val);
        throw new IncorrectValueException(val, this);
    }

    class IncorrectValueException extends Exception{
        public IncorrectValueException(Long val, GeneDataType geneDataType){
            super("Incorrect value: " + val + " for type: " + geneDataType.name());
        }

        public IncorrectValueException(Double val, GeneDataType geneDataType){
            super("Incorrect value: " + val.toString() + " for type: " + geneDataType.name());
        }

        public IncorrectValueException(String val, GeneDataType geneDataType){
            super("Incorrect value: " + val + " for type: " + geneDataType.name());
        }

        public IncorrectValueException(Boolean val, GeneDataType geneDataType){
            super("Incorrect value: " + val + " for type: " + geneDataType.name());
        }

        public IncorrectValueException(Object val, GeneDataType geneDataType){
            super("Incorrect value: " + val + " for type: " + geneDataType.name());
        }
    }

    private static String pad(String str, int numBits){
        String temp = str;
        while(temp.length() < numBits){
            temp = "0" + temp; 
        }
        return temp;
    }
}