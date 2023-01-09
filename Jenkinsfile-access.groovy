@Library('test')_

pipeline {
    agent any

    stages{
        stage("Access Management") {
            steps{
                script {
                    if (params.Access_Request_ID == "") {
                        error("Please mention Access Request ID.")
                    }

                    accessManagement(
                            userName: params.User_Name,
                            roleName: params.Project_Role_Name,
                            globalroleName: params.Global_Role_Name
                    )
                }
            }
        }
    }
}