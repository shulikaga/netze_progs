//
//  nvs16.ps.blatt2.h
//  Tansmit
//
//  Created by Ganna Shulika on 27/04/16.
//  Copyright © 2016 Ganna Shulika. All rights reserved.
//

#ifndef crc32_h
#define crc32_h

#include <stdio.h>
#include <stdlib.h>

#define POLYNOMIAL 0x04c11db7L      // Standard CRC-32 ppolynomial
#define BUFFER_LEN       4096L      // Length of buffer

static unsigned int crc_table[256];       // Table of 8-bit remainders

void gen_crc_table();
 int update_crc( int crc_accum,  char* data_blk_ptr,  int data_blk_size);


#endif /* nvs16_ps_blatt2_h */
