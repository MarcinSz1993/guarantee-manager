pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/root/guarantee-manager'
    DB_USERNAME = credentials('db-credentials').username
    DB_PASSWORD = credentials('db-credentials').password
    CLOUDINARY_API_KEY = credentials('cloudinary-api-key')
    CLOUDINARY_API_SECRET = credentials('cloudinary-api-secret')
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Deploy to VPS') {
      steps {
        withCredentials([sshUserPrivateKey(credentialsId: 'ssh-key-id', keyFileVariable: 'KEY_PATH')]) {
          sh """
            echo "📡 Przygotowanie zdalnego katalogu"
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST '
              mkdir -p $DEPLOY_DIR
            '

            echo "📦 Przesyłanie plików"
            scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r . $SSH_USER@$SSH_HOST:$DEPLOY_DIR

            echo "🚀 Uruchamianie docker-compose"
            ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST '
              cd $DEPLOY_DIR &&
              export DB_USERNAME=\$DB_USERNAME &&
              export DB_PASSWORD=\$DB_PASSWORD &&
              export CLOUDINARY_API_KEY=\$CLOUDINARY_API_KEY &&
              export CLOUDINARY_API_SECRET=\$CLOUDINARY_API_SECRET &&
              docker-compose down &&
              docker-compose up -d --build
            '
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
