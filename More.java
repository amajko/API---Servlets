package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.actions.usejpa.InvokeGwAcronymClient;
import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.utilities.ReplaceChars;
import com.nokia.dddeyet.gamewarden.db.*;

/**
  * <code>Servlet</code> that processes
 * {@link com.nokia.dddeyet.gamewarden.servlets.View View}
 * and
 * {@link com.nokia.dddeyet.gamewarden.servlets.UseView UseView}.
 * 
 * @author al.majko@nokia.com
 * @version 0.29
 * @since 0.1 06/20/12
 */
public class More extends HttpServlet {
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
	 * 
	 */
	ReplaceChars rp = new ReplaceChars();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public More() {
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
		MoreInfo info = new MoreInfo();
		info.setUrl(request.getParameter("url"));
		info.setAction(request.getParameter("action"));
		info.setUser(request.getParameter("user"));
		info.setPassword(request.getParameter("password"));
		info.setMethod(request.getParameter("method"));
		info.setSql(request.getParameter("sql"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		Connect conn = new Connect();
		String result = "";

		if (info.getMethod().equals("html")/*
											 * || info. getMethod ().
											 * equalsIgnoreCase ("html")
											 */) {
			result = conn.Retrieve(info.getUrl(), info.getAction(),
					info.getUser(), info.getPassword(), info.getSql());
		}
		if (info.getMethod().equalsIgnoreCase("wohtml")) {
			result = conn.RetrieveWOHtml(info.getUrl(), info.getAction(),
					info.getUser(), info.getPassword(), info.getSql());
		}
		if (info.getMethod().equalsIgnoreCase("wohtmlcsv")) {
			result = conn.RetrieveWOHtmlCSV(info.getUrl(), info.getAction(),
					info.getUser(), info.getPassword(), info.getSql());
		}

		// debug
		// String result = (info.getJob()+ " " + info.getAction() + " " +
		// info.getParameters() + " " + info.getLocation());
		
		//test JPA usage
		//InvokeGwAcronymClient igac = new InvokeGwAcronymClient("abc", true, true, false);

		// 3)
		// finally respond with page here,with and bean info attached if desired
		// (getter mthods on the bean)
		WritePage(request, response/*
									 * ,userinfo.getUsername(),userinfo.getPassword
									 * ()
									 */, result, info.getMethod());

	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param result
	 * @param method
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
			String result, String method) throws ServletException, IOException {

		// set content type
		if (method.equalsIgnoreCase("wohtmlcsv")) {
			resp.setContentType("text/plain");
		} else {
			resp.setContentType("text/html");
		}

		// link res output stream to ServletOutputStream
		ServletOutputStream out = resp.getOutputStream();

		// send form to user

		// 0 set String to contain the email content
		String emailthis = "";

		if (!method.equalsIgnoreCase("wohtmlcsv")) {
			// 1)
			// Headers
			out.println(tags.getHeader());

			// 0 set LINK to contain the email content
			out.println("<a href='#emailthis'>Email This Page (see form at bottom)</a><br/>");

			// out.println("<html>");//if using jqxWidget
			// 0 set String to contain the email content
			emailthis += tags.getHeader();
			// emailthis +="<html>";//if using jqxWidget

			// out.println(tags.getGuidedHeader());
			// out.println(tags.getUnGuidedHeader());

			// 0 set String to contain the email content
			// emailthis += tags.getGuidedHeader() + tags.getUnGuidedHeader();

			// 1a1)post referring url for info purposes
			// out.println(req.getRequestURL() + req.getQueryString().toString()
			// +
			// "<br>");
		}

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
			out.println("<TD><INPUT type=\"text\" name=\"url\" value=\"url\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>User");
			out.println("<TD><INPUT type=\"text\" name=\"user\" value=\"user\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Password");
			out.println("<TD><INPUT type=\"text\" name=\"password\" value=\"password\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>SQL");
			out.println("<TD><textarea name=\"sql\" value=\"sql\" cols=\"30\" rows=\"30\"></textarea></TD>");
			out.println("</TR>");
			out.println("</TABLE>");
			out.println("<UL>");
			// right justify
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"Do\">Do</li>");
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"View\" checked>View</li>");
			out.println("</UL>");

			// 2b)
			// FormEnd
			out.println(tags.getFormEnd());// from 'submit' button down thru
											// '</FORM>'
		}
		// **********sending results?*********
		if (writeresult) {

			if (method.equalsIgnoreCase("wohtmlcsv")) {
				result = result.replaceAll("\r", "\n");
			}
			// to add a jqxWidget (the Expander)
			// out.println(tags.getJqxExpanderHead());//if using jqxWidget
			// out.println("<body bgcolor='ffffff' text='000000'><center>");//if
			// using jqxWidget

			// emailthis += "<body bgcolor='ffffff' text='000000'><center>";//if
			// using jqxWidget
			// out.println(tags.getJqxExpanderBody());//if using jqxWidget

			// out.println("<div style='float:center;'>");//to add a jqxWidget
			// (the Expander)--this opnes the output
			out.println(result);
			// out.println("</div>");//to add a jqxWidget (the Expander)--this
			// closes the output
			// out.println("</div>");//to add a jqxWidget (the Expander)--this
			// closes the jqxExpanderBody
			// out.println("</div>");//to add a jqxWidget (the Expander)--this
			// closes the jqxWidget(ExpanderHead)

			// 0 set String to contain the email content
			emailthis += result;

		}

