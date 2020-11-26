pipeline {
    agent {
        // 此处设定构建环境，目前可选有
        // default, java-8, python-3.5, ruby-2.3, go-1.11 等
        // 详情请阅 https://dev.tencent.com/help/knowledge-base/how-to-use-ci#agents
        label "java-8"
    }
    stages  {

        stage("检出") {
            steps {
                sh 'ci-init'
                checkout(
                  [$class: 'GitSCM', branches: [[name: env.GIT_BUILD_REF]],
                  userRemoteConfigs: [[url: env.GIT_REPO_URL]]]
                )
            }
        }

        stage("构建") {
            steps {
                echo "构建中..."
                sh 'java -version'
                sh 'mvn package'
                echo "构建完成."
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true // 收集构建产物
            }
        }

        stage("测试") {
            steps {
                echo "单元测试中..."
                // 请在这里放置您项目代码的单元测试调用过程，例如:
                sh 'mvn test'
                // sh 'make test' // make 示例
                echo "单元测试完成."
                //junit 'target/surefire-reports/*.xml' // 收集单元测试报告的调用过程
            }
        }

        stage("分发jar包") {
            steps {
                echo "分发中..."
                echo "chmod 600 pkey"
                sh 'chmod 600 authorized_keys.pem'
                echo "upload"
              	sh 'scp -i authorized_keys.pem ./target/*.jar root@192.168.0.3:/root/'
                echo "准备部署"
            }
        }

        stage("部署") {
            steps {
                //cloud 上部署预览
                sh 'scp -i authorized_keys.pem ./deploy.sh root@192.168.0.3:/root/'
                sh 'sleep 3'
                //sh 'chmod +x deploy.sh'
                //sh './deploy.sh'
                echo "cloud添加访问链接尝试访问url"
                sh 'curl http://192.168.0.3/hook/hook/'
                echo "业务服务请登录服务器手动部署或配置上面对应webhook"
            }
        }
    }
}