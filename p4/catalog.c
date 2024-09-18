/** @file catalog.c
    @author Huy Nguyen (hqnguyen)
    This component contains function for making/freeing catalog as well as 
    read, sort, and list courses.
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>

#include "catalog.h"
#include "input.h"

Catalog *makeCatalog() 
{
    // Dynamically allocate memory for catalog.
    Catalog *catalog = malloc(sizeof(Catalog));
    catalog-> count = 0;
    catalog-> capacity = STARTING_CAPACITY;
    // Resizable array of pointer to keep track of courses.
    catalog-> list = (Course **) malloc(catalog->capacity * sizeof(Course*));

    return catalog;
}

void freeCatalog(Catalog *catalog) 
{
    // Free the courses in catalog list.
    for (int i = 0; i < catalog->count; i++) {
        free(catalog->list[i]);
    }
    // Free the pointer to list.
    free(catalog->list);
    // Free the catalog.
    free(catalog);
}

/** Helper method to check for duplicate courses in the course file.
    @param *catalog pointer to the given catalog.
    @param *idept pointer to a given course department.
    @param *inumber pointer to a given course number.
    @return true if there is a duplicate and false otherwise.
*/
static bool isDuplicate(Catalog *catalog, char *idept, char *inumber) 
{
    // Return true if given course id is already in catalog.
    for (int i = 0; i < catalog->count; i++) {
        if (strcmp(catalog->list[i]->dept, idept) == 0 && strcmp(catalog->list[i]->number, inumber) == 0) {
            return true;
        }
    }
    return false;
}

bool isTimesValid(char *timeStr) 
{
    // Valid time array.
    char times[NUMBER_OF_VALID_TIME][NUMBER_OF_VALID_TIME] = {"8:30", "10:00", "11:30", "1:00", "2:30", "4:00"};
    // Loop through the time array.
    for (int i = 0; i < NUMBER_OF_VALID_TIME; i++) {
        // Return true give time is valid.
        if (strcmp(timeStr, times[i]) == 0) {
            return true;
        }
    }
    return false;
}

void readCourses( char const *filename, Catalog *catalog ) 
{
    // Open the file to be read.
    FILE *fp = fopen(filename, "r");
    
    // Print to standard error and exit with status of 1 if file can not be open.
    if (fp == NULL) {
        fprintf(stderr, "Can't open file: %s\n", filename);
        exit(1);
    }
    
    // Loop through to read each course in the course file.
    while (1) {
        
        // Create catalog list as a resizable array.
        if (catalog->count >= catalog->capacity) {
            catalog->capacity *= DOUBLE_ARRAY;
            catalog->list = (Course **)realloc(catalog->list, catalog->capacity * sizeof(Course*));
        }
        
        // A pointer the the read line.
        char *linePointer = readLine(fp);
        
        // String array to store each of the field and later compare.
        char idept[MAX_STRING_LEN];
        char inumber[MAX_STRING_LEN];
        char idays[MAX_STRING_LEN];
        char itime[MAX_STRING_LEN];
        char iname[MAX_STRING_LEN];
        
        // If line pointer is null, close the file and return to caller function/
        if (linePointer == NULL) {
            fclose(fp);
            return;
        }
        int num;
        int newNum;
        
        // Scan the each course fields from course file into each string array.
        int fields = sscanf(linePointer, "%s%s%s%s%s%n", idept, inumber, idays, itime, iname, &num);
        
        char new[MAX_STRING_LEN];
        // Concatenate names with several words using sscanf offset.
        while (sscanf(linePointer+num, "%s%n", new, &newNum) == 1) {
            strcat(iname, " ");
            strcat(iname, new);
            num += newNum;
        }
        // Free the pointer to the line.
        free(linePointer);
        
        // Check if line is missing one of the expected field.
        if (fields != FIELDS) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        
        // Check if department does not have exactly 3 uppercase letters.
        if (strlen(idept) != DEPARTMENT_LETTERS || !isupper(idept[0]) || !isupper(idept[1]) || !isupper(idept[2])) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        
        // Check if course number does not have exactly 3 digits.
        if (strlen(inumber) != NUMBER_LETTERS || !isdigit(inumber[0]) || !isdigit(inumber[1]) || !isdigit(inumber[2])) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        // Check if days are not MW or TH.
        if (strcmp(idays, "MW") != 0 && strcmp(idays, "TH") != 0) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        // Check if name is too long.
        if (strlen(iname) > NAME_LETTERS) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        // Check if two or more courses have same course id.
        if (isDuplicate(catalog, idept, inumber)) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        // Check if time is not valid.
        if (!isTimesValid(itime)) {
            fprintf(stderr, "Invalid course file: %s\n", filename);
            exit(1);
        }
        
        // Dynamically allocate memory for list of courses.
        catalog->list[catalog->count] = (Course *) malloc( sizeof(Course));
        
        // Copy over each of the string array field to the right places in the catalog.
        strcpy(catalog->list[catalog->count]->dept, idept);
        strcpy(catalog->list[catalog->count]->number, inumber);
        strcpy(catalog->list[catalog->count]->days, idays);
        strcpy(catalog->list[catalog->count]->time, itime);
        strcpy(catalog->list[catalog->count]->name, iname);
 
        // Increment catalog count.
        catalog->count++;
        
    }

}

void sortCourses( Catalog *catalog, int (* compare) (void const *va, void const *vb )) 
{
    // Call qsort function.
    qsort(catalog->list, catalog->count, sizeof(Course*), compare);
}

void listCourses( Catalog *catalog, bool (*test)( Course const *course, char const *str1, char const *str2 ), char const *str1, char const *str2 ) 
{
    // Print the header.
    printf("Course  Name                           Timeslot\n");
    // Loop through catalog.
    for (int i = 0; i < catalog->count; i++) {
        Course *course = catalog->list[i];
        // Print all the matching courses in the catalog.
        if (test(course, str1, str2)) {
            printf("%3s %3s %-30s %2s %5s\n", course->dept, course->number, course->name, course->days, course->time);
        }
    }  
}
