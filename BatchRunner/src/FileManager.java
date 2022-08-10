import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        writeFile(GAConfig, finalTestDir+"GeneticAlgorithmConfig.json");
        writeFile(ChromosomeConfig, finalTestDir+"ChromosomeConfig.json");
        writeFile(FitnessConfig, finalTestDir+"FitnessConfig.json");
        writeFile(ModuleConfig, finalTestDir+"ModuleConfig.json");
        writeFile(CodeFile);
        writeFile(SystemFile);
        try{
            Files.createDirectories(Paths.get(finalTestDir+"/modules"));
        } catch (Exception e){
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
            Modules.forEach(mods -> {
                try{
                    Files.createDirectories(Paths.get(finalTestDir + "/modules/"+mods));
                }catch (Exception e){
                    e.printStackTrace();
                    java.lang.System.exit(-1);
                }
            });
        
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

    private static void writeFile(File file){
        try{
            Files.copy(file.toPath(), Path.of("./" + finalTestDir +"/" + file.getName()));
        } catch(Exception e) {
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
    }
    
}
/*
class ConfigChangerClass{
    public 
}
*/