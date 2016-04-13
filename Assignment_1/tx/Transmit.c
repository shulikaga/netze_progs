//
//  Transmit.c


#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

//global variables
in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr;
socklen_t addr_size;
int CLIENT_SOCKET, PORT, NBYTES;
int BUFFER_SIZE = 1024;
char BUFFER[1024];
int SEQ_NR_BYTE_LENGTH = 4;
int BLOCK_SIZE;
int PACKET_SIZE;
int NUMBER_OF_PACKETS;


void openSocket(){
    /*Create UDP socket*/
    CLIENT_SOCKET = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(4712);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverAddr;
}

void buildPacket(int packetNumber){
   
    //sequentialNr in bytes
    BUFFER[0] = (packetNumber >> 24) & 0xFF;
    BUFFER[1] = (packetNumber >> 16) & 0xFF;
    BUFFER[2] = (packetNumber >> 8) & 0xFF;
    BUFFER[3] = packetNumber & 0xFF;
    BUFFER[4] = 'M';
    BUFFER[5] = 'e';
    BUFFER[6] = 's';
    BUFFER[7] = 's';
    BUFFER[8] = 'a';
    BUFFER[9] = 'g';
    BUFFER[10] = 'e';
    
}

void sendPacketsOfBlock(int clientSocket, int *booleanBitMapReceived, int blockNumber){
    int packetNumber = blockNumber * BLOCK_SIZE;
    int startingPacketNumber = packetNumber;
    int nmbOfSentPackets = 0;
    
    while (packetNumber < startingPacketNumber + BLOCK_SIZE && packetNumber < NUMBER_OF_PACKETS) {
        if (booleanBitMapReceived[packetNumber % BLOCK_SIZE]!=1) {
            buildPacket(packetNumber);
            sendto(clientSocket,BUFFER,NBYTES,0,(struct sockaddr *)&serverAddr,addr_size);
            nmbOfSentPackets++;
        }
        packetNumber++;
    }
    printf("%d \n %s",nmbOfSentPackets, " datagrams have been (re)sent.");
    
    
}


void sendAllPackets(int numberOfPackets){
    int *booleanBitMapReceived[BLOCK_SIZE];
    int blockNumber = 0;
    int numberOfBlocks = (int)ceil(NUMBER_OF_PACKETS/(double)BLOCK_SIZE);
    int cycle = 1;
    int boolPacketsComplete = 1;
    
    while(blockNumber < numberOfBlocks){
        if (boolPacketsComplete){
            printf("%s%d%s","-----------------block ",blockNumber," ---------------------\n");
        }
        printf("%d%s",cycle,": ");
        
        sendPacketsOfBlock(CLIENT_SOCKET, booleanBitMapReceived, blockNumber);
        
       /* booleanBitMapReceived = receiveBitmap(CLIENT_SOCKET);
        boolPacketsComplete = checkIfPacketsComplete(CLIENT_SOCKET, bitMapReceived, numberOfBlocks, blockNumber);
        
        if (boolPacketsComplete) {
            cycle = 1;
            blockNumber++;
            ?????booleanBitMapReceived = new boolean[BLOCK_SIZE];
        }
        else {
            cycle++;
        }*/
    
    }

}
        
    int main(int argc, char *argv[]){
        
        if(argc < 4){
            fputs("Usage: Transmit_C <number of packets> <block size> <port> <packet size>", stderr);
            exit(1);
        }
        
        NUMBER_OF_PACKETS = atoi(argv[1]);
        BLOCK_SIZE = atoi(argv[2]);
        PORT = atoi(argv[3]);
        PACKET_SIZE = atoi(argv[4]);
        
        openSocket();
        sendAllPackets(NUMBER_OF_PACKETS);
        close(CLIENT_SOCKET);
        
        return 0;
    }
