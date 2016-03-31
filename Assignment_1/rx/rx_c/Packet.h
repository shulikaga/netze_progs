//
//  Packet.h
//  


#ifndef Packet_h
#define Packet_h

#include <stdio.h>

typedef struct{
    int seqNr;
    long timeSent;
    long timeReceived;
    String message;
}Packet;

Packet *newPacket(PacketList *pl, int seqNr);
void setSeqNr(int sNr);
int getSeqNr();
void setTimeSent(long timeSent);
long getTimeSent();
void setTimeReceived(long timeReceived);
long getTimeReceived();
void setMessage(String message);
char** getMessage();


#endif /* Packet_h */
