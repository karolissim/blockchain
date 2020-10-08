import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Hash {

    private static StringUtils stringUtils = new StringUtils();
    private static HashAlgorithm hash = new HashAlgorithm();

    private static final String EMPTY = "texts/empty.txt";
    private static final String ONE_CHAR_1 = "texts/one_1.txt";
    private static final String ONE_CHAR_2 = "texts/one_2.txt";
    private static final String RANDOM_1000_1 = "texts/random1000_1.txt";
    private static final String RANDOM_1000_1_DIF = "texts/random1000_2.txt";
    private static final String SAMPLE_TEXT = "texts/konstitucija.txt";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        if(args.length != 0) {
            String path = "texts/" + args[0];
            System.out.println("Hash of (" + args[0] + ") is : " + hash.hashFunction(stringUtils.readFromFile(path).get(0)));
        } else{
            System.out.println("Input string to hash:");
            input = scanner.nextLine();
            System.out.println("Hash of (" + input + ") is : " + hash.hashFunction(input));
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static void compareAlgorithms(ArrayList<String> testInput) throws NoSuchAlgorithmException {
        ArrayList<String> myHashesList = new ArrayList<>();
        ArrayList<String> shaHashesList = new ArrayList<>();
        ArrayList<String> md5HashesList = new ArrayList<>();
        ArrayList<String> sha1HashesList = new ArrayList<>();

        long start = System.nanoTime();
        for(String line : testInput){
            myHashesList.add(hash.hashFunction(line));
        }
        double elapsedTime = (System.nanoTime() - start) / Math.pow(10,9);
        System.out.println("My hashes: " + elapsedTime + " seconds");

        start = System.nanoTime();
        for(String line : testInput){
            shaHashesList.add(sha256(line));
        }
        elapsedTime = (System.nanoTime() - start) / Math.pow(10,9);
        System.out.println("SHA-256 hashes: " + elapsedTime + " seconds");

        start = System.nanoTime();
        for(String line : testInput){
            sha1HashesList.add(sha1(line));
        }
        elapsedTime = (System.nanoTime() - start) / Math.pow(10,9);
        System.out.println("SHA-1 hashes: " + elapsedTime + " seconds");

        start = System.nanoTime();
        for(String line : testInput){
            md5HashesList.add(md5(line));
        }
        elapsedTime = (System.nanoTime() - start) / Math.pow(10,9);
        System.out.println("MD5 hashes: " + elapsedTime + " seconds");

        int sameValuesCount = 0;
        for(int i = 0; i < shaHashesList.size(); i++){
            if(myHashesList.get(i).equals(shaHashesList.get(i))){
                sameValuesCount++;
            }
        }
        System.out.println(sameValuesCount + "  " + shaHashesList.size());
    }

    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger hash = new BigInteger(1, hashBytes);
        return hash.toString(16);
    }

    private static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger hash = new BigInteger(1, hashBytes);
        return hash.toString(16);
    }

    private static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger hash = new BigInteger(1, hashBytes);
        return hash.toString(16);
    }

    private static void comparePairs(ArrayList<Pair<String,String>> randomStringPair, int length){
        int count = 0;
        for(Pair<String, String> pair : randomStringPair){
            if(hash.hashFunction(pair.getLeft()).equals(hash.hashFunction(pair.getRight()))) count++;
        }
        System.out.println("repetition count of randomly generated string pairs (" + length + "): " + count);
    }

    private static void testFiles(){
        ArrayList<String> test5 = stringUtils.readFromFile(ONE_CHAR_1);
        ArrayList<String> test6 = stringUtils.readFromFile(ONE_CHAR_2);
        ArrayList<String> test1 = stringUtils.readFromFile(RANDOM_1000_1);
        ArrayList<String> test2 = stringUtils.readFromFile(RANDOM_1000_1_DIF);
        ArrayList<String> test4 = stringUtils.readFromFile(EMPTY);

        System.out.println("1 char: " + hash.hashFunction(test5.get(0)) + " " + hash.hashFunction(test5.get(0)).length());
        System.out.println("1 char dif: " + hash.hashFunction(test6.get(0)) + " " + hash.hashFunction(test6.get(0)).length());
        System.out.println("1100 symbols: " + hash.hashFunction(test1.get(0)) + " " + hash.hashFunction(test1.get(0)).length());
        System.out.println("1100 symbols (one different symbol): " + hash.hashFunction(test2.get(0)) + " " + hash.hashFunction(test2.get(0)).length());
        System.out.println("empty file hash: " + hash.hashFunction(test4.get(0)) + " " + hash.hashFunction(test4.get(0)).length());
    }

    private void testGivenFile(){
        ArrayList<String> test3 = stringUtils.readFromFile(SAMPLE_TEXT);
        long start = System.nanoTime();
        for(String line : test3){
            hash.hashFunction(line);
        }
        double elapsedTime = (System.nanoTime() - start) / Math.pow(10,9);
        System.out.println("My hashes: " + elapsedTime + " seconds");
    }

    private void comparePairsTest(){
        comparePairs(stringUtils.generateRandomStrings(10), 10);
        comparePairs(stringUtils.generateRandomStrings(100), 100);
        comparePairs(stringUtils.generateRandomStrings(500), 500);
        comparePairs(stringUtils.generateRandomStrings(1000), 1000);
    }
}