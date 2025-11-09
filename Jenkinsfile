pipeline {
  agent any
  environment {
    SONAR_TOKEN = credentials('sonarkey')  // SonarQube token ID in Jenkins credentials
    DOCKERHUB_USER = credentials('dockerhub') // DockerHub username credential in Jenkins
    DOCKERHUB_PASS = credentials('dockerhub') // DockerHub password credential with same ID
  }
  triggers { 
    githubPush()  // Trigger pipeline on every push to GitHub
  }
  stages {
    stage('Clone') {
      steps {
        git 'https://github.com/nisha-subramaniyan/CI-CD_SonarQube-Docker.git'  // Your repo URL
      }
    }
    stage('Build & Test') {
      steps {
        sh 'python3 -m pytest test_app.py'  // Adjust test command as needed
      }
    }
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQube') {  // SonarQube server config name in Jenkins
          sh 'sonar-scanner -Dsonar.projectKey=CI-CD_SonarQube-Docker -Dsonar.sources=. -Dsonar.login=${SONAR_TOKEN}'
        }
      }
    }
    stage('Quality Gate') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true  // Abort on failed quality gate
        }
      }
    }
    stage('Docker Build') {
      steps {
        script {
          docker.build("nishasubramaniyan/ci-cd_sonarqube-docker:${env.BUILD_NUMBER}")
        }
      }
    }
    stage('Docker Push') {
      steps {
        script {
          docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
            docker.image("nishasubramaniyan/ci-cd_sonarqube-docker:${env.BUILD_NUMBER}").push()
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        script {
          sh 'docker run -d -p 5000:5000 nishasubramaniyan/ci-cd_sonarqube-docker:${env.BUILD_NUMBER}'
        }
      }
    }
  }
}
