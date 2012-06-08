package com.richitec.sip.callcontrol.webcall;

import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.richitec.sip.ISIPResponseHandler;

public class UserHangupHandler  implements ISIPResponseHandler {

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
		int errCode = response.getStatus();
	}

	@Override
	public void doProvisionalResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

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
