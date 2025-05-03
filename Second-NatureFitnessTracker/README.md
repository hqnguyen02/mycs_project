# Final Team Project

## Second-Nature Fitness Tracker

## Progress Report

### Completed Features

* Implement all API routes completely with proper authentication and authorization
* Added MariaDB to the backend as data storage
* Dynamically show cumulative progress as a line graph by day
* Dynamically display stored exercise plans and active plans
* Ability to create and end active plans
* Ability to share stored exercise plans with other users
* Status icons change when activities are completed in the **Active Plan** page
* All app pages can be served offline using cache API with the exception of main dashboard pages that uses IndexedDB as persistent storage
* Offline pages for any functionalities that can not be provided while offline
* PWA is installable with appropriate icons and theme

### Known Issues & Limitations

* Our pages does not provide dynamic offline functionalities for post/put request
* Network may respond slowly even if resource has not changed with Network-First caching strategy
* There seems to be an issue where you may need to refresh the app at least once after installing the service worker to cache missing resources. Failing to do so might cause the user to see a blank screen. This issue occurred even when all static files were specified to be cached at installation.
* There is no functionality for deleting exercise plans. It was not an explicit requirement but is typically expected by users.
* It's possible to add or edit activities within an exercise plan, but it is not possible to remove activities from a stored exercise plan. It was not an explicit requirement but is typically expected by users.


## Authentication & Authorization

We use token-based authentication with the jsonwebtoken library to secure our API. When a user signs in successfully, a JWT is generated and returned to the client. The token is then verified for authenticity and its expiration date is checked. Our project currently supports only one type of user, so we apply the token middleware globally to all protected routes—excluding the sign-in, sign-up, and logout routes for authorization. This ensures only authenticated users can access the app's functionality and provides a `User` object that subsequent middleware or route handlers can use. This `User` object is used by the route handlers to query only the relevant data for a particular user that they are authorized to access.

User credentials are stored in the user table of the MariaDB database. When a user register an account, their password is first salted and hashed using crypto.pbkdf2. The user table contain the user's userid, username, email, hashed password, and salt. This logic is handled by AuthDAO.js on the backend.

## PWA Capabilities

<!-- Describe features available to your users offline, caching strategy, installability, theming, etc. -->
Our app includes the following pages: Sign Up, Sign In, Main Dashboard, Create Exercise Plan, and View Exercise Log. Users can navigate between these pages using a navigation bar that provides quick access to Sign In, Home (Main Dashboard), Create Exercise Plan, View Exercise Log, and a Log Out button. Offline functionality is supported on the Main Dashboard which loads data from IndexedDB for persistent access. The Create Exercise Plan and View Exercise Log pages are available offline using the Cache API. The Sign In and Sign Up pages require network access and will display an offline page if user tries to sign up or sign in while offline. If a feature cannot function offline, users are shown an offline page that informs them they need to come back online to use that service. Our PWA is installable with appropriate icons and theming that align with the app’s overall design. Users are able to retrieve resources they have previously accessed through `GET` api such as their stored exercise plans, activities, and metrics while offline. Users are not able to share exercise plans, create or edit exercise plans, or start or update an active exercise plan session while offline.

Our Progressive Web App (PWA) supports offline usage by using a network-first caching strategy for API endpoints since some API endpoints require fresh data and cannot rely on cache alone. Users are able to create and edit exercise plans, and user activity would change the metrics returned to display in the activity graph. When the service worker is installed, the application caches all static files. Static files like HTML, Javascript, and JSX files are retrieved with a cache-first strategy since these requests should return the same response each time. This hybrid approach allows the app to respond to updated information while minimizing the amount of network requests made. If a response has not been cached, the service worker responds with a specific offline response depending on whether the request was for API, JSX, or a frontend route. This allows the app to handle failed network requests gracefully.


## API Documentation

