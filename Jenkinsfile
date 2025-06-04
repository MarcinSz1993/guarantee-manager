pipeline {
    agent any

    environment {
        DEPLOY_DIR = "${env.WORKSPACE}/guarantee-manager"
        DB_USERNAME = credentials('db-username')
        DB_PASSWORD = credentials('db-password')
        CLOUDINARY_API_KEY = credentials('cloudinary-api-key')
        CLOUDINARY_API_SECRET = credentials('cloudinary-api-secret')
        POSTGRES_PASSWORD = credentials('postgres-password')
        MAIL_USERNAME = credentials('smtp-username')
        MAIL_PASSWORD = credentials('smtp-password')
    }

    stages {
        stage('Checkout') {
            steps {
                dir("${DEPLOY_DIR}") {
                    deleteDir() //
                    git branch: 'prod-kafka', url: 'https://github.com/MarcinSz1993/guarantee-manager'
                }
            }
        }

        stage('Build and Deploy') {
            steps {
                dir("${DEPLOY_DIR}") {

                    sh 'docker-compose down || true'
                    sh 'docker-compose build'
                    sh 'docker-compose up -d'
                }
            }
        }
    }
}
