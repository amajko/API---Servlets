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

/**
 * <code>Servlet</code> for viewing any data from the <code>Elephant</code> db
 * by <code>SQL</code> that is entered by user, processed by
 * {@link com.nokia.dddeyet.gamewarden.servlets.More More}.
 * 
 * @author al.majko@nokia.com
 * @version 0.19
 * @since 0.1 06/20/12
 */
public class View extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * If <code>true</code> send <code>form</code> to user. Default is
	 * <code>true</code>
	 */
	private boolean writeform = true;// sending a response requiring more form
										// inputs?
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user.
	 * Default is <code>false</code>
	 */
	private boolean writeresult = false;// sending a response with only result
										// data?
	/**
 * 
 */
	Tags tags = new Tags();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public View() {
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
		ViewInfo info = new ViewInfo();
		// userinfo.setUsername(request.getParameter("username"));
		// userinfo.setPassword(request.getParameter("password"));
		info.setSql(request.getParameter("sql"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		String result = "";
		if (info.getSql() != null) {
			result = info.getSql();
		} else {
			result = "";
		}

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
			out.println(choices.getData());

			// 2a)
			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
												// 'action=\"'

			out.println("More\">");
			out.println("<TABLE border=\"1\" bgcolor=\"silver\">");
			out.println("<TR>");
			out.println("<TD>URL");
			out.println("<TD><INPUT type=\"text\" name=\"url\" value=\""
					+ choices.url + "\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>User");
			out.println("<TD><INPUT type=\"text\" name=\"user\" value=\""
					+ choices.user + "\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Password");
			out.println("<TD><INPUT type=\"text\" name=\""
					+ choices.password
					+ "\" value=\"password\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Response Method [html, wohtml, wohtmlcsv]");
			out.println("<TD><INPUT type=\"text\" name=\"method\" value=\"html\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>SQL (in lower case)");
			out.println("<TD><textarea name=\"sql\" value=\"sql\" cols=\"90\" rows=\"40\">"
					+ result + "</textarea></TD>");
			out.println("</TR>");
			out.println("</TABLE>");
			out.println("<UL>");
			// right justify
			// out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"Do\" >Do an action and proceed to Next action</li>");
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"View\" checked>View (or update) data and proceed to More data</li>");
			out.println("</UL>");

			// 2b)
			// FormEnd
			out.println(tags.getFormEnd());// from 'submit' button down thru
											// '</FORM>'
		}

		// **********sending results?*********
		if (writeresult) {

		}

		// 3)
		// Footers
		// out.println(tags.getGuidedFooter());
		// out.println(tags.getUnGuidedFooter());
		out.println(tags.getFooter());

		out.close(); // clean up

	}

}
