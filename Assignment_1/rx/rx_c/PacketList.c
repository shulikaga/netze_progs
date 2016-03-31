//
//  PacketList.c
//  


#ifndef PacketList_h
#define PacketList_h

#include <stdio.h>
#inclide "Packet.h"

typedef struct {
    Packet **packets;
    int count;
    int allocated;
}PacketList;

PacketList *newPacketList();
void add(Packet *p);

#endif /* PacketList_h */


PacketList *newPacketList(){
    PacketList *pl = (PacketList *) malloc (sizeof (PacketList));
    pl->count = 0;
    pl->allocated = 10;
    pl->packets = (Packet **) calloc (pl->allocated, sizeof (Packet *));
    return pl;
}

void add(Packet *p){
    if (pl->count >= pl->allocated){
        pl->allocated *= 2;
        pl->citations = (Packet **) realloc (pl->packets, pl->allocated * sizeof (Packet *));
    }
    pl->packets[pl->count++] = p;
}



