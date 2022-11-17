pipeline{
    agent any
    options{
        disableConcurrentBuilds()
    }
    stages{
        stage("Build"){
            steps{ 
                script {
                    def mvnHome = tool name: 'maven-3', type: 'maven'
                    def mvnCMD = "${mvnHome}/bin/mvn"
                    sh "${mvnCMD} clean install -Dmaven.test.skip=true"
                }                
            }
        }
        stage("Build Docker Image"){
            steps{
                script {
                    sh "/var/jenkins_home/scripts/amsac-tramite-api/test/build_image.sh"
                }                
            }
        }
        stage("Push Docker Image"){
            steps{
                script {
                    withCredentials([string(credentialsId: 'docker-hub-pwd', variable: 'dockerHubPassword')]) {
                        sh "/var/jenkins_home/scripts/amsac-tramite-api/test/push_image.sh"
                    }
                }                
            }
        }
        stage("Create Container"){
            steps{
                script {
                    withCredentials([string(credentialsId: 'gitlab-password', variable: 'githubServerPwd')]) {
                        sshagent(['test-server-amsac']) {
                            sh "/var/jenkins_home/scripts/amsac-tramite-api/test/create_container.sh"
                        }
                    }
                }                
            }
        }
    }
}