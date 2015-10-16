  /**    
* @file		: UsleepCheckerLogic.cpp
* @brief	: UsleepCheckerLogic.cpp File
*
* @author	: SISC
* @date		: 17/12/2012
*/

#define USLEEP(a) usleep(a)
#ifdef SOME_MACRO       
 usleep(50);
#else 
usleep(4000); 
#endif  
static void sleepForMicroseconds(unsigned us)
{
    usleep(us);
}
 
inline void SleepFor10ms()
{
    usleep(1000);
}     

  
