//
//  PacketList.h
//  
//
//  Created by Ganna Shulika on 28/03/16.
//
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
