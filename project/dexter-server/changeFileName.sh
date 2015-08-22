#! /bin/bash
#
for filename in *
do
  fname=`basename $filename`
  n=`echo $fname | tr A-Z a-z` 
  if [ "$fname" != "$n" ]      
  then
    mv $fname $n
  fi  
done  

exit 0
