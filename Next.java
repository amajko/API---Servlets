package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.db.Connect;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.utilities.TokenString;
import com.nokia.dddeyet.gamewarden.actions.*;


/**
 * <code>Servlet</code> that processes
 * {@link com.nokia.dddeyet.gamewarden.servlets.Begin Begin}.
 * 
 * @author al.majko@nokia.com
 * @version 0.27
 * @since 0.1 06/20/12
 */
public class Next extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * If <code>true</code> send <code>form</code> to user Default is
	 * <code>false</code>
	 */
	private boolean writeform = false;// sending a response requiring more form
										// inputs?
	/**
	 * If <code>true</code> send result of <code>form</code> entry to user.
	 * Default is <code>true</code>
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
	TokenString ts = new TokenString();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Next() {
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
		NextInfo info = new NextInfo();
		info.setJob(request.getParameter("job"));// which script
		info.setAction(request.getParameter("action"));// 'Do' or 'View'
		String bundle = request.getParameter("bundle");
		String dependencies = request.getParameter("dependencieslist");
		dependencies = dependencies.replace("(", "gw_open_brace");
		dependencies = dependencies.replace(")", "gw_close_brace");
		dependencies = dependencies.replace(" ", "%20");
		info.setParameters(request.getParameter("parameters")+" "+bundle+" "+"\\\""+dependencies+"\\\"");// parms to give
																// script;
																// if from
																// t-shirt gui
																// call,
																// 'COMPONENT=xxxxxx[build
																// life #],'
																// comma
																// separated for
																// mult
																// componenets
		info.setLocation(request.getParameter("location"));// location of
															// script: hard
															// coded from
															// t-shirt gui call
		// ...for t-shirt gui driven calls:
		// directory (standard or new )
		// codename (anteater, cobra, mongoose)
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		String result = "";

		// check for t-shirt gui driven or manual driven thru plain text form
		if (info.getJob().equals("...see parameters...")) {// handling the JOB
															// parm

			String codename = request.getParameter("codename");
			String directory = request.getParameter("directory");

			// parsing the PARAMETERS parm
			// Building Content Pipeline=123456, EarthCore=654321,
			ArrayList<String> compsandblsarl = ts.Token(info.getParameters(),
					",");
			ArrayList<String> compsarl = new ArrayList<String>();
			ArrayList<String> blsarl = new ArrayList<String>();
			// compsandblsarl.remove(compsandblsarl.size()-1);//remove the empty
			// string after the final comma
			for (int i = 0; i < compsandblsarl.size(); i++) {
				ArrayList<String> holderarl = ts.Token(compsandblsarl.get(i),
						"=");
				compsarl.add(holderarl.get(0));
				blsarl.add(holderarl.get(1));
			}

			// deploy one after another
			result += "Parsing parameters: " + codename + " " + directory + " "
					+ info.getParameters() + "<br>";
			result += "compsandblsarl.size=" + compsandblsarl.size()
					+ "; compsarl.size=" + compsarl.size() + "; blsarl.size="
					+ blsarl.size() + "<br>";

			String scripttorun = "";
			// by lokkiing up script_calsign from component_description
			Connect c = new Connect();
			String qry = c.RetrieveWOHtmlCSV(
					"jdbc:postgresql://dchiecapp06:5432/elephant", "View",
					"gamewarden", "password", "select " + "description,"
							+ "final_script_callsign " + "from gw_acronyms "
							+ "where " + "description is not null "
							+ "and final_script_callsign is not null");
			ArrayList<String> qryarl = ts.Token(qry, "\r");
			for (int i = 0; i < compsarl.size(); i++) {

				result += "*****Starting Component " + compsarl.get(i)
						+ ", Build " + blsarl.get(i) + "*****<br>";

				Job job = new Job();

				// -----WHAT SUPER_SCRIPT?
				// if case codename..."super_a_" or "super_extc_"
				// if case component compare to gw_acronyms.description,
				// then find script compare to gw_superscripts.artifact joined
				// from gw.acronyms
				// then append to "super_x_"
				if (!codename.equals("cobra")) {
					result += " =====environment not accepting deployments yet=====<br>";
					break;
				} else {
					scripttorun = "super_extc_";

				}
				// add more details to script name
				for (int j = 0; j < qryarl.size(); j++) {
					String full = qryarl.get(j);
					int comma = full.indexOf(",");
					String component = compsarl.get(i);
					String compare = full.substring(0, comma);
					String toadd = full.substring(comma + 1);
					// result += full + "ending at " + comma + "<br>";
					/*
					 * result += component + "@" + j + "=" + compare + "?" +
					 * " so add " + toadd + "<br>";
					 */
					if (component.trim().equalsIgnoreCase(compare.trim())) {
						scripttorun += toadd.trim();
						result += "...MATCHED...<br>";
					}
				}

				// -----WHAT MOUNT_POINT?
				// if case directory =='new'..."add a parm(in the 3rd parm of
				// belo
				// method) and add hndling of 2nd parm to super script..
				if (directory.equals("new")) {
					result += " =====new directory creation not available yet=====<br>";
					continue;
				}

				// -----WHAT BUILD?
				// trap for 'enter' and elinate both array items (in compsarl
				// and bldarl)
				if (blsarl.get(i).equals("enter")) {
					result += " =====must enter valid build level=====<br>";
					continue;
				}

				result += job.RunAny(scripttorun, "Do", blsarl.get(i),
						info.getLocation());

				result += "*****Ending Component " + compsarl.get(i)
						+ ", Build " + blsarl.get(i) + "*****<br>";
			}

		} else {
			Job job = new Job();
			result = job.RunAny(info.getJob(), info.getAction(),
					info.getParameters(), info.getLocation());
		}
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
		out.println(tags.getHeader().replace("<center>", ""));
		// out.println(tags.getEmailForm());
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
		out.println(tags.getFooter().replace("</center>", ""));

		out.close(); // clean up

	}

}
