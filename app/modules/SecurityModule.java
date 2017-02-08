package modules;

//import be.objectify.deadbolt.java.cache.HandlerCache;

import com.google.inject.AbstractModule;
import controllers.DemoHttpActionAdapter;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.play.deadbolt2.Pac4jRoleHandler;
import org.pac4j.play.store.PlayCacheStore;
import org.pac4j.play.store.PlaySessionStore;
import play.Configuration;
import play.Environment;

public class SecurityModule extends AbstractModule {

    private final Configuration configuration;

    public SecurityModule(final Environment environment, final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        bind(Pac4jRoleHandler.class).to(MyPac4jRoleHandler.class);
        bind(PlaySessionStore.class).to(PlayCacheStore.class);

        final String fbId = configuration.getString("fbId");
        final String fbSecret = configuration.getString("fbSecret");
        final String baseUrl = configuration.getString("baseUrl");

        // OAuth
        final FacebookClient facebookClient = new FacebookClient(fbId, fbSecret);
        final TwitterClient twitterClient = new TwitterClient("39Ugjg0g9l1w3UniUB2Wx68ae",
                "uvKTPQvfwsZ8JyAYptGCxVuFGYOQcJ9JvQVTPwr75B3W4PpUAP");
        twitterClient.setCallbackUrl(baseUrl + "/callback1");
        facebookClient.setCallbackUrl(baseUrl + "/callback1");

        // HTTP
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey("137297527302-hdcf3hdi5hkp2p83sbmr8k4o82rs82uh.apps.googleusercontent.com");
        google2Client.setSecret("pDkiCTgjxk_8pIgIbja4qsVr");
        google2Client.setCallbackUrl(baseUrl + "/callback1");
        google2Client.setScope(Google2Client.Google2Scope.EMAIL_AND_PROFILE);

//        final FormClient formClient = new FormClient(baseUrl + "/loginForm", new SimpleTestUsernamePasswordAuthenticator());
//        final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());

        final Clients clients = new Clients(facebookClient, twitterClient, google2Client, new AnonymousClient());

        final Config config = new Config(clients);
//        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
//        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DemoHttpActionAdapter());
        bind(Config.class).toInstance(config);

        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        callbackController.setMultiProfile(true);
        bind(CallbackController.class).toInstance(callbackController);
        // logout
        final ApplicationLogoutController logoutController = new ApplicationLogoutController();
        logoutController.setDefaultUrl("/?defaulturlafterlogout");
        bind(ApplicationLogoutController.class).toInstance(logoutController);
    }

    private static class MyPac4jRoleHandler implements Pac4jRoleHandler {
    }
}
