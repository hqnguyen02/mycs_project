package edu.ncsu.csc216.wolf_scheduler.scheduler;

import java.util.ArrayList;

import edu.ncsu.csc216.wolf_scheduler.course.Course;
import edu.ncsu.csc216.wolf_scheduler.io.CourseRecordIO;

/**
 * WolfScheduler reads in and stores as a list all of the Course records stored
 * in a file
 * 
 * @author Huy Nguyen
 */
public class WolfScheduler {
	/** ArrayList for catalog */
	private ArrayList<Course> catalog = new ArrayList<Course>();
	/** ArrayList for schedule */
	private ArrayList<Course> schedule = new ArrayList<Course>();
	/** course's title */
	private String title;

	/**
	 * WolfScheduler reads in and stores as a list all of the Course records stored
	 * in a file. Wolf Scheduler also creates a schedule for user which includes
	 * adding course to schedule, removing course from schedule and resetting the
	 * schedule.
	 * 
	 * @param filename the filename to set
	 * @throws IllegalArgumentException if the read method throw an exception
	 */
	public WolfScheduler(String filename) {
		schedule = new ArrayList<Course>();
		title = "My Schedule";
		try {
			catalog = CourseRecordIO.readCourseRecords(filename);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot find file.");
		}
	}

	/**
	 * Returns a 2D String array of the catalog
	 * 
	 * @return the new course catalog
	 */
	public String[][] getCourseCatalog() {
		String[][] newCatalog = new String[catalog.size()][3];
		if (catalog.size() > 0) {
			for (int i = 0; i < catalog.size(); i++) {
				Course currentCourse = catalog.get(i);
				newCatalog[i][0] = currentCourse.getName();
				newCatalog[i][1] = currentCourse.getSection();
				newCatalog[i][2] = currentCourse.getTitle();
			}
		}
		return newCatalog;
	}

	/**
	 * Returns 2D String array of schedule
	 * 
	 * @return the new schedule
	 */
	public String[][] getScheduledCourses() {
		String[][] newSchedule = new String[schedule.size()][3];
		if (schedule.size() > 0) {
			for (int i = 0; i < schedule.size(); i++) {
				Course currentSchedule = schedule.get(i);
				newSchedule[i][0] = currentSchedule.getName();
				newSchedule[i][1] = currentSchedule.getSection();
				newSchedule[i][2] = currentSchedule.getTitle();
			}
		}
		return newSchedule;
	}

	/**
	 * Returns 2D String Array of the schedule with all the infos
	 * 
	 * @return the new full schedule
	 */
	public String[][] getFullScheduledCourses() {
		String[][] newFullSchedule = new String[schedule.size()][6];
		if (schedule.size() > 0) {
			for (int i = 0; i < schedule.size(); i++) {
				Course currentSchedule = schedule.get(i);

				newFullSchedule[i][0] = currentSchedule.getName();
				newFullSchedule[i][1] = currentSchedule.getSection();
				newFullSchedule[i][2] = currentSchedule.getTitle();
				newFullSchedule[i][3] = String.valueOf(currentSchedule.getCredits());
				newFullSchedule[i][4] = currentSchedule.getInstructorId();
				newFullSchedule[i][5] = currentSchedule.getMeetingString();

			}
		}
		return newFullSchedule;
	}

	/**
	 * Returns the schedule title
	 * 
	 * @return the title
	 */
	public String getScheduleTitle() {
		return title;
	}

	/**
	 * Export the file using the writeCourse method.
	 * 
	 * @param filename the filename to set
	 * @throws IllegalArgumentException if the write method throw an IOException
	 */
	public void exportSchedule(String filename) {
		try {
			CourseRecordIO.writeCourseRecords(filename, schedule);
		} catch (Exception e) {
			throw new IllegalArgumentException("The file cannot be saved");
		}

	}

	/**
	 * Find course in catalog and return the course
	 * 
	 * @param name    the name to set
	 * @param section the name to set
	 * @return the new course from catalog
	 */
	public Course getCourseFromCatalog(String name, String section) {
		for (int i = 0; i < catalog.size(); i++) {
			Course newCourse = catalog.get(i);
			if (newCourse.getName().equals(name) && newCourse.getSection().equals(section)) {
				return newCourse;
			}
		}
		return null;
	}

	/**
	 * This method add course to schedule
	 * 
	 * @param name    the name to set
	 * @param section the section to set
	 * @return false if course is not in catalog and cannot be added to schedule
	 *         true if course exists in the catalog and is successfully added to the
	 *         student's schedule
	 * @throws IllegalArgumentException if a course with the same name is already in
	 *                                  schedule
	 */
	public boolean addCourseToSchedule(String name, String section) {

		if (getCourseFromCatalog(name, section) == null) {
			return false;
		}

		for (Course newCourse : schedule) {
			if (name.equals(newCourse.getName())) {
				throw new IllegalArgumentException("You are already enrolled in " + newCourse.getName());
			}
		}
		schedule.add(getCourseFromCatalog(name, section));
		return true;
	}

	/**
	 * This method remove course from schedule
	 * 
	 * @param name    the name to set
	 * @param section the section to set
	 * @return true if course can be removed from schedule false if course is not in
	 *         schedule
	 */
	public boolean removeCourseFromSchedule(String name, String section) {
		for (Course newCourse : schedule) {
			if (name.equals(newCourse.getName())) {
				schedule.remove(getCourseFromCatalog(name, section));
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the title of schedule
	 * 
	 * @param title the title to set
	 * @throws IllegalArgumentException if the title is null
	 */
	public void setScheduleTitle(String title) {
		if (title == null) {
			throw new IllegalArgumentException("Title cannot be null.");
		}
		this.title = title;
	}

	/**
	 * Clear the schedule
	 */
	public void resetSchedule() {
		schedule.clear();
	}

}
