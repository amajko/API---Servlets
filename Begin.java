package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.db.Connect;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.actions.Command;

/**
 * <code>Servlet</code> for providing a form run a <code>shell script</code>, 
 * processed by {@link com.nokia.dddeyet.gamewarden.servlets.Next Next}
* 
 * @author al.majko@nokia.com, srujana.bobba@navteq.com
 * @version 0.53
 * @since 0.1 06/20/12
 */
public class Begin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * If <code>true</code> send <code>form</code> to user
	 * Default is <code>true</code>
	 */
	private boolean writeform = true;// sending a response requiring more form
										// inputs?
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user
	 * Default is <code>false</code>
	 * 
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
	public Begin() {
		super();
		// TODO Auto-generated constructor stub

	}

	/**
	 * Calls doPost
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
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
		//out.println(tags.getHeader().replace("<center>", ""));
		out.print("<html>");
		out.print("<head><script src='js/jquery-1.9.0.min.js' type='text/javascript'></script>" +
				"<script src='js/bundle_functions.js' type='text/javascript'></script>" +
				"</head>");
		out.print("<body bgcolor=\"ffffff\" text=\"000000\">");
		//out.println(tags.getGuidedHeader());
		//out.println(tags.getUnGuidedHeader());
		
		//1a1)post referring url for info purposes
		//out.println(req.getRequestURL() + req.getQueryString().toString() + "<br>");

		// **********sending form?*********
		if (writeform) {
			// 1a)
			// List Choices for textfield input
			//Choices choices = new Choices();
			//out.println(choices.getJobs());
			Command c = new Command();
			ResultSet rs=null;
			
			ViewEditInfo info = new ViewEditInfo();
			info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
			info.setUser("gamewarden");
			info.setPassword("password");
			
			HttpSession session = req.getSession(true);
			session.setAttribute("info", info);
			Connect co = new Connect();
			rs = co.Execute(info.getUrl(),info.getUser(), info.getPassword(),
					"select distinct bundle_id from gw_bundles  order by bundle_id");
			/*out.println("<pre>");
			out.println(c.LS("/earthcore06/DDDEYET/Test_Utilities/scripts/"));
			out.println("</pre>");*/
			out.println("<pre>");
			out.println(c.LS("/earthcore06/DDDEYET/Test_Utilities/scripts/"));
			out.println(c.LS("/earthcore06/DDDEYET/Test_Utilities/scripts/installation/"));
			//out.println("</pre>");
			//out.println("<pre>");
			out.println(c.LS("/earthcore06/DDDEYET/Test_Utilities/scripts/super_wrappers/"));
			out.println("</pre>");

			// 2a)
			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
												// 'action=\"'

			out.println("Next\" onsubmit=\"return checkbundle()\" >");
			out.println("<TABLE border=\"1\" bgcolor=\"silver\">");
			out.println("<TR>");
			out.println("<TD>Input");
			out.println("<TD><INPUT type=\"text\" name=\"job\" value=\"super_extc_\" size=\"70\" maxlength=\"40\"></TD>" +
					"<td>.sh<br/>"// +
					//"<font size=\"-1\">" +
					//"<font color=\"red\">" +
					//"scripts will overwrite any installed mount_points</font>"/* +
					//"<br/><b>To be more careful use the individual scripts CALLED by the Action_... scripts:</b><br/>" +
					//"a)get_artifacts...<br/>" +
					//"b)mount_...<br/>" +
					//"c)install_... or populate_...</td>"*/ +
					//"</font>"
					);
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Parameters</td>");
			out.println("<TD><INPUT type=\"text\" name=\"parameters\" onFocus=\"if(this.value=='{6 digit BL}')this.value='';\" value=\"{6 digit BL}\" size=\"70\" maxlength=\"1000\"></TD><td>Use space separated values. (leave blank to get parameters)</td>");
			out.println("</TR>");
			out.println("<TR>");
			out.println("<TD>Location</td>");
			out.println("<TD><INPUT type=\"text\" name=\"location\" value=\"/earthcore06/DDDEYET/Test_Utilities/scripts/super_wrappers/\" size=\"70\" maxlength=\"100\"></TD><td>(complete correctly)</td>");
			out.println("</TR>");
			out.println("<tr><td>Bundle Id</td><td><select id=\"bundle\" name=\"bundle\"><option value='' selected=selected>Select Bundle</option>");
			
			try{
				
				while(rs.next()) {
					
					out.println("<option value='"+rs.getString(1)+"'>"+rs.getString(1)+"</option>");
				}
			}
			catch (SQLException e){
				//Catch exception
				out.println(e.getMessage());
			}
			out.println("</select></td><td>To create new bundle <a href='SelectBundle'>Click Here</a></td></tr>");
			//Get the dependencies list 
			rs = co.Execute(info.getUrl(),info.getUser(), info.getPassword(),
					"select distinct artifact from gw_acronyms where artifact is NOT NULL and artifact!='' order by artifact");
			out.println("<TR>");
			out.println("<TD>Dependencies</td><TD><select multiple=\"multiple\" size=\"4\" name=\"dependencies\" id=\"dependencies\" onchange=\"getdepend();\" >");
			try{
				
				while(rs.next()) {
					
					out.println("<option value=\""+rs.getString(1)+"\" >"+rs.getString(1)+"</option>");
				}
			}
			catch (SQLException e){
				//Catch exception
				out.println(e.getMessage());
			}
			out.println("</select></TD><td>Click and Hold Ctrl to select multiple values</td>");
			out.println("</TR>");
			out.println("</TABLE><input type=hidden name=dependencieslist id=dependencieslist value=\"\" />");
			out.println("<UL>");
			// right justify
			out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"Do\" checked>Do an action and proceed to Next action</li>");
			//out.println("<li><INPUT type=\"radio\" name=\"action\" value=\"View\">View (or update) data and proceed to More data</li>");
			out.println("</UL>");

			// 2b)
			// FormEnd
			out.println("<P><INPUT type=\"submit\" name=\"submit\" value=\"Submit\" ></P></form>");// from 'submit' button down thru
											// '</FORM>'
		}

		// **********sending results?*********
		if (writeresult) {

		}

		// 3)
		// Footers
		//out.println(tags.getGuidedFooter());
		//out.println(tags.getUnGuidedFooter());
		out.println(tags.getFooter().replace("</center>", ""));

		out.close(); // clean up

	}

}
