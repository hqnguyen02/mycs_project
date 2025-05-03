-- Create users

INSERT INTO user (usr_username, usr_email, usr_password, usr_salt) VALUES
	('aschwarze', 'aschwarze@gmail.com', '83d9bdb5e20f3571b087db9aabf190a296741c3e864d7742f35658cfccc1b79c4599aad25084aa9a28c649a50c92244227b3e53e197621301d619d1ea01873c4', '48c8947f69c054a5caa934674ce8881d02bb18fb59d5a63eeaddff735b0e9'),
	('jdoe', 'jdoe@gmail.com', 'e289219c34f9a32ebc82393f09719b7f34872de95463242b5ffe8bb4b11a5fe7d454f9f5d082c8207c5d69b220ba06624b4bb15ffa05cc7d7d53c43f9e96da6a', '801e87294783281ae49fc8287a0fd86779b27d7972d3e84f0fa0d826d7cb67dfefc');
    
-- Create exercise plans
-- Assuming userId=1 for aschwarze and userId=2 for jdoe based on insertion order
INSERT INTO exercise_plan VALUES
	('Cardio-Focus', 2),
    ('Muscular Endurance', 2),
    ('Muscular Strength', 1);
    
-- Specify who can access the exercise plans
INSERT INTO user_exercise_plan VALUES
	(2, 'Cardio-Focus', 2),
    (1, 'Cardio-Focus', 2),
    (2, 'Muscular Endurance', 2),
    (1, 'Muscular Strength', 1);
    
-- Create activities used to build exercise plans 
    
INSERT INTO activity VALUES
	('Sprint', true),
    ('Running', true),
    ('Bench Press', false),
    ('Cycling', true),
    ('Plank', true),
    ('Push-up', false),
    ('Crunch', false);
    
-- Build exercise plans
INSERT INTO plan_activity VALUES
	('Cardio-Focus', 2, 'Sprint', 300, 3),
    ('Cardio-Focus', 2, 'Plank', 180, 3),
    ('Cardio-Focus', 2, 'Running', 600, 1);
INSERT INTO plan_activity VALUES
	('Muscular Endurance', 2, 'Bench Press', 12, 3),
    ('Muscular Endurance', 2, 'Push-up', 20, 3),
    ('Muscular Endurance', 2, 'Plank', 180, 3);
INSERT INTO plan_activity VALUES
	('Muscular Strength', 1, 'Bench Press', 20, 2),
    ('Muscular Strength', 1, 'Push-up', 40, 3),
    ('Muscular Strength', 1, 'Plank', 240, 2);
    

-- Create a history of active sessions
INSERT INTO active_session (plan_title, start_timestamp, user_id, end_timestamp) VALUES 
	('Cardio-Focus', '2024-03-11T12:00:00', 2, '2024-03-11T13:00:00'),
    ('Cardio-Focus', '2024-03-11T12:00:00', 1, '2024-03-11T13:30:00'),
    ('Muscular Strength', '2024-03-11T12:00:00', 1, null);
    
-- Populate log of active plan sessions
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    1,
    '2024-03-11T12:00:00',
    '2024-03-11T12:05:00',
    300
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    2,
    '2024-03-11T12:05:00',
    '2024-03-11T12:12:00',
    360
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    3,
    '2024-03-11T12:15:00',
    '2024-03-11T12:25:00',
    330
);
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Plank',
    1,
    '2024-03-11T12:30:00',
    '2024-03-11T12:33:00',
    180
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Plank',
    2,
    '2024-03-11T12:33:00',
    '2024-03-11T12:36:00',
    180
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Plank',
    3,
    '2024-03-11T12:36:00',
    '2024-03-11T12:39:00',
    180
);
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Running',
    1,
    '2024-03-11T12:45:00',
    '2024-03-11T12:50:00',
    400
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Running',
    2,
    '2024-03-11T12:52:00',
    '2024-03-11T13:00:00',
    300
);

-- Populate the second active session
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Sprint',
    1,
    '2024-03-11T12:00:00',
    '2024-03-11T12:05:00',
    300
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Sprint',
    2,
    '2024-03-11T12:05:00',
    '2024-03-11T12:12:00',
    360
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Sprint',
    3,
    '2024-03-11T12:15:00',
    '2024-03-11T12:25:00',
    330
);
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    1,
    '2024-03-11T12:30:00',
    '2024-03-11T12:33:00',
    180
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    2,
    '2024-03-11T12:33:00',
    '2024-03-11T12:36:00',
    180
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    3,
    '2024-03-11T12:36:00',
    '2024-03-11T12:39:00',
    180
);
INSERT INTO session_log VALUES (
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Running',
    1,
    '2024-03-11T12:45:00',
    '2024-03-11T12:50:00',
    400
),
(
    'Cardio-Focus',
    '2024-03-11T12:00:00',
    1,
    'Running',
    2,
    '2024-03-11T12:52:00',
    '2024-03-11T13:00:00',
    300
);

-- Populate the third (unfinished) active session
INSERT INTO session_log VALUES (
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Push-up',
    1,
    '2024-03-11T12:05:00',
    '2024-03-11T12:10:00',
    20
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    1,
    '2024-03-11T12:15:00',
    '2024-03-11T12:20:00',
    12
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    1,
    '2024-03-11T12:25:00',
    '2024-03-11T12:30:00',
    60
);
INSERT INTO session_log VALUES (
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Push-up',
    2,
    '2024-03-11T12:30:00',
    '2024-03-11T12:35:00',
    20
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    2,
    '2024-03-11T12:40:00',
    '2024-03-11T12:45:00',
    12
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    2,
    '2024-03-11T12:50:00',
    '2024-03-11T12:55:00',
    60
);
INSERT INTO session_log VALUES (
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Push-up',
    3,
    '2024-03-11T13:00:00',
    '2024-03-11T13:05:00',
    18
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    3,
    '2024-03-11T13:10:00',
    '2024-03-11T13:15:00',
    10
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Plank',
    3,
    '2024-03-11T13:20:00',
    '2024-03-11T13:25:00',
    60
);

-- Add additional types of metrics to track
INSERT INTO metric_type VALUES 
	('Gradient', '%'),
	('Weight (kg)', 'kg'),
    ('Weight (lb)', 'lb'),
    ('Heart Rate', 'BPM'),
    ('Average Running Speed (m/s)', 'm/s'),
    ('Average Running Speed (mi/h)', 'mi/h'),
    ('Average Cycling Speed (RPM)', 'RPM'),
    ('Average Power (Watts)', 'W');
    
-- Additional example optional metrics
INSERT INTO session_log_metrics VALUES (
	'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    1,
    'Average Running Speed (mi/h)',
    8
),
(
	'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    2,
    'Average Running Speed (mi/h)',
    6
),
(
	'Cardio-Focus',
    '2024-03-11T12:00:00',
    2,
    'Sprint',
    3,
    'Average Running Speed (mi/h)',
    7.5
);

    
-- Additional example optional metrics
INSERT INTO session_log_metrics VALUES (
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    1,
    'Weight (lb)',
    150
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    2,
    'Weight (lb)',
    140
),
(
	'Muscular Strength',
    '2024-03-11T12:00:00',
    1,
    'Bench Press',
    3,
    'Weight (lb)',
    135
);