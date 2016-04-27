//
//  RX.c

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "Receiver.h"

struct timeval startTimeStamp,stopTimeStamp;

int main(int argc, char *argv[]){
    
    int port = 7777;
    int dataSize = 4;
    
    if (argc == 2){
        port = atoi(argv[0]);
        dataSize = atoi(argv[1]);
    }
    
    Receiver* receiver = newReceiver(port, dataSize);
    
   // gettimeofday(&startTimeStamp, NULL);
    start(receiver);
   // gettimeofday(&stopTimeStamp, NULL);
   // printf("Duration in ms: %f\n",((stopTimeStamp.tv_sec - startTimeStamp.tv_sec) * 1000.0f + (stopTimeStamp.tv_usec - startTimeStamp.tv_usec) / 1000.0f));
   
    return 0;
}
