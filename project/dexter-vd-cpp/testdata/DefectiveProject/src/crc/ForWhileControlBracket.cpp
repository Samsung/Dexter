/**
* @file		: ForWhileControlBracket.cpp
* @brief	: ForWhileControlBracket class source file
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
    for(int i=0;i<10;i++)
         printf("print i %d",i);     	
      
    int sum=5;
    
    //Test case 2
    if(sum <=5)    
   	 	printf("Equal or less than 5");   
    else   
   	 	printf("greater than 5");
   	   
   	     
    //Test case 3
    while(sum>0)    
      printf("%d",sum =sum--);    
}

void TestMethod1()
{
    //Test case 1
    for(int i=0;i<10;i++)
    {
      printf("print i %d",i);
      
     	 for(int j=0;j<10;j++)   		 
     		 printf("print j %d",j);
   		 
    }
    
    int sum=5;
    //Test case 2
    if(sum <=5)
    {
   	 	printf("Equal or less than 5");
   	 	
	   	if(sum <=5)
	    printf("Equal or less than 5");
	    else
	    printf("greater than 5");
    }
    else
    {
   	 	printf("greater than 5");
   	 	
	   	if(sum <=5)
	    printf("Equal or less than 5");
	    else
	    printf("greater than 5");
    }
    
    //Test case 3
    while(sum>0)
    {
      sum =sum--;
      printf("%d",sum);
      
      while(sum>0)
      printf("%d",sum--);
    }
    
}

