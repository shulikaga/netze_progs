//
//  RX.c


#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <stdlib.h>

in_addr_t inet_addr(const char *cp);

int main(int argc, char *argv[]){
    
    if (argc < 1){
        fputs ("usage: RX <number of packets to receive> \n", stderr);
        exit (1);
    }
    
    int numberOfPackets = atoi(argv[1]);
    
    int udpSocket, nBytes;
    char buffer[1024];
    struct sockaddr_in serverAddr, clientAddr;
    struct sockaddr_storage serverStorage;
    socklen_t addr_size, client_addr_size;
    
    PacketList pl = newPacketList();
    
    /*Create UDP socket*/
    udpSocket = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(7891);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Bind socket with address struct*/
    bind(udpSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverStorage;
    
    int i = 0;
    while(i < numberOfPackets){
        /* Try to receive any incoming UDP datagram. Address and port of
         requesting client will be stored on serverStorage variable */
        nBytes = recvfrom(udpSocket,buffer,1024,0,(struct sockaddr *)&serverStorage, &addr_size);
        //long timeReceived =
         printf("Received from server: %s\n",buffer);
        
        Packet packet = newPacket( i, buffer);//,  timeReceived);
      i++;
    }
    return 0;
}