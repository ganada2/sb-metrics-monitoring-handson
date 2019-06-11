# Spring-Boot: Microservice Metrics Monitoring Hands-On

[2019.06.15] Oracle Developer Meetup Hands-On 문서

***

### 실습 목표

Spring Boot로 개발된 마이크로 서비스에 대한 Metrics 모니터링


### 실습을 위해 필요한 프로그램
* [Spring Tools 4 for Eclipse](https://spring.io/tools)
* [OpenJDK 12.0.1](https://jdk.java.net/12/)
* [Apache Maven 3.6.1](https://maven.apache.org/download.cgi)
* [Prometheus 2.10.0](https://prometheus.io/download/)
* [Grafana 6.2.2](https://grafana.com/grafana/download)

### 전체 프로그램 및 예제 소스 다운로드
* Windows 사용자
    http://~~~~~~~~~~~~~~~~~
* macOS 사용자
    http://~~~~~~~~~~~~~~~~~

### 설치 및 환경 구성
#### Windows
1. Windows 사용자의 경우는 Eclipse STS에 OpenJDK, Maven 구성 및 Project Import가 된 상태로 배포되기 때문에 STS에 추가적으로 설정할 부분은 없습니다. 압축 해제 시 루트 폴더 위치를 다음과 같이 C드라이브로 지정합니다.
    ```
    c:\oracle_dev_meetup0615_windows
    ```

2. OpenJDK, Maven, Prometheus, Grafana, Consul의 Path 설정을 합니다. Windows Command를 열고 Path에 다음과 같이 추가합니다.
    ```
    setx path "%PATH%;c:\oracle_dev_meetup0615_windows\jdk-12\bin;c:\oracle_dev_meetup0615_windows\apache-maven-3.6.1\bin;c:\oracle_dev_meetup0615_windows\consul-1.5.1;c:\oracle_dev_meetup0615_windows\grafana-6.2.2\bin;c:\oracle_dev_meetup0615_windows\prometheus-2.10.0"
    ```

3. 윈도우 탐색기에서 c:\oracle_dev_meetup0615_windows\sts-4.2.2.RELEASE\SpringToolSuite4.exe파일을 더블클릭해서 Eclipse STS를 실행한 후 Project Explorer에 정상적으로 6개의 Spring Boot Project가 오류없이 보이는지 확인합니다.
    ![](images/sts-import-complete.png)

#### macOS
1. macOS의 경우는 Ecipse STS가 dmg 이미지로 제공되기 때문에 설치 후 STS 설정이 필요합니다. 다운로드 받은 파일을 특정 위치(예시: /Users/DonghuKim/oracle_dev_meetup0615_macos)에 압축 해제한 후 **spring-tool-suite-4-4.2.2.RELEASE-e4.11.0-macosx.cocoa.x86_64.dmg** 파일을 더블 클릭해서 Applications 폴더로 이동합니다.

    ![](images/install-sts-on-macos.png)

2. OpenJDK, Maven, Prometheus, Grafana, Consul의 Path 설정을 합니다. .bash_profile에 환경 변수 및 Path를 설정합니다.

    ```
    $ vi ~/.bash_profile
    ```

    .bash_profile에 다음 내용을 추가합니다.
    ```
    export JAVA_HOME=/Users/DonghuKim/oracle_dev_meetup0615_macos/jdk-12/Contents/Home

    export MAVEN_HOME=/Users/DonghuKim/oracle_dev_meetup0615_macos/apache-maven-3.6.1

    export GRAFANA_HOME=/Users/DonghuKim/oracle_dev_meetup0615_macos/grafana-6.2.2

    export PROMETHEUS_HOME=/Users/DonghuKim/oracle_dev_meetup0615_macos/prometheus-2.10.0

    export CONSUL_HOME=/Users/DonghuKim/oracle_dev_meetup0615_macos/consul-1.5.1

    PATH=${PATH}:$JAVA_HOME/bin:$MAVEN_HOME/bin:$GRAFANA_HOME/bin:$PROMETHEUS_HOME:$CONSUL_HOME
    export PATH
    ```

3. $MAVEN_HOME/conf/settings.xml 파일을 열어서 Local Maven Repository를 다음과 같이 지정합니다. 아래 경로는 예시입니다.
    ```xml
    <localRepository>/Users/DonghuKim/oracle_dev_meetup0615_macos/apache-maven-3.6.1/repository</localRepository>
    ```

4. Launchpad 에서 STS를 실행합니다. workspace는 압축 해제한 폴더의 sts_workspace를 선택합니다.
    ![](images/sts-workspace.png)

5. Preferences를 열어서 Java > Installed JREs 선택 후 다음과 같이 입력하고 finish 버튼을 클릭 후, 설정한 JRE 체크 후 Apply and Close 버튼을 클릭합니다.
* JRE home
    * /Users/DonghuKim/oracle_dev_meetup0615_macos/jdk-12/Contents/Home
* JRE name
    * openjdk-12

    ![](images/sts-jre.png)

6. Preferences에서 Maven > User Settings 선택 후 다음과 같이 입력하고 Apply and Close 버튼을 클릭합니다.
* User Settings
    * /Users/DonghuKim/oracle_dev_meetup0615_macos/apache-maven-3.6.1/conf/settings.xml

    ![](images/sts-maven.png)

7. 예제 Spring Boot 프로젝트를 임포트합니다. File > Import 클릭 후 General > Existing  Projects into Workspace를 선택합니다.
    ![](images/sts-import-projects.png)
    sts_workspace 폴더내의 eureka-discovery-server 프로젝트를 포함해서 총 6개의 프로젝트(eureka-discovery-server, love-calculator-consumer, love-calculator-web, spring-admin-server, yes-or-no-consumer)를 임포트 합니다.
    ![](images/sts-import-project.png)
    Import를 하게 되면 Build와 함께 Dependency 라이브러리를 Maven Local Repository에 저장합니다. 빌드가 성공하면 아래와 같이 오류 없이 정상적으로 임포트가 완료됩니다.
    ![](images/sts-import-complete.png)

### 마이크로서비스 테스트

예제로 사용할 마이크로 서비스 애플리케이션은 Love Calculator 애플리케이션입니다. Love Calculator 애플리케이션은 주어진 두 사람의 이름으로 두 사람간의 관계 비율과 이미지로 표시해주는 애플리케이션입니다. 총 3개의 마이크로 서비스로 이뤄져 있으며, 두개의 오픈 API를 사용합니다.

........... 구성도 이미지 ...............

Windows
sts_workspace/start-all-svc.cmd

macOS
sts_workspace/start-all-svc.sh

love-calculator-web/WebContent/index.html 마우스 우 클릭 후 크롬 브라우져에서 연다...

이름 넣고 제출... 결과 나오는지 확인....

----------------------------------------------------

각 단계별로 그림을 한장씩 넣어야 할 듯...
- [Spring Boot Admin 실습으로 가기](HOL-PART1.md) --> 테스트 완료

- [Eureka + Spring Boot Admin 실습으로 가기](HOL-PART1.md)
  - Eureka 서버 설정
  - Spring Admin Server 설정
  - Service 설정
- [Prometheus + Grafana 실습으로 가기](HOL-PART1.md)

- [Consul + Prometheus + Grafana 실습으로 가기](HOL-PART1.md)
