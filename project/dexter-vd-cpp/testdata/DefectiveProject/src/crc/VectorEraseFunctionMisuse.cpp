/**
* @file		: SignedUnsignedAssignmentError.cpp
* @brief	: SignedUnsignedAssignmentError class source file
*			
* @author	: A����AE?
* @date		: 2013/11/12
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





