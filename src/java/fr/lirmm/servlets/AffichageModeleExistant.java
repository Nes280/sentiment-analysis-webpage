/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.lirmm.servlets;

import analysedesentiments.AnalyseDeSentiments;
import com.sun.xml.ws.util.StringUtils;
import fr.lirmm.beans.Polarite;
import fr.lirmm.beans.Root;
import fr.lirmm.db.BaseDeDonnee;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import weka.classifiers.Classifier;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;



/**
 *
 * @author Elsa Martel
 */
@WebServlet(name = "AffichageModeleExistant", urlPatterns = {"/affichageModeleExistant"})
public class AffichageModeleExistant extends HttpServlet {
    
    public final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static final String CHAMP_TWEET = "tweetAAnalyser";
    public static final String CHAMP_FILE = "fileUpload";
    public static final String CHAMP_CHOIX = "choix";
    public static final String CHAMP_TYPE_ANALYSIS = "typeAnalysis";
    
    public static final String CHEMIN = "chemin";
    public static final int TAILLE_TAMPON = 10240;
    
    public static final String ATT_TWEET = "tweet";
    public static final String ATT_MESSAGE = "message";
    public static final String ATT_ERREUR = "erreur";
    public static final String ATT_ROOT = "root";
    public static final String ATT_CLASSE= "classe";
    public static final String ATT_DESCRIPTION= "description";
    public static final String ATT_TYPE_ANALYSIS = "typeAnalysis";
    public static final String ATT_TITRE = "titre";
    public static final String ATT_FILE_XML = "fichierXML";
    public static final String ATT_FILE_JSON = "fichierJSON";
    public static final String ATT_INFO = "information";
    
    public String UPLOAD = "isUpload";
    
    public static final String VUE = "/WEB-INF/affichageModeleExistant.jsp";
     
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        
        //variable pour le formulaire de saisie de tweet
        String tweet = request.getParameter(CHAMP_TWEET);
        String choix = request.getParameter(CHAMP_CHOIX);
        //System.out.println("choix "+ choix);
        
        //Au debut choix est null donc il faut l'initialiser
        if(choix == null){
            choix = "";
        }
        
        //Nom du fichier JSON
        String nomFichierJSON = ""; 
        
        //variable pour le choix du type d'analyse
        String typeAnalysis = request.getParameter(CHAMP_TYPE_ANALYSIS);
        System.out.println("type Analyse " + typeAnalysis);
        
        //3 Modeles
        String modele1 = ""; 
        String modele2 = ""; 
        String modele3 = "";
        
        //Variable de titre
        String titre = ""; 
        
        //Nom du fichier XML 
        String fxml = ""; 
        
        String information = "";
        
