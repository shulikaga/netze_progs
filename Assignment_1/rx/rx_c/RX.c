//
//  RX.c

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "Receiver.h"
#include "Packet.h"

int main(int argc, char *argv[]){
    
    int port = 7777;
    int dataSize = 4;
    
    if (argc == 3){
        port = atoi(argv[1]);
        dataSize = atoi(argv[2]);
    }
    
    PacketList* packets = newPacketList();
    Receiver* receiver = newReceiver(port, dataSize, packets);
    start(receiver);
   
    return 0;
}
