/**
* @file		: NoFreeForThirdParameter.cpp
* @brief	: NoFreeForThirdParameter class source file
*			
* @author	: 
* @date		: 
*
*/
static void TestMethod1()
{
	char* strName = NULL;
	result=app_control_get_extra_data(app_control, "view", &strName);	
	
	int length = 0;
	char* key = NULL;
	char** value = NULL;
	result= app_control_get_extra_data_array(app_control, key, &value, &length);
	
	
	int length1 = 0;
	char* key1 = NULL;
	char** value1 = NULL;
	result= app_control_get_extra_data_array(app_control, key1, &value1, &length1);
	if(value1)
	{    
		free(valu1e);
		value1=NULL;
	}	

}

static void TestMethod2()
{

	char* strName = NULL;
	result=app_control_get_extra_data(app_control, "view", &strName);
	if(strName)
	{   
		 free(strName);  
	   	 strName=NULL;
	} 
	
	
	int length = 0;
	char* key = NULL;
	char** value = NULL;			
	result=app_control_get_extra_data_array(service, key, &value, &length);
	for (int i = 0; i < length; i++) 
	{    
		if (value[i])
		{         
			free(value[i]);
			value[i]=NULL;
		}
	} 
	
	if(value)
	{    
		free(value);
		value=NULL;
	}	
	
}

/*
static void TestMethod2()
{
	int length = 0;
	char* key = NULL;
	char** value = NULL;
	result= app_control_get_extra_data_array(app_control, key, &value, &length);
	if(value)
	{    
		free(value);
		value=NULL;
	}	
}	


static void TestMethod4()
{
	int length = 0;
	char* key = NULL;
	char** value = NULL;			
	result=app_control_get_extra_data_array(service, key, &value, &length);
	for (int i = 0; i < length; i++) 
	{    
		if (value[i])
		{
			free(value[i]);
			value[i]=NULL;
		}
	} 
	if(value)
	{    
		free(value);
		value=NULL;
	}
	
}
*/