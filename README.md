# Passify

**Passify** is a personal password manager application developed as a mini-project during the third semester of college. It provides a secure and user-friendly interface for managing your passwords and related information.

## Team Members

- **Anish Shinde SE IT B 42** 
- **Piyush Patil SE IT B 25** 
- **Payal Patil SE IT B 24** 

## Table of Contents

- [Functionality](#functionality)
- [Workings](#workings)
- [Project Status](#project-status)
- [Installation](#installation)
- [Technologies](#technologies)
- [License](#license)

## Functionality

**Passify** provides essential features for secure password management:

- **User Authentication**: Secure login with master passwords, which are hashed for protection.
- **Password Management**: Add, update, and delete passwords, all securely salted and encrypted with AES.
- **Data Security**: Utilizes PBKDF2 hashing with salting for master passwords, ensuring robust protection.
- **Intuitive UI**: User-friendly interface designed with JavaFX for easy navigation.
- **Categorization**: Organize passwords into **Work**, **Social**, and **Miscellaneous** categories.
- **Favorites & Trash**: Mark passwords as favorites for quick access and recover deleted passwords from trash.
- **Password Generator**: Create strong, secure passwords with the built-in generator.
- **User Dashboard**: An overview dashboard for efficient management of user settings and preferences.


## Workings

Passify operates on a simple model-view-controller (MVC) architecture with an additional Data Access Object (DAO) layer. The application interacts with a MySQL database through JDBC for user data storage and retrieval. The user interface is built using JavaFX, allowing for a responsive and modern design. The following components work together:

1. **Model**: Represents the data and the business logic. This includes user accounts and password entries.
2. **View**: The JavaFX UI components that the user interacts with.
3. **Controller**: Handles user input and updates the model and view accordingly.
4. **DAO**: Provides an abstract interface to the database, managing the CRUD operations for user accounts and password entries. 

## Project Status

Although the Minimum Viable Product (MVP) of Passify is complete, the application is still a work in progress. Future updates will address the following issues:

- **Favorites**: Implementing a feature to mark passwords as favorites for quick access from the main navigation.
- **Trash**: Adding a trash feature to list and recover deleted passwords.
- **Types**: Categorizing credentials by type, including login credentials, card details, identity documents, etc.
- **User Dashboard**: Creating a user dashboard for better usability and additional features.
- **Automatic Database Setup**: Streamlining the database setup process to allow users to set up the database automatically without needing to run SQL scripts manually.

## Installation

To set up Passify on your local machine, follow these steps:

1. **Install MySQL:** *(Skip if you already have installed and setup MySQL)*

   - **Windows**: Download the installer from the [MySQL website](https://dev.mysql.com/downloads/installer/) and follow the installation instructions.
   - **Linux**: 
     ```bash
     sudo apt install mysql-server
     ```
   - **Post-Installation**: Secure the installation:
     ```bash
     sudo mysql_secure_installation
     ```

2. **Clone the repository:**

   ```bash
   git clone https://github.com/Anish-Shinde-1/Passify.git
   ```

3. **Navigate to the project directory:**

   ```bash
   cd passify
   ```
4. **Run the MySQL database setup script:**

      - Navigate to the src/main/java/com/passify/utils/PasswordManagerDatabaseScript.sql to copy its path and run the SQL script to set up the database.
   ```bash
   # For Linux:
   mysql -u <your_username> -p < /path/to/PasswordManagerDatabaseScript.sql
   
   # For Windows:
   mysql -u <your_username> -p < "C:\\path\\to\\PasswordManagerDatabaseScript.sql"
   ```
   
5. **Update the JDBC connector class:**

      - Open the JDBC_Connector.java file located in src/main/java/com/passify/utils/ and update the server login credentials (USER and PASSWORD) according to your MySQL server settings.


6. **Install Apache Maven:**
      - For Linux:
   ```bash
   sudo apt install maven
   ```
   - For Windows:
     - a. Manual Install: Visit the Maven Download page (https://maven.apache.org/download.cgi) and follow the instructions.
     - b. Using Scoop or Chocolatey:
         ```bash 
         # Using Scoop:
         scoop install maven
       
         # Using Chocolatey:
         choco install maven
         ```
   
7. **Build the project:**

   ```bash
   mvn clean install
   ```

8. **Run the application:**

   ```bash
   mvn clean javafx:run
   ```

## Technologies

Passify is built using the following technologies:

- **Java**: The primary programming language.
- **JavaFX**: For building the user interface.
- **Scene Builder**: For designing the user interface visually.
- **JDBC**: For connecting to a MySQL database to store user data.
- **MySQL**: The database management system used for storing user data.
- **Maven**: For project management and build automation.

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE). See the LICENSE file for details.