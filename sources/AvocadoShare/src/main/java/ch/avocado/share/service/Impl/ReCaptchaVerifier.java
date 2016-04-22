package ch.avocado.share.service.Impl;

import ch.avocado.share.service.ICaptchaVerifier;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CAPTCHA verifier for Googles reCAPTCHA.
 */
public class ReCaptchaVerifier implements ICaptchaVerifier {

    private static final String CLIENT_SECRET = "6LeS4x0TAAAAAIPJ082G58EFpA9A6ejG1qfZ0ua4";
    private static final String SITE_KEY = "6LeS4x0TAAAAANcSYZwdoyTFY-NjvbFkX3zU8CiU";
    private static final String RESPONSE_PARAMETER_NAME = "g-recaptcha-response";
    private static boolean VERIFY_REMOTE_ADDRESS = false;
    private static final String API_URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final String API_PARAM_SECRET = "secret";
    private static final String API_PARAM_REMOTE_ADDRESS = "remoteip";
    private static final String API_PARAM_RESPONSE = "response";


    class ApiResponse {
        private final Date timestamp;
        private final boolean valid;
        private final String hostname;

        public ApiResponse(boolean valid, Date timestamp, String hostname) {
            if(timestamp == null) throw new IllegalArgumentException("timestamp is null");
            if(hostname == null) throw new IllegalArgumentException("hostname is null");
            this.valid = valid;
            this.timestamp = timestamp;
            this.hostname = hostname;
        }

        public String getHostname() {
            return hostname;
        }

        public boolean isValid() {
            return valid;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    private boolean parseResponse(InputStream inputStream) throws IOException {
        String response = readResponse(inputStream);
        Pattern responsePattern = Pattern.compile("^\\s*\\{\\s*\"success\":\\s+(false|true)\\s*,", Pattern.MULTILINE |  Pattern.UNICODE_CASE);
        Matcher matcher = responsePattern.matcher(response);
        if(!matcher.find()) {
            System.out.println(this.getClass().getName() + " doesn't undestand response: \n" + response);
            return false;
        }
        String success = matcher.group(1);
        return "true".equals(success);
    }

    private String readResponse(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        String response = "";
        int readLength;
        do {
            readLength = inputStream.read(buffer);
            if(readLength > 0) {
                response += new String(buffer, 0, readLength, "UTF-8");
            }
        }while (readLength != -1);
        return response;
    }

    private String encodePostParameter(String name, String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }

    private String getPostPayload(String response, String remoteAddress) throws UnsupportedEncodingException {
        if(response == null) throw new IllegalArgumentException("response is null");
        String payload = encodePostParameter(API_PARAM_SECRET, CLIENT_SECRET) + "&";
        if(VERIFY_REMOTE_ADDRESS && remoteAddress != null) {
            payload += encodePostParameter(API_PARAM_REMOTE_ADDRESS, remoteAddress) + "&";
        }
        payload += encodePostParameter(API_PARAM_RESPONSE, response);
        return payload;
    }

    private boolean getApiResponse(String response, String remoteAddress) throws IOException {
        URL apiUrl = null;

        apiUrl = new URL(API_URL);
        String payload = getPostPayload(response, remoteAddress);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(payload.length()));
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(payload.getBytes("UTF-8"));

        InputStream inputStream = connection.getInputStream();
        return parseResponse(inputStream);
    }

    @Override
    public boolean verifyRequest(HttpServletRequest request) {
        String response = request.getParameter(RESPONSE_PARAMETER_NAME);
        if(response == null) return false;
        String remoteAddress = request.getRemoteAddr();
        boolean apiResponse;
        try {
            apiResponse = getApiResponse(response, remoteAddress);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return apiResponse;
    }
}
