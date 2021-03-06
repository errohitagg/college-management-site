<%-- 
    Document   : student_attendance
    Created on : Jul 3, 2011, 6:34:27 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="initial.server, java.sql.*, java.util.ArrayList" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%!
        ResultSet get_attendance_details(String roll_no)
	{
            ResultSet result_set = null;
            try
            {
                String query;
                PreparedStatement execute_query;

                query = "select * from Student.Attendance where RollNo=?";
                execute_query = server.server_connection.prepareStatement(query);
                execute_query.setString(1, roll_no);
                result_set = execute_query.executeQuery();
            }
            catch(SQLException e)
            {
            }

            return result_set;
	}

        boolean whether_attendance(String roll_no)
        {
            boolean whether = false;

            try
            {
                String query;
                PreparedStatement execute_query;
                ResultSet result_set;

                query = "select * from Student.Attendance where RollNo=?";
                execute_query = server.server_connection.prepareStatement(query);
                execute_query.setString(1, roll_no);
                result_set = execute_query.executeQuery();

                if(result_set.next())
                    whether = true;
            }
            catch(SQLException e)
            {
            }

            return whether;
        }

        String get_subject(String subject_code)
        {
            String subject = "";

            try
            {
                String query;
                PreparedStatement execute_query;
                ResultSet result_set;

                query = "select * from Administration.Subject where SubjectCode=?";
                execute_query = server.server_connection.prepareStatement(query);
                execute_query.setString(1, subject_code);
                result_set = execute_query.executeQuery();
                result_set.next();
                subject = result_set.getString("SubjectName");
            }
            catch(SQLException e)
            {
            }

            return subject;
        }
%>

<%
    String valid_user = request.getSession().getAttribute("user").toString();
    boolean valid = false;
    if(valid_user.compareTo("Administrator") == 0)
       valid = true;
    else if(valid_user.compareTo("Administration") == 0)
       valid = true;
    else if(valid_user.compareTo("Accounts") == 0)
       valid = true;
    else if(valid_user.compareTo("Exam Cell") == 0)
       valid = true;
    else if(valid_user.compareTo("Library") == 0)
       valid = true;
    else if(valid_user.compareTo("Training and Placement") == 0)
       valid = true;
    else if(valid_user.compareTo("Faculty") == 0)
       valid = true;
    else if(valid_user.compareTo("Student") == 0)
       valid = true;
    if(valid == false)
      response.sendRedirect("/ClgMgtSite/general/login.jsp");

    String roll_no = (String)request.getParameter("roll-no");
    if(roll_no == null || roll_no.compareTo("") == 0)
        response.sendRedirect("all_student.jsp");

    boolean whether = whether_attendance(roll_no);
    if(whether == false)
    {
        request.setAttribute("warning", "No Attendance Details exists for the Student: " + roll_no + " in the database.");
        RequestDispatcher all_student = request.getRequestDispatcher("all_student.jsp");
        all_student.forward(request, response);
    }

    ResultSet attendance_details = get_attendance_details(roll_no);
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>College Management System</title>
        <link rel="stylesheet" type="text/css" href="/ClgMgtSite/resource/general.css" />
        <link rel="stylesheet" type="text/css" href="/ClgMgtSite/resource/display.css" />
    </head>
    <body>
        <div class="page-container">
            <div class="header">
                <%@include file="/WEB-INF/resource/header.jspf" %>
            </div>
            <div>
                <%@include file="/WEB-INF/resource/menubar.jspf" %>
            </div>
            <div class="main">
                <div class="main-navigation">
                    <%@include file="/WEB-INF/resource/navigationbar.jspf" %>
                </div>
                <div class="main-content">
                    <h1 class="pagetitle">Student - <%= roll_no %> (Attendance)</h1>
                    <div class="div-display">
                        <table>
                            <tr>
                                <th width="40%">Roll Number</th>
                                <td width="60%"><%= roll_no %></td>
                            </tr>
                        </table>
                    </div>
                    <div class="div-display">
                        <table width="550px">
                            <tr>
                                <th width="60%">Subject</th>
                                <th width="20%">Attendance</th>
                                <th width="20%">Total Attendance</th>
                            </tr>
                            <%
                                if(attendance_details != null)
                                {
                                    while(attendance_details.next())
                                    {
                             %>
                             <tr>
                                 <td><%= get_subject(attendance_details.getString("SubjectCode")) %></td>
                                 <td><%= attendance_details.getInt("Attendance") %></td>
                                 <td><%= attendance_details.getInt("AttendanceTotal") %></td>
                             </tr>
                             <%
                                    }
                                }
                            %>
                        </table>
                    </div>
                </div>
            </div>
            <div class="footer">
                <%@include file="/WEB-INF/resource/footer.jspf" %>
            </div>
        </div>
    </body>
</html>
