package unsw.comp4920.project;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@WebServlet(name = "HomeServlet", urlPatterns = "/home")
public class HomeServlet extends HttpServlet {
    private User currentUser;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String nextPage = "myhome.jsp";
        currentUser = (User)request.getSession().getAttribute("currentUser");
        if(action != null){
            Calendar c = (Calendar) request.getSession().getAttribute("currentDate");
            if(action.equals("next_week")) {
                c.add(Calendar.DAY_OF_WEEK,7);
            }else if(action.equals("prev_week")) {
                c.add(Calendar.DAY_OF_WEEK,-7);
            }else if(action.equals("curr_week")) {
                c = Calendar.getInstance();
                c.add( Calendar.DAY_OF_WEEK, -(c.get(Calendar.DAY_OF_WEEK)-1));
                request.getSession().setAttribute("currentDate",c);

            }else if(action.equals("add_meal")){
                try {
                    PlanUnit planUnit = new PlanUnit();
                    planUnit.setUser(currentUser);
                    System.out.println("PLAN DATE " + request.getParameter("plan_date"));
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = df.parse((String) request.getParameter("plan_date"));
                    planUnit.setDate(date);
                    System.out.println("date:  " + date);
                    String type = (String) request.getParameter("meal_type");
                    if(type.equals("breakfast")){
                        type = "B";
                    }else if(type.equals("lunch")){
                        type = "L";
                    }else if(type.equals("dinner")){
                        type = "D";
                    }
                    planUnit.setType(type);
                    System.out.println("type:  " + type);

                    String foodName = (String) request.getParameter("add_food");

                    Food food = new Food(foodName, 0, "Placeholder", UUID.randomUUID().toString());

                    planUnit.addToFoodList(food);
                    System.out.println("food:  " + foodName);
                    //write into database
                    DatabaseOperation dbo = new DatabaseOperation();
                    dbo.insertFood(food);
                    dbo.insertPlan(planUnit);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                nextPage = "add_meal.jsp";
            }else if(action.equals("save_plan")){
                nextPage = "";
            }else if(action.equals("show_recipe")){

                String id = (String)request.getParameter("selected_recipe");
                request.setAttribute("selected_recipe",id);
                nextPage = "selected_recipe.jsp";

            }else if(action.equals("search")){

                nextPage = "search.jsp";

            }else if(action.equals("search_submit")){
                request.setAttribute("search_keyword",request.getParameter("search_keyword"));
                nextPage = "search_recipe.jsp";

            }else if(action.equals("profile")){

                DatabaseOperation dbo = new DatabaseOperation();
                try {
                    currentUser = dbo.getUserProfile(currentUser.getUsername());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                request.getSession().setAttribute("currentUser",currentUser);
                nextPage = "profile.jsp";

            }else if(action.equals("edit_profile")){
                String username = currentUser.getUsername();
                String password = request.getParameter("edit_password");
                String email = request.getParameter("edit_email");
                String photourl = request.getParameter("edit_profile_photo");


                System.out.println(photourl);
                //write into database
                SearchDatabase sd = new SearchDatabase();
                if(email!=null && !"".equals(email)) {
                    DatabaseOperation dbo = new DatabaseOperation();
                    int i = dbo.updateUserInfo(username,"email",email);
                    //System.out.print
                    currentUser.setEmail(email);
                }else {
                    currentUser.setEmail(currentUser.getEmail());
                }

                if(password!=null&&!"".equals(password)){
                    DatabaseOperation dbo = new DatabaseOperation();
                    dbo.updateUserInfo(username,"password", password);
                    currentUser.setPassword(password);
                }else {
                    //currentUser.setPassword(currentUser.getPassword());
                }

                   /* if(photourl != null&&!"".equals(photourl)) {
                        //upload profile photo

                        FileInputStream pic = null;

                        System.out.println(photourl);
                        photourl = new String(photourl.getBytes());
                        if (photourl.contains(".png")) {
                            db.pngTojpg(photourl);
                        }
                        photourl = db.zippicture(photourl, 100, 100, 0.9f);
                        pic = new FileInputStream(photourl);


                        String sql = "update users set photourl =? where username = ? ";
                        System.out.println(sql);
                        PreparedStatement pStatement = connection.prepareStatement(sql);
                        pStatement.setBinaryStream(1, pic, pic.available());
                        pStatement.setString(2, username);
                        pStatement.executeUpdate();
                        currentUser.setPhotoUrl(photourl);
                    }else {
                        currentUser.setPhotoUrl(currentUser.getPhotoUrl());
                    }
                    */

                nextPage = "profile.jsp";

                request.setAttribute("currentUser",currentUser);
                request.setAttribute("username", currentUser.getUsername());

            } else if(action.equals("logout")){
                currentUser = null;
                getServletContext().setAttribute("currentUser",null);

                nextPage = "/control";
            }

        }

        request.getSession().setAttribute("currentUser",currentUser);
        RequestDispatcher rd = request.getRequestDispatcher("/"+nextPage);
        rd.forward(request, response);
    }
}
