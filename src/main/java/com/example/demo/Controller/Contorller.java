package com.example.demo.Controller;

import com.example.demo.Containers.ClientResponsesContainer;
import com.example.demo.Containers.KeepAliveContainer;
import com.example.demo.Containers.ManagerCommandsContainer;
import com.example.demo.Utilies.ByteArrayWrapper;
import com.example.demo.Utilies.Utilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
public class Contorller {

    @RequestMapping(path = "/test", method = RequestMethod.POST)
    public String PostRequestAsManager(@RequestBody String base64Command){
        System.out.println("*****\nmanagerPost\n*****");
        ManagerCommandsContainer.getInstance().AddManagerCommand(base64Command);

        ByteArrayWrapper uuid = Utilies.ExtractUUIDBytesArrayFromBase64String(base64Command);
        String clientRespondToBeSentToManager = ClientResponsesContainer.getInstance().getResponse(uuid);
        byte[] decodedBytes = Base64.getDecoder().decode(clientRespondToBeSentToManager);
        int secFromTwoThousand = KeepAliveContainer.getInstance().secondsFrom2000(uuid);
        System.out.println(secFromTwoThousand); // timestamp is seconds since 1/1/2000 midnight
        decodedBytes[19] = (byte)((secFromTwoThousand >> 24) & 0xFF);
        decodedBytes[18] = (byte)((secFromTwoThousand >> 16) & 0xFF);
        decodedBytes[17] = (byte)((secFromTwoThousand >> 8) & 0xFF);
        decodedBytes[16] = (byte)(secFromTwoThousand & 0xFF); // save it to to array
        return Base64.getEncoder().encodeToString(decodedBytes);
    }

    @RequestMapping(path = "/info", method = RequestMethod.POST)
    public String GetRequestAsClient(@RequestBody String base64Response){
        System.out.println("*****\nclientPost\n*****");
        KeepAliveContainer.getInstance().StampTimeStampNow(Utilies.ExtractUUIDBytesArrayFromBase64String(base64Response));
        ClientResponsesContainer.getInstance().AddClientResponse(base64Response);
        return ManagerCommandsContainer.getInstance().getCommand(
                Utilies.ExtractUUIDBytesArrayFromBase64String(base64Response));
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public String PostClean(){
        ManagerCommandsContainer.getInstance().getUUIDMap().clear();
        ClientResponsesContainer.getInstance().getUUIDMap().clear();
        System.out.println("All tables were cleaned");
        return "All tables were cleaned";
    }

    @RequestMapping(path = "/getUUID", method = RequestMethod.GET)
    public String getUUIDs(){
        System.out.println("***********************************\nget UUIDs....\n***********************************");
        return ClientResponsesContainer.getInstance().GetUUIDs();
    }

    @RequestMapping(path = "/getNew", method = RequestMethod.GET)
    public String getNewUUID(){
        System.out.println("***********************************\nget new UUID....\n***********************************");
        return ClientResponsesContainer.getInstance().GetNew();
    }

    @RequestMapping(path = "/getCurrentInfoByUUID", method = RequestMethod.GET)
    public String GetSecSizeChannelTimestamp(@RequestBody String UUID){
        System.out.println("***********************************\nGet UUIDp....\n*" +
                "**********************************");
        if (UUID==null){
            return "Please specify UUID";
        }
        return Utilies.GetAllInfoAboutSpecificUUID(new ByteArrayWrapper(Utilies.hexStringToByteArray(UUID)));
    }
}
