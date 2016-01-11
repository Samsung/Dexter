  /**    
* @file		: UsleepCheckerLogic.cpp
* @brief	: UsleepCheckerLogic.cpp File
*
* @author	: SISC
* @date		: 17/12/2012
*
* Copyright 2012 by Samsung Electronics Inc.
*
* This software is the confidential and proprietary information
* of Samsung Electronics Inc. (Confidential Information).  You
* shall not disclose such Confidential Information and shall use
* it only in accordance with the terms of the license agreement
* you entered into with Samsung.
*
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

  
