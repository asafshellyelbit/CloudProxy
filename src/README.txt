This doc describes Rest methods of the server.
The structure of every message is byte array in the following format, encoded to base64:

bytes[0-16] = UUID
bytes[16-20] = TimeStmp
bytes[20-24] = SequentialID
bytes[24-28] = LinkedSequentialID
bytes[28-32] = crc32
bytes[32-36] = channel
bytes[36-40] = size
bytes[40-*] = file


***Get Requests***
/getUUID (no inner content)
	returns all the clients UUID available.
	
/getCurrentInfoByUUID (inner content: UUID)
		returns lists of all the available information of the specific UUID.
		

***Post Requests***

/clear
	clear all the containers.

MANAGER SENDS:
/test (inner content: bytes array in Base64 information struct)
	add the command to the commands container.
	The received string will be the oldest response from the client with the same UUID, with the "last seen" time stamp. 
	The response will be removed.
	
	Exceptions:
	* If the size is zero, the command will not be saved, e.g. for only keep alive check. The command will be removed.
	* No content arrived.
	* channel cannot be 0.

CLIENT SENDS:
/info (inner content: bytes array in Base64 information struct)
	add the response to the responses container.
	The received string will be the oldest command from the manager with the same UUID.