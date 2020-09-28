import java.util.ArrayList;

public class Hash {

    public static final String[] constants = {
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

    public static void main(String[] args) {
        String message = stringToBytes("Labas Ka veiki ai astai nieko visai ramiai chilinu namie. Bet va greitai reieks eiit i unika");
        String paddedMessage = padMessage(message);
        ArrayList<ArrayList<String>> messageSchedules = messageBlocks(paddedMessage);
        rotateRight(messageSchedules.get(0).get(0), 10);
        shiftRight(messageSchedules.get(0).get(0), 3);
    }

    //converts String input to bytes by converting each char to int (ASCII)
    private static String stringToBytes(String input) {
        StringBuilder message = new StringBuilder();

        for(int i = 0; i < input.length(); ++i) {
            message.append(String.format("%08d", Integer.parseInt(Integer.toBinaryString(input.charAt(i)))));
        }

        return message.toString();
    }

    //pads message to contain 512 bit chunks
    private static String padMessage(String message){
        int pad = 512 - message.length()%512;
        String messageLengthBinary = Integer.toBinaryString(message.length());
        String repeated = new String(new char[pad-messageLengthBinary.length()]).replace("\0", "0");
        return message + repeated + messageLengthBinary;
    }

    //splits each message block of 512 bits into message schedules of 32 bits
    private static ArrayList<ArrayList<String>> messageBlocks(String paddedMessage){
        ArrayList<ArrayList<String>> messageBlocks = new ArrayList<>();
        int messageBlocksNumber = paddedMessage.length() / 512;

        for(int i = 0; i < messageBlocksNumber; i++){
            String currentBlock = paddedMessage.substring(512 * i, 512 + 512*i);
            ArrayList<String> messageSchedules = new ArrayList<>();
            for(int j = 0; j < 16; j++){
                messageSchedules.add(currentBlock.substring(32*j, 32 + 32*j));
            }
            messageBlocks.add(messageSchedules);
        }

        return messageBlocks;
    }

    //rotates message by given positions
    private static String rotateRight(String message, int position){
        String messageStart = message.substring(0, 32 - position);
        String messageEnd = message.substring(32 - position, 32);
        return messageEnd + messageStart;
    }

    //shifts message by given positions
    private static String shiftRight(String message, int position){
        String substringMessage = message.substring(0, 32 - position);
        String repeated = new String(new char[position]).replace("\0", "0");
        return repeated + substringMessage;
    }


}
