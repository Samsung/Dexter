/**
* @file		: SignedUnsignedAssignmentError.cpp
* @brief	: SignedUnsignedAssignmentError class source file
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
}

