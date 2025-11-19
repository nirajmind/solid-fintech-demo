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
            agent {
                        // Use a Jenkins agent image that has the Docker client installed
                        // This is a cleaner way than running apk/apt commands every time.
                        docker {
                            image 'jenkins/agent:jdk17'
                            // CRUCIAL: Map the Docker socket from the host into the agent container
                            args '-v /var/run/docker.sock:/var/run/docker.sock'
                        }
                    }
                    steps {
                        // Now the 'docker' command is available because the agent image provides it.
                        sh "docker build -t solid-project:latest ."
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