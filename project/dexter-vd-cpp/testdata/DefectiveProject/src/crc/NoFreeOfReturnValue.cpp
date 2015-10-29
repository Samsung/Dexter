/**
* @file		: NoFreeOfReturnValue.cpp
* @brief	: NoFreeOfReturnValue class source file
*			
* @author	: 
* @date		: 
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
