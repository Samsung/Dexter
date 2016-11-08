#include "stdafx.h"
#include "SampleClass.h"


SampleClass::SampleClass(char* pc)
{
	delete[] pc;
}


SampleClass::~SampleClass()
{
}

void SampleClass::methodWithFalsePositive()
{
	char* buf = new char[10]; 

	// This is a false positive defect "error:memleak:Memory leak: buf"
	gList.push_back(new (std::nothrow) SampleClass(buf)); 
}
