import java.io.File;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Module8{
    public static void main(String[] args) {
        try{
            ExmpleThread et1 = new ExmpleThread(4, 2);
            ExmpleThread et2 = new ExmpleThread(10, 5);
            et1.start();
            et2.start();
            et1.join();
            et2.join();
            for(int i=0; i < ExmpleThread.resources.length; i++){
                System.out.print(ExmpleThread.resources[i]);    
            }
            System.out.println();
        }catch(Exception e){
            e.printStackTrace();
        }
    }  

    

    
}

class ExmpleThread extends Thread{
    int index;
    int nIndex;
    static final Boolean[] resources;
    static Lock[] locks;

    ExmpleThread(int index, int nIndex){
        this.index = index;
        this.nIndex = nIndex;
    }

    static{
        //TODO: Make dynamic
        resources = new Boolean[20];
        locks = new ReentrantLock[20];
        for(int i=0; i < resources.length; i++){
            resources[i] = (i%2 == 0);
            locks[i] = new ReentrantLock();
        }
    }

    public static Integer[] readFile(){
        Integer[] returnVal = new Integer[2];
        try {
            File myObj = new File("./modules/Module8/config.txt");
            Scanner myReader = new Scanner(myObj);
            int i = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                returnVal[i%2] = Integer.parseInt(data);
                if(i >= 1)
                    break;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return returnVal;
    }

    @Override
    public void run() {
        for(int i=0; i < 100; i++){
            try{
                locks[index].lock();
                locks[nIndex].lock();
                resources[index] = !resources[nIndex];
                locks[nIndex].unlock();
                locks[index].unlock();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}