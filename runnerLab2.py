import sys
import os


print "------------------First Come First Serve--------------------"
os.system('python schedulingFC.py --verbose')
print "------------------------------------------------------------"
print "------------------Shortest Job First------------------------"
os.system('python schedulingSJF.py --verbose')
print "------------------------------------------------------------"
print "--------------------Uniprocessing---------------------------"
os.system('python schedulingUni.py --verbose')
print "------------------------------------------------------------"
