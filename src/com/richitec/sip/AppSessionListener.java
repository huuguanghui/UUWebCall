package com.richitec.sip;

import java.io.IOException;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;

import org.apache.log4j.Logger;

/**
 * SipListener class AppSessionListener
 */
@javax.servlet.sip.annotation.SipListener
public class AppSessionListener implements
		javax.servlet.sip.SipApplicationSessionListener,
		javax.servlet.sip.SipSessionListener {

	private static final Logger logger = Logger
			.getLogger(AppSessionListener.class);

	public void sessionCreated(javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionDestroyed(
			javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionExpired(javax.servlet.sip.SipApplicationSessionEvent sase) {
		// TODO -- add implementation, if necessary
	}

	public void sessionReadyToInvalidate(SipApplicationSessionEvent sase) {
		SipApplicationSession sipAppSession = sase.getApplicationSession();
		logger.debug("SipApplicationSession[" + sipAppSession.getId()
				+ "] Ready To Invalidate");
	}

	public void sessionReadyToInvalidate(SipSessionEvent sse) {
		SipSession session = sse.getSession();
		SipSession linkedSession = (SipSession) session
				.getAttribute(Constant.LINKED_SESSION);

		logger.info("Session" + session.getId() + " its call-id: "
				+ session.getCallId() + " is ready to invalidate!");

		if (null == session.getAttribute(Constant.INVITE_USER_REQUEST)) {
			if (null == linkedSession || !linkedSession.isValid()) {
				Long startTime = 0L;
				Long endTime = 0L;
			}
			// linked session is invalid
			else {
				logger.info("The session not a caller invite session and linked session is valid, return immediately!");
			}

			return;
		}

		if (null != linkedSession && linkedSession.isValid()) {
			logger.info("Caller is bye, callee session is valid!");

			// send bye to callee
			SipServletRequest bye = linkedSession.createRequest(Constant.BYE);
			try {
				bye.send();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// send event to session
	}

	@Override
	public void sessionCreated(SipSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(SipSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
