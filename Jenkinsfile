pipeline {
  agent any

  environment {
    SSH_USER = 'root'
    SSH_HOST = '157.180.16.111'
    DEPLOY_DIR = '/root/guarantee-manager'
  }

  stages {
    stage('Checkout code') {
      steps {
        git branch: 'prod', url: 'https://github.com/MarcinSz1993/guarantee-manager'
      }
    }

    stage('Deploy to VPS') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'ssh-key-id', keyFileVariable: 'KEY_PATH'),
          usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USERNAME', passwordVariable: 'DB_PASSWORD'),
          usernamePassword(credentialsId: 'mail-credentials', usernameVariable: 'MAIL_USERNAME', passwordVariable: 'MAIL_PASSWORD'),
          string(credentialsId: 'cloudinary-api-key', variable: 'CLOUDINARY_API_KEY'),
          string(credentialsId: 'cloudinary-api-secret', variable: 'CLOUDINARY_API_SECRET'),
          string(credentialsId: 'postgres-password', variable: 'POSTGRES_PASSWORD')
        ]) {
      sh """
        echo
        ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST 'mkdir -p $DEPLOY_DIR'

        echo
        scp -i \$KEY_PATH -o StrictHostKeyChecking=no -r . $SSH_USER@$SSH_HOST:$DEPLOY_DIR

        echo
        ssh -i \$KEY_PATH -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST << EOF
          cd $DEPLOY_DIR
          export POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
          export DB_USERNAME="${DB_USERNAME}"
          export DB_PASSWORD="${DB_PASSWORD}"
          export CLOUDINARY_API_KEY="${CLOUDINARY_API_KEY}"
          export CLOUDINARY_API_SECRET="${CLOUDINARY_API_SECRET}"
          docker-compose down -v
          docker-compose up -d --build
EOF
      """
        }
      }
    }
  }

  post {
    success {
      echo "Deployment zakończony sukcesem!"
    }
    failure {
      echo "Deployment nie powiódł się."
    }
  }
}