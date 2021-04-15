import sys

parsedCl = map(int, sys.argv[1].strip('[]').split(','))

print(parsedCl[1])
# strA = sys.argv[1].replace('[', ' ').replace(']', ' ').replace(',', ' ').split()
# print (strA)
# A = [int(i) for i in strA]
# print (A)
# print (A[1])
