//
//  Transmitter.c
//  Tansmit
//
#include <stdio.h>
#include <stdlib.h>
#include <zlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include "crc32.h"
#include "Transmitter.h"

in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;


Transmitter* newTransmitter(int numberOfPackets, int port, char* ip, int dataSize){
    Transmitter* transmitter = (Transmitter*) malloc(sizeof(Transmitter));
    transmitter->numberOfPackets = numberOfPackets;
    transmitter->port = port;
    transmitter->ip = ip;
    transmitter->dataSize = dataSize;
    
    transmitter->allPackets = (char**)calloc(numberOfPackets+1, sizeof(char*));
    for (int i = 1; i <= numberOfPackets; i++ ){
        transmitter->allPackets[i] = (char*) calloc(dataSize, sizeof(char));
    }
    
    return transmitter;
}

void freeTransmitterArrays(Transmitter* transmitter){
    for ( int i = 1; i <=  transmitter->numberOfPackets; i++ ){
        free(transmitter->allPackets[i]);
    }
    free(transmitter->allPackets);
    free(transmitter);
}

void start(Transmitter* transmitter){
    openSocket(transmitter);
    sendPackets(transmitter);
    
    freeTransmitterArrays(transmitter);
}

void openSocket(Transmitter* transmitter){
    
    /*Create UDP socket*/
    transmitter->client_socket = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(transmitter->port);
    serverAddr.sin_addr.s_addr = inet_addr(transmitter->ip);
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverAddr;

}

void buildPacket(Transmitter* transmitter, char* buffer,int packetNumber){
 //sequentialNr in bytes
 
 get4Bytes(transmitter, buffer, packetNumber, 0);
 buffer[4] = 'M';
 buffer[5] = 'e';
 buffer[6] = 's';
 buffer[7] = 's';
 buffer[8] = 'a';
 buffer[9] = 'g';
 buffer[10]= 'e';
 }

void get4Bytes(Transmitter* transmitter,char* buffer, int packetNumber, int start){
    buffer[start] = (packetNumber >> 24) & 0xFF;     //   the         //
    buffer[start+ 1] = (packetNumber >> 16) & 0xFF;  //   packet      //
    buffer[start + 2] = (packetNumber >> 8) & 0xFF;   //   sequential  //
    buffer[start + 3] = packetNumber & 0xFF;          //   number      //
}



void sendPackets(Transmitter* transmitter){
    int times = 1;
    int packetNr = 1;
    transmitter->nBytes = (transmitter->dataSize);
    while(packetNr <= transmitter->numberOfPackets){
        sendOnePacket(transmitter, packetNr, times,transmitter->numberOfPackets );
        packetNr++;
    }
}

void putCRC32(Transmitter* transmitter,char** allData, int packetNumber, int start){
    char* tmp_byteArray = (char*)calloc(transmitter->dataSize*transmitter->numberOfPackets,sizeof(char));
    int bitCounter = 0;
    for(int i = 1;i<=transmitter->numberOfPackets;i++){
        for(int j = 0;j<transmitter->dataSize;j++){
            tmp_byteArray[bitCounter] = transmitter->allPackets[i][j];
            bitCounter++;
        }
    }
    
    gen_crc_table();
    
    // Compute and output CRC
    int crc32 = update_crc(-1, tmp_byteArray, sizeof(allData));
    
    get4Bytes(transmitter,transmitter->allPackets[transmitter->numberOfPackets], crc32,transmitter->dataSize-4);
    printf("The checksum crc32 to send = %08X =", crc32);
}

void sendOnePacket(Transmitter* transmitter, int packetNr, int times,int numberOfPackets ){
    buildPacket(transmitter,transmitter->allPackets[packetNr], packetNr);
   
    //check for the last paket to send
    if(packetNr == numberOfPackets){
        putCRC32(transmitter, transmitter->allPackets,packetNr,(200-5 ));
        printbinchar(transmitter->allPackets[transmitter->numberOfPackets], transmitter->dataSize-4, transmitter->dataSize);
        printf("\n");
    }
    
    //send
    sendto(transmitter->client_socket,
           transmitter->allPackets[packetNr],
           transmitter->nBytes,0,
           (struct sockaddr *)&serverAddr,
           addr_size);
    
    printData(transmitter->allPackets[packetNr], packetNr);
    
    if(receiveAck(transmitter, packetNr,serverAddr) == 1){
        printf("%s %d \n\n"," - Received the correct Ack for" ,packetNr);
    }else{
        
        if(times<=10){
            printf("%s %d %s %dth time\n\n",
                   "No Ack for ",packetNr," received. Trying again.", times);
            sendOnePacket(transmitter, packetNr, times, numberOfPackets);
            times++;
        }else{
            printf("%s %d %s %dth time\n\n",
                   "No Ack for ",packetNr," received. Stopped sending.", times);

        }
    }

}

int receiveAck(Transmitter* transmitter, int packetNumber, struct sockaddr_in serverAddr){
   
    char ack_buffer[transmitter->dataSize];
    
    recvfrom(transmitter->client_socket,
             ack_buffer,
             transmitter->dataSize,
             0,
             (struct sockaddr *)&serverAddr,
             &addr_size);
    printf(" ");
    printbinchar(ack_buffer,  0,  4);
    messageToString(ack_buffer, 4, 8);
    
    int boolCorrect = 1;
    for(int i = 0;i<4;i++){
        if((transmitter->allPackets[packetNumber])[i]!= ack_buffer[i]){
            boolCorrect = 0;
        }
    }
    return boolCorrect;
    
}


void printData(char* buffer, int seqNr){
    printf("[");
    printbinchar(buffer,0, 4);
    printf(" ");
    messageToString(buffer, 4, 11);
    printf(" ] - packet Nr.%d %s\n",seqNr, "has been sent.");
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