        //Chargement des variables, modèles suivant le type d'analyse
        if(typeAnalysis.equals("FrenchTweetsPolarity")){
            //Initialiser les modèles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Tweets Polarity";

            //Fichier XML 
            fxml = "tweetPolarity.xml";
        }
        else if(typeAnalysis.equals("FrenchTweetsSubjectivity"))
        {            
            //Initialiser les modèles A changer quand on aura les modeles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Tweets Subjectivity";

            //Fichier XML 
            fxml = "tweetSubjectivity.xml";
        }
        else if(typeAnalysis.equals("FrenchTweetsEmotions"))
        {            
            //Initialiser les modèles A changer quand on aura les modeles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Tweets Emotions";

            //Fichier XML 
            fxml = "tweetEmotion.xml";
        }
        else if(typeAnalysis.equals("FrenchProductReviews"))
        {            
            //Initialiser les modèles A changer quand on aura les modeles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Product Reviews";

            //Fichier XML 
            fxml = "productReview.xml";
        }
        else if(typeAnalysis.equals("FrenchVideosGames"))
        {            
            //Initialiser les modèles A changer quand on aura les modeles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Videos Games";

            //Fichier XML 
            fxml = "videoGames.xml";
        }
        else if(typeAnalysis.equals("FrenchParlementaryDebates"))
        {            
            //Initialiser les modèles A changer quand on aura les modeles
            modele1 = "DEFT15T1STW.model";
            modele2 = "DEFT15T1IG.model";
            modele3 = "DEFT15T1SMO.model";

            //Initialiser le titre de la page 
            titre = "French Parlementary Debates";

            //Fichier XML 
            fxml = "parlementaryDebates.xml";
        }
        
        
        //Preparation des résultats
        Map<String, String> listeTweet = new HashMap<String, String>();
        String message = ""; 
        //boolean erreur = true; 
        int erreur = 0; 
        String resultat = "";
        
        
        //on utilise le formulaire de saisie de texte
        if(choix.equals("saisieTexte"))
        {
            System.out.println("tweet " + tweet);
            //il n'y a pas de tweet à traiter
            if(tweet == null || tweet.isEmpty() ){
                message = "No tweet";
                //erreur = true;
                erreur = 0;
            }
            //on a un tweet à traiter
            else if (tweet != null){
                message = "Analysis tweet";
                //erreur = false;
                erreur = 1;
                
                AnalyseDeSentiments a = new AnalyseDeSentiments();

                try {
                    resultat = a.start(tweet, modele1, modele2, modele3);
                    listeTweet.put(tweet, resultat);
                } catch (Exception ex) {
                    Logger.getLogger(AffichageAPITweet.class.getName()).log(Level.SEVERE, null, ex);
                }
 
            }
        }
        //on utilise le formulaire d'upload de fichier
        else if(choix.equals("uploadFile")){
            
            //Recuperer le prenom et nom de l'utilisateur 
            HttpSession session = request.getSession();
            Object prenom = session.getAttribute("prenom");
            Object nom = session.getAttribute("nom");
            Object email = session.getAttribute("mail");
            
            BaseDeDonnee bd = new BaseDeDonnee();
            String id = "";
            try {
                id = bd.getUserId(email + "");
            }
            catch(Exception ex)
            {
                System.out.println(ex);
            }
            //System.out.println("id "+id);
            
            //Construction du nom du cookie 
            //String nomCookie = pr + "" + no; 
            //Object isUpload = session.getAttribute(UPLOAD);
            
            /*Cookie[] cookies = request.getCookies();
            System.out.println("taille du cookie " + cookies.length);*/
            
            //Recherche du cookie
            /*boolean trouver = rechercheCookie(cookies, nomCookie); 
            
            if(!trouver)
            {*/

                //Construction du cookie
                /*Cookie c = new Cookie(prenom + "" + nom, "1");
                c.setMaxAge(24*3600);
                response.addCookie(c);*/

                Part p = request.getPart(CHAMP_FILE);
                InputStream is = request.getPart(p.getName()).getInputStream();
                int i = is.available();
                byte[] b = new byte[i];
                System.out.println("byte " + b.length);
                //A limiter la taille  
                //A faire
                is.read(b);
                
               
                String fileName = getFileName(p);
                String nomFichier = id + "" + prenom + "" + nom + ".txt"; 

                //On a pas mis de fichiers
                if(fileName.equals("")){
                    message = "No tweet";
                    //erreur = true;
                    erreur = 0;
                }
                //on a uploadé un fichier
                else{
                    System.out.println("On rentre dans le else et on écrit");
                    message = "Analysis tweet";
                    erreur = 2;
                                        
                    FileOutputStream os = new FileOutputStream("./fichiers/" + nomFichier);
                    //String d = decodeUTF8(b);
                    //b = encodeUTF8(d);
                    
                    os.write(b); 
                    
                    System.out.println("Fin d'écriture");
                                                
                    //Lecture de fichier uploader
                    //String chaine = "";
                    try{
                        InputStream ips = new FileInputStream("./fichiers/" + nomFichier); 
                        InputStreamReader ipsr = new InputStreamReader(ips);
                        BufferedReader br= new BufferedReader(ipsr);
                        
                        System.out.println("lecture du fichier");
                        //Objet pour l'analyse
                        AnalyseDeSentiments a = new AnalyseDeSentiments();
                        
                        //Chargement des modèles
                        StringToWordVector stw = a.loadModelOne(modele1);
                        AttributeSelection ats = a.loadModelTwo(modele2);
                        Classifier cls = a.loadModelThree(modele3);
                        
                        String ligne;
                        while ((ligne=br.readLine())!=null){
                            System.out.println(ligne);

                            //changement encodage 
                            byte[] somebyte = ligne.getBytes();
                            String encoding = "UTF-8"; //ANSI Cp1252 ISO-8859-1
                            String sortie = new String(somebyte, encoding);
                            System.out.println("sortie " +sortie);
    
                            //resultat = a.start(ligne, modele1, modele2, modele3);
                            resultat = a.analyse(ligne, stw, ats, cls);
                            listeTweet.put(ligne, resultat);
                        }
                        br.close(); 
                        ipsr.close();
                        System.out.println("fin de lecture");

                        /*String delim = "\n";
                        String[] tokens = chaine.split(delim);*/

                        /*for(int j = 0; j < tokens.length; j++){
                            try {
                                resultat = a.start(tokens[j], modele1, modele2, modele3);
                                listeTweet.put(tokens[j], resultat);
                            } catch (Exception ex) {
                                Logger.getLogger(AffichageAPITweet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }*/ 
                    }		
                    catch (Exception e){
                            System.out.println(e.toString());
                    }
                    os.close();
                    
                    is.close();

                    File f = new File("./fichiers/" + nomFichier);
                    f.delete();

                    System.out.println("Creation du json");
                    nomFichierJSON = id + "-" + typeAnalysis + ".json";

                    //Création du json
                    FileOutputStream fos = new FileOutputStream(new File("./fichiers/" + nomFichierJSON));

                    JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
                    JsonGenerator generator = factory.createGenerator(fos);
                    generator.writeStartArray();

                    for(Entry<String, String> entry : listeTweet.entrySet())
                    {
                        generator.writeStartObject().write("phrase", entry.getKey()).
                            write("classe", entry.getValue()).writeEnd();            
                    }            
                    generator.writeEnd().close();
                    System.out.println("Fin du fichie json");
                }
                
                
                //Supprimer le cookie
                /*Cookie[] cookiesFinal = request.getCookies();
                System.out.println("taille du cookie a la fin " + cookiesFinal.length);

                //Recherche du cookie
                for(int j = 0; j < cookiesFinal.length; j++){
                    if(cookiesFinal[j].getName().equals(nomCookie)){
                        cookiesFinal[j].setMaxAge(0);
                    }
                    System.out.println("Final cookies " + cookies[j].getName());
                }
                c.setMaxAge(0);
                response.addCookie(c);*/
            /*}
            else
            {
                information = "You have already upload a file";
            }*/
        }
          
