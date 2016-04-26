//
//  Transmit.c



#include "Transmitter.h"
        
    int main(int argc, char* argv[]){
        
        int numberOfPackets = 10000;
        int port = 7777;
        char* ip = "127.0.0.1";
        int dataSize = 4;
        
        if (argc == 5){
            numberOfPackets = atoi(argv[1]);
            port = atoi(argv[2]);
            ip = argv[3];
            dataSize = atoi(argv[4]);
         }
        
        Transmitter* transmitter = newTransmitter(numberOfPackets, port, ip, dataSize);
        start(transmitter);
        
        return 0;
    }
