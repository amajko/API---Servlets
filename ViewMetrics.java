package com.nokia.dddeyet.gamewarden.servlets;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nokia.dddeyet.gamewarden.db.Connect;
import com.nokia.dddeyet.gamewarden.html.Tags;
import com.nokia.dddeyet.gamewarden.beans.ViewEditInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Servlet implementation class ViewMetrics
 */

public class ViewMetrics extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Tags tags = new Tags();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewMetrics() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// set content type
		String option= request.getParameter("opt");
		String bundle= request.getParameter("bundle");
		String bundleid= request.getParameter("bundleid");
		String bundledesc= request.getParameter("bundledesc");
		
		ServletOutputStream out = resp.getOutputStream();
		
				resp.setContentType("text/html");
				out.print("<html><head><style>table{ border-collapse:collapse; } table, td, th { border:1px solid gray; } iframe { overflow-x: scroll; overflow-y: scroll; } </style> </head> <body bgcolor='#660000'><center>");
				//out.print("bundle is:"+bundle+".");
				//out.print("bundleid is:"+bundleid+".");
				//out.print("bundledesc is:"+bundledesc+".");
				if(option==null || option=="" || option=="null")
					out.println("Please select any option. <a href='javascript:history.go(-1);'>Click here</a> to go back");
				else if(bundle.equalsIgnoreCase("new") && bundleid.equalsIgnoreCase(""))
					out.println("Bundle id should not be a blank. <a href='javascript:history.go(-1);'>Click here</a> to go back");
				else{
					ResultSet rs=null;
					
					// link res output stream to ServletOutputStream
					
					ViewEditInfo info = new ViewEditInfo();
					info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
					info.setUser("gamewarden");
					info.setPassword("password");
					info.setAction(request.getParameter("action"));
					info.setTable(request.getParameter("table"));
					HttpSession session = request.getSession(true);
					session.setAttribute("info", info);
					Connect c = new Connect();
					if(option.equalsIgnoreCase("selbun")){
					rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),
							"select b.artifact,b.artifact_version,b.dependencies,bl from (select artifact,max(build_level) bl from gw_builds where substr(bundle_id, 7, length(bundle_id)) <= substr('"+bundle+"', 7, length('"+bundle+"')) and bundle_id!='BackUp' group by artifact) a, gw_builds b where a.bl=b.build_level  order by b.artifact");
					//temp = rs;
					out.println("<table>");
					out.println("<thead><th  bgcolor='#FFCF79'>"+bundle+"</th><th  bgcolor='#FFCF79'>Build Number</th>");
					try{
						rs.last();
						  int numberOfRows = rs.getRow();
						  rs.beforeFirst();
						  
						  ArrayList<String> dataresult = new ArrayList<String>();
						  
						  //print components horizontally
						  while(rs.next()){
							  
							  dataresult.add(rs.getString(1));
							  out.print("<th  bgcolor='#FFCF79'>"+rs.getString(1)+"</th>");
						  }
						  out.print("</thead>");
						  //print versions horizontally
						  rs.beforeFirst();
						  out.println("<thead><th  bgcolor='#FFCF79'>Components</th><th  bgcolor='#FFCF79'>Versions</th>");
						  
						  while(rs.next()){
							  
							  dataresult.add(rs.getString(1));
							  out.print("<th  bgcolor='#FFCF79'>"+rs.getString(2)+"</th>");
						  }
						  out.print("</thead>");
						  rs.beforeFirst();
						  //print components, versions and dependencies 
						while(rs.next()) {
							String a_starting="";
							String a_ending="";
							if(rs.getString(4).length()==6){
								a_starting="<a href='http://anthill:81/tasks/project/BuildLifeTasks/viewBuildLife?buildLifeId="+rs.getString(4)+"'>";
								a_ending="</a>";
							}
							out.println("<tr>");
							out.println("<td style='width:260px;' bgcolor='#FFCF79'>"+rs.getString(1)+"</td><td bgcolor='#FFCF79'>"+a_starting+rs.getString(2)+a_ending+"</td>");
							//loop for dependencies
							if (rs.getString(3)!=null ) {
								    // rename the braces codes to ( and )
								String depend = rs.getString(3).replace("gw_open_brace", "(");
								depend = depend.replace("gw_close_brace", ")");
								
								//Split it.
								String[] depend_array = depend.split(",");
								
								for(int j=0;j<numberOfRows;j++){
									
									if(Arrays.asList(depend_array).indexOf(dataresult.get(j))<0){
										out.print("<td style='background-color:#E5E4D7'></td>");
									}
									else
										out.print("<td bgcolor='#92CD00' style='text-align:center;'><font color='white' size=2>X</font></td>");
								}
							} else {
								for(int j=0;j<numberOfRows;j++){
									out.print("<td style='background-color:#E5E4D7'></td>");
								}
							}
							out.println("</tr>");
						}
					}
					
					catch (SQLException e){
						//Catch exception
						out.println(e.getMessage());
					}
					out.println("</table>");
				}
					else if(option.equalsIgnoreCase("new")){
						if(bundleid.length()<=10){
							String sql = "insert into gw_bundles(bundle_id,bundle_name) values('"+bundleid+"','"+bundledesc+"');";
							rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),sql);						
							out.print("Bundle information is updated successfully");
						}
						else
							out.print("Bundle id can be 10 characters only. <a href='javascript:history.go(-1);'>Click here</a> to go back");
						
					}
					else if(option.equalsIgnoreCase("addc")){
						String mp= request.getParameter("mp");
						String cn= request.getParameter("cn");
						String url= request.getParameter("url");
						String bl= request.getParameter("bl");
						String bid= request.getParameter("bid");
						String dep= request.getParameter("dependencieslist");
						String artifact= request.getParameter("artifact");
						String artifactver= request.getParameter("artifactver");
						String sql= "";
						if(!mp.equalsIgnoreCase("")){
							sql = "update gw_home_bases set build_level='"+bl+"',bundle_id='"+bid+"' where mount_point='"+mp+"' and code_name='"+cn+"';";
							rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),sql);	
						sql = "insert into gw_home_bases(mount_point,code_name,url,build_level,bundle_id) values('"+mp+"','"+cn+"','"+url+"','"+bl+"','"+bid+"');";
						rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),sql);
						}
						sql = "update gw_builds set artifact='"+artifact+"',artifact_version='"+artifactver+"',bundle_id='"+bid+"',dependencies='"+dep+"' where build_level='"+bl+"';";
						rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),sql);
						sql = "insert into gw_builds(build_level,artifact,artifact_version,bundle_id,dependencies) values('"+bl+"','"+artifact+"','"+artifactver+"','"+bid+"','"+dep+"');";
						rs = c.Execute(info.getUrl(),info.getUser(), info.getPassword(),sql);
						out.print("Component information is updated successfully");
					}
					out.print(tags.getFooter());
				}
		}

	}
