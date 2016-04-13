//
//  RX.c


#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <stdlib.h>
#include<time.h> 

in_addr_t inet_addr(const char *cp);
int BLOCK_SIZE;
int SERVER_TIMEOUT = 1;
int PORT;
int UDP_SOCKET, nBytes;
char BUFFER[1024];
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;



void openSocket(){
    /*Create UDP socket*/
    UDP_SOCKET = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(7891);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Bind socket with address struct*/
    bind(UDP_SOCKET, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverStorage;
}

receivePackets(){
    int booleanBitMapReceived[BLOCK_SIZE];
    printf("------------------------------------------\n");
    printf("Server is waiting for first datagram...\n");
    printf("------------------------------------------\n");
    
    nBytes = recvfrom(UDP_SOCKET,BUFFER,1024,0,(struct sockaddr *)&serverStorage, &addr_size);
    int countReceived = 0;
    
    long timeReceived = System.currentTimeMillis();
    long timeFirstReceived = timeReceived;
    long timeLastReceived = timeReceived;
    
    
    //int i = 0;
    //while(i < numberOfPackets){
        /* Try to receive any incoming UDP datagram. Address and port of
         requesting client will be stored on serverStorage variable */
      //  nBytes = recvfrom(udpSocket,buffer,1024,0,(struct sockaddr *)&serverStorage, &addr_size);
        //long timeReceived =
      //  printf("Received from server: %s\n",buffer);
        
      //  Packet packet = newPacket( i, buffer);//,  timeReceived);
       // i++;
    //}

}

void main(int argc, char *argv[]){
    
    if (argc < 2){
        fputs ("usage: RX <number of packets to receive> \n", stderr);
        exit (1);
    }
    
    BLOCK_SIZE = atoi(argv[1]);
    PORT = atoi(argv[2]);
    //Map<Integer, byte[]> packets = new HashMap <Integer,byte[]>();
    
    openSocket();
    receivePackets(packets);
    close(UDP_SOCKET);
    return 0;
}
