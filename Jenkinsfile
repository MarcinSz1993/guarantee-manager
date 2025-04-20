pipeline {
    agent any

    environment {
        CLOUDINARY_API_KEY = credentials('cloudinary-api-key')
        CLOUDINARY_API_SECRET = credentials('cloudinary-api-secret')
    }

    stages {
        stage('Start') {
            steps {
                echo 'Pipeline działa. Czas na kolejny krok.'
            }
        }

        stage('Build') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USERNAME', passwordVariable: 'DB_PASSWORD'),
                    usernamePassword(credentialsId: 'mail-credentials', usernameVariable: 'MAIL_USERNAME', passwordVariable: 'MAIL_PASSWORD')
                ]) {
                    echo "Buduję aplikację jako użytkownik: ${DB_USERNAME}"
                    dir('backend') {
                        sh './mvnw clean package -DskipTests'
                }
            }
        }
    }
}