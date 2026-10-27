# Face Recognition Attendance System

A Java application that marks student attendance automatically by recognizing faces from a webcam.

## Features
- Register students and capture face photos from the webcam
- Train a face recognition model (LBPH algorithm)
- Live face recognition that marks attendance in MySQL (once per student per day)
- Attendance reports by date

## Tech Stack
Java, Maven, JavaCV (OpenCV), MySQL, JDBC

## How it works
1. **Detect**: a Haar Cascade finds faces in each webcam frame.
2. **Register**: 30 grayscale face photos per student are saved in `faces/<id>/`.
3. **Train**: the LBPH recognizer learns from all photos and saves `model.yml`.
4. **Recognize**: each detected face is compared with the model. A low confidence number means a good match.
5. **Store**: a recognized student is saved in the `attendance` table with date and time.

## Setup
1. Install JDK 17 or newer, Maven, and MySQL.
2. Create the database by running `schema.sql` in MySQL.
3. Open `src/main/java/com/attendance/DBConnection.java` and set your MySQL username and password.
4. Run:
   ```
   mvn compile exec:java
   ```
   The first run downloads a large OpenCV package, so it can take a few minutes.

## Usage
1. Menu 1: register a student and look at the camera until 30 photos are captured.
2. Menu 2: train the model (do this after every new registration).
3. Menu 3: start attendance. Close the camera window to stop.
4. Menu 4 or 5: view attendance reports.
5. Menu 6: open the teacher dashboard (total, present and absent students for today).

## Project structure
```
face-attendance-system/
├── pom.xml
├── schema.sql
├── haarcascade_frontalface_default.xml
└── src/main/java/com/attendance/
    ├── Main.java
    ├── DBConnection.java
    ├── StudentDAO.java
    ├── AttendanceDAO.java
    ├── FaceRegistration.java
    ├── FaceTrainer.java
    └── AttendanceMarker.java
```

## Future improvements
- Swing or JavaFX GUI
- CSV or PDF report export
- Liveness detection (blink check) to stop photo spoofing
- Spring Boot web dashboard