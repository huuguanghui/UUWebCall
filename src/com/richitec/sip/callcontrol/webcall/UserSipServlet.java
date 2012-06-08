package com.richitec.sip.callcontrol.webcall;

import java.io.IOException;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.richitec.sip.Constant;

/**
 * SipServlet implementation class ControlChannelSipServlet
 */
@javax.servlet.sip.annotation.SipServlet
public class UserSipServlet extends com.richitec.sip.BaseSipServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(UserSipServlet.class);

	@Override
	protected void doBye(SipServletRequest req) {
		SipSession session = req.getSession(false);
		SipSession linkedSession = (SipSession) session
				.getAttribute(Constant.LINKED_SESSION);

		logger.info("SIP BYE From : " + req.getFrom().toString());

		// caller session
		if (null != session.getAttribute(Constant.INVITE_USER_REQUEST)) {
			doBye(session, req);
		}
		// callee session
		else {
			logger.info("callee do bye!");
			doBye(linkedSession, req);
		}
	}

	private void doBye(SipSession session, SipServletRequest request) {
		// send bye to its linked user session and notify
		SipSession reqSession = request.getSession(false);
		if (null == reqSession
				.getAttribute(Constant.INVITE_USER_REQUEST)) {
			logger.info("callee ok");
			SipServletRequest bye = session.createRequest(Constant.BYE);
			try {
				bye.send();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// send 200 OK to the session
		SipServletResponse resp = request
				.createResponse(SipServletResponse.SC_OK);
		try {
			resp.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
