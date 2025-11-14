# PillPal — Frontend
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

<img width="1920" height="1080" alt="pillpalfront" src="https://github.com/user-attachments/assets/75e7ae4c-3015-42e6-927c-86a5a1d6b8e9" />

---

### Authors
Developed by **Team Pixel Health**  
- Sofia — Backend, Database, Hardware  
- Iker — Hardware, Connectivity  
- Favour — UI/UX Design, Frontend Development  
- Ikram — Frontend Integration, Hardware Testing  

---

### Overview
**PillPal** is a smart medication management system that integrates a portable **IoT pillbox** with a **mobile companion app** to help users stay consistent with their medication schedules.

The system combines **physical reminders** (via vibration, LED, and buzzer) with **digital notifications** (via the mobile app).  
It records adherence, allows flexible scheduling, and provides accessible reminders for a wide range of users — from elderly individuals to busy professionals.

---

###  Features
- Add, edit, and delete medication schedules  
- Receive push notifications for reminders  
- Confirm, snooze, or dismiss doses  
- View daily and historical medication adherence  
- Connect via Wi-Fi to the PillPal IoT device  
- Simple, accessible UI designed using Universal Design principles  
- Works both online and offline (syncs automatically)

---

### Related Repositories
| Component | Repository |
|------------|-------------|
| **Backend** | [PILLPAL-Backend](https://github.com/violetdestiny/PILLPAL-Backend) |
| **Hardware** | [PILLPAL-Hardware](https://github.com/violetdestiny/PILLPAL-hardware) |

---

<img width="1266" height="874" alt="pillpal2" src="https://github.com/user-attachments/assets/2bd82bec-dccd-4598-8ce7-657627fad9a0" />
<img width="1265" height="867" alt="pillpal1" src="https://github.com/user-attachments/assets/c9e30932-cd1a-493c-ac60-6923f55eaa6a" />
<img width="1258" height="869" alt="pillpal3" src="https://github.com/user-attachments/assets/8532199d-8c3a-48d4-949d-a09f151668b3" />

---

### System Summary
| Component | Description |
|------------|-------------|
| **Frontend (Android)** | Displays reminders, logs confirmations, and syncs with backend via Wi-Fi |
| **Hardware (Pi Zero)** | Detects lid-open events, triggers LED/buzzer/vibration alerts, and publishes MQTT events |
| **Backend (Flask + MySQL)** | Stores user profiles, schedules, and logs adherence data |
