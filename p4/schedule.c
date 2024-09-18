/** @file schedule.c
    @author Huy Nguyen (hqnguyen)
    This is the top-level component for this schedule courses program. Contains the main
    function and function for listing/comparing courses. Uses other methods to read input file 
    and handle users commands like add, drop, list, quit courses.
*/

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>

#include "catalog.h"
#include "input.h"

/** First param order before second one */
#define ORDER_BEFORE -1

/** First param order after second one */
#define ORDER_AFTER 1

/** First param equal to second param */
#define EQUAL 0

/** There are 6 valid time */
#define NUMBER_OF_VALID_TIME 6

/** The days for calendar is Mon - Thur */
#define CALENDAR_DAYS 4

/** Minimum number of argument on command line */
#define REQUIRE_ARGS 2

/** Expected length of string array */
#define MAX_STRING_LEN 101

/** There need to be exactly 3 character in department field */
#define DEPARTMENT_CHAR 3

/** There can only be 10 course in schedule max */
#define SCHEDULE_MAX_LEN 10

/** This static boolean function is used to pass in a pointer to the test function so that 
    it will print all of the courses.
    @param *course pointer to course.
    @param *str1 one of the parameter of the test function.
    @param *str2 another one of the parameter of the test function.
    @return true always to print all the courses.
*/
static bool truePointer(Course const *course, char const *str1, char const *str2) 
{
    return true;
}

/** This static boolean function is used to apss in a pointer to the test function so that 
    it will print all of the courses that matches a particular department.
    @param *course pointer to course.
    @param *dept the given department of course to match.
    @param *blank one of the parameter of the test function that is not used in this case.
    @return true if match is found otherwise false.
*/
static bool matchDept(Course const *course, char const *dept, char const *blank) {
    bool match = strcmp(course->dept, dept) == 0;
    return match;
}

/** This static boolean function is used to apss in a pointer to the test function so that 
    it will print all of the courses that matches a particular time slot.
    @param *course pointer to course.
    @param *days the given days of the course to match.
    @param *time the given time of the course to match.
    @return true if match is found otherwise false.
*/
static bool matchTime(Course const *course, char const *days, char const *time) {
    bool match = strcmp(course->days, days) == 0 && strcmp(course->time, time) == 0;
    return match;
}

/** Comparison function used to sort list courses, list department, and list timeslot commands.
    This function sort by courseID. 
    @param *va a constant void pointer.
    @param *vb another constant void pointer.
    @return -1 if the course given by first parameter should be ordered before the second one,
    0 if the course given by first parameter is equal to the second one,
    1 if the course given by first parameter should be ordered after the second one,
*/
static int compare1(void const *va, void const *vb) 
{
    // Variables storing the value to be return.
    int num0 = EQUAL;
    int num1 = ORDER_AFTER;
    int numN = ORDER_BEFORE;
    
    // Convert void pointers to (const) pointers to courses.
    Course const a = **(Course**)va;
    Course const b = **(Course**)vb;
    
    // Compare the department for two courses first.
    if (strcmp(a.dept, b.dept) < 0) {
        return numN;
    }
    else if (strcmp(a.dept, b.dept) > 0) {
        return num1;
    }
    // If the department of two courses are equal, compare the number of the two courses.
    else if (strcmp(a.dept, b.dept) == 0) {
        if (strcmp(a.number, b.number) < 0) {
            return numN;
        }
        else if (strcmp(a.number, b.number) > 0) {
            return num1;
        }
        else if (strcmp(a.number, b.number) == 0) {
            return num0;
        }
    }
    return 0;
}

