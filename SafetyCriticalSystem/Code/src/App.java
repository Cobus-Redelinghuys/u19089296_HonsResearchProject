public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String[][] result = ModulesConfig.executeSystem();
        for(String[] res : result){
            System.out.print("stdout: " + res[0]);
            System.out.print("stderr: " + res[1]);
        }
    }
}
