/**
* @file		: IndexOutOfBoundForOneDArray.cpp
* @brief	: IndexOutOfBoundForOneDArray class source file
*			
* @author	: 
* @date		: 
*
*/
	
		/*	
	    void function1()
		{
		
			int array1[] = {4,8,15,16,23,42};       
			for(int i = 0; i <= array1.length; i++)// here, i<=array.length will be true,even when i is equal to 6  
			{ 
				//which will cause an ArrayIndexOutOfBoundsException
				System.out.println(".."+array1[i]);
			}   
												
			int array[2];
   			array[0] = 1;
    		array[1] = 2;
    		array[3] = 3;
    		array[4] = 4;
    		cout << array[3] << endl;
   			cout << array[4] << endl;
   			   			
   			int array2[1];
   			for (int i = 0; i != 100000; i++)
   			{
       			array2[i] = i;
   			}
   			   			
   			int array3[1];
  			int *ptr = array3;
   			for (int i = 0; i != 100000; i++, ptr++)
   			{
       			*ptr = i;
   			}
   			
		} 
				
		*/
		
		void function2(int* array_parameter)
		{			
			int basic_array[]={0,1,2,3,4,5,6,7,8};
			for(int i=0 ; i<11 ; i++) 
			{
			    basic_array[i]=0;
 			}  
 			
 			for(int i=0 ; i<=9 ; i++) 
			{
			    basic_array[i]=0;
 			} 
 			
 			for(int i=0 ; i<9 ; i++) 
			{
			    basic_array[i]=0;
 			} 
 			 			
 			printf("basic_array[10] : %d ",basic_array[9]);
			printf("basic_array[10] : %d ",basic_array[10]);
			printf("*(basic_array+10) : %d ",*(basic_array+10));
 			 			
		} 
		
				
	
		void function3(int array_parameter[])
		{  
			int basic_array[]={0,1,2,3,4,5,6,7,8};
			for(int i=0 ; i<11 ; i++)  
			{   
	             basic_array[i]=0;
			}  
			printf("basic_array[10] : %d ",basic_array[10]);
			printf("*(basic_array+10) : %d ",*(basic_array+10)); 

 			for(int i=0; i<11; i++) 
			{    
				array_parameter[i]=0;
			}

		}
		
 		

