pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR_FRONTEND = '/var/www/guarantee-manager'
    DEPLOY_DIR_BACKEND = '/root/guarantee-manager'
    BUILD_DIR = 'frontend/dist/guarantee-manager'
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Build Angular app') {
      steps {
        dir('frontend') {
          sh 'npm install'
          sh 'npm run build --configuration=production'
        }
      }
    }

    stage('Deploy to VPS') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'ssh-key-id', keyFileVariable: 'KEY_PATH'),
          usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USERNAME', passwordVariable: 'DB_PASSWORD'),
          string(credentialsId: 'cloudinary-api-key', variable: 'CLOUDINARY_API_KEY'),
          string(credentialsId: 'cloudinary-api-secret', variable: 'CLOUDINARY_API_SECRET'),
          string(credentialsId: 'postgres-password', variable: 'POSTGRES_PASSWORD'),
          string(credentialsId: 'smtp-username', variable: 'MAIL_USERNAME'),
          string(credentialsId: 'smtp-password', variable: 'MAIL_PASSWORD')
        ]) {
          sh """
            echo "Czyszczenie starej aplikacji Frontend na serwerze..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'rm -rf $DEPLOY_DIR_FRONTEND/*'

            echo "Kopiowanie nowej wersji Frontendu..."
            scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r $BUILD_DIR/* $SSH_USER@$SSH_HOST:$DEPLOY_DIR_FRONTEND

            echo "Aktualizacja Backend (docker-compose)..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST << EOF
              mkdir -p $DEPLOY_DIR_BACKEND

              echo "Czyszczenie starego backendu..."
              rm -rf $DEPLOY_DIR_BACKEND/*

              exit
EOF

            scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r backend docker-compose.yml credentials.env Jenkinsfile $SSH_USER@$SSH_HOST:$DEPLOY_DIR_BACKEND/

            echo "Uruchamianie docker-compose na VPS..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST << EOF
              cd $DEPLOY_DIR_BACKEND

              MAIL_USERNAME="${MAIL_USERNAME}" \\
              MAIL_PASSWORD="${MAIL_PASSWORD}" \\
              DB_USERNAME="${DB_USERNAME}" \\
              DB_PASSWORD="${DB_PASSWORD}" \\
              POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \\
              CLOUDINARY_API_KEY="${CLOUDINARY_API_KEY}" \\
              CLOUDINARY_API_SECRET="${CLOUDINARY_API_SECRET}" \\

              docker-compose down -v
              docker-compose up -d --build
EOF

            echo "Sprawdzanie konfiguracji Nginx..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'nginx -t'

            echo "Reload Nginx..."
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'systemctl reload nginx'
          """
        }
      }
    }
  }

  post {
    success {
      echo "✅ Deployment zakończony sukcesem!"
    }
    failure {
      echo "❌ Deployment nie powiódł się."
    }
  }
}