| Method | Route | Description |
| :---- | :---- | :---- |
| GET | /activities | Retrieves all stored activities |
| POST | /activities | Saves a named activity measured by duration or repetitions |
| GET | /activities/:activityId/metrics | Retrieves metrics associated with a named activity recorded over all exercise sessions for the current user |
| POST | /signin | Receives a username and password |
| POST | /signup | Registers a user from a unique username and a password |
| POST | /logout | Logs the user out of the system |
| GET | /users/current | Retrieves the currently logged-in user |
| GET | /plans/active | Returns the currently active exercise plan or null for a user |
| PUT | /plans/active | Updates the progress of an active exercise plan |
| PUT | /plans/active/end | Ends a current active plan |
| GET | /plans/:planId | Returns a stored exercise plan accessible to a user by its ID |
| POST | /plans/active | Starts a new active exercise plan session by exercise plan name for the current user |
| POST | /plans | Creates a new exercise plan with a unique title and containing a list of activities for the current user |
| PUT | /users/:userId/plans/:planId | Shares a stored exercise plan with another user by username |
| GET | /users/plans | Returns an array of stored exercise plans associated with the current user |
| POST | /subscribetopush | Saves a user's push notification subscription using their ID and returns the subscription in the response |

## Database ER Diagram

![](https://github.ncsu.edu/engr-csc342/csc342-2025Spring-TeamS/blob/main/Milestone2/img/csc342_m2_er.drawio.png)

## Team Member Contributions

#### Huy Nguyen

### Proposal Recap
* Helped facilitate discussions during our meeting
* Worked on the wireframes for the desktop views of the application
* Wrote on how we can use PWA to enhance our application
  
### Milestone 1 Recap
* Implemented sign in page
* Implemented sign out page
* Added several of the api calls to the backend
* Attended meetings and participate in discussion on Discord
* Outline and update most of milestone 1 report

### Milestone 2 Recap
* Implement user authentication and authorization
* Added changes to the schema to reference userId
* Organized meeting to discuss current progress and project designs
* Outline and update most of milestone 2 report

### Final Recap
* Implement service worker to use cache api
* Implement IndexedDB in db.js file with Dexie
* Fetch successful api call to add initial data online to IndexedDB
* Attended team meetings to brainstorm ideas and establish goals

#### Anthony Brown

##### Proposal Recap

* Suggested including the capability to share exercise plans with others  
* Suggested capturing data such as exercise duration or number of repetitions during or after an exercise as well as the capability of graphing the exercise metrics by activity and time  
* Created the wireframes for the mobile views of the application


##### Milestone 1 Recap

* Implemented the app bar and sidebar for navigation
* Implemented the homepage / main dashboard
* Implemented the Share and Info pages for stored exercise plans
* Implemented the basic layout of the page for accessing the active exercise plan
* Implemented the activity graph screen
* Implemented mock API endpoints to support retrieval of multiple exercise plans or activities


##### Milestone 2 Recap

* Configured the project to use MariaDB
* Drafted the first iteration of the database schema
* Added initial data and test data to verify functionality
* Implemented a line graph to show cumulative progress per day within a range
* Implemented SQL queries and backend logic to store, retrieve, and share exercise plans and to start and update active exercise plans
* Implemented dynamically changing status icons in the **Active Plan** page when a user satisfies the expected progress for an activity

##### Final Recap
* Added the web application manifest and applied the theming
* Added the icons and images used for the favicon, logos, and splash screen
* Added an offline page and messages to display when a user is offline
* Updated the service worker to respond with specific error responses when fetching uncached resources offline

#### Shakthi Ravichandran

### Proposal Recap
* Provided input regarding base functionalities and stretch goals during meetings.
* Determined the general features and the data required to faciliate base functionalities.
* Worked on desktop view wireframes for the application.

### Milestone 1 Recap
* Tasked with implementing the **Create Exercise Plan** page (made progresss; not submitted)
* Tasked with implementing the **Edit Exercise Plan** page (made progress; not submitted)

### Milestone 2 and Final Recap
* Zero contribution

#### Project Effort Contribution

Milestone   | Huy Nguyen | Anthony Brown | Shakthi Ravichandran
----------- | ------------- | ------------- | --------------
Proposal    | 33%            | 34%            | 33%
Milestone 1 | 40%            | 40%            | 20%
Milestone 2 | 40%            | 60%            | 0%
Final       | %            | %            | 0%
----------- | ------------- | ------------- | --------------
TOTAL:      | (A+D+M+X)%    | (B+E+N+Y)%    | 53%
