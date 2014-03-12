/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package timetable;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.sql.*;

import initial.server;

/**
 *
 * @author Administrator
 */
public class new_class2 extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            String query, message = "";
            PreparedStatement execute_query;
            ResultSet result_set;
            int degree_id = 0, branch_id = 0, batch_id;
            int start_year, end_year;

            String batch = request.getParameter("batch");
            String degree = request.getParameter("degree");
            String branch = request.getParameter("branch");
            String semester = request.getParameter("semester");

            start_year = Integer.parseInt(batch.split("-")[0].trim());
            end_year = Integer.parseInt(batch.split("-")[1].trim());
                    
            query = "select * from Administration.Degree where SDegree=?";
            execute_query = server.server_connection.prepareStatement(query);
            execute_query.setString(1, degree);
            result_set = execute_query.executeQuery();
            result_set.next();
            degree_id = result_set.getInt("DegreeID");

            query = "select * from Administration.Department where Name=?";
            execute_query = server.server_connection.prepareStatement(query);
            execute_query.setString(1, branch);
            result_set = execute_query.executeQuery();
            result_set.next();
            branch_id = result_set.getInt("DepartmentID");

            query = "select * from Administration.Batch where StartYear=? and EndYear=?";
            execute_query = server.server_connection.prepareStatement(query);
            execute_query.setInt(1, start_year);
            execute_query.setInt(2, end_year);
            result_set = execute_query.executeQuery();
            result_set.next();
            batch_id = result_set.getInt("BatchID");

            query = "select * from TimeTable.ClassDetail where BatchID=? and DegreeID=? and DepartmentID=? and Semester=?";
            execute_query = server.server_connection.prepareStatement(query);
            execute_query.setInt(1, batch_id);
            execute_query.setInt(2, degree_id);
            execute_query.setInt(3, branch_id);
            execute_query.setInt(4, Integer.parseInt(semester));
            result_set = execute_query.executeQuery();

            if(result_set.next())
            {
                message += "<li>Class Details already exists, Enter valid Class details.</li>";
            }

            query = "select * from Administration.Degree where DegreeID=?";
            execute_query = server.server_connection.prepareStatement(query);
            execute_query.setInt(1, degree_id);
            result_set = execute_query.executeQuery();
            result_set.next();

            if(result_set.getInt("NoOfSemester") < Integer.parseInt(semester))
            {
                message += "<li>Semester doesn't exist for this degree, Enter valid Semester.</li>";
            }

            if(message.length() == 0)
            {
                query = "insert into TimeTable.ClassDetail (BatchID, DegreeID, DepartmentID, Semester) values (?,?,?,?)";
                execute_query = server.server_connection.prepareStatement(query);
                execute_query.setInt(1, batch_id);
                execute_query.setInt(2, degree_id);
                execute_query.setInt(3, branch_id);
                execute_query.setInt(4, Integer.parseInt(semester));
                execute_query.executeUpdate();

                request.setAttribute("batch", batch);
                request.setAttribute("degree", degree);
                request.setAttribute("branch", branch);
                request.setAttribute("semester", semester);
                RequestDispatcher new_time_table = request.getRequestDispatcher("new_time_table.jsp");
                new_time_table.forward(request, response);
            }
            else
            {
                request.setAttribute("error", "Correct the following errors:<br/><br/>" + message);
                request.setAttribute("batch", batch);
                request.setAttribute("degree", degree);
                request.setAttribute("branch", branch);
                request.setAttribute("semester", semester);
                RequestDispatcher new_class = request.getRequestDispatcher("new_class.jsp");
                new_class.forward(request, response);
            }            
        }
        catch(SQLException e)
        {
            out.write("<h1>Error Code: " + e.getErrorCode() + "</h1>");
            out.write("<h3>Exception: " + e.getClass().getName() + "</h3>");
            out.write("<h4>" + e.getLocalizedMessage() + "</h4>");
            out.write("<h4>SQL State: " + e.getSQLState() + "</h4>");
            out.write("<h2><a href=\"new_class.jsp\">Back</a></h2>");
        }
        finally
        {
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
