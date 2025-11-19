pipeline {
    agent any

    environment {
        // Define your app name
        APP_NAME = 'solid-project'
        // SonarQube Token (You'll add this in Jenkins credentials later)
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_BIN = 'apt-get update && apt-get install -y docker.io'
    }

    stages {

            stage('Initialize Tools') {
                        steps {
                                sh '''
                                    # Check for the correct package manager and install Docker CLI as root
                                    if [ -x "$(command -v apt-get)" ]; then
                                      apt-get update
                                      apt-get install -y docker.io
                                    else
                                      echo "FATAL: Could not find apt-get. Exiting."
                                      exit 1
                                    fi
                                '''
                            }
            }

                    // Stage 2: Build & Test (Now 'mvn' and 'docker' are available)
                    stage('Build & Test') {
                        steps {
                            sh 'chmod +x ./mvnw'
                            sh './mvnw clean package verify'
                        }
                    }

                    // Stage 3: Docker Build
                    stage('Docker Build') {
                        steps {
                            sh "docker build -t solid-project:latest ."
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

    }

    post {
        always {
            // Clean up workspace to save space
            cleanWs()
        }
    }
}