/** Comparison function used to sort list names command. This function sort by course name, followed by course ID,
    if the name are identical.
    @param *va a constant void pointer.
    @param *vb another constant void pointer.
    @return -1 if the course given by first parameter should be ordered before the second one,
    0 if the course given by first parameter is equal to the second one,
    1 if the course given by first parameter should be ordered after the second one,
*/
static int compareListName(void const *va, void const *vb) 
{
    // Variables storing the value to be return.
    int num0 = EQUAL;
    int num1 = ORDER_AFTER;
    int numN = ORDER_BEFORE;
    
    // Convert void pointers to (const) pointers to courses.
    Course const a = **(Course**)va;
    Course const b = **(Course**)vb;
    
    // Compare the name for two courses first.
    if (strcmp(a.name, b.name) < 0) {
        return numN;
    }
    else if (strcmp(a.name, b.name) > 0) {
        return num1;
    }
    // If the name are the same, compare the department of two courses.
    else if (strcmp(a.name, b.name) == 0) {
        if (strcmp(a.dept, b.dept) < 0) {
            return numN;
        }
        else if (strcmp(a.dept, b.dept) > 0) {
            return num1;
        }
        // If the name and department are the same, compare the number of two courses.
        else if (strcmp(a.dept, b.dept) == 0) {
            if (strcmp(a.number, b.number) < 0) {
                return numN;
            }
            else if (strcmp(a.number, b.number) > 0) {
                return num1;
            }
            else if (strcmp(a.number, b.number) == 0) {
                return num0;
            }
        }
    }
    return 0;
}

/** Comparison function used to sort list schedule command. This function sort courses by their days followed
    by their time.
    @param *va a constant void pointer.
    @param *vb another constant void pointer.
    @return -1 if the course given by first parameter should be ordered before the second one,
    0 if the course given by first parameter is equal to the second one,
    1 if the course given by first parameter should be ordered after the second one,
*/
static int compareListSchedule(void const *va, void const *vb) 
{
    // Variables storing the value to be return.
    int num0 = EQUAL;
    int num1 = ORDER_AFTER;
    int numN = ORDER_BEFORE;

    // Variable used to assign matching time to a number.
    int idxA = 0;
    int idxB = 0;
    
    // An array to store all the 6 valid time.
    char timeString[NUMBER_OF_VALID_TIME][NUMBER_OF_VALID_TIME] = {"8:30", "10:00", "11:30", "1:00", "2:30", "4:00"};
    
    // Convert void pointers to (const) pointers to courses.
    Course const a = **(Course**)va;
    Course const b = **(Course**)vb;
    
    // Loop through and assign an idx to a valid time i in the timeString if it matches.
    for (int i = 0; i < NUMBER_OF_VALID_TIME; i++) {
        if (strcmp(a.time, timeString[i]) == 0) {
            idxA = i;
        }
        if (strcmp(b.time, timeString[i]) == 0) {
            idxB = i;
        }
 
    }
    
    // Compare the days between two courses.
    if (strcmp(a.days, b.days) < 0) {
        return numN;
    }
    else if (strcmp(a.days, b.days) > 0) {
        return num1;
    }
    // If the days between two courses is the same, compare the idxA and idxB to compare time between two courses.
    else if (strcmp(a.days, b.days) == 0) {
        if (idxA < idxB) {
            return numN;
        }
        else if (idxA > idxB) {
            return num1;
        }
        else if (idxA == idxB) {
            return num0;
        }
    }
    return 0;
}

/** This function sort the courses in the schedule. It uses qsort() function 
    with the function parameter to sort the courses in the schedule.
    @param **schedule pointer to the schedule.
    @param *compare the compare function pointer to help q sort order the schedule.
    @param *size pointer to the size of the schedule.
*/
void sortSchedule(Course **schedule, int (* compare) (void const *va, void const *vb ), int *size) 
{
    qsort(schedule, *size, sizeof(Course*), compare);
}

