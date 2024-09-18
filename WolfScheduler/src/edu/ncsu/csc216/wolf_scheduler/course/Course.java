package edu.ncsu.csc216.wolf_scheduler.course;

/**
 * This course class includes methods to construct course which includes name,
 * title, section, credit, instructor, meeting days, start time and end time
 * 
 * @author Huy Nguyen
 */
public class Course {

	/** Course's name. */
	private String name;
	/** Course's title. */
	private String title;
	/** Course's section. */
	private String section;
	/** Course's credit hours */
	private int credits;
	/** Course's instructor */
	private String instructorId;
	/** Course's meeting days */
	private String meetingDays;
	/** Course's starting time */
	private int startTime;
	/** Course's ending time */
	private int endTime;
	/** Minimum length of course's name */
	private static final int MIN_NAME_LENGTH = 5;
	/** Maximum length of course's name */
	private static final int MAX_NAME_LENGTH = 8;
	/** Minimum letter count */
	private static final int MIN_LETTER_COUNT = 1;
	/** Maximum letter count */
	private static final int MAX_LETTER_COUNT = 4;
	/** Number of digit in course's name */
	private static final int DIGIT_COUNT = 3;
	/** Length of section's name */
	private static final int SECTION_LENGTH = 3;
	/** Maximum course credits */
	private static final int MAX_CREDITS = 5;
	/** Minimum course credits */
	private static final int MIN_CREDITS = 1;
	/** Hour upper limit */
	private static final int UPPER_HOUR = 23;
	/** Minute upper limit */
	private static final int UPPER_MINUTE = 59;


	/**
	 * Constructs a Course object with values for all fields.
	 * 
	 * @param name         name of Course
	 * @param title        title of Course
	 * @param section      section of Course
	 * @param credits      credit hours for Course
	 * @param instructorId instructor's unity id
	 * @param meetingDays  meeting days for Course as series of chars
	 * @param startTime    start time for Course
	 * @param endTime      end time for Course
	 */
	public Course(String name, String title, String section, int credits, String instructorId, String meetingDays,
			int startTime, int endTime) {
		setName(name);
		setTitle(title);
		setSection(section);
		setCredits(credits);
		setInstructorId(instructorId);
		setMeetingDaysAndTime(meetingDays, startTime, endTime);
	}

	/**
	 * Creates a Course with the given name, title, section, credits, instructorId,
	 * and meetingDays for courses that are arranged.
	 * 
	 * @param name         name of Course
	 * @param title        title of Course
	 * @param section      section of Course
	 * @param credits      credit hours for Course
	 * @param instructorId instructor's unity id
	 * @param meetingDays  meeting days for Course as series of chars
	 */
	public Course(String name, String title, String section, int credits, String instructorId, String meetingDays) {
		this(name, title, section, credits, instructorId, meetingDays, 0, 0);
	}

	/**
	 * Returns the Course's name.
	 * 
	 * @return name the course name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the Course's name. If the name is null, has a length less than 5 or more
	 * than 8, does not contain a space between letter characters and number
	 * characters, has less than 1 or more than 4 letter characters, and not exactly
	 * three trailing digit characters, an IllegalArgumentException is thrown.
	 * 
	 * @param name the name to set
	 * @throws IllegalArgumentException if the name parameter is invalid
	 */

	private void setName(String name) {
		// Throw exception if the name is null
		if (name == null) {
			throw new IllegalArgumentException("Invalid course name.");
		}
		// Throw exception if the name is an empty string
		// Throw exception if the name contains less than 5 character or greater than 8
		// characters
		if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid course name.");
		}
		// Check for pattern of L[LLL] NNN
		boolean spaceFlag = false;
		int countLetter = 0;
		int countDigits = 0;

		for (int i = 0; i < name.length(); i++) {

			if (!spaceFlag) {
				if (Character.isLetter(name.charAt(i))) {
					countLetter++;
				}
				else if (name.charAt(i) == ' ') {
					spaceFlag = true;
				} else {
					throw new IllegalArgumentException("Invalid course name.");
				}
			} else {
				if (Character.isDigit(name.charAt(i))) {
					countDigits++;
				} else {
					throw new IllegalArgumentException("Invalid course name.");
				}

			}
		}
		// Check that the number of letters is correct
		if (countLetter < MIN_LETTER_COUNT || countLetter > MAX_LETTER_COUNT)
			throw new IllegalArgumentException("Invalid course name.");

