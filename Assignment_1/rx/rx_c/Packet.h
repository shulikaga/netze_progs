//
//  Packet.h
//  


#ifndef Packet_h
#define Packet_h

#include <stdio.h>

typedef struct{
    int seqNr;
    char *byteArray;
}Packet;

typedef struct {
    Packet **packets;
    int count;
    int allocated;
}PacketList;

PacketList *newPacketList();
void addPacket(PacketList *pl, char* buffer);

#endif /* Packet_h */
