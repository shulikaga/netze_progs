//
//  Packet.c
//  


#include <stdio.h>
#include <stdlib.h>
#include "Packet.h"


PacketList *newPacketList(){
    PacketList *pl = (PacketList *) malloc (sizeof (PacketList));
    pl->count = 0;
    pl->allocated = 10;
    pl->packets = (Packet **) calloc (pl->allocated, sizeof (Packet *));
    return pl;
}




void addPacket(PacketList *pl,  int receivedSeqNr, char **barray){
    Packet *p = (Packet *) malloc (sizeof (Packet));
    strncpy(*p->byteArray, *barray, sizeof(barray));
   
    if (pl->count >= pl->allocated){
        pl->allocated *= 2;
        pl->packets = (Packet **) realloc (pl->packets, pl->allocated * sizeof (Packet *));
    }
    pl->packets[pl->count++] = p;
}

