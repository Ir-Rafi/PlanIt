<div align="center">

# 🗓️ PlanIt

### *Plan it.  Execute it. Perfect it.*

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![JavaFX](https://img.shields.io/badge/JavaFX-007396? style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

<p align="center">
  <strong>A comprehensive JavaFX-based Event Management System</strong><br>
  <em>Developed by Team Abstrax for CSE 2104 - Object Oriented Design and Programming Lab</em>
</p>

**Team Members:** Kazi Maheru Tafannum • Rubaiya Sultana • Shadman Zaman Sajid • Md. Irfan Iqbal

[Features](#-key-features) •
[Architecture](#-system-architecture) •
[Installation](#-installation--setup) •
[Tech Stack](#-tech-stack) •
[Team](#-team-abstrax)

---

</div>

## 📖 Introduction

Traditional event management often suffers from **scattered communication** and **overlapping venue registrations**. **PlanIt** addresses these challenges by providing a centralized platform for event coordination, task management, and stakeholder communication.

### 🎯 Key Objectives

| Objective | Description |
|-----------|-------------|
| 🔄 **Streamline Workflows** | Simplify event planning with intuitive dashboards |
| 👥 **Role-Based Delegation** | Enable efficient task assignment and tracking |
| 🏢 **Venue Management** | Prevent scheduling conflicts with centralized booking |
| 💬 **Real-Time Collaboration** | Foster team communication through discussion forums |

---

## 🚀 Key Features

<table>
<tr>
<td width="50%">

### 👤 User Management
- **Role-Based Access Control**
  - Main Organizers
  - Sub Organizers  
  - Viewers
- **Secure Authentication**
  - "Remember Me" functionality
  - Session persistence

</td>
<td width="50%">

### 📅 Event Coordination
- **Context-Aware Dashboards**
  - Global dashboard view
  - Specific Event Portals
- **Advanced Task Management**
  - Priority levels (High/Medium/Low)
  - Categorization & completion tracking

</td>
</tr>
<tr>
<td width="50%">

### 🏢 Venue Booking
- Interactive booking system
- Database integration
- Overlap prevention
- Real-time availability check

</td>
<td width="50%">

### 💬 Collaboration Tools
- **Discussion Forum**
  - Thread-based discussions
  - 50+ concurrent clients support
  - Multi-threaded server
- **Progress Reports**
  - Create & save detailed reports
  - Split-pane interface

</td>
</tr>
</table>

---

## 🏗 System Architecture

### High-Level Architecture Diagram

```mermaid
flowchart TB
    subgraph Client["🖥️ Client Layer"]
        UI[JavaFX UI]
        FXML[FXML Views]
        CSS[CSS Styling]
    end
    
    subgraph Application["⚙️ Application Layer"]
        Controllers[Controllers]
        Session[Session Manager]
        Serialization[Object Serialization]
    end
    
    subgraph Network["🌐 Network Layer"]
        Socket[Socket Programming]
        TCP[TCP/IP Protocol]
        MultiThread[Multi-threaded Server]
    end
    
    subgraph Data["💾 Data Layer"]
        MySQL[(MySQL Database)]
        DAT[(. dat Files)]
    end
    
    UI --> Controllers
    FXML --> UI
    CSS --> UI
    Controllers --> Session
    Controllers --> Socket
    Session --> Serialization
    Socket --> TCP
    TCP --> MultiThread
    MultiThread --> MySQL
    Serialization --> DAT
```

### Application Flow Diagram

```mermaid
flowchart LR
    A[🔐 Login] --> B{Authentication}
    B -->|Success| C[📊 Dashboard]
    B -->|Failure| A
    
    C --> D[📅 Event Portal]
    C --> E[📋 Task Manager]
    C --> F[🏢 Venue Booking]
    C --> G[💬 Discussion Forum]
    
    D --> H[Create Event]
    D --> I[Manage Organizers]
    
    E --> J[Add Tasks]
    E --> K[Set Priority]
    E --> L[Track Progress]
    
    F --> M[Check Availability]
    F --> N[Book Venue]
    
    G --> O[Create Thread]
    G --> P[Reply to Posts]
```

### Database Schema (ER Diagram)

```mermaid
erDiagram
    USERS ||--o{ ORGANIZERS : "managed by"
    EVENTS ||--|{ BOOKINGS : "has"
    EVENTS ||--|{ ORGANIZERS : "coordinated by"
    USERS ||--o{ EVENTS : "creates"
    
    USERS {
        int id PK
        string username UK
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
        date booking_date
    }

    ORGANIZERS {
        int organizer_id PK
        string organizer_name
        string phone
        string code
        int event_id FK
    }
```

### Design Patterns Implemented

```mermaid
mindmap
  root((PlanIt<br>Design Patterns))
    Creational
      Singleton
        Session Management
        Database Connection
      Factory
        OrganizerViewFactory
        Role-specific Views
    Behavioral
      Command
        Client-Server Communication
        Request Handling
      Observer
        UI Updates
        Data Change Notifications
    Structural
      Context
        Dashboard State
        Event Portal State
```

---

## 💻 Tech Stack

<div align="center">

| Category | Technologies |
|: --------:|:-------------|
| **Language** | Java 8+ |
| **Frontend** | JavaFX, FXML, CSS, Scene Builder |
| **Backend** | Java Socket Programming, Multi-threading |
| **Database** | MySQL (Hosted on Clever-cloud), JDBC |
| **Tools** | IntelliJ IDEA, Git |

</div>

### Technical Highlights

- 🌐 **Networking**: Socket-based client-server architecture using TCP/IP and object serialization
- 💾 **Persistence**:  Hybrid storage using remote MySQL database (Clever-cloud) and local serialized `.dat` files
- 🎨 **UI/UX**: Modern dark theme with responsive layouts and animated loading states
- 🔐 **Security**: Password hashing with BCrypt

---

## 📥 Installation & Setup

### Prerequisites

- ☕ **JDK 8** or higher
- 🌐 Active internet connection (for remote database)
- 💻 IntelliJ IDEA or VS Code (recommended)

### Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/Ir-Rafi/PlanIt. git

# 2. Navigate to project directory
cd PlanIt

# 3. Open in your IDE and build the project

# 4. Run the Main class
```

### Configuration

```properties
# Database is pre-configured to use Clever-cloud MySQL
# No additional setup required for database connection
```

### Running the Application

1. **Import** the project into IntelliJ IDEA or VS Code
2. **Ensure** JDK 8+ is configured
3. **Add** JavaFX libraries to your classpath if needed
4. **Locate** and run the `Main` class
5. **Start** the Server module first if testing network features locally

---

## 📁 Project Structure

```
PlanIt/
├── 📂 src/
│   ├── 📄 Main.java                    # Application entry point
│   ├── 📄 Dashboard.java               # Main dashboard controller
│   ├── 📄 AdvancedTodoListApp.java     # Task management
│   ├── 📄 EventController.java         # Event handling
│   ├── 📄 DatabaseUtility.java         # Database operations
│   ├── 📄 Session.java                 # Session management
│   ├── 📂 fxml/                        # FXML view files
│   └── 📂 css/                         # Stylesheets
├── 📂 Chat/                            # Discussion forum module
├── 📂 img/                             # Images and assets
├── 📂 lib/                             # External libraries
├── 📂 reports/                         # Generated reports
└── 📄 README.md
```

---

## 👥 Team Abstrax

<div align="center">

### 🎓 30th Batch, Department of Computer Science & Engineering
### 🏛️ University of Dhaka

---

**Kazi Maheru Tafannum** • **Rubaiya Sultana** • **Shadman Zaman Sajid** • **Md. Irfan Iqbal**

---

| Roll | Name | Role | Responsibilities |
|: ----:|:-----|: ----:|:-----------------|
| 03 | **Kazi Maheru Tafannum** | 🔧 Backend Dev | Backend Design, DBMS |
| 05 | **Rubaiya Sultana** | 🎨 UI Designer | UI Design, Conceptualist |
| 14 | **Shadman Zaman Sajid** | 🧪 Tester | Testing, System Programming |
| 35 | **Md.  Irfan Iqbal** | 🏗️ Lead Dev | Backend Architecture, UI Design, Integration |

</div>

---

## 🔮 Future Roadmap

```mermaid
timeline
    title PlanIt Development Roadmap
    
    section Phase 1
        Q1 2026 :  📧 Email Notifications
               :  Push notifications for task updates
               : Event reminders
    
    section Phase 2
        Q2 2026 : 📱 Mobile Application
               : Android & iOS versions
               : Cross-platform sync
    
    section Phase 3
        Q3 2026 :  🤖 AI Integration
               :  ML-based event recommendations
               : Smart scheduling
    
    section Phase 4
        Q4 2026 : 📹 Video Conferencing
               : Remote meeting integration
               : 🌍 Multi-language Support
```

### Planned Features

- [ ] 📱 **Mobile Application** - Android/iOS native apps
- [ ] 📧 **Email Notifications** - Task updates and reminders
- [ ] 🤖 **Machine Learning** - Smart event recommendations
- [ ] 📹 **Video Conferencing** - Remote meeting integration
- [ ] 🌍 **Multi-language Support** - Internationalization

---

## 🔗 Links & Resources

<div align="center">

[![GitHub](https://img.shields.io/badge/Source_Code-181717? style=for-the-badge&logo=github&logoColor=white)](https://github.com/Ir-Rafi/PlanIt)
[![Demo](https://img.shields.io/badge/Watch_Demo-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://www.youtube.com/watch?v=your-demo-link)

</div>

---

## 📄 License

This project was created for academic purposes as part of the CSE 2104 course at the University of Dhaka.

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

Made with ❤️ by **Team Abstrax**

*Submitted on January 03, 2026*

</div>
