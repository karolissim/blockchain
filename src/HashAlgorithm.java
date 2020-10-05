import java.math.BigInteger;
import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class HashAlgorithm {

    private static final String[] constants = {
            ("01000010100010100010111110011000"), ("01110001001101110100010010010001"), ("10110101110000001111101111001111"),
            ("11101001101101011101101110100101"), ("00111001010101101100001001011011"), ("01011001111100010001000111110001"),
            ("10010010001111111000001010100100"), ("10101011000111000101111011010101"), ("11011000000001111010101010011000"),
            ("00010010100000110101101100000001"), ("00100100001100011000010110111110"), ("01010101000011000111110111000011"),
            ("01110010101111100101110101110100"), ("10000000110111101011000111111110"), ("10011011110111000000011010100111"),
            ("11000001100110111111000101110100"), ("11100100100110110110100111000001"), ("11101111101111100100011110000110"),
            ("00001111110000011001110111000110"), ("00100100000011001010000111001100"), ("00101101111010010010110001101111"),
            ("01001010011101001000010010101010"), ("01011100101100001010100111011100"), ("01110110111110011000100011011010"),
            ("10011000001111100101000101010010"), ("10101000001100011100011001101101"), ("10110000000000110010011111001000"),
            ("10111111010110010111111111000111"), ("11000110111000000000101111110011"), ("11010101101001111001000101000111"),
            ("00000110110010100110001101010001"), ("00010100001010010010100101100111"), ("00100111101101110000101010000101"),
            ("00101110000110110010000100111000"), ("01001101001011000110110111111100"), ("01010011001110000000110100010011"),
            ("01100101000010100111001101010100"), ("01110110011010100000101010111011"), ("10000001110000101100100100101110"),
            ("10010010011100100010110010000101"), ("10100010101111111110100010100001"), ("10101000000110100110011001001011"),
            ("11000010010010111000101101110000"), ("11000111011011000101000110100011"), ("11010001100100101110100000011001"),
            ("11010110100110010000011000100100"), ("11110100000011100011010110000101"), ("00010000011010101010000001110000"),
            ("00011001101001001100000100010110"), ("00011110001101110110110000001000"), ("00100111010010000111011101001100"),
            ("00110100101100001011110010110101"), ("00111001000111000000110010110011"), ("01001110110110001010101001001010"),
            ("01011011100111001100101001001111"), ("01101000001011100110111111110011"), ("01110100100011111000001011101110"),
            ("01111000101001010110001101101111"), ("10000100110010000111100000010100"), ("10001100110001110000001000001000"),
            ("10010000101111101111111111111010"), ("10100100010100000110110011101011"), ("10111110111110011010001111110111"),
            ("11000110011100010111100011110010")
    };

    private static final String[] startValues = {
            ("01101010000010011110011001100111"), ("10111011011001111010111010000101"), ("00111100011011101111001101110010"),
            ("10100101010011111111010100111010"), ("01010001000011100101001001111111"), ("10011011000001010110100010001100"),
            ("00011111100000111101100110101011"), ("01011011111000001100110100011001")
    };

    private static String[] initialValues = {
            ("01101010000010011110011001100111"), ("10111011011001111010111010000101"), ("00111100011011101111001101110010"),
            ("10100101010011111111010100111010"), ("01010001000011100101001001111111"), ("10011011000001010110100010001100"),
            ("00011111100000111101100110101011"), ("01011011111000001100110100011001")
    };

    private static String[] compressionValues = {
            ("01101010000010011110011001100111"), ("10111011011001111010111010000101"), ("00111100011011101111001101110010"),
            ("10100101010011111111010100111010"), ("01010001000011100101001001111111"), ("10011011000001010110100010001100"),
            ("00011111100000111101100110101011"), ("01011011111000001100110100011001")
    };

    public String hashFunction(String input){
        String message = stringToBytes(input);
        String paddedMessage = padMessage(message);
        ArrayList<ArrayList<String>> messageSchedules = messageBlocks(paddedMessage);
        compression(messageSchedules);
        String hash = hash(compressionValues);
        for(int i = 0; i < startValues.length; i++){
            compressionValues[i] = startValues[i];
            initialValues[i] = startValues[i];
        }
        return hash;
    }


    private String stringToBytes(String input) {
        StringBuilder message = new StringBuilder();

        for(int i = 0; i < input.length(); ++i) {
            String binaryString = new BigInteger((int) input.charAt(i) + "").toString(2);
            message.append(("00000000" + binaryString).substring(binaryString.length()));
        }

        return message.toString();
    }


    private String padMessage(String message){
        int pad = 512 - message.length()%512;
        if(pad >= 64){
            String messageLengthBinary = Integer.toBinaryString(message.length());
            String repeated = new String(new char[pad - 1 - messageLengthBinary.length()]).replace("\0", "0");
            return message + "1" + repeated + messageLengthBinary;
        } else {
            String messageLengthBinary = Integer.toBinaryString(message.length());
            String repeated = new String(new char[pad + 511 - messageLengthBinary.length()]).replace("\0", "0");
            return message + "1" + repeated + messageLengthBinary;
        }
    }


    private ArrayList<ArrayList<String>> messageBlocks(String paddedMessage){
        ArrayList<ArrayList<String>> messageBlocks = new ArrayList<>();
        int messageBlocksNumber = paddedMessage.length() / 512;

        for(int i = 0; i < messageBlocksNumber; i++){
            String currentBlock = paddedMessage.substring(512 * i, 512 + 512*i);
            ArrayList<String> messageSchedules = new ArrayList<>();
            int j;
            for(j = 0; j < 16; j++){
                messageSchedules.add(currentBlock.substring(32*j, 32 + 32*j));
            }
            while(messageSchedules.size() < 64){
                String newMessage = sum(sigma1(messageSchedules.get(j-2)), messageSchedules.get(j-7), sigma0(messageSchedules.get(j-15)), messageSchedules.get(j-16));
                messageSchedules.add(newMessage);
                j++;
            }
            messageBlocks.add(messageSchedules);
        }
        return messageBlocks;
    }


    private String rotateRight(String message, int position){
        String messageStart = message.substring(0, 32 - position);
        String messageEnd = message.substring(32 - position, 32);
        return messageEnd + messageStart;
    }


    private String shiftRight(String message, int position){
        String substringMessage = message.substring(0, 32 - position);
        String repeated = new String(new char[position]).replace("\0", "0");
        return repeated + substringMessage;
    }


    private String sum(String binary1, String binary2, String binary3, String binary4, String binary5){
        BigInteger dec1 = new BigInteger(binary1, 2);
        BigInteger dec2 = new BigInteger(binary2, 2);
        BigInteger dec3 = new BigInteger(binary3, 2);
        BigInteger dec4 = new BigInteger(binary4, 2);
        BigInteger dec5 = new BigInteger(binary5, 2);

        BigInteger sum = dec1.add(dec2).add(dec3).add(dec4).add(dec5);
        String countResult = sum.mod(new BigInteger("4294967296")).toString(2);
        String repeated = new String(new char[32 - countResult.length()]).replace("\0", "0");

        return repeated + countResult;
    }


    private String sum(String binary1, String binary2, String binary3, String binary4){
        BigInteger dec1 = new BigInteger(binary1, 2);
        BigInteger dec2 = new BigInteger(binary2, 2);
        BigInteger dec3 = new BigInteger(binary3, 2);
        BigInteger dec4 = new BigInteger(binary4, 2);

        BigInteger sum = dec1.add(dec2).add(dec3).add(dec4);
        String countResult = sum.mod(new BigInteger("4294967296")).toString(2);
        String repeated = new String(new char[32 - countResult.length()]).replace("\0", "0");

        return repeated + countResult;
    }


    private String sum(String binary1, String binary2){
        BigInteger dec1 = new BigInteger(binary1, 2);
        BigInteger dec2 = new BigInteger(binary2, 2);

        BigInteger sum = dec1.add(dec2);
        String countResult = sum.mod(new BigInteger("4294967296")).toString(2);
        String repeated = new String(new char[32 - countResult.length()]).replace("\0", "0");

        return repeated + countResult;
    }


    private String xor(String binary1, String binary2, String binary3){
        StringBuilder newBinary = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i < binary1.length(); i++){
            temp.append(binary1.charAt(i)^binary2.charAt(i));
            newBinary.append(temp.charAt(i)^binary3.charAt(i));
        }
        return newBinary.toString();
    }


    private String sigma0(String input){
        String firstRotation = rotateRight(input, 7);
        String secondRotation = rotateRight(input, 18);
        String shift = shiftRight(input, 3);
        return xor(firstRotation, secondRotation, shift);
    }


    private String sigma1(String input){
        String firstRotation = rotateRight(input, 17);
        String secondRotation = rotateRight(input, 19);
        String shift = shiftRight(input, 10);
        return xor(firstRotation, secondRotation, shift);
    }


    private String usigma0(String input){
        String firstRotation = rotateRight(input, 2);
        String secondRotation = rotateRight(input, 13);
        String thirdRotation = rotateRight(input, 22);
        return xor(firstRotation, secondRotation, thirdRotation);
    }


    private String usigma1(String input){
        String firstRotation = rotateRight(input, 6);
        String secondRotation = rotateRight(input, 11);
        String thirdRotation = rotateRight(input, 25);
        return xor(firstRotation, secondRotation, thirdRotation);
    }


    private String choice(String binary1, String binary2, String binary3){
        StringBuilder newBinaryValue = new StringBuilder();
        for(int i = 0; i < binary1.length(); i++){
            if(binary1.charAt(i) == '0')
                newBinaryValue.append(binary3.charAt(i));
            else
                newBinaryValue.append(binary2.charAt(i));
        }
        return newBinaryValue.toString();
    }


    private String majority(String binary1, String binary2, String binary3){
        StringBuilder newBinaryValue = new StringBuilder();
        for(int i = 0; i < binary1.length(); i++){
            int amountOfZeroes = 0;
            int amountOfOnes = 0;
            if(binary1.charAt(i) == '0') amountOfZeroes++;
            else amountOfOnes++;
            if(binary2.charAt(i) == '0') amountOfZeroes++;
            else amountOfOnes++;
            if(binary3.charAt(i) == '0') amountOfZeroes++;
            else amountOfOnes++;
            if(amountOfZeroes > amountOfOnes) newBinaryValue.append("0");
            else newBinaryValue.append("1");
        }
        return newBinaryValue.toString();
    }


    private void compression(ArrayList<ArrayList<String>> messageSchedules){
        for(ArrayList<String> messageSchedule : messageSchedules){
            int i = 0;
            for(String message : messageSchedule){
                String temp1 = sum(usigma1(compressionValues[4]), choice(compressionValues[4], compressionValues[5], compressionValues[6]), compressionValues[7], message, constants[i]);
                String temp2 = sum(usigma0(compressionValues[0]), majority(compressionValues[0], compressionValues[1], compressionValues[2]));

                System.arraycopy(compressionValues, 0, compressionValues, 1, compressionValues.length - 1);
                compressionValues[0] = sum(temp1, temp2);
                compressionValues[4] = sum(compressionValues[4], temp1);
                i++;
            }
            for(int j = 0; j < compressionValues.length; j++){
                compressionValues[j] = sum(initialValues[j], compressionValues[j]);
            }
            System.arraycopy(compressionValues, 0, initialValues, 0, initialValues.length);
        }
    }


    private String hash(String[] compressionValues){
        StringBuilder hash = new StringBuilder();
        for(String compressionValue : compressionValues){
            String hexStr = new BigInteger(compressionValue, 2).toString(16);
            hash.append(("00000000" + hexStr).substring(hexStr.length()));
        }
        return hash.toString();
    }
}
