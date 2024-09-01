package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/london")
public class LondonServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String year = request.getParameter("year");
        String month = request.getParameter("month");

        // Determine the next month and adjust the year if necessary
        String nextYear = year;
        String nextMonth;

        if (Integer.parseInt(month) == 12) {
            nextMonth = "01";  // January of the next year
            nextYear = String.valueOf(Integer.parseInt(year) + 1);  // Increment the year
        } else {
            nextMonth = String.format("%02d", Integer.parseInt(month) + 1);  // Increment the month
        }

        String apiUrl = "http://fakewings.eu:9003/api/v1/tire-change-times/available?from="
                + year + "-" + month + "-01&until=" 
                + nextYear + "-" + nextMonth + "-01";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Available Times in London</h1>");

        HashMap<String, String> timeUuidMap = new HashMap<>();

        // Regular expression patterns
        Pattern uuidPattern = Pattern.compile("<uuid>(.*?)</uuid>");
        Pattern timePattern = Pattern.compile("<time>(.*?)</time>");
        
        String[] availableTimes = content.toString().split("</availableTime>");
        for (String i : availableTimes) {
            Matcher uuidMatcher = uuidPattern.matcher(i);
            Matcher timeMatcher = timePattern.matcher(i);

            if (uuidMatcher.find() && timeMatcher.find()) {
                String uuid = uuidMatcher.group(1);
                String time = timeMatcher.group(1);
                timeUuidMap.put(time, uuid);
            }
        }

        // Store the map in the session
        HttpSession session = request.getSession();
        session.setAttribute("timeUuidMap", timeUuidMap);

        // Generate the calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Adjusting to start week from Sunday

        out.println("<form action='london2' method='post'>");
        out.println("<table border='1'>");
        out.println("<tr><th>Sun</th><th>Mon</th><th>Tue</th><th>Wed</th><th>Thu</th><th>Fri</th><th>Sat</th></tr>");

        out.println("<tr>");
        // Print empty cells until the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            out.println("<td></td>");
        }

        // Print the days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            if ((day + firstDayOfWeek - 1) % 7 == 0 && day != 1) {
                out.println("</tr><tr>"); // Start a new row every week
            }
            
            String dayKey = String.format("%s-%02d-%02d", year, Integer.parseInt(month), day);
            boolean hasTimes = false;

            out.println("<td>");
            out.println("<strong>" + day + "</strong><br>");

            // Iterate over timeUuidMap to check if there are available times for this day
            for (Map.Entry<String, String> entry : timeUuidMap.entrySet()) {
                String time = entry.getKey();
                if (time.startsWith(dayKey)) {
                    hasTimes = true;
                    out.println("<label for='" + time + "'>" + time.substring(11, 16) + "</label>");
                    out.println("<input type='radio' id='" + time + "' name='selectedTime' value='" + time + "'><br>");
                }
            }

            if (!hasTimes) {
                out.println("No available times");
            }
            
            out.println("</td>");
        }

        // Fill the remaining cells if the month does not end on Saturday
        int remainingCells = (firstDayOfWeek + daysInMonth) % 7;
        if (remainingCells != 0) {
            for (int i = remainingCells; i < 7; i++) {
                out.println("<td></td>");
            }
        }

        out.println("</tr>");
        out.println("</table>");
        out.println("<input type='submit' value='OK'>");
        out.println("</form>");

        out.println("</body></html>");
    }
}

