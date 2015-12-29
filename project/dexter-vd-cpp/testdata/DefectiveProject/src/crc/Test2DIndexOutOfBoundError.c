#include <config.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h> /* for memcmp() */

#include "types.h"  /* for byte and u32 typedefs */
#include "g10lib.h"
#include "cipher.h"

#define MAXKC			(256/32)
#define MAXROUNDS		14
#define BLOCKSIZE               (128/8)


/* USE_PADLOCK indicates whether to compile the padlock specific
   code.  */
#undef USE_PADLOCK
#ifdef ENABLE_PADLOCK_SUPPORT
# if defined (__i386__) && SIZEOF_UNSIGNED_LONG == 4 && defined (__GNUC__)
# define USE_PADLOCK
# endif
#endif /*ENABLE_PADLOCK_SUPPORT*/

static const char *selftest(void);

typedef struct 
{
  int   ROUNDS;             /* Key-length-dependent number of rounds.  */
  int decryption_prepared;  /* The decryption key schedule is available.  */
#ifdef USE_PADLOCK
  int use_padlock;          /* Padlock shall be used.  */
  /* The key as passed to the padlock engine.  */
  unsigned char padlock_key[16] __attribute__ ((aligned (16)));
#endif
  union
  {
    PROPERLY_ALIGNED_TYPE dummy;
    byte keyschedule[MAXROUNDS+1][4][4];
  } u1;
  union
  {
    PROPERLY_ALIGNED_TYPE dummy;
    byte keyschedule[MAXROUNDS+1][4][4];	
  } u2;
} RIJNDAEL_context;

#define keySched  u1.keyschedule
#define keySched2 u2.keyschedule

/* All the numbers.  */
#include "rijndael-tables.h"



/* Encrypt one block.  A and B need to be aligned on a 4 byte
   boundary.  A and B may be the same. */
static void
do_encrypt_aligned (const RIJNDAEL_context *ctx, 
                    unsigned char *b, const unsigned char *a)
{
//#define rk (ctx->keySched)
  int ROUNDS = ctx->ROUNDS;
  int r;
  union
  {
    u32  tempu32[4];  /* Force correct alignment. */
    byte temp[4][4];
  } u;

 
   /* Last round is special. */   
  *((u32*)u.temp[0]) = *((u32*)(b   )) ^ *((u32*)rk[ROUNDS-1][0]);
  *((u32*)u.temp[1]) = *((u32*)(b+ 4)) ^ *((u32*)rk[ROUNDS-1][1]);
  *((u32*)u.temp[2]) = *((u32*)(b+ 8)) ^ *((u32*)rk[ROUNDS-1][2]);
  *((u32*)u.temp[3]) = *((u32*)(b+12)) ^ *((u32*)rk[ROUNDS-1][3]);
  b[ 0] = T1[u.temp[0][1]][2];
  b[ 1] = T1[u.temp[1][0]][2];
  b[ 2] = T1[u.temp[2][2]][1];
  b[ 3] = T1[u.temp[3][3]][1];
  b[ 4] = T1[u.temp[1][0]][1];
  b[ 5] = T1[u.temp[2][1]][1];
  b[ 6] = T1[u.temp[3][2]][1];
  b[ 7] = T1[u.temp[0][3]][1];
  b[ 8] = T1[u.temp[2][0]][1];
  b[ 9] = T1[u.temp[3][1]][1];
  b[10] = T1[u.temp[0][2]][1];
  b[11] = T1[u.temp[1][3]][1];
  b[12] = T1[u.temp[3][0]][1];
  b[13] = T1[u.temp[0][1]][1];
  b[14] = T1[u.temp[1][2]][1];
  b[15] = T1[u.temp[2][3]][1];
  *((u32*)(b   )) ^= *((u32*)rk[ROUNDS][0]);
  *((u32*)(b+ 4)) ^= *((u32*)rk[ROUNDS][1]);
  *((u32*)(b+ 8)) ^= *((u32*)rk[ROUNDS][2]);
  *((u32*)(b+12)) ^= *((u32*)rk[ROUNDS][3]);
//#undef rk
}


static void
do_encrypt (const RIJNDAEL_context *ctx,
            unsigned char *bx, const unsigned char *ax)
{
  /* BX and AX are not necessary correctly aligned.  Thus we need to
     copy them here. */
  union
  {
    u32  dummy[4]; 
    byte a[16];
  } a;
  union
  {
    u32  dummy[4]; 
    byte b[16];
  } b;

  memcpy (a.a, ax, 16);
  do_encrypt_aligned (ctx, b.b, a.a);
  memcpy (bx, b.b, 16);
}


/* Encrypt or decrypt one block using the padlock engine.  A and B may
   be the same. */
#ifdef USE_PADLOCK
static void
do_padlock (const RIJNDAEL_context *ctx, int decrypt_flag,
            unsigned char *bx, const unsigned char *ax)
{
  /* BX and AX are not necessary correctly aligned.  Thus we need to
     copy them here. */
  unsigned char a[16] __attribute__ ((aligned (16)));
  unsigned char b[16] __attribute__ ((aligned (16)));
  unsigned int cword[4] __attribute__ ((aligned (16)));

  /* The control word fields are:
      127:12   11:10 9     8     7     6     5     4     3:0
      RESERVED KSIZE CRYPT INTER KEYGN CIPHR ALIGN DGEST ROUND  */
  cword[0] = (ctx->ROUNDS & 15);  /* (The mask is just a safeguard.)  */
  cword[1] = 0;
  cword[2] = 0;
  cword[3] = 0;
  if (decrypt_flag)
    cword[0] |= 0x00000200;

  memcpy (a, ax, 16);
   
  asm volatile 
    ("pushfl\n\t"          /* Force key reload.  */            
     "popfl\n\t"
     "xchg %3, %%ebx\n\t"  /* Load key.  */
     "movl $1, %%ecx\n\t"  /* Init counter for just one block.  */
     ".byte 0xf3, 0x0f, 0xa7, 0xc8\n\t" /* REP XSTORE ECB. */
     "xchg %3, %%ebx\n"    /* Restore GOT register.  */
     : /* No output */
     : "S" (a), "D" (b), "d" (cword), "r" (ctx->padlock_key)
     : "%ecx", "cc", "memory"
     );

  memcpy (bx, b, 16);

}
#endif /*USE_PADLOCK*/


