/**
* @file		: SignedUnsignedAssignmentError.cpp
* @brief	: SignedUnsignedAssignmentError class source file
*			
* @author	: A����AE?
* @date		: 2013/11/12
*
* Copyright 2013 by Samsung Electronics Inc.
*
* This software is the confidential and proprietary information
* of Samsung Electronics Inc. (Confidential Information).  You
* shall not disclose such Confidential Information and shall use
* it only in accordance with the terms of the license agreement
* you entered into with Samsung.
*
*/



#include "SignedUnsignedAssignmentError.h"

#define SOME_MACRO;

/*
#ifdef SOME_MACRO       
 	std::vector<int> testVector (5,100);
	  std::vector<int> testVector (testVector.begin(),testVector.end());	
	
	  for (std::vector<int>::iterator it = testVector.begin(); it != testVector.end(); ++it)
	  {
		testVector.erase(it);
	  }

#endif
*/

inline void TestMethod2()
{
    std::vector<int> testVector (5,100);
	  std::vector<int> testVector (testVector.begin(),testVector.end());	
	
	  for (std::vector<int>::iterator it = testVector.begin(); it != testVector.end(); ++it)
	  {
		testVector.erase(it);
	  }
	  
	  for (std::vector<int>::iterator it = testVector.begin(); it != testVector.end(); it++)
	  {
		testVector.erase(it);
	  }
	  
	  for (std::vector<int>::iterator it = testVector.begin(); it != testVector.end(); it)
	  {
		testVector.erase(it++);
	  }
	  
	  for (std::vector<int>::iterator it = testVector.begin(); it != testVector.end(); it)
	  {
		testVector.erase(++it);
	  }
}     