        //Valeur pour Root
        Root root = new Root();
        root.setSample(valeurXml(fxml, "/modele/root/sample"));
        root.setMicrofmeasure(valeurXml(fxml, "/modele/root/microfmeasure"));
        root.setMacrofmeasure(valeurXml(fxml, "/modele/root/macrofmeasure"));
        root.setMicroprecision(valeurXml(fxml, "/modele/root/microprecision"));
        root.setMacroprecision(valeurXml(fxml, "/modele/root/macroprecision"));
        root.setMicrorecall(valeurXml(fxml, "/modele/root/microrecall"));
        root.setMacrorecall(valeurXml(fxml, "/modele/root/macrorecall"));
        
        
        //Recuperer les id des éléments de type classe
        String[] classe = valeurClasseXml(fxml, "/modele/classe/@id");
        ArrayList<Polarite> listeClasse = new ArrayList<Polarite>(); 
        
        //Recuperer les valeurs samples, f-measure, precision, recall
        for(int i = 0; i < classe.length; i++)
        {
            //System.out.println("nom classe :" + classe[i]);
            Polarite p = new Polarite();
            p.setClasse(classe[i]);
            p.setSample(valeurXml(fxml, "/modele/classe[@id='"+ classe[i] +"']/sample"));
            p.setFmeasure(valeurXml(fxml, "/modele/classe[@id='"+ classe[i] +"']/fmeasure"));
            p.setPrecision(valeurXml(fxml, "/modele/classe[@id='"+ classe[i] +"']/precision"));
            p.setRecall(valeurXml(fxml, "/modele/classe[@id='"+ classe[i] +"']/recall"));
            listeClasse.add(p);
        }
        
        
        //Description 
        String description = valeurXml(fxml, "/modele/description");
        
