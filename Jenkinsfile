pipeline {
    agent any

    tools {
        // This matches the Maven installation name from "Global Tool Configuration"
        maven 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(
                    branches: [[name: '*/sc']], 
                    extensions: [], 
                    userRemoteConfigs: [[
                        url: 'https://github.com/xinz02/qualitymanagementsystem_backend.git'
                    ]]
                )
            }
        }
        
        stage('Build') {
            steps {
                bat 'mvn install'
            }
        }
        
        stage('Check Docker Version') {
            steps {
                bat 'docker --version'
            }
        }
    
        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("xiuying545/qms:latest")
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'fa17b010-fee1-4528-90a9-d0057f35d125') {
                        dockerImage.push()
                    }
                }
            }
        }
    }
}
