package com.example.demo.Containers;

import com.example.demo.Finals.COMM_CHANNELS;
import com.example.demo.Utilies.ByteArrayWrapper;
import com.example.demo.Utilies.Utilies;

import java.nio.ByteBuffer;
import java.util.*;

public class ClientResponsesContainer {
    private HashMap<ByteArrayWrapper, List<String>> UUIDMap;
    private static ClientResponsesContainer instance = null;
    private ByteArrayWrapper LastAdded = null;

    private ClientResponsesContainer()
    {
        UUIDMap = new HashMap<>();
    }

    public String GetUUIDs(){
        if (UUIDMap.isEmpty()){
            return "no UUIDs on server...";
        }
        StringBuilder sb = new StringBuilder();
        for (ByteArrayWrapper bb : UUIDMap.keySet()){
            sb.append(Utilies.BytesToHexString(bb.getBytes())).append("\n");
        }
        return sb.toString();
    }

    public String GetNew(){
        if (LastAdded==null){
            return "";
        }
        return Utilies.BytesToHexString(LastAdded.getBytes());
    }

    public static ClientResponsesContainer getInstance() {
        if (instance == null) {
            instance = new ClientResponsesContainer();
        }
        return instance;
    }

    public void AddClientResponse(String ClientResponse){
        System.out.println(ClientResponse+" was received");

        ByteArrayWrapper uuidd = Utilies.ExtractUUIDBytesArrayFromBase64String(ClientResponse);
        this.LastAdded = uuidd;
        byte[] decodedBytes = Base64.getDecoder().decode(ClientResponse);
        Integer channel = ByteBuffer.wrap(decodedBytes,32,4).getInt();

        if (COMM_CHANNELS._INVALID == channel)
            return;
        else if (COMM_CHANNELS.UNDEFINED == channel)
            return;
        else if (COMM_CHANNELS.READNEXT == channel)
            return;
        else if (COMM_CHANNELS.KEEPALIVE == channel)
            return;
        else if (COMM_CHANNELS.INITIAL == channel){
           return;
        }
        else {
            ByteArrayWrapper uuid = Utilies.ExtractUUIDBytesArrayFromBase64String(ClientResponse);

            System.out.println("the uuid is "+uuid);
            if (!UUIDMap.containsKey(uuid)){
                UUIDMap.put(uuid,new ArrayList<>());
            }
            if (!UUIDMap.get(uuid).contains(ClientResponse)){
                UUIDMap.get(uuid).add(ClientResponse);
            }

            System.out.println("response was added to the client responses");
        }


    }

    public HashMap<ByteArrayWrapper, List<String>> getUUIDMap() {
        return UUIDMap;
    }

    public String getResponse(ByteArrayWrapper uuid){
        if (UUIDMap.isEmpty()){
            System.out.println("No response from client... empty struct returned");
            return Utilies.ReturnEmptyStructInBase64(uuid);
        }
        else if (UUIDMap.containsKey(uuid)) {
            if (!UUIDMap.get(uuid).isEmpty()){
                String retVal = UUIDMap.get(uuid).get(0);
                UUIDMap.get(uuid).remove(0);
                return retVal;
            } else {
                return Utilies.ReturnEmptyStructInBase64(uuid);
            }
        }
        else {
            return Utilies.ReturnEmptyStructInBase64(uuid);
        }
    }
}

