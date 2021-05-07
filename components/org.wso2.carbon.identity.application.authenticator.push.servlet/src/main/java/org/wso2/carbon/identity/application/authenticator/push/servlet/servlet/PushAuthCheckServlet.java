package org.wso2.carbon.identity.application.authenticator.push.servlet.servlet;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.InboundConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.PushServletConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.model.WaitStatus;
import org.wso2.carbon.identity.application.authenticator.push.servlet.store.impl.PushDataStoreImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for handling the status checks for authentication requests from the push authenticator wait page
 */
public class PushAuthCheckServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(PushAuthCheckServlet.class);
    private PushDataStoreImpl pushDataStoreInstance = PushDataStoreImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Handling request from authenticator wait page to check status of the auth request
        if (!(request.getParameterMap().containsKey(InboundConstants.RequestProcessor.CONTEXT_KEY))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            if (log.isDebugEnabled()) {
                log.debug("Error occurred when checking authentication status. The session data key was "
                        + "null or the HTTP request was unsupported.");
            }

        } else {
            handleWebResponse(request, response);
        }
    }

    /**
     * Handles requests received from the wait page to check the authentication status
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @throws IOException
     */
    private void handleWebResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        WaitStatus waitResponse = new WaitStatus();
        String sessionDataKeyWeb = request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
        String status = pushDataStoreInstance.getAuthStatus(sessionDataKeyWeb);

        if (status == null) {
            response.setStatus(HttpServletResponse.SC_OK);
            if (log.isDebugEnabled()) {
                log.debug("Mobile authentication response has not been received yet!");
            }

        } else if (status.equals(PushServletConstants.COMPLETED)) {
            // If the signed challenge sent from the mobile application is not null,else block is executed..
            response.setStatus(HttpServletResponse.SC_OK);
            waitResponse.setStatus(PushServletConstants.COMPLETED);
            pushDataStoreInstance.removePushData(sessionDataKeyWeb);
            response.setContentType(MediaType.APPLICATION_JSON);
            String json = new Gson().toJson(waitResponse);

            if (log.isDebugEnabled()) {
                log.debug("Json Response to the wait page: " + json);
            }

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

        } else {
            if (log.isDebugEnabled()) {
                log.debug("Unknown value given for status!");
            }
            // TODO: Return appropriate response
        }
    }
}