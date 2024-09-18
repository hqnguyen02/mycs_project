/** @file input.c
    @author Huy Nguyen (hqnguyen)
    This component contains function reading line from input file.
*/

#include "input.h"
#include <stdlib.h>
#include <stdio.h>

char *readLine(FILE *fp) 
{
    int capacity = INITIAL_CAPACITY;
    int pos = 0;
    // Dynamically allocate memory for the line pointer.
    char *line = (char *)malloc(capacity * sizeof(char));

    // Loop through to read each character in a line of the file.
    while (1) {
        int ch = fgetc(fp);
        if (ch == EOF) {
            // Check for empty file.
            if (pos == 0) {
                free(line);
                return NULL;
            } 
            // Break out of loop if end of file is reached.
            else {
                break;
            }
        }
        // Break out of the loop if a new line character is read or end of line.
        if (ch == '\n') {
            break;
        }
        // Assign the character read to the line string.
        line[pos++] = ch;
        
        // Resizable array to read line that could be arbitrarily large.
        if (pos >= capacity) {
            capacity *= DOUBLE_ARRAY;
            void *temp = (char *)realloc(line, capacity * sizeof(char));
            if (temp == NULL) {
                free(line); 
            } 
            else {
                line = temp;
            }
        }
        
    }
    // Add a null character.
    line[pos] = '\0';
    // Return the line pointer.
    return line;
}
