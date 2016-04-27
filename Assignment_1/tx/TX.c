//
//  Transmit.c

#include <sys/time.h>
#include "Transmitter.h"

struct timeval startTimeStamp,stopTimeStamp;

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
        
        gettimeofday(&startTimeStamp, NULL);
        start(transmitter);
        gettimeofday(&stopTimeStamp, NULL);
        printf("Duration in ms: %f\n",(
               (stopTimeStamp.tv_sec - startTimeStamp.tv_sec) * 1000.0f + (stopTimeStamp.tv_usec - startTimeStamp.tv_usec) / 1000.0f));

        return 0;
    }
