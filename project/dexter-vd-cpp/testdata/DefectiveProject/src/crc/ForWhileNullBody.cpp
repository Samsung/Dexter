/**
* @file		: ForWhileNullBody.cpp
* @brief	: ForWhileNullBody class source file
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
    //Test case 1
    for(int i=0;i<10;i++);          	
         	     
    //Test case 2
    while(sum>0);    
     
      //Test case 3
    for(int i=0;i<10;i++)
    ;
    
  
    //Test case 4
    while(sum>0)
    ;
    
    
      //Test case 5
    for(int i=0;i<10;i++)
    {
    	;
    }
  
    //Test case 6
    while(sum>0)
    {
   		;
    }
    
    //Test case 7
    for(int i=0;i<10;i++)
    {
       
    }
      
    //Test case 8
    while(sum>0)
    {
     
    }
    
}

void TestMethod1()
{
      //Test case 1
    for(int i=0;i<10;i++)
    ; /* NULL */
    
  
    //Test case 2
    while(sum>0)
    ; /* NULL */
    
    
      //Test case 1
    for(int i=0;i<10;i++)
    {
    	; /* NULL */
    }
  
    //Test case 2
    while(sum>0)
    {
   		; /* NULL */
    }
       
}


