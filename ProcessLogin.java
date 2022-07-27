package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import com.nokia.dddeyet.gamewarden.beans.UserInfo;
import com.nokia.dddeyet.gamewarden.utilities.ldap.SOAPClient;

/**
 * 
 * Handles Login using <code>ldap</code><code>SOAP</code> utilities <br/>
 * (from 07/11/03)
 * 
 * @author al.majko@nokia.com
 * @version 0.52 0108
 * @since 0.52 01/08/13
 */
public class ProcessLogin extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// feature.55.txt Improved look and feel to GUI
		// Added by sattish to include the Header1.htm
		ServletContext servletContext = req.getSession().getServletContext();

		UserInfo userinfo = new UserInfo();
		userinfo.setUsername(req.getParameter("username"));

		userinfo.setGroup(req.getParameter("group"));
		// getServletContext().getRequestDispatcher("resources\\web\\html\\400\\Welcome.html").forward(req,resp);

		String result = "";

		// login to ldap server
		SOAPClient callClient = new SOAPClient(
				"http://titan:8077/axis2/services/LDAPService?wsdl", "ldap",
				"http://ldap.services.navteq",
				"<ldap:authenticateWithGroup xmlns:ldap=\"http://ldap.services.navteq\">"
						+ "<!--Optional:-->" + "<ldap:login>"
						+ userinfo.getUsername().trim() + "</ldap:login>"
						+ "<!--Optional:-->" + "<ldap:password>"
						+ req.getParameter("password").trim()
						+ "</ldap:password>" + "<!--Optional:-->"
						+ "<ldap:group>" + userinfo.getGroup().trim()
						+ "</ldap:group>" + "</ldap:authenticateWithGroup>",
				"ns:return");
		try {
			result = callClient.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userinfo.setResponse(callClient.returnvaluesNokiahm.get(result));

		HttpSession session = req.getSession(true);
		session.setAttribute("userinfo", userinfo);

		WritePage(req, resp, userinfo.getResponse(), userinfo.getUsername(),
				userinfo.getGroup());
	}

	public void WritePage(HttpServletRequest req, HttpServletResponse resp,
			String result, String username, String group)
			throws ServletException, IOException {

		// set content type
		resp.setContentType("text/html");

		// link res output stream to ServletOutputStream
		ServletOutputStream out = resp.getOutputStream();

		// send form to user

		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		out.println("<META name=\"GENERATOR\" content=\"IBM WebSphere Studio\">");
		out.println("<TITLE>WelcomeConstants.html</TITLE>");
		out.println("</HEAD>");
		out.println("<BODY>");
		out.println("<H1>Welcome to Guardian</H1>");
		out.println("<TABLE border=\"1\" bgcolor=\"#c0c0c0\">");
		out.println("<TBODY>");
		out.println("<TR>");
		out.println("<TD>UserName is:</TD>");
		out.println("<TD>");

		out.println(username);

		out.println("</TD>");
		out.println("</TR>");

		out.println("<TR>");
		out.println("<TD>Group is:</TD>");
		out.println("<TD>");

		out.println(group);

		out.println("</TD>");
		out.println("</TR>");

		out.println("<TR>");
		out.println("<TD>Result is:</TD>");
		out.println("<TD>");

		out.println(result);

		out.println("</TD>");
		out.println("</TR>");

		out.println("</TBODY>");
		out.println("</TABLE>");
		out.println("</BODY>");
		out.println("</HTML>");

		out.close(); // clean up

	}

}
