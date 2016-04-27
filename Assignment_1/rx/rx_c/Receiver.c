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
#include "crc32.h"

in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;


Receiver* newReceiver(int port, int dataSize){
    Receiver *receiver = (Receiver *) malloc (sizeof (Receiver));
    receiver->port = port;
    receiver->serverTimeout = 1;
    receiver->dataSize = dataSize;
    receiver->allocated = 10;
    receiver->counter = 1;
    receiver->allPackets = (char **) calloc (receiver->allocated, sizeof (char *));
    for (int i = 1; i <  receiver->allocated; i++ ){
        receiver->allPackets[i] = (char*) calloc(dataSize, sizeof(char));
    }

    return  receiver;
}

void allPacketsDoubleSize(Receiver* receiver){
    receiver->allocated *= 2;
    receiver->allPackets = (char **) realloc (receiver->allPackets, receiver->allocated * sizeof (char *));
    receiver->counter++;
}

void freeReceiverArrays(Receiver* receiver){
        for ( int i = 1; i <=  receiver->counter; i++ ){
            free(receiver->allPackets[i]);
        }
        free(receiver->allPackets);
        free(receiver);
    
}


void start(Receiver* receiver){
    
    openSocket(receiver);
    
    printf("%s\n","-------SERVER READY-------------");

    
    while(1){
        receivePacket(receiver);
    }
    
    freeReceiverArrays(receiver);
    
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

void receivePacket(Receiver* receiver){
    //change the size of the array
    if(receiver->counter==receiver->allocated){
        allPacketsDoubleSize(receiver);
    }
    
    char* client_IP;
    
    //receive the packet
    recvfrom(receiver->udpSocket,
               receiver->allPackets[receiver->counter],
               receiver->dataSize,
               0,
               (struct sockaddr *)&clientAddr,
               &addr_size);
 
    client_IP = inet_ntoa(clientAddr.sin_addr);
    printf("Received a packet from the IP = %s \n ", client_IP);
    printData(receiver->allPackets[receiver->counter]);
    
    if( (receiver->allPackets[receiver->counter])[receiver->dataSize-4]!='\0'){
    
        checkCRC32(receiver,receiver->allPackets[receiver->counter], receiver->counter, receiver->dataSize-4);
    }
    
    //answer back to the client
    sendto(receiver->udpSocket,
           receiver->allPackets[receiver->counter],
           receiver->dataSize,
           0,
           (struct sockaddr *)&clientAddr,
           addr_size);
}


void checkCRC32(Receiver* receiver,char* receivedLastPacket, int packetNumber, int start){
    
    char* tmp_byteArray = (char*)calloc(receiver->dataSize*receiver->counter,sizeof(char));
    int bitCounter = 0;
    for(int i = 1;i<receiver->counter;i++){
        for(int j = 0;j<receiver->dataSize;j++){
            tmp_byteArray[bitCounter] = receiver->allPackets[i][j];
            bitCounter++;
        }
    }
    tmp_byteArray[bitCounter-4] = '\0';
    tmp_byteArray[bitCounter-3] = '\0';
    tmp_byteArray[bitCounter-2] = '\0';
    tmp_byteArray[bitCounter-1] = '\0';
    
    gen_crc_table();
    
    // Compute and output CRC
    int crc32 = update_crc(-1, tmp_byteArray, sizeof(receivedLastPacket));
    
    char* checksum_array = (char*)calloc(4,sizeof(char));
    get4Bytes(receiver,checksum_array, crc32,0);
    printf("The checksum crc32 calculated in Receiver = %08X =", crc32);
    printbinchar(checksum_array,0,4);
    printf("\n");
    
    int boolCRC32 = 1;
    for(int i = 0;i<4;i++){
        if((receiver->allPackets[receiver->counter])[receiver->dataSize-4+i]!= checksum_array[i]){
            boolCRC32 = 0;
        }
    }
    
    if(boolCRC32==1){
        printf("The checksums are identical!");
    }else{
        printf("Tha checksums are not identicel!");
    }
    free(tmp_byteArray);
    free(checksum_array);
}

void get4Bytes(Receiver* receiver, char* buffer, int packetNumber, int start){
    buffer[start] = (packetNumber >> 24) & 0xFF;
    buffer[start + 1] = (packetNumber >> 16) & 0xFF;
    buffer[start + 2] = (packetNumber >> 8) & 0xFF;
    buffer[start + 3] = packetNumber & 0xFF;
}

void printData(char* buffer){
    printf("[");
    printbinchar(buffer,0, 4);
    printf(" ");
    messageToString(buffer, 4, 11);
    printf("\n");
    printf("\n");

}

void messageToString(char* buffer, int start, int end){
    int i;
    for(i=start;i<end;i++){
        printf("%c",buffer[i]);
    }
}

void printbinchar(char* buffer, int start, int end){
    int i;
    for(i=start;i<end;i++){
        printf(" ");
        char c = buffer[i];
        for(int j = 7; 0 <= j; j --){
            printf("%d", (c >> j) & 0x01);
        }
        
        
    }
}
