# user_rest_api

## Task Overview
The task involved implementing a RESTful API for managing user resources in a Spring Boot web application.

## Implementation Details
**Field Validation:** Implemented validation for fields such as email pattern, required fields (email, first name, last name, birth date), and birth date validity (must be earlier than the current date).

**Functionality:**
- **Create User:** Users can be registered if they are more than 18 years old, with the minimum age configurable via properties file.
- **Update User Fields:** Allows updating one or more fields of a user.
- **Update All User Fields:** Enables updating all fields of a user.
- **Delete User:** Removes a user from the system.
- **Search Users by Birth Date Range:** Users can be searched within a specified birthdate range, with validation to ensure "From" date is before "To" date.
- **Unit Testing:** Extensive unit tests were implemented using Spring Testing framework to ensure code reliability and functionality.
- **Error Handling:** The API includes error handling mechanisms to handle various scenarios and provide informative responses.
- **JSON Format:** All API responses are formatted in JSON as per RESTful API standards.