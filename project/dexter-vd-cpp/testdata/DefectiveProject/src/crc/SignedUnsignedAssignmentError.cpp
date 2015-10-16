/**
* @file		: SignedUnsignedAssignmentError.cpp
* @brief	: SignedUnsignedAssignmentError class source file
*			
* @author	: 
* @date		: 
*
*/

static void TestMethod1()
{
    unsigned  aaa;
	char zzs;
	int a;
	int b;
	int c;
	a+b;
	signed char ss;	
	ss= zzs = 0xF0;
	aaa = ss ; //Error
	aaa = zzs ; //Error  
	for (char depth = 3; depth >= 0 ; --depth)
	{

	}	
	ss= zzs = 0x03;
	aaa = ss ; //No Error
	aaa = zzs ; //No Error  
	
	/*
	ss= zzs = 5;
 	aaa = ss ; //Error
	 aaa = zzs ; //Error 
	*/
}

