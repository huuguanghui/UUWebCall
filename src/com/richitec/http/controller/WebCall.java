package com.richitec.http.controller;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.richitec.Configuration;
import com.richitec.sip.Constant;
import com.richitec.sip.ISIPResponseHandler;
import com.richitec.sip.SDPHelper;
import com.richitec.sip.callcontrol.webcall.UserSipServlet;
import com.richitec.sip.callcontrol.webcall.latencyAck.InviteAHandler;

@Controller
@RequestMapping("/webcall")
public class WebCall extends ErrorController{
	
	private static final Logger logger = Logger.getLogger(WebCall.class);

	@RequestMapping("/call")
	public @ResponseBody String latencyAck(HttpSession httpSession,
			@RequestParam(value = "caller", required = true) String caller,
			@RequestParam(value = "callee", required = true) String callee) throws ServletException, IOException{
		
		ServletContext ctx = httpSession.getServletContext();
		SipFactory sipFactory = (SipFactory)ctx.getAttribute(Constant.SipFactory);
		
		logger.info("caller = " + caller);
		logger.info("callee = " + callee);
		
		Address callerAddr = sipFactory.createAddress(caller);
		Address calleeAddr = sipFactory.createAddress(callee);
		Address fromAddr = sipFactory.createAddress(Configuration.getSipUri());

		// get ss sipUri and callbackUrl
		String ssSipUri = Configuration.getSoftSwitchSipURI();

		// create a sipApplicationSession
		SipApplicationSession sipAppSession = sipFactory.createApplicationSession();
		sipAppSession.setExpires(Configuration.getConfSessionExpire());
		sipAppSession.setInvalidateWhenReady(true);
		sipAppSession.setAttribute(Constant.Soft_Switch_SIP_URI, ssSipUri);

		SipServletRequest invite = sipFactory.createRequest(
				sipAppSession, Constant.INVITE, fromAddr, callerAddr);

		// set no media sdp
		invite.setContent(SDPHelper.getNoMediaSDP(""),
				SDPHelper.SDP_CONTENT_TYPE);

		// set route address
		Address routeAddr = sipFactory.createAddress(ssSipUri);
		// set lr parameter, it is important
		routeAddr.setParameter("lr", "");
		invite.pushRoute(routeAddr);

		// set caller sip session
		SipSession session = invite.getSession();
		// remember this session so we can unjoin this call.
		session.setAttribute(Constant.INVITE_USER_REQUEST, invite);
		session.setAttribute(Constant.USER_SIP_ADDR, callerAddr);
		session.setAttribute(Constant.LINKEDUSER_SIP_ADDR, calleeAddr);

		// set server outbound ip
		String outboundIPAddr = Configuration
				.getOutboundIpAddrToSoftSwitch();
		Integer port = Configuration.getOutboundPort();
		InetSocketAddress address = 
			new InetSocketAddress(outboundIPAddr, port);
		session.setOutboundInterface(address);

		// set caller call handle
		String cseq = invite.getHeader("CSeq");
		session.setAttribute(cseq + ISIPResponseHandler.RESPONSE_HANDLER,
				InviteAHandler.getInstance(sipFactory));
		session.setHandler(UserSipServlet.class.getSimpleName());
		invite.send();
		
		return "ok";
	}
}