		if (!method.equalsIgnoreCase("wohtmlcsv")) {
			// 3)
			// Footers
			// out.println(tags.getGuidedFooter());
			// out.println(tags.getUnGuidedFooter());
			// 0 set String to contain the email content
			// emailthis += tags.getGuidedFooter()+tags.getGuidedFooter();

			// 0
			// finish emailthis so email form contains html closing
			emailthis += tags.getFooter();

			// 0 set LINK to contain the email content
			out.println("<a name='#emailthis'></a>");

			// --BEGIN email form----------------
			out.println(tags.getEmailForm());
			// process url parameters and add to content value
			// or
			// parse entire response
			String fullurl = "";
			String partresp = "";
			boolean parseurl = false;
			boolean parseresp = true;
			if (parseurl) {
				ParseHttp pu = new ParseHttp();
				fullurl = pu.ParseUrl(req);
				out.println(
				/*
				 * "<TR>" +
				 * "<td><INPUT type=\"hidden\" name=\"subject\" value=\"" +
				 * req.getRequestURL()"Report from Guardian" +
				 * "\" size=\"40\" maxlength=\"60\"></td>" + "</TR>"
				 */
				"<TR>" + "<td><INPUT type=\"hidden\" name=\"content\" value=\'"
						+ "<a href=\""
						+ /* req.getRequestURL().toString() */resp
						+ "\">Click Here</a>"
						+ "\' size=\"40\" maxlength=\"60\"></td>" + "</TR>"
						+ "</TABLE>" + tags.getFormEnd());
			}
			if (parseresp) {// wont work casue out serlvetoutputstream hasnt
							// been
							// written yet...put this for on bottom of page?
				// BELO not nec if sending out.toString()[emailthis] as hidden
				// value works OK
				// ParseHttp pu = new ParseHttp();
				// partresp = pu.ParseResp(resp);
				emailthis = rp.ReplaceString("'", "&#39;", emailthis);
				out.println(
				/*
				 * "<TR>" +
				 * "<td><INPUT type=\"hidden\" name=\"subject\" value=\"" +
				 * req.getRequestURL()"Report from Guardian" +
				 * "\" size=\"40\" maxlength=\"60\"></td>" + "</TR>"
				 */
				"<TR>" + "<td><INPUT type=\"hidden\" name=\"content\" value=\'"
						// + "<a href=\""
						+ /* req.getRequestURL().toString() */emailthis
						// + "\">Click Here</a>"
						+ "\' size=\"40\" maxlength=\"60\"></td>" + "</TR>"
						+ "</TABLE>" + tags.getFormEnd());
			}
			// --END email form----------------

			out.println(tags.getFooter());
		}

		out.close(); // clean up

	}

}
