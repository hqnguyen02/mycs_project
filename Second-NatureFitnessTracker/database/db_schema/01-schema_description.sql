-- Create a table for storing user data
CREATE TABLE user (
	usr_id int(11) unsigned NOT NULL AUTO_INCREMENT,
    usr_username varchar(150) NOT NULL UNIQUE,
    usr_email varchar(255) NOT NULL UNIQUE,
    usr_password varchar(255) NOT NULL,
    usr_salt varchar(100) NOT NULL,
    PRIMARY KEY (usr_id)
);

-- Create a table for optional session metric types
CREATE TABLE metric_type (
	name VARCHAR(128) NOT NULL,
    unit VARCHAR(128) NOT NULL,
    PRIMARY KEY (name)
);

-- Create a table for storing usable activities
CREATE TABLE activity (
	name VARCHAR(128) NOT NULL,
    is_timed bool,
    PRIMARY KEY(name)
); 

CREATE TABLE exercise_plan (
	title VARCHAR(128),
    author_id int(11) unsigned,
    PRIMARY KEY (title, author_id),
    FOREIGN KEY (author_id) REFERENCES user(usr_id)
);

-- Create a table for giving users access to specific exercise plans
CREATE TABLE user_exercise_plan (
	user_id int(11) unsigned,
    plan_title VARCHAR(128),
    plan_author_id int(11) unsigned,
    PRIMARY KEY (user_id, plan_title),
    FOREIGN KEY (user_id) REFERENCES user(usr_id),
    FOREIGN KEY (plan_title, plan_author_id) REFERENCES exercise_plan(title, author_id)
);

-- Create a table for storing activities associated with an exercise plan
CREATE TABLE plan_activity (
	plan_title VARCHAR(128),
    plan_author_id int(11) unsigned,
    activity_name VARCHAR(128),
    progress_estimate INT NOT NULL,
    num_sets INT NOT NULL,
    PRIMARY KEY (plan_title, activity_name),
    FOREIGN KEY (plan_title, plan_author_id) REFERENCES exercise_plan(title, author_id),
    FOREIGN KEY (activity_name) REFERENCES activity(name)
);

-- Create a table for tracking current or previously active sessions
CREATE TABLE active_session (
	plan_title VARCHAR(128),
    start_timestamp TIMESTAMP,
    user_id int(11) unsigned,
    end_timestamp TIMESTAMP,
    PRIMARY KEY (plan_title, start_timestamp, user_id),
    FOREIGN KEY (user_id, plan_title) REFERENCES user_exercise_plan(user_id, plan_title),
    CONSTRAINT positive_session_duration CHECK (start_timestamp < end_timestamp OR end_timestamp IS NULL)
);

-- Create a table for logging updates to an active session
CREATE TABLE session_log (
	plan_title VARCHAR(128),
    session_start_timestamp TIMESTAMP,
    user_id int(11) unsigned,
    activity_name VARCHAR(128),
    activity_set INT,
    activity_start_timestamp TIMESTAMP NOT NULL,
    activity_end_timestamp TIMESTAMP,
    progress INT NOT NULL,
    PRIMARY KEY (plan_title, session_start_timestamp, user_id, activity_name, activity_set),
    FOREIGN KEY (plan_title, session_start_timestamp, user_id) REFERENCES active_session(plan_title, start_timestamp, user_id),
    FOREIGN KEY (plan_title, activity_name) REFERENCES plan_activity(plan_title, activity_name),
    CONSTRAINT start_after_session CHECK (activity_start_timestamp >= session_start_timestamp),
    CONSTRAINT positive_duration CHECK (activity_start_timestamp < activity_end_timestamp OR activity_end_timestamp IS NULL)
);

-- Create a table for storing optional metrics associated with activities of an active session
CREATE TABLE session_log_metrics (
	plan_title VARCHAR(128),
    session_start_timestamp TIMESTAMP,
    user_id int(11) unsigned,
    activity_name VARCHAR(128),
    activity_set INT,
    metric VARCHAR(128) NOT NULL,
    value INT NOT NULL,
    PRIMARY KEY (plan_title, session_start_timestamp, user_id, activity_name, activity_set, metric),
    FOREIGN KEY (plan_title, session_start_timestamp, user_id, activity_name, activity_set) 
        REFERENCES session_log(plan_title, session_start_timestamp, user_id, activity_name, activity_set),
    FOREIGN KEY (metric) REFERENCES metric_type(name) 
);