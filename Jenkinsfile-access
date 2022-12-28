@Library('test')_

pipeline {
    agent any
    parameters {
        string(name: "Access_Request_ID", description: "Jira ticket id only")
        string(name: "User_Name", description: "user email address or username")
        string(name: "Global_Role_Name", defaultValue: "readonly", trim: true, description: "Global Role name")
        string(name: "Project_Role_Name", trim: true, description: "Global Role name")
    }
    stages{
        stage("Access Management") {
            steps{
                script {
                    if (params.Access_Request_ID == "") {
                        error("Please mention Access Request ID.")
                     }
                accessManagement.call(
                    userName: params.User_Name,
                    roleName: params.Project_Role_Name,
                    globalroleName: params.Global_Role_Name
                    )
                }
            }
        }
    }
}