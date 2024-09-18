/** @file catalog.h
    @author Huy Nguyen (hqnguyen)
    This is the header file for the components that make/free catalog as well as 
    read, sort, and list courses.
*/
#include <stdbool.h>

/** Length of department field accounting for null character. */
#define DEPARTMENT_LEN 4

/** Length of number field accounting for null character. */
#define NUMBER_LEN 4

/** Length of days field accounting for null character. */
#define DAYS_LEN 3

/** Length of time field accounting for null character. */
#define TIME_LEN 6

/** Length of name field accounting for null character. */
#define NAME_LEN 31

/** The initial capacity of catalog->list */
#define STARTING_CAPACITY 5

/** There are 6 valid time */
#define NUMBER_OF_VALID_TIME 6

/** Expected length of string array */
#define MAX_STRING_LEN 101

/** Expected number of fields for course */
#define FIELDS 5

/** Number of letters in department field */
#define DEPARTMENT_LETTERS 3

/** Number of letters in number field */
#define NUMBER_LETTERS 3

/** Number of letters in name field */
#define NAME_LETTERS 30

/** This is the course struct with five fields representing the course department,
    number, days, time, and name. All of the fields will be stored as strings.
*/
typedef struct {
    char dept[DEPARTMENT_LEN];
    char number[NUMBER_LEN];
    char days[DAYS_LEN];
    char time[TIME_LEN];
    char name[NAME_LEN];
    
} Course;

/** The catalog struct contain fields to store a resizable array of pointers to Course.
    Count and capacity field will be used to resize the array and the list field will
    keep track of all the courses.
*/
typedef struct {
    Course **list;
    int count;
    int capacity;
    
} Catalog;

/** This function dynamically allocate memory for the Catalog and contruct the Catalog.
    @return a pointer to the new catalog.
*/
Catalog *makeCatalog();

/** This function free all the memory for the given Catalog.
    @param *catalog pointer to the given catalog.
*/
void freeCatalog(Catalog *catalog);

/** This function read all the courses from a course file.
    @param *filename pointer to the course file.
    @param *catalog pointer to the catalog.
*/
void readCourses( char const *filename, Catalog *catalog );

/** This function sort the courses in the catalog. It uses qsort() function 
    with the function parameter to sort the courses in the schedule.
    @param *catalog pointer to the catalog.
    @param *compare the compare function pointer to help q sort order the schedule.
*/
void sortCourses( Catalog *catalog, int (* compare) (void const *va, void const *vb ));

/** This function print all of courses in the lsit. Uses funciton pointer parameter 
    to determine what courses to print.
    @param *catalog pointer to the catalog.
    @param *test the test function.
    @param *str1 first void constant.
    @param *str2 second void constant.
*/
void listCourses( Catalog *catalog, bool (*test)( Course const *course, char const *str1, char const *str2 ), char const *str1, char const *str2 );

/** Helper method to check if the time given is one of the 6 valid times.
    @param *timeStr pointer to a given course time.
    @return true if the given time is valid and false otherwise.
*/
bool isTimesValid(char *timeStr);