        //URL 
        String[] url = valeurUrlXml(fxml, "/modele/url");
        for(int i = 0; i < url.length; i++)
        {
            //System.out.println(url[i]);
            String lien = "";
            if(url[i].contains("2007")){
                lien = "<a href=\" "+url[i] + " \">DEFT 2007</a>";
                description = description.replace("DEFT 2007", lien);
            }
            else if(url[i].contains("2015"))
            {
                lien = "<a href=\" "+url[i] + " \">DEFT 2015</a>";
                description = description.replace("DEFT 2015", lien);
            }
        }
        
        
        request.setAttribute( "title", "Tweet" );
        request.setAttribute(ATT_TWEET, listeTweet);
        request.setAttribute(ATT_MESSAGE, message);
        request.setAttribute(ATT_ERREUR, erreur);
        request.setAttribute(ATT_ROOT, root);
        request.setAttribute(ATT_CLASSE, listeClasse);
        request.setAttribute(ATT_DESCRIPTION, description);
        request.setAttribute(ATT_TYPE_ANALYSIS, typeAnalysis);
        request.setAttribute(ATT_TITRE, titre);
        request.setAttribute(ATT_FILE_XML, fxml);
        request.setAttribute(ATT_FILE_JSON, nomFichierJSON);
        
        this.getServletContext().getRequestDispatcher(VUE).forward( request, response );
    }
    
    
    //Fonction qui renvoie le nom du fichier uploader
    private String getFileName(Part part) { 
        String partHeader = part.getHeader("content-disposition"); 
        System.out.println("Part Header = " + partHeader); 
        for (String cd : part.getHeader("content-disposition").split(";")) { 
          if (cd.trim().startsWith("filename")) { 
            return cd.substring(cd.indexOf('=') + 1).trim() 
                .replace("\"", ""); 
          } 
        } 
        return null;
    }

    
    //Fonction qui va chercher les valeurs dans le fichier xml
    public String valeurXml(String f, String expression){
        String valeur = "";
        try{
            File file = new File("XML/" + f);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            Element root = xmlDocument.getDocumentElement();
            XPath xPath =  XPathFactory.newInstance().newXPath();
            valeur = xPath.evaluate(expression,root);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }catch (XPathExpressionException e) {
            e.printStackTrace();
        } 
        return valeur;

    }
    
    //Fonction qui recupere les URL 
    public String[] valeurUrlXml(String f,String expression){
        String[] valeur = null;
        try{
            File file = new File("XML/" + f);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expr = xPath.compile(expression);
            NodeList listNode = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
            valeur = new String[listNode.getLength()];
            
            for (int i = 0; i< listNode.getLength(); i++){
                //System.out.println("taille de id " + listNode.getLength() );
                Node classe = listNode.item(i).getChildNodes().item(0);
                valeur[i] = classe.getNodeValue();
                //System.out.println(valeur[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }catch (XPathExpressionException e) {
            e.printStackTrace();
        } 
        return valeur;
    }
    
    //Fonction qui recupere les classes des modèles
    public String[] valeurClasseXml(String f,String expression){
        String[] valeur = null;

        try{
            File file = new File("XML/" + f);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expr = xPath.compile(expression);
            NodeList listNode = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
            valeur = new String[listNode.getLength()];
            
            for (int i = 0; i< listNode.getLength(); i++){
                //System.out.println("taille de id " + listNode.getLength() );
                Node classe = listNode.item(i).getChildNodes().item(0);
                valeur[i] = classe.getNodeValue();
                //System.out.println(valeur[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }catch (XPathExpressionException e) {
            e.printStackTrace();
        } 
        return valeur;
    }
    
    public boolean rechercheCookie(Cookie[] cookies, String nomCookie){
        boolean trouver = false; 
        //Recherche du cookie
        for(int i = 0; i < cookies.length; i++){
            if(cookies[i].getName().equals(nomCookie)){
                trouver = true; 
            }
            System.out.println("cookies " + cookies[i].getName());
        }
        return trouver; 
    }
    
    //encodage
    byte[] encodeUTF8(String string){
        return string.getBytes(UTF8_CHARSET);
    }
    
    //decodage
    String decodeUTF8(byte[] bytes){
        return new String(bytes, UTF8_CHARSET);
    }

}


/*<c:if test="${t.value.equals('+')}" >
                                            <span class="success label"><i class="fi-plus"></i> Positif</span>
                                        </c:if>
                                        <c:if test="${t.value.equals('-')}" >
                                            <span class="alert label"><i class="fi-minus"></i> Negatif</span>
                                        </c:if>
                                        <c:if test="${t.value.equals('=')}" >
                                            <span class="info label"><i class="fi-list"></i> Neutre</span>
                                        </c:if>*/