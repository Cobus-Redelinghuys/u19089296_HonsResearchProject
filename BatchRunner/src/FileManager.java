import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
public class FileManager {
    static final JSONObject GAConfig;
    static final JSONObject ChromosomeConfig;
    static final JSONObject FitnessConfig;
    static final JSONObject ModuleConfig;
    static final File CodeFile;
    static final File SystemFile;
    static final Stream<Path> Modules;
    static final String testName;
    private static final String testsDir = "Experiments";
    private static final String finalTestDir;
    static final ConfigChangerClass[] configChangerClasses;

    
    static{
        LocalDateTime lDT = LocalDateTime.now();
        testName = lDT.getDayOfMonth() + "_" +lDT.getMonth().name() + "_" + lDT.getYear() + "_" + lDT.getHour() + "h" + lDT.getMinute();
        GAConfig = readJSON("./projectFiles/GeneticAlgorithmConfig.json");
        ChromosomeConfig = readJSON("./projectFiles/ChromosomeConfig.json");
        FitnessConfig = readJSON("./projectFiles/FitnessConfig.json");
        ModuleConfig = readJSON("./projectFiles/ModuleConfig.json");
        CodeFile = readFile("./projectFiles/Code.jar");
        SystemFile = readFile("./projectFiles/System.jar");
        Stream<Path> temp;
        try{
            temp = Files.walk(Paths.get("./projectFiles/modules"));
        } catch (Exception e){
            temp = null;
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
        Modules = temp;

        if(Files.notExists(Paths.get(testsDir))){
            try{
                Files.createDirectories(Paths.get(testsDir));
            } catch(Exception e){
                e.printStackTrace();
                java.lang.System.exit(-1);
            }
        }

        if(Files.notExists(Paths.get(testsDir + "/" + testName))){
            try{
                Files.createDirectories(Paths.get(testsDir + "/" + testName));
            } catch(Exception e){
                e.printStackTrace();
                java.lang.System.exit(-1);
            }
        }
        finalTestDir = testsDir + "/" + testName + "/";
       
        ArrayList<ConfigChangerClass> list = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try{
            JSONObject tName = (JSONObject)jsonParser.parse(new FileReader("./Config.json"));
            tName.keySet().forEach(testName -> {
                JSONObject pNames = (JSONObject)tName.get(testName);
                pNames = (JSONObject)pNames.get("params");
                pNames.keySet().forEach(paramName -> {
                    list.add(new ConfigChangerClass((JSONObject)tName.get(testName), testName.toString(), paramName.toString()));
                });
            });
        } catch(Exception e){
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
        configChangerClasses = list.toArray(new ConfigChangerClass[0]);
        numTests = 0;
        for(ConfigChangerClass c: configChangerClasses){
            createTestDirs(c);
        }

        
    }

    static int numTests;

    private static void createTestDirs(ConfigChangerClass changerClass){
        for(Object v: changerClass.values){
            String dir = finalTestDir + "/" + changerClass.testSetName + "/" + changerClass.param;
            dir += "/" + v + "/";
            try{
                Files.createDirectories(Paths.get(dir));
            }catch(Exception e){
                e.printStackTrace();
                java.lang.System.exit(-1);
            }
            if(changerClass.fileName.contains("GeneticAlgorithmConfig"))
                writeFile(GAConfig, dir+"GeneticAlgorithmConfig.json", changerClass.param, v);
            else
                writeFile(GAConfig, dir+"GeneticAlgorithmConfig.json");

            if(changerClass.fileName.contains("ChromosomeConfig"))
                writeFile(ChromosomeConfig, dir+"ChromosomeConfig.json", changerClass.param, v);
            else
                writeFile(ChromosomeConfig, dir+"ChromosomeConfig.json");

            if(changerClass.fileName.contains("FitnessConfig"))
                writeFile(FitnessConfig, dir+"FitnessConfig.json", changerClass.param, v);
            else
                writeFile(FitnessConfig, dir+"FitnessConfig.json");

            if(changerClass.fileName.contains("ModuleConfig"))
                writeFile(ModuleConfig, dir+"ModuleConfig.json", changerClass.param, v);
            else
                writeFile(ModuleConfig, dir+"ModuleConfig.json");

            writeFile(CodeFile, dir);
            writeFile(SystemFile, dir);

            /*try{
                Files.createDirectories(Paths.get(dir+"/modules"));
            } catch (Exception e){
                e.printStackTrace();
                java.lang.System.exit(-1);
            }*/
            final String fDir = dir;
            Stream<Path> tempModules = null;
            
            try{
                Path p = Paths.get("./projectFiles/modules/");
                tempModules = Files.walk(p);
                //tempModules = Files.walk(Paths.get("./projectFiles/modules"));
            } catch (Exception e){
                tempModules = null;
                e.printStackTrace();
                java.lang.System.exit(-1);
            }

            tempModules.forEach(mods -> {
                try{
                    String modulesWithoutProject = mods.subpath(2,mods.getNameCount()).toString();
                    String fileDir = fDir + "/" + modulesWithoutProject;
                    
                    
                    // Files.createDirectories(Paths.get(fileDir));
                    //if(!Files.exists(mods.subpath(2,mods.getNameCount())))
                        Files.copy(mods, Paths.get(fileDir));
                }catch (Exception e){
                    e.printStackTrace();
                    java.lang.System.exit(-1);
                }
            });
            numTests++;
        }
        
    }

    private static JSONObject readJSON(String filename){
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try{
            obj = jsonParser.parse(new FileReader(filename));
        } catch (Exception e){
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
        return (JSONObject)obj;
    }

    private static File readFile(String filename){
        File myObj = null;
        try {
            myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              System.out.println(data);
            }
            myReader.close();
          } catch (Exception e) {
            e.printStackTrace();
            java.lang.System.exit(-1);
          }
        return myObj;
    }

    private static void writeFile(JSONObject jsonObject, String fileName){
        try(FileWriter file = new FileWriter(fileName)){
            String jsonString = jsonObject.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeFile(JSONObject jsonObject, String fileName, Object param, Object val){
        try(FileWriter file = new FileWriter(fileName)){
            JSONObject copy = (JSONObject)jsonObject.clone();
            copy.replace(param, val);
            String jsonString = jsonObject.toJSONString();
            file.write(jsonString);
            file.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeFile(File file, String dir){
        try{
            Files.copy(file.toPath(), Path.of(dir + "/" + file.getName()));
        } catch(Exception e) {
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
    }
    
}

class ConfigChangerClass{
    public final String testSetName;
    public final String fileName;
    public final Object defaultValue;
    public final Object[] values;
    public final String param;
    
    public ConfigChangerClass(JSONObject jsonObject, String testName, String param){
        testSetName = testName;
        this.param = param;
        fileName = jsonObject.get("fileName").toString();
        JSONObject obj = ((JSONObject)jsonObject.get("params"));
        defaultValue = ((JSONObject)obj.get(param)).get("default");
        values = ((JSONArray)((JSONObject)obj.get(param)).get("values")).toArray();
    }
}
