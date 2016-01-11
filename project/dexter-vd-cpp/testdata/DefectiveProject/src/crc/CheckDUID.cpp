// template specialization
#include <iostream>
using namespace std;


int main () 
{
	//TestCase1
  	vconf_get_str("db/comss/duid");
  	vconf_get_str("db/comss/hwduid");
  	vconf_get_str("db/comss/psid"); 
  	
	//Testcase 2
	#define VCONF_DUID "db/comss/duid"
	vconf_get_str(VCONF_DUID);
	#define VCONF_HWDUID "db/comss/hwduid"
	vconf_get_str(VCONF_HWDUID);	
	#define VCONF_PSID "db/comss/psid"	
	vconf_get_str(VCONF_PSID);
	 
	//Testcase 3
	string variable1 = "db/comss/duid";
	vconf_get_str(variable1);
	string variable2 = "db/comss/hwduid";
	vconf_get_str(variable2);
	string variable3 = "db/comss/psid";
	vconf_get_str(variable3);
 
	return 0;
}