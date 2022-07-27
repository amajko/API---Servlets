package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.db.Connect;
import com.nokia.dddeyet.gamewarden.html.Tags;
import com.nokia.dddeyet.gamewarden.beans.ViewEditInfo;

/**
 * Servlet implementation class SelectBundle
 */
@WebServlet("/SelectBundle")
public class SelectBundle extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	Tags tags = new Tags();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SelectBundle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("text/html");
		out.print("<html>");
		out.print("<head><script src='js/jquery-1.9.0.min.js' type='text/javascript'></script>" +
				"<script src='js/bundle_functions.js' type='text/javascript'></script>" +
				"</head>");
		out.print("<body bgcolor='#660000' text=\"ffffff\"><center>");
		ResultSet rs=null;
		ResultSet rs1=null;
		
		ViewEditInfo info = new ViewEditInfo();
		info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
		info.setUser("gamewarden");
		info.setPassword("password");
		info.setAction(request.getParameter("action"));
		info.setTable(request.getParameter("table"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);
		Connect c = new Connect();
		rs1 = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),
				"select distinct bundle_id from gw_bundles order by bundle_id");
		out.println(tags.getFormBegin());// from '<form>' up thru
		// 'action=\"'

		out.println("ViewMetrics\" onsubmit=\"return checkselb()\" >");
		out.println("<table border=0>");
		out.println("<thead><th>Select an option</th></thead>");
		out.println("<tr><td><select id=opt name=opt onchange='checkoption(this.value);'><option value='' selected=selected>Select Option</option><option value='selbun'>Select bundle for Matrix</option><option value='new'>Create New Bundle</option><option value='addc'>Add Components to Bundle</option></select>");
		out.println("</td></tr></table>");
		out.println("<div id='bundlediv' style='display:none;'><br/><select id=bundle name=bundle onchange='checknew(this.value);'>");
		try{
			
			while(rs1.next()) {
				
				out.println("<option value='"+rs1.getString(1)+"'>"+rs1.getString(1)+"</option>");
			}
		}
		catch (SQLException e){
			//Catch exception
			out.println(e.getMessage());
		}
		out.println("</select></div>");

		out.println("<div id='newdiv' style='display:none;'><br/><table border=0><tr><td>Create new bundle:</td><td><input type=text name=bundleid /></td></tr>" +
				"<tr><td>Bundle Description:</td><td><input type=text name=bundledesc /></td></tr></table> </div>");
		

		out.println("<div id='asscdiv' style='display:none;'><br/>Build ID: <input type=text name=buildid /><br/>" +
				"Bundle id:");
		out.println("<select id=addcbid name=addcbid >");
		try{
			rs1.beforeFirst();
			
			while(rs1.next()) {
				
				out.println("<option value='"+rs1.getString(1)+"'>"+rs1.getString(1)+"</option>");
			}
		}
		catch (SQLException e){
			//Catch exception
			out.println(e.getMessage());
		}
		out.println("</select>");				
		out.println("</div>");
		out.println("<div id='addcdiv' style='display:none;'><br/>To insert a new record, give build number,mount point, code name and all other necessary values. <br/> To update, leave mount point empty. <br/><br/><table border=0><tr><td>Build Level:</td><td> <input type=text name=bl id=\"bl\" /></td></tr>" +
				"<tr><td>Mount Point:</td><td><input type=text name=mp id=\"mp\" /></td></tr><tr><td>Code Name:</td><td>");
				out.print("<select name=\"cn\" id=\"cn\" ><option value=\"anteater\">Anteater</option><option value=\"cobra\">Cobra</option></select></td></tr><tr><td>URL:</td><td><input type=text name=url /></td></tr>");
		//Get the dependencies list 
		rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),
				"select distinct artifact from gw_acronyms where artifact is NOT NULL and artifact!='' order by artifact");
		out.println("<TR>");
		out.println("<TD>Artifact</td><TD><select name=\"artifact\" id=\"artifact\" >");
		try{
			
			while(rs.next()) {
				
				out.println("<option value=\""+rs.getString(1)+"\" >"+rs.getString(1)+"</option>");
			}
		}
		catch (SQLException e){
			//Catch exception
			out.println(e.getMessage());
		}
		out.println("</select></TD>");
		out.println("</TR>");
				out.println("<tr><td>Artifact Version:</td><td><input type=text name=artifactver id=\"artifactver\" /></td></tr><tr><td>Bundle ID:</td><td> " );
		out.println("<select  id=\"bid\" name=bid >");
		try{
			rs1.beforeFirst();
			
			while(rs1.next()) {
				
				out.println("<option value='"+rs1.getString(1)+"'>"+rs1.getString(1)+"</option>");
			}
		}
		catch (SQLException e){
			//Catch exception
			out.println(e.getMessage());
		}
		out.println("</select></td></tr>");	
				
					
				//Get the dependencies list 
				
				out.println("<TR>");
				out.println("<TD>Dependencies</td><TD><select multiple=\"multiple\" size=\"4\" name=\"dependencies\" id=\"dependencies\" onchange=\"getdepend();\" >");
				try{
					rs.beforeFirst();
					while(rs.next()) {
						
						out.println("<option value=\""+rs.getString(1)+"\" >"+rs.getString(1)+"</option>");
					}
				}
				catch (SQLException e){
					//Catch exception
					out.println(e.getMessage());
				}
				out.println("</select></TD>");
				out.println("</TR>");
				out.println("</TABLE><input type=hidden name=dependencieslist id=dependencieslist value=\"\" /> </div>");
		//Ask to create new
		out.println(tags.getFormEnd());
		out.print(tags.getFooter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
