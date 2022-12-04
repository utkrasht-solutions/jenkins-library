
def call(List admin_env, String admin_role) {
    properties([parameters([[$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filterable: false, name: 'Env', randomName: 'choice-parameter-4186073374950', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], oldScript: '', sandbox: false, script: ''], script: [classpath: [], oldScript: '', sandbox: false, script: """
import hudson.model.User
import hudson.model.Hudson
import hudson.security.AuthorizationStrategy
import hudson.security.Permission
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap

AuthorizationStrategy strategy = Hudson.getInstance().getAuthorizationStrategy();

jobs = []
user = User.current()
userId = user.getId()

if (strategy != null && strategy instanceof com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy) {
    roleStrategy = (RoleBasedAuthorizationStrategy) strategy;
    // not very straightforward to get the groups for a given user
    roles = roleStrategy.getGrantedRoles("globalRoles")
    for (entry in roles) {
        role = entry.key
        users = entry.value
        if (role.getName().equals("dev")) {
            if (userId in users) {
                jobs = ["Dev", "Stg"]
                break
            }
        } else if (role.getName().equals($admin_role)) {
            if (userId in users) {
                jobs = $admin_env
                break
            }
        }
    }
}

return jobs
"""]]]])])
}