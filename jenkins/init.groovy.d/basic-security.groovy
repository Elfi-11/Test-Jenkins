import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import jenkins.install.InstallState
import jenkins.model.Jenkins

def instance = Jenkins.get()

def realm = new hudson.security.HudsonPrivateSecurityRealm(false)
realm.createAccount("admin", "admin")
instance.setSecurityRealm(realm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)
instance.save()
