# 🌦️ Branch Weather UI

A lightweight **Tailwind CSS** frontend for the **Branch Weather API** built with Spring Boot and SQL Server.

The UI provides a simple, responsive dashboard for browsing Malaysian branches and retrieving live weather information using the backend REST API.

---

# Features

- Responsive Tailwind CSS interface
- Search branches by:
  - Branch Code
  - Branch Name
  - City
  - Country
- Live weather retrieval
- Google Maps integration
- Responsive weather cards
- Refresh weather for individual branches
- Clean portfolio-friendly UI

---

# Technology Stack

| Technology | Version |
|------------|---------|
| HTML5 | ✓ |
| Tailwind CSS | CDN |
| Vanilla JavaScript | ES6 |
| Spring Boot Backend | 4.1 |
| SQL Server | 2022 |
| Open-Meteo API | ✓ |

---

# Project Structure

```
ui/
│
├── index.html
├── app.js
└── README.md
```

---

# Backend Requirements

The Spring Boot backend must be running.

Expected REST endpoints:

```text
GET http://localhost:8080/api/branches?page=0&size=100

GET http://localhost:8080/api/branches/{id}/weather
```

---

# Running the UI

Instead of opening `index.html` directly, serve the UI using a lightweight local web server.

From the **ui** folder:

```powershell
npx serve .
```

The UI will be available at:

```text
http://localhost:3000
```

The backend should be running on:

```text
http://localhost:8080
```

---

# CORS Configuration

Since the frontend and backend run on different ports, Cross-Origin Resource Sharing (CORS) must be enabled.

Example configuration:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                )
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
                .allowedHeaders("*");
    }
}
```

---

# Dashboard

The dashboard displays

- Branch Code
- Branch Name
- City
- Country
- Temperature
- Relative Humidity
- Wind Speed
- Weather Code
- Google Maps shortcut
- Refresh Weather button

---

# Demo

## Current Weather Dashboard

![Current Weather](../demo/Current_weather.gif)

# Future Improvements

Planned enhancements include:

- Hero images for each tourist location
- Dark Mode
- Weather icons
- Temperature color gradients
- Branch detail modal
- Forecast view
- Interactive map
- Auto-refresh weather
- Pagination / Infinite scrolling
- Deploy UI to GitHub Pages

---

# Author

**Retnalogan Thirujanasambanthan**

Senior Software Engineer

Java • Spring Boot • SQL Server • REST APIs • Cloud • DevOps