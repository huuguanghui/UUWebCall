package com.richitec.sip.callcontrol.webcall.latencyAck;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.richitec.sip.Constant;
import com.richitec.sip.ISIPResponseHandler;
import com.richitec.sip.callcontrol.webcall.UserHangupHandler;

public class InviteBHandler implements com.richitec.sip.ISIPResponseHandler {

	private SipServletResponse aResponse;

	public InviteBHandler(SipServletResponse response) {
		this.aResponse = response;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// get callee sipSession and linkedSession caller sipSession
		SipServletRequest ackB = response.createAck();
		try {
			ackB.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession sessionB = response.getSession(false);
		SipSession sessionA = (SipSession) sessionB
				.getAttribute(Constant.LINKED_SESSION);
		int statusCode = response.getStatus();

		// process 480 and 486, send bye to caller sipSessionA
		try {
			if (SipServletResponse.SC_TEMPORARLY_UNAVAILABLE == statusCode
					|| SipServletResponse.SC_BUSY_HERE == statusCode) {
				SipServletRequest bye = sessionA.createRequest(Constant.BYE);

				// set handle
				String cseq = bye.getHeader("CSeq");
				sessionA.setAttribute(cseq
						+ ISIPResponseHandler.RESPONSE_HANDLER,
						new UserHangupHandler());

				bye.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doProvisionalResponse(SipServletResponse response) {
		// get response status
		int status = response.getStatus();
		try {
			// process 183 session_process of inviteB
			if (SipServletResponse.SC_SESSION_PROGRESS == status) {
				SipServletRequest ackA = aResponse.createAck();
				ackA.setContent(response.getContent(),
						response.getContentType());
				ackA.send();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doBranchResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doRedirectResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

}

