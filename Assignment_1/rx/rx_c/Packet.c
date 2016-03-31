//
//  Packet.c
//  



#ifndef Packet_h
#define Packet_h
#include <stdlib.h>

#include <stdlib.h>
#include <stdlib.h>
#include <stdio.h>

typedef struct{
    byte ** data;
    int receivedSeqNr;
    long timeSent;
    long timeReceived;
    String message;
}Packet;

Packet *newPacket(int receivedSeqNr, byte **barray);//, long timeReceived);
void setReceivedSeqNr(int sNr);
int getReceivedSeqNr();
void setTimeSent(long timeSent);
long getTimeSent();
void setTimeReceived(long timeReceived);
long getTimeReceived();
void setMessage(String message);
char** getMessage();


#endif /* Packet_h */


Packet *newPacket(int receivedSeqNr, byte **barray){//, long timeReceived){
    Packet *p = (Packet *) malloc (sizeof (Packet));
    data = barray;
    setReceivedSeqNr(receivedSeqNr);
    setTimeReceived(timeReceived);
    return p;
}


void setReceivedSeqNr(int sNr){
    receivedSeqNr = sNr;
}

int getReceivedSeqNr(){
    return receivedSeqNr;
}

 long getTimeSent() {
    return timeSent;
}


 void setTimeSent(long timeSent) {
    this.timeSent = timeSent;
}



 long getTimeReceived() {
    return timeReceived;
}



 void setTimeReceived(long timeReceived) {
    this.timeReceived = timeReceived;
}


 chaar** getMessage() {
    return message;
}



 void setMessage(String message) {
    this.message = message;
}
