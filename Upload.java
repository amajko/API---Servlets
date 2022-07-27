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
import com.nokia.dddeyet.gamewarden.actions.Command;

/**
 * <code>Servlet</code> for providing a form to upload <code>ROBOT</code> test
 * cases, processed by {@link com.nokia.dddeyet.gamewarden.servlets.Store Store}
 * .
 * 
 * @author al.majko@nokia.com
 * @author michal.rys@nokia.com
* @author Srujana.Bobba@navteq.com
 * @version 0.52 0108
 * @since 0.13 07/29/12
 */
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * If <code>true</code> send <code>form</code> to user
	 * Default is <code>true</code>
	 */
	private boolean writeform = true;// sending a response requiring more form
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user
	 * Default is <code>false</code>
	 * 
	 */
	private boolean writeresult = false;// sending a response with only result
										// data?
	/**
	 * <code>Constant</code> to hold default value for
	 * <code>form testfield</code>. Default = <code>enter testcase name</code>
	 */
	public static final String defaultTestCaseName = "enter testcase name";

	/**
	 * 
	 */
	Tags tags = new Tags();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Upload() {
		super();
		// TODO Auto-generated constructor stub

	}

	/**
	 * Calls doPost
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      
	 *     
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
		BeginInfo info = new BeginInfo();
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
			// out.println("<h1>Note how ROBOT shows where it will store its status  files</h1>");
			// out.println("<img src=\"images/commandoutput.png\"><br/>");
			// out.println("<h1>Find the folder in your directories and choose the \"output.xml\"</h1>");
			// out.println("<img src=\"images/whereoutput.png\"><br/>");

			// 2a)
			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
												// 'action=\"'

			out.println("Store\" ENCTYPE=\"multipart/form-data\">");
			out.println("<TABLE border=\"1\" bgcolor=\"silver\">");


			// suite file input
			out.println("<TR>");
			out.println("<TD>Location of suite file</td>");
			out.println("<TD><INPUT type=\"file\" name=\"suite_file\" accept=\"text/*\" value=\"\" size=\"100\" maxlength=\"200\"></TD>");
			out.println("</TR>");


			// testcase text input
			out.println("<TR>");
			out.println("<TD>Test Case Name (replace \" \" with \"_\")");
			out.println("<TD><INPUT type=\"text\" name=\"TestCaseName\" onFocus=\"if(this.value=='"
					+ defaultTestCaseName
					+ "')this.value='';\" value=\""
					+ defaultTestCaseName
					+ "\"  size=\"70\" maxlength=\"1000\"></TD>");
			out.println("</TR>");


			// resource file text area
			out.println("<TR>");
			out.println("<TD>Location of resource file(s) (optional)" +
			"<br/>" +
					"[hold down CNTL to select multiple files]" +
					"</td>");
			out.println("<TD><INPUT type=\"file\" name=\"resource_file\" multiple=\"multiple\" accept=\"text/*\" value=\"\" size=\"100\" maxlength=\"200\"></TD>");
			 
			out.println("</TR>");
			
			out.println("</TABLE>");
			out.println("<P><INPUT type=\"submit\" name=\"submit\" value=\"Submit\"></P><HR>");

			// test report file input
			out.println("<TABLE border=\"1\" bgcolor=\"silver\">");
			out.println("<TD>LOCAL Location of output.xml</td>");
			out.println("<TD><INPUT type=\"file\" name=\"output\" accept=\"application/xml\" value=\"\" size=\"100\" maxlength=\"200\"></TD>");
			out.println("</TR>");
			out.println("</TABLE>");
			out.println("<UL>");

			// right justify
			// out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"Do\" checked>Do an action and proceed to Next action</li>");
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
