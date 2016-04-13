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
int clientSocket, PORT, nBytes;
int BUFFER_SIZE = 1024;
char buffer[BUFFER_SIZE];

int SEQ_NR_BYTE_LENGTH = 4;
int BLOCK_SIZE;
int PACKET_SIZE;
int NUMBER_OF_PACKETS;



int getNumberOfPackets(int number){
    if(number!=0){
        return number;
    }else{
        fputs("Usage: Transmit_C <number of packets to be sent>", stderr);
        exit(1);
    }
    
}

void setSocketAddress(){
    /*Create UDP socket*/
    clientSocket = socket(PF_INET, SOCK_DGRAM, 0);
    
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
    buffer[0] = (packetNumber >> 24) & 0xFF;
    buffer[1] = (packetNumber >> 16) & 0xFF;
    buffer[2] = (packetNumber >> 8) & 0xFF;
    buffer[3] = packetNumber & 0xFF;
    buffer[4] = 'M';
    buffer[5] = 'e';
    buffer[6] = 's';
    buffer[7] = 's';
    buffer[8] = 'a';
    buffer[9] = 'g';
    buffer[10] = 'e';
    
}

void sendAllPackets(int numberOfPackets){
    int *booleanBitMapReceived[BLOCK_SIZE];
    int blockNumber = 0;
    int numberOfBlocks = (int)ceil(NUMBER_OF_PACKETS/(double)BLOCK_SIZE);
    int cycle = 1;
    int boolPacketsComplete = 1;
    
    while(blockNumber < numberOfBlocks){
        if (boolPacketsComplete){
            printf("%s%d%s","-----------------block ",blockNumber," ---------------------");
        }
        printf("%d%s",cycle,": ");
        
        sendPacketsOfBlock(clientSocket, booleanBitMapReceived, blockNumber);
        
        booleanBitMapReceived = receiveBitmap(clientSocket);
        boolPacketsComplete = checkIfPacketsComplete(clientSocket, bitMapReceived, numberOfBlocks, blockNumber);
        
        if (boolPacketsComplete) {
            cycle = 1;
            blockNumber++;
            ?????booleanBitMapReceived = new boolean[BLOCK_SIZE];
        }
        else {
            cycle++;
        }
    }
    
    }

}


int[] receiveBitmap(int socket){
    nBytes = recvfrom(udpSocket,buffer,128,0,(struct sockaddr *)&serverStorage, &addr_size);
    return toBooleanArray(incomingDPacket.getData());
}

void sendPacketsOfBlock(int clientSocket, int *booleanBitMapReceived, int blockNumber){
    int packetNumber = blockNumber * BLOCK_SIZE;
    int startingPacketNumber = packetNumber;
    int nmbOfSentPackets = 0;
    
        while (packetNumber < startingPacketNumber + BLOCK_SIZE && packetNumber < NUMBER_OF_PACKETS) {
            if (booleanBitMapReceived[packetNumber % BLOCK_SIZE]!=1) {
                buildPacket(packetNumber);
                sendto(clientSocket,buffer,nBytes,0,(struct sockaddr *)&serverAddr,addr_size);
                nmbOfSentPackets++;
            }
            packetNumber++;
        }
    printf("%d \n %s",nmbOfSentPackets, " datagrams have been (re)sent.");
    
    
}



int main(int argc, char *argv[]){
    
    NUMBER_OF_PACKETS = getNumberOfPackets(atoi(argv[1]));
    BLOCK_SIZE = atoi(argv[2]);
    PORT = atoi(argv[3]);
    PACKET_SIZE = atoi(argv[4]);
    
    setSocketAddress();
    sendAllPackets(NUMBER_OF_PACKETS);
    close(clientSocket);

    
    return 0;
}