/**
* @file		: AddFallThroughCommentOnSwitchCase.cpp
* @brief	: AddFallThroughCommentOnSwitchCase class source file
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

int main() 
{
 	char o; 
 	scanf("%c",&o);

 	switch(o) 
	{ 
		case '1': 
			printf(" Print 1"); 
			
		case '2': 
			printf(" Print 2"); 
			break; 
		case '3': 
			printf(" Print 3"); 
			
		case '4': 
			printf(" Print 4"); 
			break; 
		default: 
			printf(" Print default"); 
			break; 
	}
 return 0;
}



