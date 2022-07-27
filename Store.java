package com.nokia.dddeyet.gamewarden.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.nokia.dddeyet.gamewarden.beans.*;
import com.nokia.dddeyet.gamewarden.html.*;
import com.nokia.dddeyet.gamewarden.utilities.Dater;
import com.nokia.dddeyet.gamewarden.utilities.FileIO;
import com.nokia.dddeyet.gamewarden.validations.Validate;
import com.nokia.dddeyet.gamewarden.actions.*;

/**
 * <code>Servlet</code> that processes {@link com.nokia.dddeyet.gamewarden.servlets.Upload Upload}.
 * <br/> and is called by any client using {@link com.nokia.dddeyet.listener.client.ReportListener ReportListener}
 * 
 * @author al.majko@nokia.com
 * @author michal.rys@nokia.com
 * @author Srujana.Bobba@navteq.com
 * @version 0.52 0108
 * @since 0.13 07/29/12
 */
public class Store extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String runId = "";
	private Boolean for_log_result= false;

	/**
	 * If <code>true</code> send <code>form</code> to user
	 * Default is <code>false</code>
	 */
	private boolean writeform = false;// sending a response requiring more form
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
	 * @see HttpServlet#HttpServlet()
	 */
	public Store() {
		super();
	}

	/**
	 * Calls doPost
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      
	 *      Calls {@link #doPost(HttpServletRequest, HttpServletResponse) doPost}
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.runId = request.getParameter("runid");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.runId = request.getParameter("runid");
		String from ;
		from = request.getParameter("from");
		ViewEditInfo info = new ViewEditInfo();
		info.setUrl("jdbc:postgresql://dchiecapp06:5432/elephant");
		info.setUser("gamewarden");
		info.setPassword("password");
		HttpSession session = request.getSession(true);
		session.setAttribute("info", info);


		// local vars for form processing
		String result = "";
		FileItem item = null;
		TestInfo testInfo = new TestInfo();
		String fileContents = null;
		FileData fd = new FileData();
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

		// Parse the request
		List items = null;
		try {
			items = uploadHandler.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Iterator itr = items.iterator();

		
		
		/* Handle form fields */
		while (itr.hasNext())
		{
			
			item = (FileItem) itr.next();
			
			// OUTPUT.XML: extract and store the file
			if (item.getFieldName().equals("output") && item.isFormField() == false)
			{
				fileContents = extractFileContents(item);
				Validate v = new Validate();
				boolean chk = v.Check("filelength", 5000000, fileContents.length());
				if(!chk){
					fileContents = "Output too large to process";
				}
				if(fileContents.isEmpty()){					
					continue;
				}
				else
				{
					Dater d = new Dater();
					String newdatestamp = d.getDate("MMddHHmmssSSS");					
					result += fd.Save(fileContents, info.getUrl(), info.getUser(), info.getPassword(),this.runId,"output",newdatestamp);
					
					if(from!=null){
						for_log_result = true;
						result = newdatestamp;
					}
					// fire test
					Job job = new Job();
					job.RunAny("process_robot_test_results", "Do","", "/earthcore06/DDDEYET/Test_Utilities/scripts/");	//run processing script immediately
					testInfo.processingOutputXml = true;
				}
			}
			else if (item.getFieldName().equals("log") && item.isFormField() == false)
			{
				fileContents = extractFileContents(item);
				Validate v = new Validate();
				boolean chk = v.Check("filelength", 5000000, fileContents.length());
				if(!chk){
					fileContents = "Log file too large to process";
				}
				if(fileContents.isEmpty()){					
					continue;
				}
				else
				{
					result += fd.Save(fileContents, info.getUrl(), info.getUser(), info.getPassword(),this.runId,"log","");					
					testInfo.processingOutputXml = true;
				}
			}

			// SUITE FILE
			else if (item.getFieldName().equals("suite_file") && item.isFormField() == false)
			{
				testInfo.suiteFileContents = this.extractFileContents(item);
				testInfo.suiteFileName = item.getName();
			}

			// RESOURCE FILE
			else if (item.getFieldName().equals("resource_file") && item.isFormField() == false)
			{
				testInfo.resources.add( new ResourceFile( item.getName(), this.extractFileContents(item) ) );
			}
			
			// TESTCASE NAME
			else if(item.getFieldName().equals("TestCaseName"))
				testInfo.TestCaseName = item.getString();

			
		}


		
		// Save resource/suite files to host running robot
		result = fireRobotTestCase(testInfo) + result;

		
		
		
		// 3)
		// finally respond with page here,with and bean info attached if desired
		// (getter mthods on the bean)
		WritePage(request, response, result);
	}
	
	
	
	
	

	/**
	 * 
	 * @param testInfo
	 * @return
	 */
	private String fireRobotTestCase(TestInfo testInfo)
	{
		
		String retmsg = "";
		final String baseDir = "/earthcore06/DDDEYET/Test_Utilities/scripts/robots";
		
		if(( testInfo.suiteFileContents == null ||testInfo.suiteFileContents.isEmpty()) && testInfo.processingOutputXml==true)
			return "";
		if (testInfo.suiteFileContents.isEmpty() )
			return "Error: No suite file found. Please upload a suite file\n\n";
		if (testInfo.TestCaseName == null || testInfo.TestCaseName.equals("") || testInfo.TestCaseName.equals(Upload.defaultTestCaseName))
			return "Error: Missing parameter. Test suite file failed to upload. Please specify test case name to be run." + "\n\n";

		
		//create time-stamped directory
		Dater d = new Dater();
		String datestamp = d.getDate("MMddHHmmssSSS");
		String fullDirName = baseDir+"/"+datestamp;
		String suitename = new File(testInfo.suiteFileName).getName();
		suitename = suitename.substring(0, suitename.lastIndexOf('.'));
		
		
		retmsg += FileIO.Mkdir(fullDirName);

		
		//Save suite file
		FileIO.Write(fullDirName + "/" + testInfo.suiteFileName, testInfo.suiteFileContents);

		
		// Resource file is optional. If it doesn't exist, do nothing, report nothing.
		if(testInfo.resources.isEmpty() == false)
		{
			//save resource file
			for(ResourceFile resource : testInfo.resources)
				FileIO.Write(fullDirName + "/" + resource.resourceFileName, resource.resourceFileContents);	
		}
		
		

		// fire test
		Job job = new Job();
		retmsg += job.RunAny("start_robot_tests_upload", "Do", suitename + " " + "\""+testInfo.TestCaseName +"\"" + " " + datestamp, "/earthcore06/DDDEYET/Test_Utilities/scripts/");
		
		return retmsg;
	}

	
