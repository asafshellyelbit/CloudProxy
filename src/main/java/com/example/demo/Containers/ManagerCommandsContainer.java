package com.example.demo.Containers;

import com.example.demo.Finals.COMM_CHANNELS;
import com.example.demo.Utilies.ByteArrayWrapper;
import com.example.demo.Utilies.Utilies;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;

public class ManagerCommandsContainer {
    private HashMap<ByteArrayWrapper, List<String>> UUIDMap;
    private static ManagerCommandsContainer instance = null;

    private ManagerCommandsContainer()
    {
        UUIDMap = new HashMap<>();
    }

    public static ManagerCommandsContainer getInstance() {
        if (instance == null) {
            instance = new ManagerCommandsContainer();
        }
        return instance;
    }


    public HashMap<ByteArrayWrapper, List<String>> getUUIDMap() {
        return UUIDMap;
    }

    public void AddManagerCommand(String managerCommand){
        System.out.println(managerCommand + " was received");


        byte[] decodedBytes = Base64.getDecoder().decode(managerCommand);
        int channel = ByteBuffer.wrap(decodedBytes,32,4).getInt();
        if (COMM_CHANNELS._INVALID == channel)
            return;
        else if (COMM_CHANNELS.UNDEFINED == channel)
            return;
        else if (COMM_CHANNELS.READNEXT == channel)
            return;
        else if (COMM_CHANNELS.KEEPALIVE == channel)
            return;
        else if (COMM_CHANNELS.INITIAL == channel)
            return;
        else {
            ByteArrayWrapper uuid = Utilies.ExtractUUIDBytesArrayFromBase64String(managerCommand);
            if (!Utilies.IsItCommandWithNoFile(managerCommand)){
                if (!UUIDMap.containsKey(uuid)){
                    UUIDMap.put(uuid,new ArrayList<>());
                }
                if (!UUIDMap.get(uuid).contains(managerCommand)){
                    UUIDMap.get(uuid).add(managerCommand);
                }
                System.out.println("command was added to the manager commands");
            }
            else {
                System.out.println("this is an empty command, it was not added to the list on server...");
            }
        }
    }

    public String getCommand(ByteArrayWrapper uuid){
        if (UUIDMap.isEmpty()){
            System.out.println("NO commands to return. Empty Struct was sent...");
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