		// Check that the number of digits is correct
		if (countDigits != DIGIT_COUNT)
			throw new IllegalArgumentException("Invalid course name.");

		this.name = name;
		
	}

	/**
	 * Returns the Course's title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the Course's title.
	 * 
	 * @param title the title to set
	 * @throws IllegalArgumentException if title is null or empty
	 */
	public void setTitle(String title) {
		// Conditional 1:
		if (title == null || "".equals(title)) {
			throw new IllegalArgumentException("Invalid title.");
		}

		this.title = title;
	}

	/**
	 * Returns the Course's section.
	 * 
	 * @return the section
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Sets the Course's section.
	 * 
	 * @param section the section to set
	 * @throws IllegalArgumentException if section is null or not three characters
	 * @throws IllegalArgumentException if any of section’s three characters are not
	 *                                  digits
	 */
	public void setSection(String section) {
		if (section == null || section.length() != SECTION_LENGTH) {
			throw new IllegalArgumentException("Invalid section.");
		}
		for (int i = 0; i < SECTION_LENGTH; i++) {
			if (!Character.isDigit(section.charAt(i))) {
				throw new IllegalArgumentException("Invalid section.");
			}
		}
		this.section = section;
	}

	/**
	 * Returns the Course's credits.
	 * 
	 * @return the credits
	 */
	public int getCredits() {
		return credits;
	}

	/**
	 * Sets the Course's credits.
	 * 
	 * @param credits the credits to set
	 * @throws IllegalArgumentException if credits is out of bounds
	 */
	public void setCredits(int credits) {
		if (credits < MIN_CREDITS || credits > MAX_CREDITS) {
			throw new IllegalArgumentException("Invalid credits.");
		}
		this.credits = credits;
	}

	/**
	 * Returns the Course's instructorId.
	 * 
	 * @return the instructorId
	 */
	public String getInstructorId() {
		return instructorId;
	}

	/**
	 * Sets the Course's instructorId.
	 * 
	 * @param instructorId the instructorId to set
	 * @throws IllegalArgumentException if instructorId is null or empty string
	 */
	public void setInstructorId(String instructorId) {
		if (instructorId == null || "".equals(instructorId)) {
			throw new IllegalArgumentException("Invalid instructor id.");
		}
		this.instructorId = instructorId;
	}

	/**
	 * Set meeting days, start time, and end time for course
	 * 
	 * @param meetingDays the meetingDays to set
	 * @param startTime   the startTime to set
	 * @param endTime     the endTime to set
	 * @throws IllegalArgumentException if meeting days is null, empty, or contains
	 *                                  invalid characters
	 * @throws IllegalArgumentException if an arranged class has non-zero start or
	 *                                  end times
	 * @throws IllegalArgumentException if start time is an incorrect time
	 * @throws IllegalArgumentException if end time is an incorrect time
	 * @throws IllegalArgumentException if end time is less than start time
	 *
	 */
	public void setMeetingDaysAndTime(String meetingDays, int startTime, int endTime) {

		int startHour;
		int startMin;
		int endHour;
		int endMin;

		if (meetingDays == null || "".equals(meetingDays))
			throw new IllegalArgumentException("Invalid meeting days and times."); // IAE = IllegalArgumentException

		if ("A".equals(meetingDays)) { // Arranged
			if (startTime != 0 || endTime != 0)
				throw new IllegalArgumentException("Invalid meeting days and times.");
			this.meetingDays = meetingDays;
			this.startTime = 0;
			this.endTime = 0;
		} else { // not arranged
			int mondayCount = 0;
			int tuesdayCount = 0;
			int wednesdayCount = 0;
			int thursdayCount = 0;
			int fridayCount = 0;

			for (int i = 0; i < meetingDays.length(); i++)
				if (meetingDays.charAt(i) == 'M') {
					mondayCount++;
				} else if (meetingDays.charAt(i) == 'T') {
					tuesdayCount++;
				} else if (meetingDays.charAt(i) == 'W') {
					wednesdayCount++;
				} else if (meetingDays.charAt(i) == 'H') {
					thursdayCount++;
				} else if (meetingDays.charAt(i) == 'F') {
					fridayCount++;
				} else {
					throw new IllegalArgumentException("Invalid meeting days and times.");
				}
			// check for duplicates
			if (mondayCount > 1 || tuesdayCount > 1 || wednesdayCount > 1 || thursdayCount > 1 || fridayCount > 1) {
				throw new IllegalArgumentException("Invalid meeting days and times.");
			}

			if (startTime > endTime) {
				throw new IllegalArgumentException("Invalid meeting days and times.");
			}

			// break apart startTime and endTime into hours and minutes (several lines of
			// code)
			startHour = startTime / 100;
			startMin = startTime % 100;
			endHour = endTime / 100;
			endMin = endTime % 100;

			if (startHour < 0 || startHour > UPPER_HOUR) // not between 0 and 23, inclusive
				throw new IllegalArgumentException("Invalid meeting days and times.");

			if (startMin < 0 || startMin > UPPER_MINUTE) // not between 0 and 59, inclusive
				throw new IllegalArgumentException("Invalid meeting days and times.");

			if (endHour < 0 || endHour > UPPER_HOUR) // not between 0 and 23, inclusive
				throw new IllegalArgumentException("Invalid meeting days and times.");

			if (endMin < 0 || endMin > UPPER_MINUTE) // not between 0 and 59, inclusive
				throw new IllegalArgumentException("Invalid meeting days and times.");

			// everything is valid and works together!
			this.meetingDays = meetingDays;
			this.startTime = startTime;
			this.endTime = endTime;

		}
	}

	/**
	 * Helper method to convert military time to standard time
	 * 
	 * @return timeString
	 * @param time the time to set
	 */
	private String getTimeString(int time) {

		int hr = time / 100;
		int min = time % 100;
		String minutes = "" + min;
		String amOrPm = "AM";
		String timeString = "";
		
		if (min < 10) {
			minutes = "0" + min;
		}
		if (hr > 12 ) {
			hr = hr - 12;
			amOrPm = "PM";
		}
		if (hr == 12) {
			amOrPm = "PM";
		}
		else if (hr == 0) {
			hr = 12;
			amOrPm = "AM";
		}
		
		timeString = "" + hr + ":" + minutes + amOrPm;
		return timeString;
		
	}

	/**
	 * Returns the meeting days and times with the assigned format (AM/PM)
	 * 
	 * @return the meeting days and times in the correct format
	 */
	public String getMeetingString() {
		String startTimeFinal = getTimeString(this.startTime);
		String end = getTimeString(this.endTime);
		
		if ("A".equals(this.meetingDays)) {
			return "Arranged";
		}
		else {
			return meetingDays + " " + startTimeFinal + "-" + end;
		}

	}

	/**
	 * Returns the Course's meetingDays.
	 * 
	 * @return the meetingDays
	 */
	public String getMeetingDays() {
		return meetingDays;
	}

	/**
	 * Returns the Course's startTime.
	 * 
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Returns the Course's endTime.
	 * 
	 * @return the endTime
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * Generates a hashCode for Course using all fields.
	 * 
	 * @return hashCode for Course
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + credits;
		result = prime * result + endTime;
		result = prime * result + ((instructorId == null) ? 0 : instructorId.hashCode());
		result = prime * result + ((meetingDays == null) ? 0 : meetingDays.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		result = prime * result + startTime;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/**
	 * Compares a given object to this object for equality on all fields.
	 * 
	 * @param obj the object to compare
	 * @return true if the objects are the same on all fields.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (credits != other.credits)
			return false;
		if (endTime != other.endTime)
			return false;
		if (instructorId == null) {
			if (other.instructorId != null)
				return false;
		} else if (!instructorId.equals(other.instructorId))
			return false;
		if (meetingDays == null) {
			if (other.meetingDays != null)
				return false;
		} else if (!meetingDays.equals(other.meetingDays))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		if (startTime != other.startTime)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	/**
	 * Returns a comma separated value String of all Course fields.
	 * 
	 * @return String representation of Course
	 */
	@Override
	public String toString() {
		if ("A".equals(meetingDays)) {
			return name + "," + title + "," + section + "," + credits + "," + instructorId + "," + meetingDays;
		}
		return name + "," + title + "," + section + "," + credits + "," + instructorId + "," + meetingDays + ","
				+ startTime + "," + endTime;
	}

}
