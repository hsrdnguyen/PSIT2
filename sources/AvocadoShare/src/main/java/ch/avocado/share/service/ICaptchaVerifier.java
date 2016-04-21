package ch.avocado.share.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Verifies a CAPTCHA
 */
public interface ICaptchaVerifier {
    boolean verifyRequest(HttpServletRequest request);
}
