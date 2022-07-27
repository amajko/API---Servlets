package com.nokia.dddeyet.gamewarden.servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.beans.ViewEditInfo;
import com.nokia.dddeyet.gamewarden.db.Connect;
import com.nokia.dddeyet.gamewarden.html.Tags;

/**
 * Servlet implementation class BundleLink
 */
@WebServlet("/BundleLink")
public class BundleLink extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Tags tags = new Tags();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BundleLink() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/html");
		out.println(tags.getHeader());
		ViewEditInfo info = new ViewEditInfo();
		info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
		info.setUser("gamewarden");
		info.setPassword("password");
		info.setAction(request.getParameter("action"));
		info.setTable(request.getParameter("table"));
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);
		Connect c = new Connect();
		ResultSet rs1=null;
		rs1 = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),
				"select bundle_id from gw_bundles order by bundle_id desc LIMIT 1");
		try{
			
			while(rs1.next()) {
				
				out.println("<a href='http://dchiecapp06.hq.navteq.com:8080/gamewardenServlets-0.3/ViewMetrics?opt=selbun&bundle="+rs1.getString(1)+"'>"+rs1.getString(1)+"</a>");
			}
		}
		catch (SQLException e){
			//Catch exception
			out.println(e.getMessage());
		}
		out.println(tags.getFooter());
		
	}

}
