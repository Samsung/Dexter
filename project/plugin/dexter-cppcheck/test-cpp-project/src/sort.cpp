#include <iostream>
using std::cout;
using std::endl;

//#include <list>      // list class-template definition
//#include <algorithm> // copy algorithm
//#include <iterator>  // ostream_iterator

#define ucmMax 120


int main()
{
   int array[ 4 ] = { 2, 6, 4, 8 };
   std::list< int > values;      // create list of ints
   std::list< int > otherValues; // create list of ints
   std::ostream_iterator< int > output( cout, " " );

   // insert items in values
   values.push_front( 1 );
   values.push_front( 3 );
   values.push_back( 4 );
   values.push_back( 2 );

   cout << "values contains: ";
   std::copy( values.begin(), values.end(), output );

   values.sort(); // sort values

   cout << "\n\nvalues contains: ";
   std::copy( values.begin(), values.end(), output );

   cout << endl;
     
   return 0;
}

void test()
{
    HPEN pen = CreatePen(PS_SOLID, 1, RGB(255,0,0));
}

  /* 
values contains: 3 1 4 2

values contains: 1 2 3 4

 */      


/* Macro: touch all ucmMax references:

// to run, place cursor on next line and invoke ¡°Run Macro¡± 
hbuf = NewBuf("TouchRefs")      // create output buffer
if (hbuf == 0)
    stop
SearchForRefs(hbuf, "ucmMax", TRUE)
SetCurrentBuf(hbuf)            // put search results in a window
SetBufDirty(hbuf, FALSE);    // don¡¯t bother asking to save
Stop  
*/

