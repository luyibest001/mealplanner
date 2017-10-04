package unsw.comp4920.project;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = "/home")
public class HomeServlet extends HttpServlet {
    private User currentUser;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String nextPage = "myhome.jsp";
        currentUser = (User)getServletContext().getAttribute("currentUser");

        if(currentUser==null){
            System.out.println("OHNONONONOONONONNON!");
        }else{
            System.out.println("HAHAHHAHA");
            request.setAttribute("currentUser",currentUser);
        }

        if(action != null){
            if(action.equals("edit_profile")){
                request.setAttribute("currentUser", currentUser);
            }else if(action.equals("search")){

            }else if(action.equals("profile")){
                nextPage = "profile.jsp";
            }else if(action.equals("edit_profile")){

            }else if(action.equals("logout")){
                currentUser = null;
                getServletContext().setAttribute("currentUser",null);
                getServletContext().setAttribute("action","logout");
                nextPage = "/control";
            }
        }

        RequestDispatcher rd = request.getRequestDispatcher("/"+nextPage);
        rd.forward(request, response);
    }
}
