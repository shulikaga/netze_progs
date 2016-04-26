//
//  Receiver.c
//  
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <arpa/inet.h>
#include "Receiver.h"
#include "Packet.h"

in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;


Receiver* newReceiver(int port, int dataSize, PacketList* packets){
    Receiver *receiver = (Receiver *) malloc (sizeof (Receiver));
    receiver->port = port;
    receiver->serverTimeout = 1;
    receiver->pl = packets;
    receiver->dataSize = dataSize;
    return  receiver;
}

void start(Receiver* receiver){
    
    openSocket(receiver);
    
    PacketList* packets = newPacketList();
    
    printf("%s\n","-------SERVER READY-------------");

    
    while(1){
        receivePacket(receiver, packets);
    }
    
}

void openSocket(Receiver* receiver){
    
    /*Create UDP socket*/
   receiver->udpSocket = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(receiver->port);
    
    //serverAddr.sin_addr.s_addr;
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Bind socket with address struct*/
    bind(receiver->udpSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof clientAddr;
}

void receivePacket(Receiver* receiver, PacketList* packets){
    char* client_IP;
      recvfrom(receiver->udpSocket,
                                receiver->buffer,
                                receiver->dataSize,
                                0,
                                (struct sockaddr *)&clientAddr,
                                &addr_size);
    
    // set ip_address from where the DPackets are coming
    client_IP =inet_ntoa(clientAddr.sin_addr);
    printf("client_IP =%s, %c\n ", client_IP, receiver->buffer[10]);
    
    
    addPacket(packets,receiver->buffer);
    
    

}