/** This function add a course to the schedule. 
    @param **schedule pointer to the schedule.
    @param *dept the pointer to the given course department.
    @param *number the pointer to the given course number.
    @param *size pointer to the size of the schedule.
*/
void addCourse(Course **schedule, Catalog *catalog, char *dept, char *number, int *size) 
{
    // Set boolean flag to false.
    bool valid = false;
    
    // Check to see if the given course id is in the course list.
    for (int i = 0; i < catalog->count; i++) {    
        // Set boolean flag to true if given course id is in course list, otherwise it remain false.
        if (strcmp(catalog->list[i]->dept, dept) == 0 && strcmp(catalog->list[i]->number, number) == 0) {
            valid = true;         
        }   
    }
    
    int idx = 0;
    // Locate the index where given course id a course in the list.
    for (int i = 0; i < catalog->count; i++) {
        if (strcmp(catalog->list[i]->dept, dept) == 0 && strcmp(catalog->list[i]->number, number) == 0) {
            idx = i;
        }
    }
    // Used the located index to compare the given timeslot with all the courses timeslot already in schedule.
    for (int j = 0; j < *size; j++) { 
        // If schedule contains another course with the same timeslot, set boolean flag to false.
        if (strcmp(schedule[j]->days, catalog->list[idx]->days) == 0 && strcmp(schedule[j]->time, catalog->list[idx]->time) == 0){
            valid = false;
        }
    }
    
    // Check to see if the course is already in schedule.
    for (int i = 0; i < *size; i++) { 
        // Set boolean flag to false if schedule has previously been added to the schedule.
        if (strcmp(schedule[i]->dept, dept) == 0 && strcmp(schedule[i]->number, number) == 0){
            valid = false;
        }
        
    }
    // If boolean flag is false, print invalid command and return to calling function.
    if (!valid) {
        printf("Invalid command\n");
        return;
    }
    
    // Add the course in the course list that matches the given course id to the schedule.
    for (int i = 0; i < catalog->count; i++) {
        if (strcmp(catalog->list[i]->dept, dept) == 0 && strcmp(catalog->list[i]->number, number) == 0) {
            schedule[*size] = catalog->list[i];
            // Increment size when adding.
            *size = *size + 1;
        }
    }
}
/** This function drop a course from the schedule. 
    @param **schedule pointer to the schedule.
    @param *catalog pointer to the catalog.
    @param *dept the pointer to the given course department.
    @param *number the pointer to the given course number.
    @param *size pointer to the size of the schedule.
*/
void dropCourse(Course **schedule, Catalog *catalog, char *dept, char *number, int *size) 
{  
    // If the parameter are null, return to calling function.
    if (schedule == NULL || catalog == NULL || dept == NULL || number == NULL || size == NULL) {
        return;
    }
    // Set boolean flag to false.
    bool valid = false;

    // Check if course with given course id is in schedule.
    for (int i = 0; i < *size; i++) { 
        // Set boolean flag to true, if given course id is in schedule.
        if (strcmp(schedule[i]->dept, dept) == 0 && strcmp(schedule[i]->number, number) == 0){
            valid = true;
        }
    }
    
    // If boolean flag is false, print invalid command and return to calling function.
    if (!valid) {
        printf("Invalid command\n");
        return;
    }
    
    // Loop through to check if given course id is already in schedule.
    for (int i = 0; i < *size; i++) {     
        if (schedule[i] != NULL && strcmp(schedule[i]->dept, dept) == 0 && strcmp(schedule[i]->number, number) == 0) {
            // If so, remove it by shifting the array to the left.
            for (int j = i; j < *size - 1; j++) {
                schedule[j] = schedule[j+1];          
            }
            // Decrement size when removing.
            *size = *size - 1;
        }     
    } 
}

/** This function list all the courses in the student's schedule.
    @param **schedule pointer to the schedule.
    @param *size pointer to the size of the schedule.
*/
void listSchedule(Course **schedule, int *size) 
{
    // Print the header.
    printf("Course  Name                           Timeslot\n");
    // Loop through the schedule array and print each courses in the schedule in the correct format.
    for (int i = 0; i < *size; i++) {
        printf("%3s %3s %-30s %2s %5s\n", schedule[i]->dept, schedule[i]->number, schedule[i]->name, schedule[i]->days, schedule[i]->time);
    }  
}

