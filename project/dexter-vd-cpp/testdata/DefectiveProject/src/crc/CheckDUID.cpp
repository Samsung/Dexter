// template specialization
#include <iostream>
using namespace std;


int main () 
{
	//TestCase1
  	vconf_get_str("db/comss/duid");
  	 
  	//TestCase2
	#define VCONF_DUID "db/comss/duid"
	vconf_get_str(VCONF_DUID); 
	
	//TestCase3
	string variable = "db/comss/duid";
	vconf_get_str(variable);
	   
  return 0;
}