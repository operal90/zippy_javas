node{
  stage('Scm Checkout'){
    git'https://github.com/operal90/zippy_javas'
  }
  stage('Compile Package'){
    sh 'mvn package'
  }
}
