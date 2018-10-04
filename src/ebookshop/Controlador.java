package ebookshop;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Controlador
 */
@WebServlet("/Controlador")
public class Controlador extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Vector<Book> shoplist;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controlador() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);
		shoplist = (Vector<Book>)session.getAttribute("carrito");
		String do_this = request.getParameter("do_this");
		if (do_this == null) {
		      cargaLibro(request, response, session);
		} else {
		    switch(do_this) {
		    	case "checkout":
		    		checkOut(request, response);
		    		break;
		    	case "remove":
		    		removeBook(request, response);
		    		break;
		    	case "add":
		    		addBook(request, response);
		    		break;
		    }
		}
	   
	}
	
	private void cargaLibro(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
		Vector<String> blist = new Vector<String>();
	      blist.addElement("Beginning JSP, JSF and Tomcat. Zambon/Sekler $39.99");
	      blist.addElement("Beginning JBoss Seam. Nusairat $39.99");
	      blist.addElement("Founders at Work. Livingston $25.99");
	      blist.addElement("Business Software. Sink $24.99");
	      blist.addElement("Foundations of Security. Daswani/Kern/Kesavan $39.99");
	      session.setAttribute("ebookshop.list", blist);
	      ServletContext    sc = getServletContext();
	      RequestDispatcher rd = sc.getRequestDispatcher("/");
	      rd.forward(req, res);
	}
	
	private void checkOut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		float dollars = 0;
        int   books = 0;
        for (int i = 0; i < shoplist.size(); i++) {
          Book  aBook = (Book)shoplist.elementAt(i);
          float price = aBook.getPrice();
          int   qty = aBook.getQuantity();
          dollars += price * qty;
          books += qty;
          }
        req.setAttribute("dollars", new Float(dollars).toString());
        req.setAttribute("books", new Integer(books).toString());
        ServletContext    sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher("/Checkout.jsp");
        rd.forward(req, res);
	}
	
	private void removeBook( HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pos = req.getParameter("position");
        shoplist.removeElementAt((new Integer(pos)).intValue());
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher("/");
        rd.forward(req, res);
	}
	
	private void addBook(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		 boolean found = false;
         Book aBook = getBook(req);
         if (shoplist == null) {  // the shopping cart is empty
        	 System.out.println("Lista Vacia!");
           shoplist = new Vector<Book>();
           shoplist.addElement(aBook);
           req.getSession().setAttribute("carrito", shoplist);
         } else {  // update the #copies if the book is already there
           for (int i = 0; i < shoplist.size() && !found; i++) {
             Book b = (Book)shoplist.elementAt(i);
             if (b.getTitle().equals(aBook.getTitle())) {
               b.setQuantity(b.getQuantity() + aBook.getQuantity());
               shoplist.setElementAt(b, i);
               found = true;
             }
           } 
           if (!found) {  
             shoplist.addElement(aBook);
           }
         } 
         ServletContext sc = getServletContext();
	     RequestDispatcher rd = sc.getRequestDispatcher("/");
	     rd.forward(req, res);
	}
	
	private Book getBook(HttpServletRequest req) {
	    String myBook = req.getParameter("book");
	    int    n = myBook.indexOf('$');
	    String title = myBook.substring(0, n);
	    String price = myBook.substring(n+1);
	    String qty = req.getParameter("qty");
	    return new Book(title, Float.parseFloat(price), Integer.parseInt(qty));
	} // getBook
	  
}
