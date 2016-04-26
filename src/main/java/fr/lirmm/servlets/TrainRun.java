/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.servlets;

import analysedesentiments.LearnModel;
import fr.lirmm.db.BaseDeDonnee;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;
import java.util.List;
import java.util.Iterator;

/**
 *
 * @author Niels
 */
public class TrainRun extends HttpServlet {
     public static final String NAME = "fileName";
    //A CODER
     
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Learn ::");
        
        HttpSession session = request.getSession();
        String mail = session.getAttribute("mail").toString();
        
        String fileName = request.getParameter(NAME);
            
        String propPath = generatesPath(mail,fileName) + fileName+".properties";
        String  path = generatesPath(mail,fileName);
        
        try {
            System.out.println("Learn commancé");
            LearnModel.learn(propPath, path);
            System.out.println("Learn terminé");
        } catch (Exception ex) {
            Logger.getLogger(TrainRun.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         SAXBuilder sxb = new SAXBuilder();
         org.jdom.Document document = new Document();
        try
        {
           //On crée un nouveau document JDOM avec en argument le fichier XML
           
           document = sxb.build(new File(path+"result.xml"));
        }
        catch(Exception e){}

        //On initialise un nouvel élément racine avec l'élément racine du document.
        Element racine = document.getRootElement();

        List listEtudiants = racine.getChildren("root");

        //On crée un Iterator sur notre liste
        Iterator i = listEtudiants.iterator();
        i.hasNext();
        
           //On recrée l'Element courant à chaque tour de boucle afin de
           //pouvoir utiliser les méthodes propres aux Element comme :
           //sélectionner un nœud fils, modifier du texte, etc...
           Element courant = (Element)i.next();
           
           request.setAttribute("microprecision",courant.getChild("microprecision").getText());
           request.setAttribute("microrecall",courant.getChild("microrecall").getText());
           request.setAttribute("microfmeasure",courant.getChild("microfmeasure").getText());
           request.setAttribute("macroprecision",courant.getChild("macroprecision").getText());
           request.setAttribute("macrorecall",courant.getChild("macrorecall").getText());
           request.setAttribute("macrofmeasure",courant.getChild("macrofmeasure").getText());
           request.setAttribute("path",path);
        
         
        //request.setAttribute("fileName", fileName);
        request.setAttribute( "title", "Result" );
        request.setAttribute( "topMenuName", "WorkFlow" );

        this.getServletContext().getRequestDispatcher( "/WEB-INF/affichageModeleUtilisateur.jsp" ).forward( request, response );
    }
    protected String generatesPath(String mail, String fileName)
    {
       
        BaseDeDonnee bd = new BaseDeDonnee();
        String id = "";
        try {
            id = bd.getUserId(mail);
        } catch (SQLException ex) {
            Logger.getLogger(TrainData.class.getName()).log(Level.SEVERE, null, ex);
        }
         String path = "./user_models/" + id + "/" + fileName +"/";
         return path;
    }

}
