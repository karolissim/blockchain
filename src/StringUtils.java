import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class StringUtils {

    ArrayList<String> readFromFile(String textFileName){
        ArrayList<String> data = new ArrayList<>();
        try{
            File myFile = new File(textFileName);
            Scanner myReader = new Scanner(myFile);
            if(!myReader.hasNextLine()) data.add("");
            while(myReader.hasNextLine()){
                data.add(myReader.nextLine());
            }
        } catch (FileNotFoundException e){
            System.out.println("Cant find text file");
            e.printStackTrace();
        }
        return data;
    }

    ArrayList<Pair<String, String>> generateRandomStrings(int length){
        ArrayList<Pair<String, String>> randomStringArray = new ArrayList<>();

        for(int i = 0; i < 25000; i++){
            randomStringArray.add(Pair.of(RandomStringUtils.random(length, true, false), RandomStringUtils.random(length, true, false)));
        }

        return randomStringArray;
    }

}