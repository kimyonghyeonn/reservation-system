package kr.co.wikibook.backend.common.location;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class LocationHelper {

    public String getLocationFromIp(String ip) {
        // 로컬 테스트일 경우 null 반환
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "Localhost";
        }

        try {
            URL url = new URL("http://ip-api.com/json/" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();
            JSONObject json = new JSONObject(response.toString());

            if ("success".equals(json.getString("status"))) {
                String country = json.optString("country", "");
                String region = json.optString("regionName", "");
                String city = json.optString("city", "");
                return country + "/" + region + "/" + city;
            }
        } catch (Exception e) {
            e.printStackTrace(); // 실서비스에선 로깅
        }
        return "Unknown";
    }


}
