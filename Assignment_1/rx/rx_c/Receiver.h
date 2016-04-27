//
//  Receiver.h
//  Tansmit
//

#ifndef Receiver_h
#define Receiver_h

//------GLOBAL VARIABLES------------//


typedef struct{
    int     blockSize;
    int     serverTimeout;
    int     udpSocket;
    int     nBytes;
    int     numberOfPackets;
    int     port;
    int     dataSize;
    int     client_socket;
    int     server_socket;
    int     allocated;
    int     counter;
    char*   ip;
    char**  allPackets;

}Receiver;

Receiver *newReceiver(int port,int dataSize);
void allPacketsDoubleSize(Receiver* receiver);
void freeReceiverArrays(Receiver* receiver);

void start(Receiver* receiver);
void openSocket(Receiver* receiver);
void receivePacket(Receiver* receiver);
void get4Bytes(Receiver* receiver, char* buffer, int packetNumber, int start);
void checkCRC32(Receiver* receiver,char* receivedLastPacket, int packetNumber, int start);

void printData(char* buffer);
void messageToString(char* buffer, int start, int end);

void printbinchar(char* buffer, int start, int end);


#endif /* Receiver_h */
