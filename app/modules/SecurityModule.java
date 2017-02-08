package modules;

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

import java.util.Map;

public class SecurityModule extends AbstractModule {

    private final Configuration configuration;

    public SecurityModule(final Environment environment, final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(PlaySessionStore.class).to(PlayCacheStore.class);
        Map<String, Object> map = configuration.getConfig("play").getConfig("server").getConfig("http").asMap();

        final String callback = /*"http://127.0.0.1" + ":" + map.get("port") + */configuration.getString("_callback");

        final String fbKey = configuration.getString("fbKey");
        final String fbSecret = configuration.getString("fbSecret");

        final String twitterKey = configuration.getString("twitterKey");
        final String twitterSecret = configuration.getString("twitterSecret");

        final String googleKey = configuration.getString("googleKey");
        final String googleSecret = configuration.getString("googleSecret");

        // OAuth 2
        final FacebookClient facebookClient = new FacebookClient(fbKey, fbSecret);
        final TwitterClient twitterClient = new TwitterClient(twitterKey, twitterSecret);
        final Google2Client google2Client = new Google2Client(googleKey, googleSecret);

        final Clients clients = new Clients(callback, facebookClient, twitterClient, google2Client, new AnonymousClient());

        final Config config = new Config(clients);

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
