package providers;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.google.inject.Inject;
import models.LinkedAccount;
import models.User;
import play.Application;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Http;
import controllers.routes;

import static play.data.Form.form;

public class BattleshipUsernamePasswordAuthProvider extends UsernamePasswordAuthProvider<String, BattleshipLoginUsernamePasswordAuthUser, BattleshipUsernamePasswordAuthUser, Login, Signup> {

    protected Form<Signup> SIGNUP_FORM = form(Signup.class);
    protected Form<Login> LOGIN_FORM = form(Login.class);

    @Inject
    public BattleshipUsernamePasswordAuthProvider(Application app) {
        super(app);
    }

    @Override
    protected BattleshipLoginUsernamePasswordAuthUser buildLoginAuthUser(Login login, Http.Context context) {
        return new BattleshipLoginUsernamePasswordAuthUser(login.getPassword(), login.getEmail());
    }

    @Override
    protected BattleshipLoginUsernamePasswordAuthUser transformAuthUser(BattleshipUsernamePasswordAuthUser authUser, Http.Context context) {
        return new BattleshipLoginUsernamePasswordAuthUser(authUser.getEmail());
    }

    @Override
    protected BattleshipUsernamePasswordAuthUser buildSignupAuthUser(Signup signup, Http.Context context) {
        return new BattleshipUsernamePasswordAuthUser(signup);
    }

    @Override
    protected UsernamePasswordAuthProvider.LoginResult loginUser(BattleshipLoginUsernamePasswordAuthUser authUser) {
        final User user = User.findByAuthUserIdentity(authUser);
        if (user == null) {
            return UsernamePasswordAuthProvider.LoginResult.NOT_FOUND;
        } else {
            for (final LinkedAccount acc : user.linkedAccounts) {
                if (getKey().equals(acc.providerKey)) {
                    if (authUser.checkPassword(acc.providerUserId, authUser.getPassword())) {
                        // Password correct
                        return UsernamePasswordAuthProvider.LoginResult.USER_LOGGED_IN;
                    } else {
                        // Not returning here means user is allowed to have multiple passwords
                        return UsernamePasswordAuthProvider.LoginResult.WRONG_PASSWORD;
                    }
                }
            }
            return UsernamePasswordAuthProvider.LoginResult.WRONG_PASSWORD;
        }
    }

    @Override
    protected UsernamePasswordAuthProvider.SignupResult signupUser(BattleshipUsernamePasswordAuthUser userAuth) {
        final User user = User.findByAuthUserIdentity(userAuth);
        if (user != null) {
            return UsernamePasswordAuthProvider.SignupResult.USER_EXISTS;
        }
        // The user either doesn't exist or is inactive -> create a new one
        final User newUser = User.create(userAuth);
        return UsernamePasswordAuthProvider.SignupResult.USER_CREATED;
    }

    @Override
    protected Form<Signup> getSignupForm() {
        return SIGNUP_FORM;
    }

    @Override
    protected Form<Login> getLoginForm() {
        return LOGIN_FORM;
    }

    @Override
    protected Call userExists(UsernamePasswordAuthUser usernamePasswordAuthUser) {
        return routes.SignupController.exists();
    }

    @Override
    protected String generateVerificationRecord(BattleshipUsernamePasswordAuthUser battleshipUsernamePasswordAuthUser) {
        // Not used
        return null;
    }

    @Override
    protected String getVerifyEmailMailingSubject(BattleshipUsernamePasswordAuthUser battleshipUsernamePasswordAuthUser, Http.Context context) {
        // Not used
        return null;
    }

    @Override
    protected Mailer.Mail.Body getVerifyEmailMailingBody(String s, BattleshipUsernamePasswordAuthUser battleshipUsernamePasswordAuthUser, Http.Context context) {
        // Not used
        return null;
    }

    @Override
    protected Call userUnverified(UsernamePasswordAuthUser usernamePasswordAuthUser) {
        // Not used
        return null;
    }
}
