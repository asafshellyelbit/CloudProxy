package com.example.demo.Containers;
import com.example.demo.Utilies.ByteArrayWrapper;

import java.sql.Timestamp;
import java.util.HashMap;

public class KeepAliveContainer {
    private HashMap<ByteArrayWrapper, Timestamp> TimestampMap;
    private static KeepAliveContainer instance = null;

    public KeepAliveContainer() {
        TimestampMap = new HashMap<>();
    }

    public static KeepAliveContainer getInstance() {
        if (instance == null) {
            instance = new KeepAliveContainer();
        }
        return instance;
    }


    public void StampTimeStampNow(ByteArrayWrapper uuid){
        TimestampMap.put(uuid,new Timestamp(System.currentTimeMillis()));
        System.out.println("new keep alive saved for UUID: "+uuid);
    }

    public int secondsFrom2000(ByteArrayWrapper uuid){
        Timestamp twoThousand = java.sql.Timestamp.valueOf("2000-01-01 00:00:00.0");
        if (!TimestampMap.containsKey(uuid)){
            System.out.println("no 'last keep alive' on server for this uuid. 0 returned");
            return 0;
        }
        long difference = TimestampMap.get(uuid).getTime() - twoThousand.getTime();
        if (difference==0){
            System.out.println("keep alive problem. no difference");
            return 0;
        }
        difference/=1000;
        return (int)difference;
    }
}
