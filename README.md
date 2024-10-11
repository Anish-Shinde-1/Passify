# Passify

**Passify** is a personal password manager application developed as a mini-project during the third semester of college. It provides a secure and user-friendly interface for managing your passwords and related information.

## Team Members

- **Anish Shinde SE IT B 42** 
- **Piyush Patil SE IT B 25** 
- **Payal Patil SE IT B 24** 

## Table of Contents

- [Features](#features)
- [Workings](#workings)
- [Functionality](#functionality)
- [Project Status](#project-status)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)

## Features

- **User Authentication**: Secure login and user management to protect your sensitive data.
- **Password Storage**: Safely store and manage your passwords with encryption.
- **Intuitive UI**: User-friendly interface designed with JavaFX and Scene Builder for easy navigation.

## Workings

Passify operates on a simple model-view-controller (MVC) architecture with an additional Data Access Object (DAO) layer. The application interacts with a MySQL database through JDBC for user data storage and retrieval. The user interface is built using JavaFX, allowing for a responsive and modern design. The following components work together:

1. **Model**: Represents the data and the business logic. This includes user accounts and password entries.
2. **View**: The JavaFX UI components that the user interacts with.
3. **Controller**: Handles user input and updates the model and view accordingly.
4. **DAO**: Provides an abstract interface to the database, managing the CRUD operations for user accounts and password entries. 

## Functionality

The core functionality of Passify includes:

- **Secure User Login**: Users can authenticate themselves using a master password.
- **Password Management**: Users can add, update, and delete password entries securely.
- **Data Encryption**: All sensitive data is encrypted to ensure user privacy.
- **User Dashboard**: An intuitive dashboard allows users to view and manage their saved passwords efficiently.

## Project Status

Although the Minimum Viable Product (MVP) of Passify is complete, the application is still a work in progress. Future updates will address the following issues:

- **Favorites**: Implementing a feature to mark passwords as favorites for quick access from the main navigation.
- **Trash**: Adding a trash feature to list and recover deleted passwords.
- **Types**: Categorizing credentials by type, including login credentials, card details, identity documents, etc.
- **User Dashboard**: Creating a user dashboard for better usability and additional features.
- **Automatic Database Setup**: Streamlining the database setup process to allow users to set up the database automatically without needing to run SQL scripts manually.

## Installation

To set up Passify on your local machine, follow these steps:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Anish-Shinde-1/Passify.git
   ```

2. **Navigate to the project directory:**

   ```bash
   cd passify
   ```
3. **Run the MySQL server script:**

      - Navigate to the src/main/java/com/passify/utils/ directory and run the SQL script to set up the database.
      - Ensure you have a MySQL server running.


4. **Update the JDBC connector class:**

      - Open the JDBC_Connector.java file located in src/main/java/com/passify/utils/ and update the server login credentials (username and password) according to your MySQL server settings.

   
5. **Build the project:**

   ```bash
   mvn clean install
   ```

6. **Run the application:**

   ```bash
   mvn clean javafx:run
   ```
   
## Usage

1. **Login & Registration**: Use the login screen to register and authenticate yourself.
2. **Manage Passwords**: Access and manage your passwords from the main dashboard.
3. **Categorize Passwords**: Organize your passwords into categories such as **Work**, **Social**, and **Miscellaneous** for easier access and management.
4. **Favourites & Trash**: Mark important passwords as favorites for quick access, and move any deleted passwords to the trash for possible recovery later.


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