pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'maven3.9.11'
    }

    environment {
        // Jenkins SonarQube server config name (keep as is if configured)
        SONARQUBE_ENV = 'SonarQube'

        // YOUR SonarQube token stored in Jenkins credentials
        SONAR_TOKEN = credentials('sonarkey')

        // YOUR DockerHub image name (username/repo:tag)
        DOCKER_IMAGE = 'nishasubramaniyan/ci-cd_sonarqube-docker:${BUILD_NUMBER}'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub'
                // YOUR GitHub repository
                git branch: 'main', url: 'https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Java project'
                bat 'mvn clean install -Dmaven.compiler.source=17 -Dmaven.compiler.target=17'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis'
                withSonarQubeEnv('SonarQube') {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=CI-CD_SonarQube-Docker ^
                        -Dsonar.projectName=CI-CD_SonarQube-Docker ^
                        -Dsonar.sources=src/main/java ^
                        -Dsonar.token=%SONAR_TOKEN%
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Checking Quality Gate'
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image'
                bat 'docker build -t %DOCKER_IMAGE% .'
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing image to DockerHub'
                // YOUR DockerHub credentials ID
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        docker push %DOCKER_IMAGE%
                    """
                }
            }
        }

        stage('Local Deploy') {
            steps {
                echo ' Deploying container locally'
                bat """
                    docker stop ci-cd-demo || echo "No existing container to stop"
                    docker rm ci-cd-demo || echo "No existing container to remove"
                    docker run -d --name ci-cd-demo -p 5000:8080 %DOCKER_IMAGE%
                """
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully — Build, SonarQube, Docker, and Deploy... all OK!'
        }
        failure {
            echo 'Pipeline failed — check logs for the failed stage.'
        }
    }
}
