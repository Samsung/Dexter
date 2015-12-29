/**
* @file		: NoFreeOfParameterValue.cpp
* @brief	: NoFreeOfParameterValue class source file
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

static void TestMethod11()
{    
	char *settingValue = NULL;	
	result = system_settings_get_value_string(system_settings_key_e.SYSTEM_SETTINGS_KEY_LOCALE_LANGUAGE , &settingValue);	
	//Do Some Things
	
	if(settingValue)	
	{	
		settingValue=NULL;
	
	}
}
static void TestMethod12()
{    
	char *settingValue = NULL;	
	result = system_settings_get_value_string(system_settings_key_e.SYSTEM_SETTINGS_KEY_LOCALE_LANGUAGE , &settingValue);	
	//Do Some Things
	
	if(settingValue)	
	{
		free(settingValue);	
		settingValue=NULL;
	
	}
}
/*
static void TestMethod13()
{    
	char* ret_val = NULL;	
	result = system_info_get_value_string(SYSTEM_INFO_KEY_MODEL, &ret_val);	
	if(ret_val)	
	{		
		ret_val=NULL;
	}
}
static void TestMethod14()
{
	char* ret_val = NULL;	
	result = system_info_get_value_string(SYSTEM_INFO_KEY_MODEL, &ret_val);	
	if(ret_val)	
	{	
		free(ret_val);	
		ret_val=NULL;
	}
}
*/
