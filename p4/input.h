/** @file input.h
    @author Huy Nguyen (hqnguyen)
    This is the header file for the components that read line.
*/

#include <stdio.h>

/** Number to multiply by to resize the resizable array */
#define DOUBLE_ARRAY 2

/** The initial capacity of string length. */
#define INITIAL_CAPACITY 100

/** This function reads a single line of input from the input stream and return a 
    string in a dynamically allocated block of memory.
    @param *fp the pointer to the file.
    @return a pointer to a dynamically allocated memory.
*/
char *readLine(FILE *fp);
