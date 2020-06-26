package main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.javalin.Javalin;
import models.Auteur;
import models.Book;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Javalin app = Javalin.create();
        EntityManagerFactory emf =
        		Persistence.createEntityManagerFactory("me.adomecq.client.jpa.fruits");
        
        
        app.get("/book/add/:titre/:authorID/:date", ctx -> {
        	EntityManager entityManager = emf.createEntityManager();
        	Auteur auteur = entityManager.find(Auteur.class, Integer.parseInt(ctx.pathParam("authorID")));
        	
        	Book b = new Book(ctx.pathParam("titre"), Integer.parseInt(ctx.pathParam("authorID")), Integer.parseInt(ctx.pathParam("date")));
        	auteur.books.add(b);
        	
        	entityManager.getTransaction().begin();
        	entityManager.persist(auteur);
        	entityManager.getTransaction().commit();
        	entityManager.close();
        });
        
        app.get("/auteur/:authorID", ctx -> {
        	
        	
        	EntityManager entityManager = emf.createEntityManager();
        	Auteur auteur = entityManager.find(Auteur.class, Integer.parseInt(ctx.pathParam("authorID")));
        	
        	ctx.json(auteur);

        });   

        app.get("/auteur/:nom/:prenom/:date", ctx -> {
        	
        	EntityManager entityManager = emf.createEntityManager();
        	
        	
        	Auteur au = new Auteur(ctx.pathParam("nom"), ctx.pathParam("prenom"), ctx.pathParam("date"));
        	
        	entityManager.getTransaction().begin();
        	entityManager.persist(au);
        	entityManager.getTransaction().commit();
        	entityManager.close();

        });

        /*app.get("/article/:key", ctx -> {
        	EntityManager entityManager$ = emf.createEntityManager();
        	ArticleDTO article = entityManager$.find(ArticleDTO.class, Integer.parseInt(ctx.pathParam("key")));

        	PriceResult pr = new PriceResult();
        	pr.price = article.total();
        	entityManager$.close();
        	ctx.json(pr);

        });           
        app.get("/price/:fruit", ctx -> {
        	EntityManager em = emf.createEntityManager();
        	
        	ArticleDTO article = ClientFruit.getArticleByName(em, ctx.pathParam("fruit"));
        	
	        if(article.soldes.size() > 0) {
	        	Double totalReduction = 0.0;
	        	for(int i = 0; i < article.soldes.size(); i++) {
	        		totalReduction = totalReduction + article.soldes.get(i).reduction;
	        	}
	        	article.prix = (Double.valueOf(article.prix) * (100.0 - totalReduction) / 100.0);
	        }
	        System.out.println(article.prix);
        	
        	ctx.result("{\"price\" : "+article.prix +" }");
        	
        });*/
        
        app.start(7002);

	}

}