/** This function print the calendar based on the student's schedule.
    @param **schedule pointer to the schedule.
    @param size size of the schedule.
*/
void printCalendar(Course **schedule, int size) 
{
    // Days array to compare to.
    char* days[] = {"MW", "TH", "MW", "TH"};
    // Time array to compare to.
    char* times[] = {"8:30", "10:00", "11:30", "1:00", "2:30", "4:00"};
    // Print the header of the calendar.
    printf("         Mon      Tue      Wed      Thu\n");
    
    // Nested for loop. First loop through valid time to print them.
    for (int i = 0; i < NUMBER_OF_VALID_TIME; i++) {
        printf("%5s", times[i]);
        // Loop through each days in the days array.
        for (int j = 0; j < CALENDAR_DAYS; j++) {
            // Boolean flag set to false at the start.
            bool match = false;
            // Loop through and check the days of each course in schedule with days in the days array.
            for (int t = 0; t < size; t++) {
                // If match, print the course id and set flag to true.
                if (strcmp(schedule[t]->days, days[j]) == 0 && strcmp(schedule[t]->time, times[i]) == 0) {
                    printf("  %s %s", schedule[t]->dept, schedule[t]->number);
                    match = true;
                }
            }
            // If flag is false, it means there was not a match so we print a bunch of blank spaces.
            if (!match) {
                printf("         ");
            }
        }
    // Print new line character at the end.
    printf("\n");
    }
}

