import java.io.File;
import java.util.Scanner;

public class Module7{
    public static void func(int n, int v){
        if(n < v){
            func(n-1, v);
        }
    }

    public static int readFile(){
        int returnVal = 0;
        try {
            File myObj = new File("./modules/Module7/config.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                returnVal = Integer.parseInt(data);
                break;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return returnVal;
    }

    public static void main(String[] args) {
        int n = readFile();
        int v = Integer.parseInt(args[0]);
        func(n,v);
    }
}