package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.db.Sql;
import com.nokia.dddeyet.gamewarden.db.Form;

/**
 * <code>Servlet</code> for editing the <code>Elephant</code> db, processed by
 * itself.
 * 
 * @author al.majko@nokia.com
 * @version 0.38
 * @since 0.12 07/27/12
 */
public class UseEdit extends HttpServlet {
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
	 * If <code>true</code> send <code>radio button form</code> to user. Default
	 * is <code>false</code>
	 * 
	 */
	private boolean writeusebuttons = false;// sending a response with useful
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
	 * 
	 */
	Form f = new Form();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UseEdit() {
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
		ViewEditInfo info = new ViewEditInfo();
		info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
		info.setUser("gamewarden");
		info.setPassword("password");
		info.setAction(request.getParameter("action"));
		info.setTable(request.getParameter("table"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);

		// 2)
		// do processing here
		// this servlet will take input and report results in same servlet
		// unlike the other servlets: where Begin calls Next, View calls More
		String result = "";

		// A) get list of tables to pick from, if first call to this servlet
		if (info.getAction() == null) {
			result += "<font color='blue'><b>Choose table to edit</b></font><br/>";
			result += f.RetrieveTables(info.getUrl(), info.getUser(),
					info.getPassword(), sql.getListTables());
		}

		// B) get list of columns for blank form plus full data to pick from, if
		// second call to this servlet
		if (info.getAction() != null
				&& info.getAction().equalsIgnoreCase("edit")) {
			// result +="edit<br/>";

			if (info.getTable() != null) {
				result += "<font color='blue'><b>Submit to change database</font></b><br/>";
				/*
				 * result += "<b>Use " + "<a href='" + "View?sql=update " +
				 * info.getTable() + " set colname= where colname=" +
				 * "'>raw form</a> " + "to update data</b><br/>";
				 */
				// result += "table<br/>";
				sql.doSetListBlankDatatoGetColumns(info.getTable());
				// result += sql.getListBlankDatatoGetColumns() + "<br/>";
				result += f.RetrieveColumns(info.getUrl(), info.getUser(),
						info.getPassword(), sql.getListBlankDatatoGetColumns(),
						info.getTable());

				// 3) RetrieveColumns will also display all data

			}
		}

		// C) do insert
		if (info.getAction() != null
				&& info.getAction().equalsIgnoreCase("insert")) {

			// 1) do insert from the "edit" action
			// 1a) success|failure will show
			//
			// build parms - BEGIN
			//
			// Get the values of all request parameters
			Enumeration enumparms = request.getParameterNames();
			String name = "";
			ArrayList<String> namesarl = new ArrayList<String>();
			ArrayList<String> valuesarl = new ArrayList<String>();
			for (; enumparms.hasMoreElements();) {

				// Get the name of the request parameter
				name = (String) enumparms.nextElement();
				// only get those parms that are columnnames
				if (!name.equalsIgnoreCase("action")
						&& !name.equalsIgnoreCase("table")
						&& !name.equalsIgnoreCase("Submit")
						&& !name.equalsIgnoreCase("colname")
						&& !name.equalsIgnoreCase("colvalue")
						&& !name.equalsIgnoreCase("keycol")
						&& !name.equalsIgnoreCase("keyvalue")
						&& !name.contains("_timestamp") && !name.equals("")) {
					namesarl.add(name);
					// out.println(name);

					// Get the value of the request parameter
					valuesarl.add((String) request.getParameter(name));

					// If the request parameter can appear more than once in the
					// query string, get all values
					// String[] values = request.getParameterValues(name);
				}

			}
			//
			// build parms - END

			sql.doSetInsertData(info.getTable(), namesarl, valuesarl);
			// use for inserts AND updates
			result += f.InsertData(info.getUrl(), info.getUser(),
					info.getPassword(), sql.getInsertData());

			// 2) repeat entry line for a new row
			if (info.getTable() != null) {
				result += "<font color='blue'><b>Submit to change database</font></b><br/>";
				/*
				 * result += "<b>Use " + "<a href='" + "View?sql='update " +
				 * info.getTable() + " set colname='' where colname=''" +
				 * "'>raw form</a> " + "to update data</b><br/>";
				 */
				// result += "table<br/>";
				sql.doSetListBlankDatatoGetColumns(info.getTable());
				// result += sql.getListBlankDatatoGetColumns() + "<br/>";
				result += f.RetrieveColumns(info.getUrl(), info.getUser(),
						info.getPassword(), sql.getListBlankDatatoGetColumns(),
						info.getTable());

				// 3) RetrieveColumns will also display all data
			}
		}// end if insert

		// D) do update
		if (info.getAction() != null
				&& info.getAction().equalsIgnoreCase("update")) {

			sql.doSetUpdateData(info.getTable(),
					request.getParameter("colname"),
					request.getParameter("colvalue"),
					request.getParameterValues("keycol"),
					request.getParameterValues("keyvalue"));
			// use for inserts AND updates
			result += f.InsertData(info.getUrl(), info.getUser(),
					info.getPassword(), sql.getUpdateData());

			// 2) repeat entry line for a new row
			if (info.getTable() != null) {
				result += "<font color='blue'><b>Submit to change database</font></b><br/>";
				/*
				 * result += "<b>Use " + "<a href='" + "View?sql='update " +
				 * info.getTable() + " set colname='' where colname=''" +
				 * "'>raw form</a> " + "to update data</b><br/>";
				 */
				// result += "table<br/>";
				sql.doSetListBlankDatatoGetColumns(info.getTable());
				// result += sql.getListBlankDatatoGetColumns() + "<br/>";
				result += f.RetrieveColumns(info.getUrl(), info.getUser(),
						info.getPassword(), sql.getListBlankDatatoGetColumns(),
						info.getTable());

				// 3) RetrieveColumns will also display all data
			}
		}

		// E) do delete
		if (info.getAction() != null
				&& info.getAction().equalsIgnoreCase("delete")) {

			sql.doSetDeleteData(info.getTable()/* , namesarl, valuesarl */);
			result += f.DeleteData(info.getUrl(), info.getUser(),
					info.getPassword(), sql.getDeleteData());

			// 2) repeat entry line for a new row
			if (info.getTable() != null) {
				result += "<font color='blue'><b>Submit to change database</font></b><br/>";
				/*
				 * result += "<b>Use " + "<a href='" + "View?sql='update " +
				 * info.getTable() + " set colname='' where colname=''" +
				 * "'>raw form</a> " + "to update data</b><br/>";
				 */
				// result += "table<br/>";
				sql.doSetListBlankDatatoGetColumns(info.getTable());
				// result += sql.getListBlankDatatoGetColumns() + "<br/>";
				result += f.RetrieveColumns(info.getUrl(), info.getUser(),
						info.getPassword(), sql.getListBlankDatatoGetColumns(),
						info.getTable());

				// 3) RetrieveColumns will also display all data
			}
		}

		// 3)
		// finally respond with page here,with and bean info attached if desired
		// (getter mthods on the bean)
		WritePage(request, response/*
									 * ,userinfo.getUsername(),userinfo.getPassword
									 * ()
									 */, result);

	}

	/*
	 * @deprecated
	 */
	public String doEdit() {
		String result = "";

		return result;
	}

	/*
	 * @deprecated
	 */
	public String doInsert() {
		String result = "";

		return result;

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

			// FormBegin
			out.println(tags.getFormBegin());// from '<form>' up thru
			// 'action=\"'

			out.println("UseEdit\">");

			out.println(result);

			/*
			 * out .println(
			 * "<li><INPUT type=\"radio\" name=\"action\" value=\"none\" checked>None</li>"
			 * ); out .println(
			 * "<li><INPUT type=\"radio\" name=\"action\" value=\"edit\">Edit Data</li>"
			 * );
			 */

			// FormEnd
			out.println(tags.getFormEnd());// from 'submit' button down thru

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

			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListTables() + "\" checked>List Tables</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListColumns() + "\">List Columns</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListKeysIndices()
					+ "\">List Keys and Indices</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListEnvironments() + "\">List Environments</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListDatabases() + "\">List Databases</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListTests() + "\">List Tests</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListTestsStatus()
					+ "\">List Status by Test Case</li>");
			out.println("<li><INPUT type=\"radio\" name=\"sql\" value=\""
					+ sql.getListTestRunsStatus()
					+ "\">List Status by Test Run</li>");
			out.println("</UL>");
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