/** Uses the other function to read the course file and user commands.
    Perform user commands such as list, add, drop, quit, calendar for catalog and schedule.
    @param argc the number of command-line argument.
    @param *argv the pointer to each command line argument.
    @return program exit status.
*/
int main(int argc, char *argv[]) 
{
    // Make the catalog with the makeCatalog() function.
    Catalog *catalog = makeCatalog();
    
    // Create a schedule array pointer to one course objects in catalog.
    Course **schedule = (Course **) malloc(catalog->capacity * sizeof(Course *));
    
    // If the no filenames are given on the command line, print to standard error and exit with status of 1.
    if (argc < REQUIRE_ARGS) {
        fprintf(stderr, "usage: schedule <course-file>*\n");
        exit(1);
    }
    
    // Loop through to read each course files in the commmand line.
    for (int i = 1; i < argc; i++) {
        readCourses(argv[i], catalog);
    }
    
    // Variable size to keep track of the size of schedule.
    int size = 0;
    
    // Loop through to keep asking users for commands.
    while (1) {
        
        // User prompt.
        printf("cmd> ");
        // Pointer to the line read on the command line.
        char *input = readLine(stdin);
        
        // Array to hold user commands.
        char command[MAX_STRING_LEN];
        char param1[MAX_STRING_LEN];
        char param2[MAX_STRING_LEN];
        char param3[MAX_STRING_LEN];
        
        

        // Read commands from users.
        
        
        // Break out of loop if no input.
        if (input == NULL) {
            break;
        }
        
        // Store each of commands from users.
        int match = sscanf(input, "%s %s %s %s", command, param1, param2, param3);

        // If command is list.
        if (strcmp(command, "list") == 0) {
            
            // If command is list courses, print all the courses.
            if (strcmp(param1, "courses") == 0) {
                printf("%s\n", input);
                if (match != 2) {
                    printf("Invalid command\n");
                    continue;
                }
                sortCourses(catalog, compare1);
                listCourses(catalog, truePointer, NULL, NULL);
            }
            // If the command is list names, print all the courses sorted by names.
            else if (strcmp(param1, "names") == 0) {
                printf("%s\n", input);
                if (match != 2) {
                    printf("Invalid command\n");
                    continue;
                }
                sortCourses(catalog, compareListName);
                listCourses(catalog, truePointer, NULL, NULL);
            }
            // If the command is list department, sort the courses and print all the courses that matches 
            // the given department field.
            else if (strcmp(param1, "department") == 0) {
                printf("%s\n", input);
                if (match != 3) {
                    printf("Invalid command\n");
                    continue;
                }
                // If the given department field is not 3 uppercase letters, it is invalid.
                if (strlen(param2) != DEPARTMENT_CHAR || !isupper(param2[0]) || !isupper(param2[1]) || !isupper(param2[2])) {
                    printf("Invalid command\n");
                    printf("\n");
                    free(input);
                    continue;
                }
                sortCourses(catalog, compare1);
                listCourses(catalog, matchDept, param2, NULL);
            }
            // If the command is list timeslot, sort the course by their timeslot and print 
            // all the courses the matches the given timeslot.
            else if (strcmp(param1, "timeslot") == 0) {
                printf("%s\n", input);
                // Check to see if it is a valid day.
                if (match != 4) {
                    printf("Invalid command\n");
                    continue;
                }
                if (strcmp(param2, "MW") != 0 && strcmp(param2, "TH") != 0) {
                    printf("Invalid command\n");
                    printf("\n");
                    free(input);
                    continue;
                }
                // Check to see if it is a valid time.
                if (!isTimesValid(param3)) {
                    printf("Invalid command\n");
                    printf("\n");
                    free(input);
                    continue;
                }
                sortCourses(catalog, compare1);
                listCourses(catalog, matchTime, param2, param3);
            }
            // If the command is list schedule, list all the course in the student's schedule.
            // Should be sorted by days followed by time.
            else if (strcmp(param1, "schedule") == 0) {
                printf("%s\n", input);
                if (match != 2) {
                    printf("Invalid command\n");
                    continue;
                }
                sortSchedule(schedule, compareListSchedule, &size);
                listSchedule(schedule, &size);
            }
            // If the parameter after list is not valid.
            else {
                printf("%s %s\n", command, param1);
                printf("Invalid command\n");
                
            }
        }
        // If the command is add, add course to the schedule.
        else if (strcmp(command, "add") == 0) {
            printf("%s\n", input);
            if (match != 3) {
                printf("Invalid command\n");
                continue;
            }
            // If the schedule already contain 10 courses, the command is invalid.
            if (size >= SCHEDULE_MAX_LEN) {
                printf("Invalid command\n");
                printf("\n");
                free(input);
                continue;
            }
            addCourse(schedule, catalog, param1, param2, &size);
        }
        // If the command is drop, drop the course from the schedule.
        else if(strcmp(command, "drop") == 0) {
            printf("%s\n", input);
            if (match != 3) {
                printf("Invalid command\n");
                continue;
            }
            dropCourse(schedule, catalog, param1, param2, &size);
        }
        // If the command is calendar, print the calendar for the student's schedule.
        else if(strcmp(command, "calendar") == 0) {
            printf("%s\n", input);
            if (match != 1) {
                printf("Invalid command\n");
                continue;
            }
            printCalendar(schedule, size);
        }
        // Terminate the program if the command is quit. Also terminate successfully if it reaches EOF 
        // on standard input while trying to read the next command.
        else if(strcmp(command, "quit") == 0) {
            printf("%s\n", input);
            if (match != 1) {
                printf("Invalid command\n");
                continue;
            }
            free(input);
            break;
        }
        // The the user did not give one of the 5 valid command, list, add, drop, calendar, or quit.
        // Print invalid command.
        else {
            printf("Invalid command\n");
        }
        // Print a new line character.
        printf("\n");
        // Free the input.
        free(input);
    }
    
    // Free schedule.
    free(schedule);
    // Call the freeCatalog function to free catalog.
    freeCatalog(catalog);
    
    // The standard library constant for successful execution.
    return EXIT_SUCCESS;
}
