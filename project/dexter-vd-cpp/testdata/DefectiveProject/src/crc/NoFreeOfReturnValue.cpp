/**
* @file		: NoFreeOfReturnValue.cpp
* @brief	: NoFreeOfReturnValue class source file
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


static void TestMethod3()
{
    char* setupvalue = NULL;  
     char* setupvalue1 = NULL;  
    setupvalue = vconf_get_str( KEY_SETUPOFFTIMER_1 );
    if( setupvalue )
    {      
      free( setupvalue );
    }
    setupvalue1 = vconf_get_str( KEY_SETUPOFFTIMER_1 );
}
