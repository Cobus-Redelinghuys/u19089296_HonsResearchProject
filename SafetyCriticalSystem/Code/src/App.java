public class App {
    public static void main(String[] args) throws Exception {
        Input input = null;
        try{
            input = new Input();
        } catch(Exception e){
            e.printStackTrace();
        }
        String[][] result = ModulesConfig.executeSystem(input);
        for(String[] res : result){
            if(res[0].contains("\n"))
                System.out.print("stdout: " + res[0]);
            else
                System.out.println("stdout: " + res[0]);
            if(res[1].contains("\n"))
                System.out.print("stderr: " + res[1]);
            else
                System.out.println("stderr: " + res[1]);
        }
    }
}
