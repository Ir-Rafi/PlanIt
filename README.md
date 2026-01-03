PlanIt - Event Management System
Planit. Execute it. Perfect it. 

📖 Introduction

PlanIt is a comprehensive JavaFX-based Event Management System developed by Team Abstrax for the CSE 2104 Object Oriented Design and Programming Lab.



Traditional event management often suffers from scattered communication and overlapping venue registrations . PlanIt addresses these challenges by providing a centralized platform for event coordination, task management, and stakeholder communication.



Key Objectives:

Streamline event planning workflows.

Enable role-based task delegation.

Provide real-time collaboration and centralized venue booking.

🚀 Key Features
👤 User Management

Role-Based Access Control: Distinct dashboards for Main Organizers, Sub Organizers, and Viewers.


Secure Authentication: Secure login system with "Remember Me" functionality and session persistence.


📅 Event Coordination

Context-Aware Dashboards: Switch between a global dashboard and specific Event Portals.


Task Management: Advanced To-Do lists with priority levels (High, Medium, Low), categorization, and completion tracking .



Venue Booking: Interactive booking system with database integration to prevent overlapping schedules.


💬 Collaboration Tools

Discussion Forum: Thread-based discussions (similar to Reddit/Stack Overflow) supporting 50+ concurrent clients via a multi-threaded server.



Progress Reports: Create, save, and view detailed progress reports in a split-pane interface .

🛠 Technical Highlights

Networking: Socket-based client-server architecture using TCP/IP and object serialization .


Persistence: Hybrid storage using a remote MySQL database (clever-cloud.com) and local serialized .dat files.



UI/UX: Modern dark theme, responsive layouts, and animated loading states .

🏗 System Architecture & Design
Database Schema Diagram
The system utilizes a relational schema to manage Users, Events, Bookings, and Organizers .

Code snippet

erDiagram
    USERS ||--o{ ORGANIZERS : "managed by"
    EVENTS ||--|{ BOOKINGS : "has"
    EVENTS ||--|{ ORGANIZERS : "coordinated by"
    
    USERS {
        int id PK
        string username
        string password_hashed
        string email
        string department
        string session
    }

    EVENTS {
        int event_id PK
        string event_name
        int organizer_id FK
        datetime start_date
        datetime end_date
        string place_name
        string description
        string color
    }

    BOOKINGS {
        int booking_id PK
        string place_name
        int organizer_id FK
        time start_time
        time end_time
    }

    ORGANIZERS {
        int organizer_id PK
        string organizer_name
        int event_id FK
    }
Design Patterns Implemented
To ensure modularity and scalability, the following OOP design patterns were applied :

Singleton: Used for session management.

Factory: OrganizerViewFactory for creating role-specific views.

Command: Handles client-server communication logic.

Observer: Updates UI elements upon data changes.

Context: Manages switching between Event Portal and Dashboard states.

💻 Tech Stack
Category	Technology Used
Language	
Java (JDK 8 or higher) 

Frontend	
JavaFX, FXML, CSS, Scene Builder 

Backend	
Java Socket Programming, Multi-threading 


Database	
MySQL (Hosted on Clever-cloud), JDBC 

Tools	
IntelliJ IDEA, Git 


Export to Sheets

📸 Screenshots
(Note: Add the images from the report folder to your repo and link them here)

Login Screen	Main Dashboard
Secure entry point 

Event overview 


Export to Sheets

Venue Booking	Discussion Forum
Interactive availability check 

Real-time collaboration 


Export to Sheets

📥 Installation & Setup
Clone the repository:

Bash

git clone https://github.com/Ir-Rafi/Planlt.git



Database Configuration:

The project is configured to use a remote MySQL database on clever-cloud.com.

Ensure your internet connection is active to connect to the DB.

Build the Project:

Open the project in IntelliJ IDEA or VS Code.

Ensure JDK 8 or higher is installed.

Reload Maven/Gradle dependencies (if applicable) or add JavaFX libraries to your classpath.

Run the Application:

Locate the Main class and run.

Note: Ensure the Server module is running if testing network features locally.

👥 Team Abstrax
This project was created by the 30th Batch, Department of Computer Science & Engineering, University of Dhaka.

Roll No.	Name	Role
03	Kazi Maheru Tafannum	
Backend Design, DBMS 

14	Shadman Zaman Sajid	
Testing, System Programmer 

05	Rubaiya Sultana	
UI Designer, Conceptualist 

35	Md. Irfan Iqbal	
Backend Arch, UI Design, Integration 


Export to Sheets

🔮 Future Roadmap
We plan to scale PlanIt into a robust system with the following enhancements :

📱 Mobile Application (Android/iOS).

📧 Email Notifications for task updates.

🤖 Machine Learning for event recommendations.

📹 Video Conferencing integration for remote meetings.

🌍 Multi-language Support for international events.

🔗 Links & Resources

Source Code: GitHub Repository 


Demo Video: Watch Demonstration 


Submitted on January 03, 2026
