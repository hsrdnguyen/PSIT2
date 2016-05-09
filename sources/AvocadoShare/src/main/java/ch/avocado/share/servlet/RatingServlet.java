package ch.avocado.share.servlet;


import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.constants.sql.RatingConstants;
import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.model.exceptions.HttpServletException;
import ch.avocado.share.service.IRatingDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.servlet.resources.base.ExtendedHttpServlet;
import ch.avocado.share.servlet.resources.base.Parameter;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static ch.avocado.share.common.HttpStatusCode.*;

@WebServlet("/rating")
public class RatingServlet extends ExtendedHttpServlet{

    public static final String PARAMETER_OBJECT_ID = "object";
    public static final String PARAMETER_RATING = "rating";

    private long getAndCheckObjectId(Parameter parameter) throws HttpServletException {
        long objectId;
        try {
            objectId = Long.parseLong(parameter.getRequiredParameter(PARAMETER_OBJECT_ID));
        } catch (NumberFormatException e) {
            throw new HttpServletException(BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        if(objectId < 0) {
            throw new HttpServletException(BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        return objectId;
    }

    private int getAndCheckRating(Parameter parameter) throws HttpServletException {
        int rating;
        try {
            rating = Integer.parseInt(parameter.getRequiredParameter(PARAMETER_RATING));
        } catch (NumberFormatException e) {
            throw new HttpServletException(BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        if( rating < RatingConstants.MIN_RATING_VALUE || rating > RatingConstants.MAX_RATING_VALUE) {
            throw new HttpServletException(BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        return rating;
    }

    private long getUserId(UserSession session) throws HttpServletException {
        if (!session.isAuthenticated()) {
            throw new HttpServletException(UNAUTHORIZED, ErrorMessageConstants.NOT_LOGGED_IN);
        }
        long userId;
        try {
            userId = Long.parseLong(session.getUserId());
        } catch (NumberFormatException e) {
            throw new HttpServletException(BAD_REQUEST, ErrorMessageConstants.INVALID_REQUEST);
        }
        return userId;
    }


    private Rating getCurrentRating(long objectId) throws HttpServletException {
        IRatingDataHandler ratingDataHandler;
        try {
            ratingDataHandler = ServiceLocator.getService(IRatingDataHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new HttpServletException(e);
        }
        try {
            return ratingDataHandler.getRatingForObject(objectId);
        } catch (DataHandlerException e) {
            throw new HttpServletException(e);
        }
    }

    private void writeCurrentRating(Rating rating, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        String jsonData = "{\"rating\": ";
        jsonData += rating.getRating();
        jsonData += "}";
        outputStream.write(jsonData.getBytes());
        outputStream.flush();
    }

    @Override
    protected void doReplace(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        long userId = getUserId(session);
        long objectId = getAndCheckObjectId(parameter);
        int rating = getAndCheckRating(parameter);
        IRatingDataHandler ratingDataHandler;
        try {
            ratingDataHandler = ServiceLocator.getService(IRatingDataHandler.class);
            ratingDataHandler.putRating(objectId, userId, rating);
        } catch (DataHandlerException | ServiceNotFoundException e) {
            throw new HttpServletException(e);
        }
        Rating currentRating = getCurrentRating(objectId);
        writeCurrentRating(currentRating, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response, UserSession session, Parameter parameter) throws HttpServletException, IOException {
        long userId = getUserId(session);
        long objectId = getAndCheckObjectId(parameter);
        IRatingDataHandler ratingDataHandler;
        try {
            ratingDataHandler = ServiceLocator.getService(IRatingDataHandler.class);
            ratingDataHandler.deleteRating(objectId, userId);
        } catch (DataHandlerException | ServiceNotFoundException | ObjectNotFoundException e) {
            throw new HttpServletException(e);
        }
        Rating currentRating = getCurrentRating(objectId);
        writeCurrentRating(currentRating, response);
    }
}
