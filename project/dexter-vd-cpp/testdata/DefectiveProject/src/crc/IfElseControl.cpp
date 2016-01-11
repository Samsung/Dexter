/**
* @file		: CheckIfElseControl.cpp
* @brief	: CheckIfElseControl class source file
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

void TestMethod()
{
        
    int sum=5;
    
    //Test case 1
    if(sum <=5)
    {    
   	 	printf("Equal or less than 5"); 
   	}else  
   	{ 
   	 	printf("greater than 5");
   	    printf("print again");
   	    if(sum <=5) 
	   	{   
	   	 	printf("Equal or less than 5");   
	    }else if(sum >5 && sum <10)
	    {  
	   	 	printf("greater than 5 and less than 10");  
	    }else 
	   	 	printf("greater than 10"); 
	} 
   	
   	
}


void TestMethod()
{
        
    int sum=5;
    
    //Test case 1
    if(sum <=5)
    {    
   	 	printf("Equal or less than 5"); 
   	}
   	else  
   	{ 
   	 	printf("greater than 5");
   	    printf("print again");
   	} 
   	
   	if(sum <=5) 
   	{   
   	 	printf("Equal or less than 5");   
    }
    else if(sum >5 && sum <10)
    {  
   	 	printf("greater than 5 and less than 10");  
    }
    else 
   	 	printf("greater than 10"); 
}
