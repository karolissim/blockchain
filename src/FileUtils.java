import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class FileUtils {

    String readLine(String textFileName){
        String data = "";
        try{
            File myFile = new File(textFileName);
            Scanner myReader = new Scanner(myFile);
            while(myReader.hasNextLine()){
                data =  myReader.nextLine();
            }
        } catch (FileNotFoundException e){
            System.out.println("Cant find text file");
            e.printStackTrace();
        }
        return data;
    }

    ArrayList<String> readLines(String textFileName){
        ArrayList<String> data = new ArrayList<>();
        try{
            File myFile = new File(textFileName);
            Scanner myReader = new Scanner(myFile);
            while(myReader.hasNextLine()){
                data.add(myReader.nextLine());
            }
        } catch (FileNotFoundException e){
            System.out.println("Cant find text file");
            e.printStackTrace();
        }
        return data;
    }

}