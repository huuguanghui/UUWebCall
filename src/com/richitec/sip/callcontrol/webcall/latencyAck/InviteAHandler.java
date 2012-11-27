package com.richitec.sip.callcontrol.webcall.latencyAck;

import java.net.InetSocketAddress;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.richitec.Configuration;
import com.richitec.sip.Constant;
import com.richitec.sip.ISIPResponseHandler;
import com.richitec.sip.callcontrol.webcall.UserSipServlet;

public class InviteAHandler implements com.richitec.sip.ISIPResponseHandler {

	private SipFactory sipFactory;
	
	private static InviteAHandler _instance;
	
	public synchronized static ISIPResponseHandler getInstance(SipFactory sipFactory){
		if (null == _instance){
			_instance = new InviteAHandler(sipFactory);
		}
		return _instance;
	}

	private InviteAHandler(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	@Override
	public void doSuccessResponse(SipServletResponse response) {
		// get caller sipSession and sipApplicationSession
		SipSession sessionA = response.getSession(false);
		SipApplicationSession sipAppSession = sessionA.getApplicationSession();

		try {
			// set callee sipServletRequest header
			Address fromAddr = sipFactory.createAddress(Configuration.getSipUri());
			Address toAddr = (Address) sessionA.getAttribute(Constant.LINKEDUSER_SIP_ADDR);

			SipServletRequest invite = sipFactory.createRequest(sipAppSession,
					Constant.INVITE, fromAddr, toAddr);

			// set invite to B content with SDP of A
			invite.setContent(response.getContent(), response.getContentType());

			// set route address
			Address routeAddr = sipFactory.createAddress(
							(String) sipAppSession.getAttribute(Constant.Soft_Switch_SIP_URI));
			// set lr parameter, it is important
			routeAddr.setParameter("lr", "");
			invite.pushRoute(routeAddr);

			// set callee sip session and exchange its sipSession as
			// linkedSession
			SipSession sessionB = invite.getSession();
			sessionB.setAttribute(Constant.LINKED_SESSION, sessionA);
			sessionA.setAttribute(Constant.LINKED_SESSION, sessionB);
			// set sessionB userSipUri and its linked userSipUri
			sessionB.setAttribute(Constant.USER_SIP_ADDR,
			        sessionA.getAttribute(Constant.LINKEDUSER_SIP_ADDR));
			sessionB.setAttribute(Constant.LINKEDUSER_SIP_ADDR,
					sessionA.getAttribute(Constant.USER_SIP_ADDR));

			// set server outbound ip
			String outboundIPAddr = 
				Configuration.getOutboundIpAddrToSoftSwitch();
			Integer port = Configuration.getOutboundPort();
			InetSocketAddress address = new 
				InetSocketAddress(outboundIPAddr, port);
			sessionB.setOutboundInterface(address);

			// set callee call handle
			String cseq = invite.getHeader("CSeq");
			sessionB.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
					new InviteBHandler(response));
			sessionB.setHandler(UserSipServlet.class.getSimpleName());

			invite.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doErrorResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
		int statusCode = response.getStatus();
	}

	@Override
	public void doProvisionalResponse(SipServletResponse response) {
		SipSession session = response.getSession(false);
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

