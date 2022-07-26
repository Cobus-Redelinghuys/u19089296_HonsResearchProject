import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("rawtypes")
public class ChromosomeConfig{
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

    public static <T> int indexOfGeneConfig(GeneConfig<T> geneConfig){
        for(int i=0; i < geneConfigs.length; i++){
            if(geneConfigs[i].equals(geneConfig))
                return i;
        }
        return -1;
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

    @SuppressWarnings("unchecked")
    public boolean validate(T val){
        Comparable<T> compVal = (Comparable<T>)val;
        if(compVal.compareTo(minValue) < 0 || compVal.compareTo(maxValue) > 0)
            return false;

        for(T inValid: invalidValues){
            if(inValid.equals(val))
                return false;
        }

        return true;
    }

}

enum CrossOverType{
    OnePointCrossOver{
        @Override
        public Chromosome[] crossOver(Chromosome c1, Chromosome c2) {
            Chromosome[] res = new Chromosome[2];
            int point = GeneticAlgorithmConfig.nextInt(c1.toString().length());
            String str1 = c1.toString().substring(0, point) + c2.toString().substring(point);
            String str2 = c2.toString().substring(0, point) + c1.toString().substring(point);
            res[0] = new Chromosome(str1);
            res[1] = new Chromosome(str2);
            return res;
        }
    },
    NPointCrossOver{
        @Override
        public Chromosome[] crossOver(Chromosome c1, Chromosome c2) {
            Chromosome[] res = new Chromosome[]{c1, c2};
            for(int i=0; i < GeneticAlgorithmConfig.nCrossOver; i++){
                res = OnePointCrossOver.crossOver(res[0], res[1]);
            }
            return res;
        }
    },
    SegmentedCrossOver{
        @Override
        public Chromosome[] crossOver(Chromosome c1, Chromosome c2) {
            Chromosome[] res = new Chromosome[]{c1, c2};
            int bound = GeneticAlgorithmConfig.nextInt(c1.toString().length());
            for(int i=0; i < bound; i++){
                res = OnePointCrossOver.crossOver(res[0], res[1]);
            }
            return res; 
        }
    },
    UniformCrossOver{
        @Override
        public Chromosome[] crossOver(Chromosome c1, Chromosome c2) {
            Chromosome[] res = new Chromosome[2];
            String str1 = "";
            String str2 = "";
            for(int i=0; i < c1.toString().length(); i++){
                if(i % 2 == 0){
                    str1 += c2.toString().charAt(i);
                    str2 += c1.toString().charAt(i);
                } else {
                    str1 += c1.toString().charAt(i);
                    str2 += c2.toString().charAt(i);
                }
            }
            res[0] = new Chromosome(str1);
            res[1] = new Chromosome(str2);
            return res;
        }
    },
    ShuffleCrossOver{
        @Override
        public Chromosome[] crossOver(Chromosome c1, Chromosome c2) {
            String[] shuffeled = shuffle(c1.toString(), c2.toString());
            Chromosome[] res = CrossOverType.OnePointCrossOver.crossOver(new Chromosome(shuffeled[0]), new Chromosome(shuffeled[1]));
            return res;
        }

        private String[] shuffle(String s1, String s2){
            String[] res = new String[2];
            ArrayList<Character> ch1 = new ArrayList<>();
            ArrayList<Character> ch2 = new ArrayList<>();

            for(int i=0; i < s1.length(); i++){
                ch1.add(s1.charAt(i));
                ch2.add(s2.charAt(i));
            }

            String str1 = "";
            String str2 = "";

            while(!ch1.isEmpty()){
                int pos = GeneticAlgorithmConfig.nextInt(ch1.size());
                str1 += ch1.get(pos);
                str2 += ch2.get(pos);
                ch1.remove(pos);
                ch2.remove(pos);
            }

            res[0] = str1;
            res[1] = str2;
            return res;
        }
    };

    public abstract Chromosome[] crossOver(Chromosome c1, Chromosome c2);
}

enum MutationType{
    SingleBitInversion{
        @Override
        public Chromosome mutate(Chromosome c) {
            int pos = GeneticAlgorithmConfig.nextInt(c.toString().length()-2)+1;
            String pre = c.toString().substring(0, pos);
            char bit;
            if(c.toString().charAt(pos) == '1')
                bit = '0';
            else 
                bit = '1';
            String post = c.toString().substring(pos+1);
            String chrom = pre + bit + post;
            return new Chromosome(chrom);
        }
    },
    BitWisInversion{
        @Override
        public Chromosome mutate(Chromosome c) {
            String res = "";
            for(int i=0; i < c.toString().length(); i++){
                if(c.toString().charAt(i) == '1')
                    res += '0';
                else 
                    res += '1';
            }
            return new Chromosome(res);
        }
    },
    RandomSelection{
        @Override
        public Chromosome mutate(Chromosome c) {
            Chromosome temp = ChromosomeConfig.generatChromosome();
            Chromosome[] res = CrossOverType.OnePointCrossOver.crossOver(c, temp);
            if(GeneticAlgorithmConfig.nextBoolean())
                return res[0];
            return res[1];
        }
    };

    public abstract Chromosome mutate(Chromosome c);
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