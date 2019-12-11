package com.example.demo.Utilies;

import com.example.demo.Containers.ClientResponsesContainer;
import com.example.demo.Containers.KeepAliveContainer;
import com.example.demo.Containers.ManagerCommandsContainer;
import com.example.demo.Finals.Finals;

import java.nio.ByteBuffer;
import java.util.*;

public class Utilies implements Finals {
    public static ByteArrayWrapper ExtractUUIDBytesArrayFromBase64String(String base64String){
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        byte[] uuidBytes = new byte[UUID_SIZE];
        System.arraycopy(decodedBytes,0,uuidBytes,0,UUID_SIZE);
//        String s = new String(uuidBytes);
//        UUID uuid = UUID.fromString(s);
//        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
        return new ByteArrayWrapper(uuidBytes);
    }

    public static boolean IsItCommandWithNoFile(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        byte[] size = new byte[4];
        System.arraycopy(decodedBytes,36,size,0,4);
        return (0  == ByteBuffer.wrap(size).getInt());  // check if SIZE is 0
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String BytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String BuildAStructForSending(ByteArrayWrapper uuid, String HexTimeStamp, String HexSequentialID, String HexLinkedSequentialID,
                                                String HexCRC32, String HexChannel, String HexSize, byte[] fileBuff){
        Integer sizeOfFile = Integer.parseInt(HexSize, 16);
        byte[] ret = new byte[48+sizeOfFile];
        // UUID
        System.arraycopy(uuid.getBytes(),0,ret,0,UUID_SIZE);

        //TimeStamp
        System.arraycopy(hexStringToByteArray(HexTimeStamp),0,ret,16,4);
        // SequentialID
        System.arraycopy(hexStringToByteArray(HexSequentialID),0,ret,20,4);
        // LinkedSequentialID
        System.arraycopy(hexStringToByteArray(HexLinkedSequentialID),0,ret,24,4);
        // HexCRC32
        System.arraycopy(hexStringToByteArray(HexCRC32),0,ret,28,4);
        // Channel
        System.arraycopy(hexStringToByteArray(HexChannel),0,ret,32,4);
        // Size
        System.arraycopy(hexStringToByteArray(HexSize),0,ret,36,4);
        // file
        if (sizeOfFile!=0){
            System.arraycopy(fileBuff,0,ret,40,sizeOfFile);
        }
        return Base64.getEncoder().encodeToString(ret);
    }

    public static String ReturnEmptyStructInBase64(ByteArrayWrapper uuid){
        String zeroHex = "00000000";
        return BuildAStructForSending(uuid,zeroHex,zeroHex,zeroHex,zeroHex,zeroHex,zeroHex,null);
    }

    public static String GetAllInfoAboutSpecificUUID(ByteArrayWrapper uuid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("manager commands of specific UUID:\n");
        if (ManagerCommandsContainer.getInstance().getUUIDMap().containsKey(uuid)){
            stringBuilder.append(FromListToRellevanceInfo(ManagerCommandsContainer.getInstance().getUUIDMap().get(uuid)));
        } else {
            stringBuilder.append("none\n");
        }

        stringBuilder.append("client responses of specific UUID:\n");

        if (ClientResponsesContainer.getInstance().getUUIDMap().containsKey(uuid)){
            stringBuilder.append(FromListToRellevanceInfo(ClientResponsesContainer.getInstance().getUUIDMap().get(uuid)));
        }
        else {
            stringBuilder.append("none\n");
        }
        return stringBuilder.toString();
    }

    public static String FromListToRellevanceInfo(List<String> list){
        List<String> cpy = new ArrayList<>(list);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("list contains "+cpy.size()+" items:\n\n");
        for (String item:cpy){
            stringBuilder.append(Utilies.CutTheCommand(item)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String CutTheCommand(String command){
        StringBuilder stringBuilder = new StringBuilder();
        byte[] decodedBytes = Base64.getDecoder().decode(command);
        stringBuilder.append("seq ID is: "+Integer.toHexString(ByteBuffer.wrap(decodedBytes,20,4).getInt())).append("\n");
        stringBuilder.append("seq LinkedSequentialID is: "+Integer.toHexString(ByteBuffer.wrap(decodedBytes,24,4).getInt())).append("\n");
        stringBuilder.append("seq CRC32 is: "+Integer.toHexString(ByteBuffer.wrap(decodedBytes,28,4).getInt())).append("\n");
        stringBuilder.append("size is: "+Integer.toHexString(ByteBuffer.wrap(decodedBytes,36,4).getInt())).append("\n");
        stringBuilder.append("channel is: "+Integer.toHexString(ByteBuffer.wrap(decodedBytes,32,4).getInt())).append("\n");
        stringBuilder.append("timestamp is: "+ KeepAliveContainer.getInstance().secondsFrom2000(ExtractUUIDBytesArrayFromBase64String(command))).append("\n");
        return stringBuilder.toString();
    }

    public static int GetCRC(byte[] buffer) {
        int CRC = 0xA983F419;
        for (int i=0;i<buffer.length;i++) {
            CRC ^= buffer[i];
            CRC = CRC ^ (CRC << 5) ^ (CRC << 9) ^ (CRC << 14) ^ (CRC << 21);
        }
        return (CRC);
    }
}
