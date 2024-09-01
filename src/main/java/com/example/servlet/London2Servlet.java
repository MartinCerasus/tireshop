package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@WebServlet("/london2")
public class London2Servlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        HashMap<String, String> timeUuidMap = (HashMap<String, String>) session.getAttribute("timeUuidMap");

        String selectedTime = request.getParameter("selectedTime");
        String selectedUuid = timeUuidMap.get(selectedTime);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Selected Time and UUID</h1>");
        if (selectedUuid != null) {
            out.println("<p>Selected Time: " + selectedTime + "</p>");
            out.println("<p>UUID: " + selectedUuid + "</p>");
        } else {
            out.println("<p>No selection made.</p>");
        }

        // New block of code to make the API call using the selected UUID
        if (selectedUuid != null) {
            String apiUrl = "http://fakewings.eu:9003/api/v1/tire-change-times/" + selectedUuid + "/booking";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("accept", "text/xml");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setDoOutput(true);

            String xmlRequestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<london.tireChangeBookingRequest>"
                    + "<contactInformation>string</contactInformation>"
                    + "</london.tireChangeBookingRequest>";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = xmlRequestBody.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            // out.println("API Call Result");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                out.println("<p>Booking confirmed successfully!</p>");
            } else {
                out.println("<p>Failed to confirm booking. Response Code: " + responseCode + "</p>");
            }

            conn.disconnect();
        }

        out.println("</body></html>");
    }
}