//parameters for upload robot shell script: "non-specific " + testInfo.TestCaseName +  " " + "non-specific"	
	
	
	
	/**
	 * 
	 * @param item
	 * @return
	 * @throws IOException
	 * 
	 *             Helper function to extract the contents of a file from a
	 *             FileItem object in the form of a String
	 */
	private String extractFileContents(FileItem item) throws IOException {
		long flen = item.getSize();
		byte b[] = new byte[(int) flen];
		InputStream is = item.getInputStream();
		is.read(b);
		return new String(b);
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
		if(!for_log_result)
		out.println(tags.getHeader());
		// out.println(tags.getGuidedHeader());
		// out.println(tags.getUnGuidedHeader());

		// 1a1)post referring url for info purposes
		// out.println(req.getRequestURL() + req.getQueryString().toString() +
		// "<br>");

		// **********sending form?*********
		if (writeform) {
			out.println(result);
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

			out.print(result);
			/*out.println("{"+
   "\"name\":\"picture1.jpg\","+
   "\"size\":902604,"+
   "\"url\":\"\\/\\/example.org\\/files\\/picture1.jpg\","+
   "\"thumbnail_url\":\"\\/\\/example.org\\/thumbnails\\/picture1.jpg\","+
   "\"delete_url\":\"\\/\\/example.org\\/upload-handler?file=picture1.jpg\","+
   "\"delete_type\":\"DELETE\""+
  "},"+
  "{"+
  "\"name\":\"picture2.jpg\","+
  "\"size\":841946,"+
  "\"url\":\"\\/\\/example.org\\/files\\/picture2.jpg\","+
  "\"thumbnail_url\":\"\\/\\/example.org\\/thumbnails\\/picture2.jpg\","+
  "\"delete_url\":\"\\/\\/example.org\\/upload-handler?file=picture2.jpg\","+
  "\"delete_type\":\"DELETE\""+
  "}"+
"]");*/

		}

		// 3)
		// Footers
		// out.println(tags.getGuidedFooter());
		// out.println(tags.getUnGuidedFooter());
		if(!for_log_result)
		out.println(tags.getFooter());

		out.close(); // clean up

	}

	
	
	
	
	
	/**
	 * @author mrys@nokia.com
	 * TestInfo: Data structure to hold data from Upload form
	 */
	private class TestInfo
	{
		public String TestCaseName;
		public String suiteFileContents;
		public String suiteFileName;
		public ArrayList<ResourceFile> resources;
		public boolean processingOutputXml;

		public TestInfo()
		{
			this.TestCaseName = null;
			this.suiteFileContents = null;
			this.resources = new ArrayList<ResourceFile>();
			this.suiteFileName = null;
			this.processingOutputXml = false;
		}

	}
	/**
	 * 
	 * @author mrys@nokia.com
	 *
	 */
	private class ResourceFile
	{
		public String resourceFileName;
		public String resourceFileContents;
		
		public ResourceFile(String name, String contents)
		{
			this.resourceFileName = new String(name);
			this.resourceFileContents = new String(contents);
		}
		
	}

	
	
	
}
