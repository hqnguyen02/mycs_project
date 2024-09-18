package edu.ncsu.csc216.wolf_scheduler.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import edu.ncsu.csc216.wolf_scheduler.course.Course;

/**
 * Reads Course records from text files. Writes a set of CourseRecords to a
 * file.
 * 
 * @author Huy Nguyen
 */
public class CourseRecordIO {

	/**
	 * Reads course records from a file and generates a list of valid Courses. Any
	 * invalid Courses are ignored. If the file to read cannot be found or the
	 * permissions are incorrect a File NotFoundException is thrown.
	 * 
	 * @param fileName file to read Course records from
	 * @return a list of valid Courses
	 * @throws FileNotFoundException if the file cannot be found or read
	 */
	public static ArrayList<Course> readCourseRecords(String fileName) throws FileNotFoundException {

		Scanner fileReader = new Scanner(new FileInputStream(fileName));
		ArrayList<Course> courses = new ArrayList<Course>(); // Create an empty array of Course objects
		while (fileReader.hasNextLine()) { // While we have more lines in the file
			try { // Attempt to do the following
					// Read the line, process it in readCourse, and get the object
					// If trying to construct a Course in readCourse() results in an exception, flow
					// of control will transfer to the catch block, below

				Course course = readCourse(fileReader.nextLine());

				// Create a flag to see if the newly created Course is a duplicate of something
				// already in the list
				boolean duplicate = false;
				// Look at all the courses in our list
				for (int i = 0; i < courses.size(); i++) {
					// Get the course at index i
					Course current = courses.get(i);
					// Check if the name and section are the same
					if (course.getName().equals(current.getName())
							&& course.getSection().equals(current.getSection())) {
						// It's a duplicate!
						duplicate = true;
						break; // We can break out of the loop, no need to continue searching
					}
				}
				// If the course is NOT a duplicate
				if (!duplicate) {
					courses.add(course); // Add to the ArrayList!
				} // Otherwise ignore
			} catch (IllegalArgumentException e) {
				// The line is invalid b/c we couldn't create a course, skip it!
			}
		}
		fileReader.close();

		return courses;
	}

	/**
	 * readCourse is a method use to read course classes
	 * 
	 * @param line the line to set
	 * @return a newly constructed course object
	 * @throws IllegalArgumentException if there are more tokens
	 */
	private static Course readCourse(String line) {
		Scanner courseReader = new Scanner(line);
		courseReader.useDelimiter(",");

		try {
			// read in tokens for name, title, section, credits, instructorId, and
			// meetingDays and store in local variables
			String name = courseReader.next();
			String title = courseReader.next();
			String section = courseReader.next();
			int creditHours = courseReader.nextInt();
			String instructor = courseReader.next();
			String meetingDay = courseReader.next();
			int startTime;
			int endTime;

			if ("A".equals(meetingDay)) {
				if (courseReader.hasNext()) {
					courseReader.close();
					throw new IllegalArgumentException();
				} else {
					courseReader.close();
					return new Course(name, title, section, creditHours, instructor, meetingDay);
				}
			} else {
				startTime = courseReader.nextInt();
				endTime = courseReader.nextInt();
				if (courseReader.hasNext()) {
					courseReader.close();
					throw new IllegalArgumentException();
				}
				courseReader.close();
				return new Course(name, title, section, creditHours, instructor, meetingDay, startTime, endTime);

			}
		} catch (Exception e) {// any exceptions and throw a new IllegalArgumentException
			throw new IllegalArgumentException();
		}
		// Remember to close the scanner on all possible paths out of the method -
		// that's not in the pseudocode

	}

	/**
	 * Writes the given list of Courses to
	 * 
	 * @param fileName file to write schedule of Courses to
	 * @param courses  list of Courses to write
	 * @throws IOException if cannot write to file
	 */
	public static void writeCourseRecords(String fileName, ArrayList<Course> courses) throws IOException {

		PrintStream fileWriter = new PrintStream(new File(fileName));

		for (int i = 0; i < courses.size(); i++) {
			fileWriter.println(courses.get(i).toString());
		}

		fileWriter.close();

	}

}
