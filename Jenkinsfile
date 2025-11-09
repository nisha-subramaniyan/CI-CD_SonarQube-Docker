pipeline {
    agent any
    environment {
        SONARQUBE = 'sonarkey'                // SonarQube server configured in Jenkins with this ID
        DOCKERHUB_CREDENTIALS = 'dockerhub'  // Jenkins credentials ID for DockerHub login (username + password/token)
        IMAGE_NAME = "nishasubramaniyan/your-app"  // DockerHub repo name; adjust replace 'your-app' with your image name
        GIT_REPO = 'https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git'
        GIT_BRANCH = 'main'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
            }
        }
        stage('Build & Test') {
            steps {
                echo 'Running build and tests...'
                // Use Windows batch commands here
                bat 'npm install'
                bat 'npm test'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Use backslash paths and .bat for Windows
                    def scannerHome = tool 'SonarQubeScanner'
                    withSonarQubeEnv('sonarkey') {
                        bat "\"${scannerHome}\\bin\\sonar-scanner.bat\""
                    }
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    docker.build("${IMAGE_NAME}:latest")
                }
            }
        }
        stage('Push to DockerHub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', "${DOCKERHUB_CREDENTIALS}") {
                        docker.image("${IMAGE_NAME}:latest").push()
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying Docker container...'
                // Docker run command for Windows batch
                bat "docker run -d --name myapp -p 8080:8080 ${IMAGE_NAME}:latest || docker restart myapp"
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
