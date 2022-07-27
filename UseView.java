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
import com.nokia.dddeyet.gamewarden.db.Sql;

/**
 * <code>Servlet</code> for viewing cusomized reports on the
 * <code>Elephant</code> db, processed by
 * {@link com.nokia.dddeyet.gamewarden.servlets.More More}.
 * 
 * @author al.majko@nokia.com
 * @version 0.44
 * @since 0.1 06/20/12
 */
public class UseView extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * If <code>true</code> send <code>form</code> to user. Default is
	 * <code>false</code>
	 */
	private boolean writeform = false;// sending a response requiring more form
										// inputs?
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user.
	 * Default is <code>false</code>
	 */
	private boolean writeresult = false;// sending a response with only result
										// data?

	/**
	 * If <code>true</code> send <code>radio button form</code> to user. Default
	 * is <code>false</code>
	 */
	private boolean writeusebuttons = true;// sending a response with useful
											// buttons and
											// no other entries in a form?

	/**
 * 
 */
	Tags tags = new Tags();
	/**
	 * 
	 */
	Sql sql = new Sql();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UseView() {
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
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		String result = "";

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
			out.println("<TD><textarea name=\"sql\" value=\"sql\" cols=\"30\" rows=\"20\"></textarea></TD>");
			out.println("</TR>");
			out.println("</TABLE>");
			out.println("<UL>");
			// right justify
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"Do\" >Do an action and proceed to Next action</li>");
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

		// **********sending results?*********
		if (writeusebuttons) {

			// 2a)
			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
												// 'action=\"'

			out.println("More\">");
			out.println("<TABLE>");
			out.println("<TR>");
			out.println("<TD><INPUT type=\"hidden\" name=\"url\" value=\"jdbc:postgresql://dchiecapp06:5432/elephant\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD><INPUT type=\"hidden\" name=\"user\" value=\"gamewarden\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD><INPUT type=\"hidden\" name=\"password\" value=\"password\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("<TD><INPUT type=\"hidden\" name=\"action\" value=\"View\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("<TD><INPUT type=\"hidden\" name=\"method\" value=\"html\" size=\"40\" maxlength=\"60\"></TD>");
			out.println("<TR>");
			out.println("<td>");
			out.println("<UL>");
			// right justify

			/*
			 * out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 * sql.getListTables() + "\" checked><b>Tables</b></li>");
			 * out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 * sql.getListColumns() + "\"><b>Columns</b></li>");
			 */// out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
				// +
				// sql.getListKeysIndices()
				// + "\">Database <b>Keys</b></li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListEnvironments()
					+ "\"><img src=\"images/Anteater Logo.png\" height=\"37\" width=\"37\"><b>Environments</b></li>");
			out.println("<ul>");
			out.println("<font size=\"-1\">");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListPorttoSchemaEnvironments()
					+ "\"><b>Ports and Schemas</b></li>");
			out.println("</font>");
			out.println("</ul>");
			out.println("<br/>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListDatabases()
					+ "\"><img src=\"images/Cobra_Logo_2.0.png\" height=\"37\" width=\"37\"><b>Databases</b></li>");
			//out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
				//	+ sql.getListTests()
				//	+ "\"><img src=\"images/Mongoose Logo.png\" height=\"37\" width=\"37\"><b>Test Details</b></li>");
			//out.println("<ul>");
			//out.println("<font size=\"-1\">");
			//out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
			//		+ sql.getListTestsStatus()
				//	+ "\"><b>Status</b></li>");
			//out.println("</font>");
			//out.println("</ul>");
			//out.println("<br/>");
			/*
			 * out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 * sql.getListTestsStatus() + "\"><b>Status by Test Case</b></li>");*/
			 out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 sql.getListLatestStatus() + "\"><img src=\"images/Mongoose_Logo_2.0.png\" height=\"37\" width=\"37\"><b>Latest Status</b></li>");
			 /* out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 * sql.getListTestRunsStatus() + "\"><b>Test Runs</b></li>");
			 * out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\"" +
			 * sql.getListTestResults() + "\"><b>Test Results</b></li>");
			 */out.println("</UL>");
			out.println("</td>");
			out.println("</TR>");
			out.println("</TABLE>");

			// 2b)
			// FormEnd
			out.println(tags.getFormEnd());// from 'submit' button down thru
											// '</FORM>'
		}

		// 3)
		// Footers
		// out.println(tags.getGuidedFooter());
		// out.println(tags.getUnGuidedFooter());
		out.println(tags.getFooter());

		out.close(); // clean up

	}

}
