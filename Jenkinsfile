pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/root/guarantee-manager'
    FRONTEND_DIR = 'frontend'
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Install Dependencies') {
      steps {
        dir(FRONTEND_DIR) {
          script {

            sh 'npm install'
          }
        }
      }
    }

    stage('Build Angular App') {
      steps {
        dir(FRONTEND_DIR) {
          script {

            sh 'npm run build -- --configuration production'
          }
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
          script {

            sh """
              echo "Tworzenie katalogu na serwerze..."
              ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'mkdir -p $DEPLOY_DIR'
            """


            sh """
              echo "Kopiowanie pliku Nginx na serwer..."
              scp -i \$KEY_PATH -o StrictHostKeyChecking=no frontend/guaranteemanager.conf $SSH_USER@$SSH_HOST:/etc/nginx/sites-available/guaranteemanager
            """


            sh """
              ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'nginx -t'
              ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'systemctl reload nginx'
            """


            sh """
              echo "Kopiowanie plików frontend na serwer..."
              scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r frontend/dist/frontend/browser/* $SSH_USER@$SSH_HOST:$DEPLOY_DIR
            """


            sh """
              echo "Uruchamianie docker-compose na VPS..."
              ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST << EOF
                cd $DEPLOY_DIR

                MAIL_USERNAME="${MAIL_USERNAME}" \\
                MAIL_PASSWORD="${MAIL_PASSWORD}" \\
                DB_USERNAME="${DB_USERNAME}" \\
                DB_PASSWORD="${DB_PASSWORD}" \\
                POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \\
                CLOUDINARY_API_KEY="${CLOUDINARY_API_KEY}" \\
                CLOUDINARY_API_SECRET="${CLOUDINARY_API_SECRET}" \\
                docker-compose down -v

                MAIL_USERNAME="${MAIL_USERNAME}" \\
                MAIL_PASSWORD="${MAIL_PASSWORD}" \\
                DB_USERNAME="${DB_USERNAME}" \\
                DB_PASSWORD="${DB_PASSWORD}" \\
                POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \\
                CLOUDINARY_API_KEY="${CLOUDINARY_API_KEY}" \\
                CLOUDINARY_API_SECRET="${CLOUDINARY_API_SECRET}" \\
                docker-compose up -d --build
EOF
            """
          }
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
