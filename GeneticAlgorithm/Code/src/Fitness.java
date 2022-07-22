import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Fitness {
    public static double determineFitness(Chromosome input, int gen){
        FileManager.writeChromosomeToFile(input);
        executeSystem();
        ModuleReturns[] output = null;
        try{
            output = parseOutput();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } 

        double result = 0;
        FitnessResult resA = LTL(output);
        result += resA.val;
        if(resA.moduleFailures.size() <= 0){
            result += Double.POSITIVE_INFINITY;
            result += FitnessConfig.GWeight*FitnessMemory.G(input, gen, false);
        }
        else {
            result += FitnessConfig.MWeight*(1 / resA.moduleFailures.size());
            result += FitnessConfig.GWeight*FitnessMemory.G(input, gen, true);
        }            
        return result;
    }

    private static void executeSystem(){
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(GeneticAlgorithmConfig.interperterCommand + " " + GeneticAlgorithmConfig.interperterPath);
            InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
            String result[] = new String[2];
            result[0] = "";
            result[1] = "";
            int n1;
            while((n1 = isr.read()) > 0){
                result[0] += (char)n1; 
            }
            while((n1 = esr.read()) > 0){
                result[1] += (char)n1;
            }
            while(process.isAlive()){}
            if(process.exitValue() == 139)
                result[1] += "Seg fault";

            /*for(String res: result){
                System.out.print(res);
            }*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ModuleReturns[] parseOutput() throws Exception{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("./Output.json"));
        } catch (Exception e){
            e.printStackTrace();
        }

        JSONArray outputArray = (JSONArray)obj;
        ModuleReturns[] tempArr = new ModuleReturns[outputArray.size()];
        for(int i=0; i < tempArr.length; i++){
            JSONObject jObject = (JSONObject)outputArray.get(i);
            tempArr[i] = new ModuleReturns(jObject);
        }
        return tempArr;
    }

    private static FitnessResult LTL(ModuleReturns[] output){
        return FitnessConfig.determineFitness(output);
    }


}

@SuppressWarnings({"rawtypes"})
class FitnessMemory{
    private static HashMap<GeneConfig,HashMap<String,HashMap<Boolean,ArrayList<String>>>> database;

    static{
        database = new HashMap<>();
        for(GeneConfig geneConfig: ChromosomeConfig.geneConfigs){
            HashMap<String,HashMap<Boolean,ArrayList<String>>> geneTable = new HashMap<>();
            database.put(geneConfig, geneTable);
        }
    }

    static double G(Chromosome x, int gen, boolean failed){
        HashMap<GeneConfig,Integer> geneCount = new HashMap<>();
        for(int i=0; i < ChromosomeConfig.geneConfigs.length; i++){
            HashMap<String, HashMap<Boolean,ArrayList<String>>> geneTable = database.get(ChromosomeConfig.geneConfigs[i]);
            String geneStr = x.convertFromBin()[i].toString();
            if(geneTable.containsKey(geneStr)){
                geneTable.get(geneStr).get(failed).add(x.toString());
            } else {
                HashMap<Boolean,ArrayList<String>> failureTable = new HashMap<>();
                failureTable.put(true, new ArrayList<>());
                failureTable.put(false, new ArrayList<>());
                geneTable.put(geneStr, failureTable);
                geneTable.get(geneStr).get(failed).add(x.toString());
            }
            geneCount.put(ChromosomeConfig.geneConfigs[i], geneTable.get(geneStr).get(true).size());
        }

        double result = 0;
        for(Integer count: geneCount.values()){
            result += (double)count / (double)gen;
        }
        return result/geneCount.size();
    }

    @SuppressWarnings("unchecked")
    public static void jsonSummary(){
        UUID Errid = null;
        JSONArray geneTypes = new JSONArray();
        for(GeneConfig geneConfig: database.keySet()){
            int index = ChromosomeConfig.indexOfGeneConfig(geneConfig);
            JSONObject geneInfo = new JSONObject();
            JSONArray subGeneInfo = new JSONArray();
            for(String geneStr: database.get(geneConfig).keySet()){
                JSONObject subGene = new JSONObject();
                try{
                    subGene.put("gene value", geneStr);
                }catch(Exception e){
                    e.printStackTrace();
                    subGene.put("gene value", "error occured");
                }
                JSONArray failureArray = new JSONArray();
                for(Boolean failure: database.get(geneConfig).get(geneStr).keySet()){
                    JSONObject failureInfo = new JSONObject();
                    UUID id = UUID.randomUUID();
                    failureInfo.put("ID", id.toString());
                    failureInfo.put("failure", failure);
                    JSONArray chroms = new JSONArray();
                    for(String c: database.get(geneConfig).get(geneStr).get(failure)){
                        if(!chroms.contains(c))
                            chroms.add(c);
                    }
                    failureInfo.put("chromosomes", chroms);
                    failureArray.add(failureInfo);
                }
                subGene.put("failure info", failureArray);
                subGeneInfo.add(subGene);
            }
            geneInfo.put("gene info", subGeneInfo);
            geneInfo.put("gene number", index);
            geneInfo.put("gene type", geneConfig.geneDataType.name());
            geneTypes.add(geneInfo);
        }

        try(FileWriter file = new FileWriter("DatabaseSummary.json")){
            String jsonString = geneTypes.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

class ModuleReturns{
    public final String stdout;
    public final String stderr;
    public final long executionDuration;
    public final long exitValue;

    public ModuleReturns(JSONObject obj) throws MalformedOutput{
        if(!obj.containsKey("stdout") || !obj.containsKey("stderr") || !obj.containsKey("duration"))
            throw new MalformedOutput();

        try{
            stdout = (String)obj.get("stdout");
            stderr = (String)obj.get("stderr");
            executionDuration = (Long)obj.get("duration");
            exitValue = (Long)obj.get("exitvalue");
        } catch(Exception e){
            e.printStackTrace();
            throw new MalformedOutput();
        }
       
    }

    class MalformedOutput extends Exception{
        public MalformedOutput(){
            super("Output received from critical system is malformed");
        }
    }

    @Override
    public String toString() {
        String res = "";
        res += "stdout: " + stdout + "stderr: " + stderr + "execution duration: " + executionDuration;
        return res;
    }
}

class FitnessConfig{
    public static final FitnessConfigField Safety;
    public static final FitnessConfigField Livelyness;
    public static final FitnessConfigField SegFault;
    public static final FitnessConfigField Exceptions;
    public static final ExecutionTimeField ExecutionTime;
    public static final IllegalOutputField IllegalOutput;
    public static final double LTLWeight;
    public static final double GWeight;
    public static final double MWeight;

    static{
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader("FitnessConfig.json"));
        } catch (Exception e){
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject)obj;
        Object res = null;
        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Safety"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Safety = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Livelyness"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Livelyness = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("SegFault"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            SegFault = (FitnessConfigField)res;
        }

        try{
            res = new FitnessConfigField((JSONObject)jsonObject.get("Exceptions"));
        } catch (Exception e){
            e.printStackTrace();
            res = new FitnessConfigField();
        } finally{
            Exceptions = (FitnessConfigField)res;
        }

        try{
            res = new ExecutionTimeField((JSONObject)jsonObject.get("ExecutionTime"));
        } catch (Exception e){
            e.printStackTrace();
            res = new ExecutionTimeField(); 
        } finally{
            ExecutionTime = (ExecutionTimeField)res;
        }

        try{
            res = new IllegalOutputField((JSONObject)jsonObject.get("IllegalOutput"));
        } catch (Exception e){
            e.printStackTrace();
            res = new IllegalOutputField();
        } finally{
            IllegalOutput = (IllegalOutputField)res;
        }

        try{
            res = (Double)jsonObject.get("LTLWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            LTLWeight = (Double)res;
        }

        try{
            res = (Double)jsonObject.get("MWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            MWeight = (Double)res;
        }

        try{
            res = (Double)jsonObject.get("GWeight");
        } catch(Exception e){
            e.printStackTrace();
            res = 0.5;
        } finally{
            GWeight = (Double)res;
        }
    }

    public static FitnessResult determineFitness(ModuleReturns[] output){
        FitnessResult res = new FitnessResult();
        res = addFitnesses(res, Safety(output));
        res = addFitnesses(res, Livelyness(output));
        res = addFitnesses(res, SegFault(output));
        res = addFitnesses(res, Exception(output));
        res = addFitnesses(res, ExecutionTime(output));
        res = addFitnesses(res, IllegalOutput(output));
        res.val = FitnessConfig.LTLWeight * res.val;
        return res;
    }

    private static FitnessResult addFitnesses(FitnessResult result, FitnessResult input){
        result.val += input.val;
        for(ModuleReturns moduleResults: input.moduleFailures.keySet()){
            if(result.moduleFailures.containsKey(moduleResults)){
                result.moduleFailures.replace(moduleResults, result.moduleFailures.get(moduleResults) + input.moduleFailures.get(moduleResults));
            } else {
                result.moduleFailures.put(moduleResults, input.moduleFailures.get(moduleResults));
            }
        }
        return result;
    }

    private static FitnessResult Safety(ModuleReturns[] output){
        if(!Safety.enabled){
            return new FitnessResult();
        }
        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            if(!(moduleReturns.stderr.isBlank() && moduleReturns.stderr.isEmpty())){
                result += 1;
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
            }
            if(moduleReturns.stdout.toUpperCase().contains("EXCEPTION")){
                result += 1;
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
            }
        }
        res.val = Safety.weight * result/(output.length * 2);
        return res;
    }

    private static FitnessResult Livelyness(ModuleReturns[] output){
        if(!Livelyness.enabled)
            return new FitnessResult();

        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.exitValue != 0){
                result += 1;
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
            }
        }
        res.val = Livelyness.weight * result / output.length;
        return res;
    }

    private static FitnessResult SegFault(ModuleReturns[] output){
        if(Safety.enabled)
            return new FitnessResult();

        if(!SegFault.enabled)
            return new FitnessResult();

        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.stderr.toLowerCase().contains("segfault") || moduleReturns.stderr.toLowerCase().contains("segmentation fault") || moduleReturns.exitValue == 139){
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
                result += 1;
            }
        }
        res.val = SegFault.weight * result / output.length;
        return res;
    }

    private static FitnessResult Exception(ModuleReturns[] output){
        if(Safety.enabled)
            return new FitnessResult();

        if(!SegFault.enabled)
            return new FitnessResult();

        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.stdout.toLowerCase().contains("exception") || moduleReturns.stdout.toLowerCase().contains("exceptions")){
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
                result += 1;
            } else if (moduleReturns.stderr.toLowerCase().contains("exception") || moduleReturns.stderr.toLowerCase().contains("exceptions")){
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
                result += 1;
            }
        }
        res.val = Exceptions.weight * result / output.length;
        return res;
    }

    private static FitnessResult ExecutionTime(ModuleReturns[] output){
        if(!ExecutionTime.enabled)
            return new FitnessResult();

        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            if(moduleReturns.executionDuration > ExecutionTime.maxTime){
                if(!res.moduleFailures.containsKey(moduleReturns))
                    res.moduleFailures.put(moduleReturns, 1);
                result += 1;
            }
        }
        res.val = ExecutionTime.weight * result / output.length;
        return res;
    }

    private static FitnessResult IllegalOutput(ModuleReturns[] output){
        if(!IllegalOutput.enabled)
            return new FitnessResult();

        if(IllegalOutput.words.length <=0)
            return new FitnessResult();

        double result = 0;
        FitnessResult res = new FitnessResult();
        for(ModuleReturns moduleReturns: output){
            for(String word: IllegalOutput.words){
                if(moduleReturns.stdout.contains(word)){
                    if(!res.moduleFailures.containsKey(moduleReturns))
                        res.moduleFailures.put(moduleReturns, 1);
                    result += 1;
                }
            }
        }
        res.val = IllegalOutput.weight * result / (IllegalOutput.words.length * output.length);
        return res;
    }
    
}

class FitnessResult{
    double val = 0;
    HashMap<ModuleReturns, Integer> moduleFailures = new HashMap<>();
}

class FitnessConfigField{
    public final boolean enabled;
    public final double weight;

    public FitnessConfigField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
        } catch (Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public FitnessConfigField(){
        enabled = false;
        weight = Double.NaN;
    }
}

class IllegalOutputField{
    public final boolean enabled;
    public final double weight;
    public final String[] words;

    public IllegalOutputField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
            JSONArray arr = (JSONArray)jsonObject.get("words");
            String[] res = new String[arr.size()];
            for(int i=0; i < res.length; i++){
                res[i] = (String)arr.get(i);
            }
            words = res;
        } catch (Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public IllegalOutputField(){
        enabled = false;
        weight = Double.NaN;
        words = new String[0];
    }
}

class ExecutionTimeField{
    public final boolean enabled;
    public final double weight;
    public final long maxTime;

    public ExecutionTimeField(JSONObject jsonObject) throws MalformedFitnessConfig{
        try{
            enabled = (Boolean)jsonObject.get("enabled");
            weight = ((Long)jsonObject.get("weight")).doubleValue();
            maxTime = (Long)jsonObject.get("maxTime");
        } catch(Exception e){
            e.printStackTrace();
            throw new MalformedFitnessConfig();
        }
    }

    public ExecutionTimeField(){
        enabled = false;
        weight = Double.NaN;
        maxTime = 0;
    }

}

class MalformedFitnessConfig extends Exception{
    MalformedFitnessConfig(){
        super("Malformed fitnress config file");
    }
}