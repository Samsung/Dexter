#pragma once
class SampleClass
{
public:
	SampleClass(char * pc);
	~SampleClass();
	void methodWithFalsePositive();

protected:
	std::list<SampleClass*> gList;

};

