/**
* @file		: MacroParenthesis.cpp
* @brief	: MacroParenthesis class source file
*			
* @author	: 
* @date		: 
*
* Copyright 2015 by Samsung Electronics Inc.
*
* This software is the confidential and proprietary information
* of Samsung Electronics Inc. (Confidential Information).  You
* shall not disclose such Confidential Information and shall use
* it only in accordance with the terms of the license agreement
* you entered into with Samsung.
*
*/
#define multiply( f1, f2 ) ( f1 * f2 )
#define multiply( a1, a2 ) ( (a1) * a2 )
#define WIDTH       80
#define LENGTH      WIDTH + 10 
#define getmax(a,b)  a>b?a:b 
#define SumOf(x,y) (x)+(y) 


#define multiply( f1, f2 )  (f1) * (f2) 
#define multiply( a1, a2 ) ( a1 * (a2))
#define getmax2(a,b)  (a>b?(a):(b)) 
#define SumOf(x,y) (x+y) 

#define multiply( f1, f2 ) ( (f1) * (f2) )
#define multiply( a1, a2 ) ( (a1) * (a2)) 
#define WIDTH       80
#define LENGTH      (WIDTH + 10)
#define getmax1(a,b)  ((a)>(b)?(a):(b)) 
#define SumOf(x,y) ((x)+(y)) 


int main()
{
  int x=5, y;
  y= getmax(x,2);
   int arg1=1, arg2=7;
  
  printf("\nSumOf(arg1,arg2)=%i",SumOf(arg1,arg2)); 
  printf(y);
  return 0;
}

