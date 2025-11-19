pipeline {
    agent any

    environment {
        // Define your app name
        APP_NAME = 'solid-project'
        // SonarQube Token (You'll add this in Jenkins credentials later)
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        // Stage 1: Build and Test
        stage('Build & Test') {
            steps {
                sh 'chmod +x ./mvnw' // ADD THIS LINE: Grant execute permission
                sh './mvnw clean package verify'
            }
        }

        // Stage 2: Code Quality
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') { // You'll name your server 'LocalSonar' in Jenkins
                    sh "export SONAR_TOKEN='${SONAR_TOKEN}'; ./mvnw sonar:sonar -Dsonar.projectKey=${APP_NAME} -Dsonar.host.url=http://sonar-service:9000"
                }
            }
        }

        // Stage 3: Build Docker Image
        stage('Docker Build') {
            steps {
                script {
                    // 1. Install Docker Client (Standard method for Alpine-based images)
                                sh 'apk add --no-cache docker'

                                // 2. Build the image
                                sh "docker build -t solid-project:latest ."
                }
            }
        }
    }

    post {
        always {
            // Clean up workspace to save space
            cleanWs()
        }
    }
}