package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.utilities.SendHTMLEmail;
//import com.nokia.dddeyet.gamewarden.actions.*;
//import javax.mail.MessagingException;

/**
 * <code>Servlet</code> to provide email services. Especially used to send
 * results from {@link com.nokia.dddeyet.gamewarden.servlets.More More}.
 * Entry <code>form</code> is embedded in each {@link com.nokia.dddeyet.gamewarden.servlets.More More} response.
 * 
 * @author al.majko@nokia.com
 * @version 0.17
 * @since 0.17 08/14/12
 */
public class EMail extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * If <code>true</code> send <code>form</code> to user Default is
	 * <code>false</code>
	 */
	private boolean writeform = false;// sending a response requiring more form
										// inputs?
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user
	 * Default is <code>true</code>
	 * 
	 */
	private boolean writeresult = true;// sending a response with only result
										// data?
	/**
 * 
 */
	Tags tags = new Tags();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EMail() {
		super();
		// TODO Auto-generated constructor stub

	}

	/**
	 * Calls doPost.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		// 1)
		// get paramters from any form that called this action here and
		// inititlize beasn, if desired
		EmailInfo info = new EmailInfo();
		// public String Send(String to, String from,String host,String
		// property,String subject,String content)
		info.setTo(request.getParameter("to"));
		info.setFrom(request.getParameter("from"));
		info.setHost(request.getParameter("host"));
		info.setProperty(request.getParameter("property"));
		info.setSubject(request.getParameter("subject"));
		info.setContent(request.getParameter("content"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		String result = "";
		SendHTMLEmail email = new SendHTMLEmail();
		result = email.Send(info.getTo(), info.getFrom(), info.getHost(),
				info.getProperty(), info.getSubject(), info.getContent());
		// debug
		// String result = (info.getJob()+ " " + info.getAction() + " " +
		// info.getParameters() + " " + info.getLocation());

		// 3)
		// finally respond with page here,with and bean info attached if desired
		// (getter mthods on the bean)
		WritePage(request, response/*
									 * ,userinfo.getUsername(),userinfo.getPassword
									 * ()
									 */, result);

	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param result
	 * @throws ServletException
	 * @throws IOException
	 */
	public void WritePage(HttpServletRequest req, HttpServletResponse resp/*
																		 * ,
																		 * String
																		 * username
																		 * ,
																		 * String
																		 * password
																		 */,
			String result) throws ServletException, IOException {

		// set content type
		resp.setContentType("text/html");

		// link res output stream to ServletOutputStream
		ServletOutputStream out = resp.getOutputStream();

		// send form to user

		// 1)
		// Headers
		out.println(tags.getHeader());
		// out.println(tags.getGuidedHeader());
		// out.println(tags.getUnGuidedHeader());

		// 1a1)post referring url for info purposes
		// out.println(req.getRequestURL() + req.getQueryString().toString() +
		// "<br>");

		// **********sending form?*********
		if (writeform) {
			// 1a)
			// List Choices for textfield input
			Choices choices = new Choices();
			out.println(choices.getJobs());

			// 2a)
			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
												// 'action=\"'

			out.println("Next\">");
			out.println("<TABLE border=\"1\" bgcolor=\"silver\">");
			out.println("<TR>");
			out.println("<TD>Input");
			out.println("<TD><INPUT type=\"text\" name=\"job\" value=\"Job\" size=\"40\" maxlength=\"40\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Parameters");
			out.println("<TD><INPUT type=\"text\" name=\"parameters\" value=\"space separated\" size=\"40\" maxlength=\"100\"></TD>");
			out.println("</TR>");
			out.println("</TABLE>");
			out.println("<UL>");
			// right justify
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"  ChooseToDo\" checked>Do</li>");
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"ChooseToView\">View</li>");
			out.println("</UL>");

			// 2b)
			// FormEnd
			out.println(tags.getFormEnd());// from 'submit' button down thru
											// '</FORM>'
		}
		// **********sending results?*********
		if (writeresult) {

			out.println(result);

		}

		// 3)
		// Footers
		// out.println(tags.getGuidedFooter());
		// out.println(tags.getUnGuidedFooter());
		out.println(tags.getFooter());

		out.close(); // clean up

	}

}
