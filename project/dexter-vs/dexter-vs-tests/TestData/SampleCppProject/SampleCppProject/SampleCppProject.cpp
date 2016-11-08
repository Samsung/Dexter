#include "stdafx.h"
#include "SampleClass.h"

int main()
{
	// This variable should trigger "assigned but unused variable" defect by Dexter
	int unusedVariable = 69; 

    return 1;
}

