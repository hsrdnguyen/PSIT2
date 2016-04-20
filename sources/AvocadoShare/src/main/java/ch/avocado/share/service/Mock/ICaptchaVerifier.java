package ch.avocado.share.service.Mock;

import javax.servlet.http.HttpServletRequest;

/**
 * Verifies a CAPTCHA
 */
public interface ICaptchaVerifier {
    boolean verifyRequest(HttpServletRequest request);
}
