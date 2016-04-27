
#include <stdio.h>
#include <stdlib.h>
#include "crc32.h"

//  CRC32 table initialization                                               =
void gen_crc_table(){
    register  short int i, j;
    register  int crc_accum;
    
    for (i=0;  i<256;  i++){
        crc_accum = ( (unsigned int) i << 24 );
        for ( j = 0;  j < 8;  j++ ){
            if ( crc_accum & 0x80000000L )
                crc_accum = (crc_accum << 1) ^ POLYNOMIAL;
            else
                crc_accum = (crc_accum << 1);
        }
        crc_table[i] = crc_accum;
    }
}

//  CRC32 generation
 int update_crc( int crc_accum,  char *data_blk_ptr,  int data_blk_size){
    register  int i, j;
    
    for (j=0; j<data_blk_size; j++){
        i = ((int) (crc_accum >> 24) ^ *data_blk_ptr++) & 0xFF;
        crc_accum = (crc_accum << 8) ^ crc_table[i];
    }
    crc_accum = ~crc_accum;
    
    return crc_accum;
}
