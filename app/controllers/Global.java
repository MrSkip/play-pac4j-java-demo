package controllers;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Clients;
import org.pac4j.http.client.BasicAuthClient;
import org.pac4j.http.client.FormClient;
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.play.CallbackController;
import org.pac4j.play.Config;

import play.Application;
import play.GlobalSettings;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

public class Global extends GlobalSettings {
    
    @Override
    public Result onError(final RequestHeader arg0, final Throwable arg1) {
        return play.mvc.Controller.internalServerError(views.html.error500.render());
    }
    
    @Override
    public void onStart(final Application arg0) {
        CallbackController.setErrorPage401(CallbackController.unauthorized(views.html.error401.render()));
        CallbackController.setErrorPage403(CallbackController.unauthorized(views.html.error403.render()));
        
        // OAuth
        final FacebookClient facebookClient = new FacebookClient("132736803558924", "e461422527aeedb32ee6c10834d3e19e");
        final TwitterClient twitterClient = new TwitterClient("HVSQGAw2XmiwcKOTvZFbQ",
                                                              "FSiO9G9VRR4KCuksky0kgGuo8gAVndYymr4Nl7qc8AA");
        // HTTP
        final FormClient formClient = new FormClient("http://localhost:9000/theForm",
                                                     new SimpleTestUsernamePasswordAuthenticator());
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        
        // CAS
        final CasClient casClient = new CasClient();
        casClient.setCasLoginUrl("http://localhost:8080/cas/login");
        final Clients clients = new Clients("http://localhost:9000/callback", facebookClient, twitterClient,
                                            formClient, basicAuthClient, casClient);
        
        Config.setClients(clients);
        // for test purposes : profile timeout = 60 seconds
        // Config.setProfileTimeout(60);
    }
}
