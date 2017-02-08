package controllers;

import com.google.inject.Inject;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class Application extends Controller {

    @Inject
    private Config config;

    @Inject
    private PlaySessionStore playSessionStore;

    private List<CommonProfile> getProfiles() {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager(context);
        return profileManager.getAll(true);
    }

    @Secure(clients = "AnonymousClient", authorizers = "csrfToken")
    public Result index() throws Exception {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final String token = (String) context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN);
        // profiles (maybe be empty if not authenticated)
        return ok(views.html.index.render(getProfiles(), token));
    }

    private Result protectedIndexView() {
        // profiles
        return ok(views.html.protectedIndex.render(getProfiles()));
    }


    @Secure(clients = "FacebookClient")
    public Result facebookIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "FacebookClient", authorizers = "admin")
    public Result facebookAdminIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "FacebookClient", authorizers = "custom")
    public Result facebookCustomIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "TwitterClient")
    public Result twitterIndex() {
        return protectedIndexView();
    }

    @Secure(clients = "Google2Client")
    public Result googleIndex() {
        return protectedIndexView();
    }

    @Secure
    public Result protectedIndex() {
        return protectedIndexView();
    }


    public Result forceLogin() {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final Client client = config.getClients().findClient(context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER));
        try {
            final HttpAction action = client.redirect(context);
            return (Result) config.getHttpActionAdapter().adapt(action.getCode(), context);
        } catch (final HttpAction e) {
            throw new TechnicalException(e);
        }
    }
}
