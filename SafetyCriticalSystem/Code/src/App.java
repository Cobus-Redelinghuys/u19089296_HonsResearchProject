import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class App {
    public static void main(String[] args) throws Exception {
        Input input = null;
        try{
            input = new Input();
        } catch(Exception e){
            e.printStackTrace();
        }

        JSONArray result = ModulesConfig.executeSystem(input);

        try(FileWriter file = new FileWriter("Output.json");){
            file.write(result.toJSONString());
            file.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

        for(int i=0; i < result.size(); i++){
            JSONObject res = (JSONObject)result.get(i);
            System.out.println("Module: " + i);
            if(((String)res.get("stdout")).contains("\n"))
                System.out.print("stdout: " + res.get("stdout"));
            else
                System.out.println("stdout: " + res.get("stdout"));
            if(((String)res.get("stderr")).contains("\n"))
                System.out.print("stderr: " + res.get("stderr"));
            else
                System.out.println("stderr: " + res.get("stderr"));
            System.out.println("duration: " + res.get("duration"));
        }
    }
}
