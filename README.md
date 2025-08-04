# HTTP Server

## Overview

This project contains:

- A custom Java HTTP server (`src/main/java/com/gavro/httpserver`)
- A simple React frontend app (`reactapp/`)
- Database configuration

## Features

- Handles HTTP requests and serves both backend and frontend on the same port (default: 8000)
- Basic routing and request handling
- Database integration
- UI for testing the API endpoints from the browser

## How to set up

### Backend (Java HTTP Server)

1. **Requirements:**

   - Java 17+
   - Maven

2. **Configuration:**

   - Move `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and modify it with your database connection details.

3. **Build & Run:**
   ```sh
   mvn clean package
   java -jar target/http-server-1.0.0.jar
   ```

### Frontend (React App)

1. **Requirements:**

   - Node.js 18+
   - npm

2. **Setup & Build:**
   ```sh
   cd reactapp
   npm install
   npm run build
   ```
   This will generate the `dist` folder, which is served by the backend on port 8000 (or whatever you set it to in the config).

## Database setup

- The backend uses a single table called `Subjects` with fields: `name`, `abstract`, and `code` (all strings) which you have to create.
- Example SQL:
  ```sql
  INSERT INTO Subjects (name, abstract, code) VALUES ('Math', 'Mathematics subject', 'MATH101');
  ```
