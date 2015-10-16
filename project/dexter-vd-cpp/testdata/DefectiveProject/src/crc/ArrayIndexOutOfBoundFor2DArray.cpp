/**
* @file		: IndexOutOfBoundFor2DArray.cpp
* @brief	: IndexOutOfBoundFor2DArray class source file
*			
* @author	: 
* @date		: 
*
*/
		
		void function_1(int* array_parameter)
		{			
			int basic_array[8][9]={0,};
			
			for(int j=0 ; j<15 ; j++) 
			{
				for(int i=0 ; i<15 ; i++) 
				{
				    basic_array[i][j]=0; //should report issue
	 			}  
 			}
 			
 			 						
			printf("basic_array[5] : %d ",basic_array[5]);
			printf("*(basic_array+5) : %d ",*(basic_array+5));
			printf("(*(basic_array+5))[10] : %d ",(*(basic_array+5))[10]); //Test case not able to cover right now.
			printf("*(basic_array[5]+10) : %d ",*(basic_array[5]+10));//Test case not able to cover right now.
			printf("*(*(basic_array+10)+10) : %d ",*(*(basic_array+10)+10));//should report issue
 			
 		} 		
		
	
		void function_2(int (*array_parameter)[4])
		{
		    int base_1Darray[5]={};
			int *ptr =base_1Darray;
			int basic_array[9][4]={};		
			int (*array_ptr)[4] = basic_array; 
			for(int i=0 ; i<9 ; i++) 
			{
		 		for(int j=0 ; j<12 ; j++)
		 		{
		 			array_ptr[i][j] = 0; //Should report issue.
				}
		 	}
				
		}
		void function_3(int (*array_parameter)[4])
		{
		 	int basic_array[9][4]={};		
			int (*array_ptr)[4] = basic_array; 
			for(int i=0 ; i<9 ; i++) 
			{
		 		for(int j=0 ; j<4 ; j++)
		 		{
					 array_ptr[i][j] = 0; //Should not report issue.
				 }
			 }
		}

		
 		

