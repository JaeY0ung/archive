pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checkout the repository
                git credentialsId: 'gitlab-access-token', url: 'https://lab.ssafy.com/s11-webmobile2-sub2/S11P12A507.git'
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        sh 'docker build -t frontend-app:latest .'
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        sh 'docker build -t backend-app:latest .'
                    }
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                script {
                    sh 'docker stop frontend || true && docker rm frontend || true'
                    sh 'docker run -d --name frontend -p 3000:80 frontend-app:latest'
                }
            }
        }

        stage('Deploy Backend') {
            steps {
                script {
                    sh 'docker stop backend || true && docker rm backend || true'
                    sh 'docker run -d --name backend -p 8080:8080 backend-app:latest'
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
